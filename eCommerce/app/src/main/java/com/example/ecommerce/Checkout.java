package com.example.ecommerce;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.ecommerce.HelperClasses.MyLocationListner;
import com.example.ecommerce.Room.RoomFactory;
import com.example.ecommerce.asyncTasks.CustomerProducts.DeleteAsyncTask;
import com.example.ecommerce.asyncTasks.CustomerProducts.GetAsyncTask;
import com.example.ecommerce.models.CustomerProducts;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Checkout extends FragmentActivity implements OnMapReadyCallback {

    private String Uid;
    private double Total_Price;
    private String email;
    private  double percent = 87f;
    private int confirmationCode;
    List<CustomerProducts> customerProducts;


    private GoogleMap mMap;
    private MyLocationListner locListner;
    private LocationManager locManager;
    private MaterialButton change_location_btn;
    private MaterialButton setloc_btn;
    private SupportMapFragment mapFragment;

    private LatLng address_pos;

    private TextView checkout_username;
    private TextView checkout_street;
    private TextView checkout_city;
    private TextView checkout_town;
    private TextView checkout_phone;
    private FloatingActionButton getMYloc_btn;

    //3rd constraint
    private TextView checkout_subtotal_tv;
    private TextView shippingfees3_tv;
    private TextView checkout_total_tv;
    private TextView shippingfees2_tv;


    private MaterialButton proceed_to_del_btn;
    private EditText confirmation_code_et;



    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        init();
        final int[] Color_ = new int[1];
        change_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.map_cons).setVisibility(View.VISIBLE);
                setupMap();
                Color_[0] = checkout_city.getCurrentTextColor();
                checkout_street.setTextColor(Color.RED);
                checkout_city.setTextColor(Color.RED);
                checkout_town.setTextColor(Color.RED);
            }
        });
        setloc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.map_cons).setVisibility(View.GONE);
                checkout_street.setTextColor(Color_[0]);
                checkout_city.setTextColor(Color_[0]);
                checkout_town.setTextColor(Color_[0]);
            }
        });
        proceed_to_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildMessage();


            }
        });

        AddToSoldOrders();

    }

    private void AddToSoldOrders() {
        confirmation_code_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final ProgressDialog progressDialog;
                if (s.toString().equals(String.valueOf(confirmationCode))){
                    progressDialog = ProgressDialog.show(Checkout.this,
                            "Please wait","Checking credentials...",true);
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    int total_quantity=0;
                    for (CustomerProducts c : customerProducts) {
                        total_quantity+= c.getProduct_quantity();
                    }
                    userDataMap.put("quantity",total_quantity);
                    userDataMap.put("Total_price", Total_Price+percent);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' hh-mm");
                    String currentDateandTime = sdf.format(new Date());
                    userDataMap.put("date",currentDateandTime);

                    DatabaseReference RootRef;
                    RootRef = FirebaseDatabase.getInstance().getReference();
                        RootRef.child("sold").child(currentDateandTime).updateChildren(userDataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Checkout.this, "Congratulations!! Your Order on way!! ", Toast.LENGTH_SHORT).show();
                                            new DeleteAsyncTask(RoomFactory.getCustomerDao(Checkout.this).getCustomerDao()).execute();
                                            customerProducts.clear();
                                            finish();



                                            progressDialog.dismiss();
                                        }else {
                                            progressDialog.dismiss();
                                        }

                                    }
                                });

                    //Toast.makeText(Checkout.this, "Goood", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void BuildMessage() {
        Random r = new Random();
        customerProducts = new ArrayList<>();
        try {
            customerProducts.addAll(new GetAsyncTask(RoomFactory.getCustomerDao(Checkout.this).getCustomerDao()).execute().get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StringBuilder message = new StringBuilder();
        message.append("Hello "+checkout_username.getText()+",\n");
        for (CustomerProducts c : customerProducts){
            message.append(c.getProduct_quantity() + "x    ");
            message.append(c.getProductName() + " --> ");

            message.append(c.getProduct_price()*c.getProduct_quantity() + " EGP\n\n");
        }
        message.append("----------------------------------------------------------------\n");
        message.append("Subtotal = "+Total_Price+" EGP\n");
        message.append("Total fees = "+percent + "EGP\n");
        message.append("-------------------------------\n");
        message.append("Total = "+ checkout_total_tv.getText()+"\n\n");
        message.append("Confirmation code = ");
        confirmationCode = r.nextInt((99999-9999)+1)+9999;
        message.append(confirmationCode);
        message.append("\n------------Deceiver Details-----------\n");
        message.append(checkout_street.getText()+", "+checkout_city.getText()+", "+checkout_town.getText());
        sendMail(message.toString());
    }

    private void sendMail(String RandomM) {
        final String sEmail = "abanoub.express@gmail.com";
        final String sPassword = "01274198513";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sEmail,sPassword);
            }
        });

        MimeMessage message= new MimeMessage(session);

        try {
            //sender email
            message.setFrom(new InternetAddress(sEmail));
            //recipeient mail
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            //Email Subject
            message.setSubject("Final Delivery confirmation");
            //Email message
            message.setText(RandomM);
            //send email
            new SendMail().execute(message);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public class SendMail extends AsyncTask<Message,String,String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Checkout.this,
                    "Please wait","Sending Confirmation mail...",true);

        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Failed to send mail";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("Success"))
            {
                Toast.makeText(Checkout.this, "Email has been sent", Toast.LENGTH_SHORT).show();
                confirmation_code_et.setVisibility(View.VISIBLE);
                proceed_to_del_btn.setVisibility(View.GONE);
            }
            else Toast.makeText(Checkout.this, "failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        change_location_btn = findViewById(R.id.change_location_btn);
        setloc_btn = findViewById(R.id.setloc_btn);

        address_pos = new LatLng(30.0444, 31.237);

        checkout_username = findViewById(R.id.checkout_username);
        checkout_street = findViewById(R.id.checkout_street);
        checkout_city = findViewById(R.id.checkout_city);
        checkout_town = findViewById(R.id.checkout_town);
        checkout_phone = findViewById(R.id.checkout_phone);
        getMYloc_btn = findViewById(R.id.getMYloc_btn);

        checkout_subtotal_tv = findViewById(R.id.checkout_subtotal_tv);
        shippingfees3_tv = findViewById(R.id.shippingfees3_tv);
        checkout_total_tv = findViewById(R.id.checkout_total_tv);
        shippingfees2_tv = findViewById(R.id.shippingfees2_tv);

        proceed_to_del_btn = findViewById(R.id.proceed_to_del_btn);
        confirmation_code_et = findViewById(R.id.confirmation_code_et);

        final String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedpreferences;
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "Welcome!");
        String email1 = sharedpreferences.getString("email", "Enter your Account");
        checkout_username.setText(username);
        checkout_phone.setText(email1);

        Intent i = getIntent();
        Uid = i.getStringExtra("Uid");
        Total_Price = i.getDoubleExtra("Total_Price",0.0f);
        email = i.getStringExtra("email");
        percent = Total_Price*.09;
        percent= BigDecimal.valueOf(percent)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        if(percent<87f) percent=87f;
        checkout_subtotal_tv.setText("EGP " + Total_Price);
        shippingfees3_tv.setText("EGP " + (percent));
        checkout_total_tv.setText("EGP " + (percent+Total_Price));
        shippingfees2_tv.setText("EGP " + (percent));
    }

    @SuppressLint("MissingPermission")
    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locListner = new MyLocationListner(this);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, locListner);

        }catch (Exception e){

        }
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address_pos, 8));

        getMYloc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder geocoder = new Geocoder(Checkout.this);
                List<Address> addressList;
                try {
                    address_pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    addressList = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
                    if (!addressList.isEmpty()){
                        String address = "";
                        for(int i=0;i<addressList.get(0).getMaxAddressLineIndex();i++){
                            address+=addressList.get(0).getAddressLine(i) +" ,";
                        }
                        getAddressDetails(addressList);

                      //  mMap.addMarker(new MarkerOptions().position(mypos)
                            //    .title("My Location").snippet(address)).setDraggable(true);
                    }else {
                        Toast.makeText(Checkout.this, "no address for this location", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(Checkout.this, "Can't get the address check you network", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        mMap.clear();
        Geocoder coder = new Geocoder(this);
        List<Address> addressList;
        Location loc = null;

        try {
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            Toast.makeText(this, "You didn't allow to access current location!", Toast.LENGTH_SHORT).show();
        }
        if (loc!=null){
            address_pos= new LatLng(loc.getLatitude(),loc.getLongitude());
            try {
                addressList = coder.getFromLocation(address_pos.latitude,address_pos.longitude,1);

                if (!addressList.isEmpty()){
                    String address = "";
                    for(int i=0;i<addressList.get(0).getMaxAddressLineIndex();i++){
                        address+=addressList.get(0).getAddressLine(i) +" ,";
                    }
                    mMap.addMarker(new MarkerOptions().position(address_pos)
                            .title("My Location").snippet(address)).setDraggable(true);

                    getAddressDetails(addressList);

                }
            }catch (Exception e){
                mMap.addMarker(new MarkerOptions().position(address_pos)
                        .title("My Location"));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address_pos,15));
        }else {
            Toast.makeText(this, "Please wait until your location is determined!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressDetails(List<Address> addressList) {
        Address addr = addressList.get(0);
        StringBuffer str = new StringBuffer();
        str.append("“Name: ”" + addr.getLocality() + "\n");
        str.append("“Sub-Admin Ares: ”" + addr.getSubAdminArea() + "\n");
        str.append("“Admin Area: ”" + addr.getAdminArea() +"\n");
        str.append("“Country: ”" + addr.getCountryName() + "\n");
        str.append("“Country Code: ”" + addr.getCountryCode() + "\n");
        String strAddress = str.toString();
        Toast.makeText(this, strAddress, Toast.LENGTH_SHORT).show();


        checkout_street.setText(addr.getAddressLine(0).substring(0,addr.getAddressLine(0).indexOf(',')));
        checkout_city.setText(addr.getAdminArea());
        checkout_town.setText(addr.getSubAdminArea());
    }


}