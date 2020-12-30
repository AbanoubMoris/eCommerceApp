package com.example.ecommerce.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.HelperClasses.GridSpanDecoration;
import com.example.ecommerce.R;
import com.example.ecommerce.Room.RoomFactory;
import com.example.ecommerce.adapters.ProductRvAdapter;
import com.example.ecommerce.asyncTasks.CustomerProducts.InsertAsyncTask;
import com.example.ecommerce.models.CustomerProducts;
import com.example.ecommerce.models.ProductModel;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {


    private RecyclerView product_rv;
    private FloatingActionButton add_new_product_btn;
    private EditText search_result;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private Boolean isAdmin=false;
    ProgressDialog progressDialog;

    List<ProductModel> productList = new ArrayList<>();
    ProductRvAdapter productsRvAdapter;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    private ImageButton barcode_btn;
    private ImageButton voice_btn;


    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setRequestedPreviewSize(800, 800)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                search_result.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                search_result.setText(barcodeData);
                                barcodeText.setVisibility(View.INVISIBLE);
                                surfaceView.setVisibility(View.INVISIBLE);

                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                            }
                        }
                    });

                }
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void initProgressDialog(String Title,String Message) {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(Title);
        progressDialog.setMessage(Message);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        product_rv = v.findViewById(R.id.product_rv);
        add_new_product_btn = v.findViewById(R.id.add_new_product_btn);
        initProgressDialog("Loading products","please wait a while...");
        progressDialog.show();

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = v.findViewById(R.id.surface_view);
        barcodeText = v.findViewById(R.id.barcode_text);
        surfaceView.setVisibility(View.INVISIBLE);

        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IsAdmin();
        add_new_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putCharSequence("sourceFragment","product");
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_newCategoryFragment,b);
            }
        });

        setupActionBar(view);

        Bundle args = getArguments();
        if (args!=null){
            String cat = (String)args.getCharSequence("filter");
            getProducts(cat);
        }else{
            getProducts("All");
        }


        setupRecyclerView();
        initialiseDetectorsAndSources();


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String mAnswer = results.get(0);
                search_result.setText(mAnswer);
            }
        }
    }

    private void setupActionBar(final View view) {
        View v = ((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView();
        search_result = v.findViewById(R.id.search_et);
        barcode_btn = v.findViewById(R.id.barcode_btn);
        voice_btn = v.findViewById(R.id.voice_btn);
        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-en");
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
                try
                {
                    startActivityForResult(i, 100);

                }
                catch (ActivityNotFoundException a)
                {
                    Toast.makeText(requireContext(),"Helllloooooooooooooooo",Toast.LENGTH_LONG).show();
                }
            }
        });
        barcode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.setVisibility(View.VISIBLE);
            }
        });
        final Button cart_btn = v.findViewById(R.id.cart_btn);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_cartFragment);
            }
        });
        search_result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = String.valueOf(s);
                productList.clear();
                getProducts(s1);
                progressDialog.show();
                //Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getProducts(final String filter){
        productList.clear();

        DatabaseReference mProductDB = FirebaseDatabase.getInstance().getReference().child("products");
        mProductDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                    ProductModel product = dSnapshot.getValue(ProductModel.class);
                    //Log.d("Show", product.getCategory() == null ? product.getProduct_pic() : product.getProduct_description());
                    if(filter.equals("All")) productList.add(product);
                    else if(product.getProductName().startsWith(filter)) productList.add(product);

                }
                productsRvAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setupRecyclerView(){

        product_rv.setLayoutManager(new GridLayoutManager(requireContext(),2));
        product_rv.addItemDecoration(new GridSpanDecoration(20));

        productsRvAdapter = new ProductRvAdapter(productList, requireContext()
                , new ProductRvAdapter.OnProductClick()
        {
            @Override
            public void onClick(View view, int position) {

                ProductModel clickedProduct = productList.get(position);
                clickedProduct.setProduct_quantity("1");
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", clickedProduct);
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_detailsFragment, bundle);


            }
        }, new ProductRvAdapter.OnProductClick() {
            @Override
            public void onClick(View view, int position) {
                ProductModel addProduct = productList.get(position);
                CustomerProducts p = new CustomerProducts();
                p.setProduct_quantity(1);
                p.setProductName(addProduct.getProductName());
                new InsertAsyncTask(RoomFactory.getCustomerDao(requireContext()).getCustomerDao()).execute(p);
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_cartFragment);
            }
        });
        product_rv.setAdapter(productsRvAdapter);
    }

    private void IsAdmin() {
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedpreferences.edit();
        String username = sharedpreferences.getString("username", "Welcome!");
        String password = sharedpreferences.getString("email", "Enter your Account");
        if(username.equals("admin")&&password.equals("admin")){
            isAdmin=true;
            add_new_product_btn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Welcome Admin", Toast.LENGTH_SHORT).show();
        }else {
            isAdmin=false;
            add_new_product_btn.setVisibility(View.GONE);

        }
    }
}