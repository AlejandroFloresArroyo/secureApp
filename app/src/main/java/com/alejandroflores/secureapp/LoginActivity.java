package com.alejandroflores.secureapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;
    private Button loginEmailButton;
    private EditText etEmail;


    private String idUsuario;

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null)
                writeInPreferences("");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Context context = this.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("IDUsuario", Context.MODE_PRIVATE);
        loginButton = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginEmailButton = findViewById(R.id.btnLoginEmail);
        etEmail = findViewById(R.id.editTextCorreo);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                idUsuario = loginResult.getAccessToken().getUserId();
                writeInPreferences(idUsuario);
                changeActivity();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });

        loginEmailButton.setOnClickListener((View v) ->{
            writeInPreferences(etEmail.getText().toString());
            changeActivity();
        });
    }

    private void writeInPreferences(String id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IdUsuario", id);
        editor.commit();
    }

    private void changeActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}