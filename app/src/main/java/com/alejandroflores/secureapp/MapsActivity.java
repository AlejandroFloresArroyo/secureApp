package com.alejandroflores.secureapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alejandroflores.secureapp.Interface.SegurappUserApi;
import com.alejandroflores.secureapp.Model.UsersPosts;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final int Request_User_Location_Code = 101;
    private LocationManager locationManager;
    private FloatingActionButton floatingActionButton;
    private Boolean userRegistered = false;

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://rocky-mesa-36353.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
    SegurappUserApi segurappUserApi = retrofit.create(SegurappUserApi.class);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        floatingActionButton = findViewById(R.id.fab);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermition();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        floatingActionButton.setOnClickListener((View v) ->{
            Snackbar.make(v, "Ahora estás pidiendo ayuda", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            getHelp();
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings controlesMapa;
        controlesMapa = googleMap.getUiSettings();
        controlesMapa.setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
        }
    }


    public boolean checkUserLocationPermition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        makeCameraUpdate(location);
        locationManager.removeUpdates((android.location.LocationListener) this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getNearUsers();
        getEverySecond();
        makeCameraUpdate(getLastLocation());
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }


    public void makeCameraUpdate(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
    }


    public void addMarkerForUsers(Location location, Boolean ayuda){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if (ayuda) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.title("Auxilio!");

        } else{
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.title("Disponible!");

        }
        mMap.addMarker(markerOptions);
    }


    public void removeAllMarkers(){
        mMap.clear();
    }


    public Location getLastLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(provider);
        }
        return location;
    }

    public String getSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("IDUsuario", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("IdUsuario", "");
        return id;
    }


    public void getNearUsers(){
        ArrayList<Location> locations = new ArrayList<Location>();
        Call<List<UsersPosts>> call = segurappUserApi.getNearestUsers(getLastLocation().getLongitude(), getLastLocation().getLatitude());
        call.enqueue(new Callback<List<UsersPosts>>() {
            @Override
            public void onResponse(Call<List<UsersPosts>> call, Response<List<UsersPosts>> response) {
                Log.d("onresponse", "onResponse: =========start======================= ");
                MapsActivity mapsActivity = new MapsActivity();
                if (!response.isSuccessful()){
                    Log.d("Codigo error", "onResponse: " + response.code());
                    return;
                }
                List<UsersPosts> usersPosts = response.body();
                for (UsersPosts userPost: usersPosts) {
                    if (userPost.getFacebookId().equals(getSharedPreferences())) {
                        userRegistered = true;
                        Log.d("Obtuvimos user", ":::::::::::::::::::::::::::::::Ya registrado:::::::::::::::::::::::::::::  " + userPost.getFacebookId());
                    }



                    if (userPost.getFacebookId().equals(getSharedPreferences())) {
                        Log.d("lololololololololo", "onResponse: catch user ");
                    }else {
                        Location location = new Location("");
                        List loc = userPost.getGeometry().getCoordinates();
                        Boolean ayuda = userPost.isNeedHelp();
                        location.setLatitude(Double.valueOf(loc.get(1).toString()));
                        location.setLongitude(Double.valueOf(loc.get(0).toString()));
                        addMarkerForUsers(location, ayuda);
                        Log.d("Obtuvimos user", "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::  " + userPost.getFacebookId() + " " + location.getLongitude() + " " + location.getLatitude());

                    }
                }

                if (userRegistered == false){
                    postMyUser();
                    Log.d("postMyUser", "onMapReady:dddddd!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! executed");
                }
            }


            @Override
            public void onFailure(Call<List<UsersPosts>> call, Throwable t) {
                Log.d("Falla", "onFailure: " + t.getMessage());
            }
        });
    }


    public void updateMyUserLocation() {
        List<Number> numbers = new ArrayList<Number>();
        numbers.add(0, getLastLocation().getLongitude());
        numbers.add(1, getLastLocation().getLatitude());
        UsersPosts.GeometryBean geometryBean = new UsersPosts.GeometryBean(numbers);
        UsersPosts usersPosts = new UsersPosts(geometryBean);

        Call<UsersPosts> call = segurappUserApi.putPost(getSharedPreferences(),usersPosts);
        call.enqueue(new Callback<UsersPosts>() {
            @Override
            public void onResponse(Call<UsersPosts> call, Response<UsersPosts> response) {
                if (!response.isSuccessful()){
                    Log.d("getHelp failed", "onResponse: " + response);
                }
                Log.d("getHelp success", "onResponse::::::::::::::::::::::::::::::::::: " + response);
            }

            @Override
            public void onFailure(Call<UsersPosts> call, Throwable t) {
                Log.d("getHelp Failure", "onFailure: " + t.getMessage());
            }
        });

    }

    private void getHelp() {
        UsersPosts usersPosts = new UsersPosts(true);
        Call<UsersPosts> call = segurappUserApi.putPost(getSharedPreferences(), usersPosts);
        call.enqueue(new Callback<UsersPosts>() {
            @Override
            public void onResponse(Call<UsersPosts> call, Response<UsersPosts> response) {
                if (!response.isSuccessful()){
                    Log.d("getHelp failed", "onResponse: " + response);
                }
                Log.d("getHelp success", "onResponse::::::::::::::::::::::::::::::::::: " + response);
            }

            @Override
            public void onFailure(Call<UsersPosts> call, Throwable t) {
                Log.d("getHelp Failure", "onFailure: " + t.getMessage());
            }
        });
    }


    public boolean isMyUserRegistered(){
        return userRegistered;
    }


    public void postMyUser(){
        List<Number> numbers = new ArrayList<Number>();
        numbers.add(0, getLastLocation().getLongitude());
        numbers.add(1, getLastLocation().getLatitude());
        UsersPosts.GeometryBean geometryBean = new UsersPosts.GeometryBean(numbers);
        UsersPosts usersPosts = new UsersPosts(true, false, getSharedPreferences(), geometryBean);
        Call<UsersPosts> call = segurappUserApi.createPost(usersPosts);

        call.enqueue(new Callback<UsersPosts>() {
            @Override
            public void onResponse(Call<UsersPosts> call, Response<UsersPosts> response) {
                if (!response.isSuccessful()){
                    Log.d("Onresponse failed", "onResponse: " + response);
                }

                Log.d("OnResponse success", "onResponse::::::::::::::::::::::::::::::::::: " + response);
            }

            @Override
            public void onFailure(Call<UsersPosts> call, Throwable t) {
                Log.d("Failure", "onFailure: " + t.getMessage());
            }
        });

    }

    public void getEverySecond(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getNearUsers();
                updateMyUserLocation();
                Log.d("Near=====", "run: Getting near users");

            }
        }, 0, 5000);
    }


}
