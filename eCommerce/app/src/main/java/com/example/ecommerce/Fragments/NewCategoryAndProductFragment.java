package com.example.ecommerce.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class NewCategoryAndProductFragment extends Fragment {


    private Boolean isProduct = false;
    private MaterialButton add_new_btn;
    private Bitmap photo;
    private Bitmap QR;
    private static final int pic_id = 123;

    private TextView click_me_tv;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private Spinner product_cat_sp;
    private ImageView new_category_iv;
    private ImageView qr_code;
    private TextInputLayout cat_name_ti;
    private TextInputLayout product_price_ti;
    private TextInputLayout product_quantity_ti;
    private TextInputLayout product_description_ti;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_new_category_and_product, container, false);


        add_new_btn = v.findViewById(R.id.add_new_btn);
        cat_name_ti = v.findViewById(R.id.cat_name_ti);
        product_price_ti = v.findViewById(R.id.product_price_ti);
        product_quantity_ti = v.findViewById(R.id.product_quantity_ti);
        product_description_ti = v.findViewById(R.id.product_description_ti);
        new_category_iv = v.findViewById(R.id.new_category_iv);
        qr_code = v.findViewById(R.id.qr_generate_iv);
        click_me_tv = v.findViewById(R.id.click_me_tv);
        product_cat_sp = v.findViewById(R.id.product_cat_sp);
        mStorageRef = FirebaseStorage.getInstance().getReference();




        checkSourceFragment(v);
        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    QR = TextToImageEncode(cat_name_ti.getEditText().getText().toString());
                    qr_code.setImageBitmap(QR);
                    click_me_tv.setVisibility(View.GONE);
                    //String path = saveImage(bitmap);  //give read write permission
                    //Toast.makeText(requireContext(), "QRCode saved to -> "+path, Toast.LENGTH_SHORT).show();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        add_new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProgressDialog("Uploading..."
                        ,"Please wait while we are checking the credentials.");
                progressDialog.show();
                uploadToFirebase();
            }
        });
        registerForContextMenu(new_category_iv);
        new_category_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Long press to choose a picture from camera or gallary", Toast.LENGTH_SHORT).show();
            }
        });




        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<String> categories=new ArrayAdapter<String>(requireContext(),android.R.layout.simple_list_item_1);
        RootRef.child("categories").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                categories.add(snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        product_cat_sp.setAdapter(categories);



    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getActivity().getMenuInflater().inflate(R.menu.image_context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
         super.onContextItemSelected(item);
         switch (item.getItemId()){
             case R.id.camera:
                 Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 startActivityForResult(i, pic_id);
                 break;
             case R.id.gallary:
                 Intent intent = new Intent();
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(intent, "Select Picture"),1234);
                 break;
         }

         return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        new_category_iv.setImageBitmap(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }else{
            try {
                photo = (Bitmap) data.getExtras().get("data");
                new_category_iv.setImageBitmap(photo);
            }catch (Exception e){
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), inImage, cat_name_ti.getEditText().getText().toString(), null);
        return Uri.parse(path);
    }
    private void initProgressDialog(String Title,String Message) {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(Title);
        progressDialog.setMessage(Message);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void uploadToFirebase(){

        if(isProduct){
            product_cat_sp.setVisibility(View.VISIBLE);
            cat_name_ti.getEditText().setHint("Product Name");
            final String ProductName = cat_name_ti.getEditText().getText().toString();
            final String product_price = product_price_ti.getEditText().getText().toString();
            final String product_quantity = product_quantity_ti.getEditText().getText().toString();
            final String product_description = product_description_ti.getEditText().getText().toString();
            final String[] product_pic = {""};
            final String[] qr_pic = {""};

            final StorageReference productRef = mStorageRef.child("images/products/"+ProductName+".jpg");
            productRef.putFile(getImageUri(photo))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Task<Uri> downloadUrl = productRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    product_pic[0] =uri.toString();
                                    //Toast.makeText(requireContext(), "data uploading successfully.", Toast.LENGTH_SHORT).show();
                                    //progressDialog.dismiss();
                                    //uploadProduct(ProductName, product_price, product_quantity, product_description, product_pic[0],qr);
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(requireContext(), "failed to upload picture", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
            final StorageReference productQRRef = mStorageRef.child("images/QR/"+ProductName+".jpg");
            productQRRef.putFile(getImageUri(QR))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Task<Uri> downloadUrl = productQRRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    qr_pic[0] =uri.toString();
                                    Toast.makeText(requireContext(), "data uploading successfully.", Toast.LENGTH_SHORT).show();
                                    //progressDialog.dismiss();
                                    uploadProduct(ProductName, product_price, product_quantity, product_description, product_pic[0],qr_pic[0]);
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(requireContext(), "failed to upload picture", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });




        }else{
            product_cat_sp.setVisibility(View.GONE);
            cat_name_ti.setHint("Category Name");

            final String ProductName = Objects.requireNonNull(cat_name_ti.getEditText()).getText().toString();
            final String[] product_pic = {""};

            final StorageReference productRef = mStorageRef.child("images/categories/"+ProductName+".jpg");
            productRef.putFile(getImageUri(photo))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Task<Uri> downloadUrl = productRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    product_pic[0] =uri.toString();
                                    Toast.makeText(requireContext(), "data uploading successfully.", Toast.LENGTH_SHORT).show();
                                    //progressDialog.dismiss();
                                    uploadProduct(ProductName, "", "", "", product_pic[0],"");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(requireContext(), "failed to upload picture", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });


        }
    }

    private void uploadProduct(String productName, String product_price, String product_quantity, String product_description, String value,String qr) {
        HashMap<String,Object> userDataMap= new HashMap<>();
        String toBeUploaded = "categories";
        if(!product_price.equals("")) {
            toBeUploaded = "products";
            userDataMap.put("ProductName", productName);
            userDataMap.put("product_price", product_price);
            userDataMap.put("product_quantity", product_quantity);
            userDataMap.put("product_description", product_description);
            userDataMap.put("QR",qr);
            userDataMap.put("category",product_cat_sp.getSelectedItem().toString());
            userDataMap.put("product_pic", value);
        }
        if(toBeUploaded.equals("categories")) {
            userDataMap.put("category_Name", productName);
            userDataMap.put("category_pic", value);
        }



        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child(toBeUploaded).child(productName).updateChildren(userDataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(requireContext() ,"Congratulations!! Your product has been created", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else {
                            Toast.makeText(requireContext(), "Network Error, please try again later", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void checkSourceFragment(View v) {
        Bundle i = getArguments();
        String sourceFragment = (String)i.getCharSequence("sourceFragment");
        if(sourceFragment.equals("product")){
            v.findViewById(R.id.product_cons).setVisibility(View.VISIBLE);
            isProduct = true;
        }else{
            v.findViewById(R.id.product_cons).setVisibility(View.GONE);
            isProduct = false;
        }
    }
}