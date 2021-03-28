package com.example.ecommerce.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    Button account_login_btn;
    Button account_bottom_login_btn;
    TextView welcome_tv;
    TextView account_email_tv;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        account_login_btn = v.findViewById(R.id.account_login_btn);
        account_bottom_login_btn = v.findViewById(R.id.account_bottom_login_btn);
        welcome_tv = v.findViewById(R.id.welcome_tv);
        account_email_tv = v.findViewById(R.id.account_email_tv);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClickListeners();
        getUserName();
    }

    private void getUserName(){
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedpreferences.edit();
        String username = sharedpreferences.getString("username", "Welcome!");
        String email = sharedpreferences.getString("email", "Enter your Account");
        if(!email.equals("Enter your Account")){
            welcome_tv.setText("Welcome "+username+"!");
            welcome_tv.setTextColor(Color.parseColor("#ED4915"));
            account_email_tv.setText(email);
            account_login_btn.setVisibility(View.GONE);
            account_bottom_login_btn.setText("Logout");
        }



    }

    private void initClickListeners() {
        account_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_login);
            }
        });
        account_bottom_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account_bottom_login_btn.getText().equals("Logout")){
                    account_login_btn.setVisibility(View.VISIBLE);
                    account_bottom_login_btn.setText("Login");
                   // welcome_tv.setText("Welcome!");
                    welcome_tv.setTextColor(Color.WHITE);
                    account_email_tv.setText("Enter your Account");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();

                    FirebaseAuth.getInstance().signOut();


                }else {
                    Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_login);
                }
            }
        });
    }

}