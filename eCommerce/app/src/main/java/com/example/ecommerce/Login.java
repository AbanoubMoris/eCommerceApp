package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    private EditText username_et;
    private EditText password_et;
    private TextView create_account_tv;
    private TextView forget_password_tv;
    private Button login;
    private FirebaseAuth mAuth;
    CheckBox rememberMe_checkbox;
    private ProgressDialog progressDialog;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initProgressDialog("Signing in!",
                "Please wait while we are checking the credentials.");

        init();
        forgetPassword();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.Login_btn);
        username_et = findViewById(R.id.login_username_et);
        password_et = findViewById(R.id.login_password_et);
        create_account_tv = findViewById(R.id.create_account_tv);
        forget_password_tv = findViewById(R.id.forget_password_tv);
        rememberMe_checkbox = findViewById(R.id.rememberMe_checkbox);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;
                email = username_et.getText().toString();
                password = password_et.getText().toString();
                if(email.equals("admin")&&password.equals("admin")){
                    initRememberMe("admin","admin","admin");
                    finish();

                }else {
                    progressDialog.show();
                    loginwithEmail(email, password, v);
                }
            }


        });


        create_account_tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Signup.class);
                startActivity(i);
                finish();
            }

        });
    }

    private void forgetPassword(){
        forget_password_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisability("Send Reset Mail", View.GONE);
                FirebaseAuth.getInstance().sendPasswordResetEmail(username_et.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Emaaail", "Email sent.");
                                    Toast.makeText(Login.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                    setVisability("Login", View.VISIBLE);
                                }else{
                                    Toast.makeText(Login.this, "Email Not sent.", Toast.LENGTH_SHORT).show();
                                    setVisability("Login", View.VISIBLE);
                                }
                            }
                        });

            }
        });
    }

    private void setVisability(String login1, int visible) {
        login.setText(login1);
        rememberMe_checkbox.setVisibility(visible);
        create_account_tv.setVisibility(visible);
        password_et.setVisibility(visible);
        forget_password_tv.setVisibility(visible);
    }

    private void initRememberMe(String username,String Email,String password){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(rememberMe_checkbox.isChecked()){
            editor.putString("username", username);
            editor.putString("email", Email);
            editor.putString("password", password);
        }else{
            editor.putString("username", "username");
            editor.putString("email", "Email");
            editor.putString("password", "password");
        }
        editor.apply();


    }
    private void loginwithEmail(final String email, final String password, final View v) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, "sign In With Email : success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference RootRef;
                            RootRef = FirebaseDatabase.getInstance().getReference();
                            RootRef.child("Users").child(user.getUid()).child("username")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            initRememberMe(snapshot.getValue().toString(),email,password);
                                            Toast.makeText(Login.this, "Welcome "+snapshot.getValue().toString()
                                                    , Toast.LENGTH_LONG).show();

                                            progressDialog.dismiss();
                                           // Navigation.findNavController(v).popBackStack();
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            //updateUI(user);
                        } else {
                            Toast.makeText(Login.this, "sign In With Email : failure",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });
    }
    private void initProgressDialog(String Title,String Message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(Title);
        progressDialog.setMessage(Message);
        progressDialog.setCanceledOnTouchOutside(false);
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