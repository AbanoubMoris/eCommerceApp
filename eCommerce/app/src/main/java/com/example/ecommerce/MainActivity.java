package com.example.ecommerce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {



    BottomNavigationView BottomNavigationView;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        IFRememberdAccout();

    }

    private void IFRememberdAccout() {
        final String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedpreferences;
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //String username = sharedpreferences.getString("username", "Welcome!");
        String email = sharedpreferences.getString("email", "Enter your Account");
        String password = sharedpreferences.getString("password", "Enter your Account");
        boolean isRemembered = sharedpreferences.getBoolean("rememberMe", false);
        if (isRemembered)
            loginwithEmail(email,password);
        else
            FirebaseAuth.getInstance().signOut();
    }

    private void init() {
        //to link each fragment with navigation component
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        NavigationUI.setupWithNavController(bottomNav, navController);

        //set custom action Bar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        //getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();
//        SearchView search_result = view.findViewById(R.id.search_et);
        Button cart_btn = view.findViewById(R.id.cart_btn);
    }

    private void loginwithEmail(final String email, final String password) {

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "sign In With Email : success",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            //Toast.makeText(getApplicationContext(), "sign In With Email : failure",
                                 //   Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}