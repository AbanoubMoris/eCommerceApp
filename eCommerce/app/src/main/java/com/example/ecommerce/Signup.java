package com.example.ecommerce;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private TextView birthdate_tv;
    private EditText mjobTitle;
    private Spinner mGender;
    private Button mSignupBtn;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUsername = findViewById(R.id.signup_username_et);
        mEmail = findViewById(R.id.signup_email_et);
        mPassword = findViewById(R.id.signup_password_et);
        mjobTitle = findViewById(R.id.signup_job_title_et);
        mGender = findViewById(R.id.signup_gender);
        mSignupBtn = findViewById(R.id.signup_btn);
        birthdate_tv = findViewById(R.id.birthdate_tv);
        birthdate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(birthdate_tv);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username,email,password,jobtitle,gender,birthdate;
                username = mUsername.getText().toString();
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                jobtitle = mjobTitle.getText().toString();
                gender = mGender.getTransitionName();
                birthdate = birthdate_tv.getText().toString();
                progressDialog.setTitle("Create Account");
                progressDialog.setMessage("Please wait while we are checking the credentials.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                signupToFireBase(username,email,password,jobtitle,gender,birthdate);
            }
        });
    }

    private void signupToFireBase(final String username, final String email, final String password, final String jobtitle, final String gender, final String birthdate) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadUserData(username, email, password, jobtitle, gender, birthdate);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(Signup.this, "User Already Exist,please try to login.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(Signup.this,Login.class));
                            finish();
                        }

                    }
                });

    }

    private void uploadUserData(String username, String email, String password, String jobtitle, String gender, String birthdate) {
        FirebaseUser user = mAuth.getCurrentUser();
        HashMap<String,Object> userDataMap= new HashMap<>();
        userDataMap.put("username",username);
        userDataMap.put("email",email);
        userDataMap.put("jobtitle",jobtitle);
        userDataMap.put("gender",gender);
        userDataMap.put("birthdate",birthdate);

        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(user.getUid()).updateChildren(userDataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Signup.this, "Congratulations!! Your account has been created", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(Signup.this, Login.class));
                            finish();
                        }else {
                            Toast.makeText(Signup.this, "Network Error, please try again later", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        TextView birth_date;
        public DatePickerFragment(TextView birth_date){
            this.birth_date = birth_date;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++;
            birth_date.setText(""+year+"/"+ month+"/"+day+"");


        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        getSupportActionBar().show();
    }
}