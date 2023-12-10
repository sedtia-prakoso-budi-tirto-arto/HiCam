package com.example.fp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_LOCATION = 3;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationSaver locSaver;
    AlertDialog alertDialog;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private TextView showLocation;

    ArrayList<String> permissionsList;
    String[] permissionsStr = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
    };
    int permissionsCount = 0;
    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
//                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onActivityResult(Map<String,Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                }else if (!hasPermission(MainActivity.this, permissionsStr[i])){
                                    permissionsCount++;
                                }
                            }if (permissionsList.size() > 0) {
                                //Some permissions are denied and can be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
                                showPermissionDialog();
                            } else {
                                initializeLocationManager();
                                //All permissions granted. Do your stuff 🤞
                            }
                        }
                    });


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View kameraView = findViewById(R.id.kameraView);
        kameraView.setOnClickListener(v -> openKameraView());

        View camView = findViewById(R.id.camView);
        camView.setOnClickListener(v -> openKameraView());

        View aboutView = findViewById(R.id.about);
        aboutView.setOnClickListener(v -> sendAndRequestResponse());

//        View mainGrid = findViewById(R.id.about_bawah);
//        mainGrid.setOnClickListener(v -> openMainGrid());

        View AlbView = findViewById(R.id.galeriView);
        AlbView.setOnClickListener(v -> openAlbumView());

        showLocation = findViewById(R.id.show_location);

        View imageViewGetLocation = findViewById(R.id.Peta);

        imageViewGetLocation.setOnClickListener(v -> requestLocationUpdates());

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);

    }


    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        } else {
        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are need to be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    dialog.dismiss();
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    private void openKameraView() {
//        if(locSaver.getLocStatus()){
            Intent kamera = new Intent(this, KameraView.class);
            startActivity(kamera);
//        }
//        else{
//            showToast("lokasi tidak tersedia atau izinkan kamera, lokasi, dan penyimpanan");
//        }
    }

    private void openAlbumView(){
        Intent album = new Intent(this, AlbumView.class);
        startActivity(album);
    }


    private void initializeLocationManager() {
        // Check if the app has the location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Check if GPS or network location providers are enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                // Start listening for location updates
                startLocationUpdates();
            } else {
                // Show a message to the user to enable GPS or network
                Toast.makeText(this, "Enable GPS or network location", Toast.LENGTH_LONG).show();
            }
        } else {
            // Request location permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    // Mulai mendengarkan pembaruan lokasi
    private void startLocationUpdates() {
        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Mendapatkan longitude dan latitude
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();


                    // Menampilkan Toast dengan informasi lokasi
                    Toast.makeText(MainActivity.this, "Longitude: " + longitude + "\nLatitude: " + latitude, Toast.LENGTH_SHORT).show();

                    // Hentikan pembaruan lokasi setelah mendapatkan satu pembaruan
                    locationManager.removeUpdates(this);
//                    requestLocation(latitude, longitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Jika izin lokasi belum diberikan, minta izinnya
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
                return;
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    locationListener);

        } else {
            // Jika izin lokasi belum diberikan, minta izinnya
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    private void requestLocationUpdates() {
        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Initialize location manager
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Check if GPS or network location providers are enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                // Initialize location listener
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // Display latitude and longitude in a Toast
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Toast.makeText(MainActivity.this, "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_LONG).show();

                        // Stop location updates after receiving the first location
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                };

                // Request location updates
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

            } else {
                // Show a message to the user to enable GPS or network
                Toast.makeText(this, "Enable GPS or network location", Toast.LENGTH_LONG).show();
            }
        } else {
            // Request location permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationManager();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestLocation(double lan, double lon) {

        RequestQueue queue = Volley.newRequestQueue(this);


        String baseUrl = "http://192.168.0.104:3000/get-loc";
        String param1 = String.valueOf(lan);
        String param2 = String.valueOf(lon);
//        String param1 = "7.290942279635451";
//        String param2 = "112.7967344248446";
//
//
        String url = baseUrl + "?latitude=" + param1 + "&longitude=" + param2;
//        String url ="http://192.168.0.108:3000/";

        Toast.makeText(getApplicationContext(),"telah klik about" , Toast.LENGTH_LONG).show();

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    String lokasi = null;
                    String deskripsi = null;
                    boolean locStatus = false;

                    if(jsonResponse.getInt("status") == 200){
                        lokasi = jsonResponse.getString("lokasi");
                        deskripsi = jsonResponse.getString("deskripsi");
                        locStatus = true;

                        locSaver = new LocationSaver(lokasi, deskripsi, locStatus);
                        showLocation.setText(locSaver.getLocName());
                    }
                    else {
                        lokasi = jsonResponse.getString("message");
                        deskripsi = "lokasi mungkin tidak terkenal";

                        locSaver = new LocationSaver(lokasi, deskripsi, locStatus);
                        showLocation.setText(locSaver.getLocName());
                    }

                    Toast.makeText(MainActivity.this, "lokasi :" + lokasi + "\n" +" deskripsi :" + deskripsi, Toast.LENGTH_LONG).show();
                }
                catch (JSONException e){
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Response :" + error, Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error :" + error.toString());
            }
        });

        queue.add(mStringRequest);
    }
    private void sendAndRequestResponse() {

        //RequestQueue initialized
//        mRequestQueue = Volley.newRequestQueue(this);
        RequestQueue queue = Volley.newRequestQueue(this);


        String baseUrl = "http://192.168.0.104:3000/get-loc";
        String param1 = "7.290942279635451";
        String param2 = "112.7967344248446";
//
//
        String url = baseUrl + "?latitude=" + param1 + "&longitude=" + param2;
//        String url ="http://192.168.0.108:3000/";

        Toast.makeText(getApplicationContext(),"telah klik about" , Toast.LENGTH_LONG).show();

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() >= 500) {
                    // Jika panjang response cukup, tampilkan substring
                    Toast.makeText(getApplicationContext(), "Response :" + response.substring(0, 500), Toast.LENGTH_LONG).show();
                } else {
                    // Jika panjang response kurang dari 500, tampilkan response utuh
                    Toast.makeText(getApplicationContext(), "Response :" + response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Response :" + error, Toast.LENGTH_LONG).show();
                Log.i(TAG,"Error :" + error.toString());
            }
        });

        queue.add(mStringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

