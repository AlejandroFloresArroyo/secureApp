package com.alejandroflores.secureapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("IDUsuario", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("IdUsuario", "");

        if (id == ""){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        }
        else
        {
            Intent intent2 = new Intent(this, MainActivity.class);
            startActivity(intent2);
        }
        finish();
    }
}