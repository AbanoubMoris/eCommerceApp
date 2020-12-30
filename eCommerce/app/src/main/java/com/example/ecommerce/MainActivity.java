package com.example.ecommerce;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView BottomNavigationView;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView = findViewById(R.id.bottom_navigation);



        BottomNavigationView.setVisibility(0x00000000);//visable
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
}