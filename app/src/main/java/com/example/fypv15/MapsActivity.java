package com.example.fypv15;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.fypv15.lowPassFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {
    private RequestQueue requestQueue;
    private GoogleMap mMap;
    protected LocationManager locationManager;
    private SensorManager sensorManager;
    Sensor accelerometer;
    static double longitude,latitude;
    static String addLatLngData;
    public float [] gravSensorVals;
    public static double zValueRounded;
    static double vibration;
    static String AddressName;
    static String oldAddressName = "empty";
    static double distanceInMeters;
    static Location lastLocation = null;
    static Location currLocation;
    static double vibrationTotal;
    public static ArrayList<String> accDataArrayList = new ArrayList<String>();
    public static ArrayList<String> locDataArrayList = new ArrayList<String>();
    static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//Submit();
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MapsActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");

                    break;
                default:
                    locationAddress = null;
            }

            AddressName = locationAddress.toString();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());

            currLocation = location;

            addLatLngData =  latitude + " " + longitude ;
            locDataArrayList.add(addLatLngData);
        } else {
            showSettingsAlert();
        }
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

    @Override
    public void onSensorChanged (SensorEvent sensorEvent)  {
        Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " "+"Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
//      Filtering data with low pass filter
        gravSensorVals = lowPassFilter.lowPass(sensorEvent.values.clone(), gravSensorVals);

        Log.d(TAG, "ontesterChanged: X: " + gravSensorVals[0] + "Y: " + gravSensorVals[1] + "Z: " + gravSensorVals[2]);
//      Filtering data with High Pass filter and get the Z axis value
        zValueRounded = highPassFilter.highPass(gravSensorVals);

        Log.d(TAG, "zValueRounded: " + zValueRounded);

        vibration = Math.abs(0.5 *  zValueRounded * (0.02*0.02)) ;

        CheckAddressName.checkAddressName();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MapsActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MapsActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void Submit(){
        final String savedata = "[{ \"userId\":\"031d8a3bac5e1276\",\"roadName\":\"Station Road\" ,\"accelerometer\":\"[0.16, 0.45, 0.67, 0.84, 0.82, 0.68, 0.52, 0.4, 0.3, 0.2, 0.1, -0.01, -0.11, -0.21, -0.28, -0.3, -0.27, -0.2, -0.09, 0.15, 0.43, 0.65, 0.82, 0.82, 0.72, 0.59, 0.47, 0.36, 0.26, 0.15, 0.02, -0.1, -0.21, -0.3, -0.36, -0.36, -0.31, -0.22, 0.03, 0.33, 0.56, 0.74, 0.73, 0.58, 0.4, 0.26, 0.15, 0.05, -0.05, -0.15, -0.27, -0.39, -0.45, -0.46, -0.41, -0.31, -0.11, 0.21, 0.46, 0.67, 0.73, 0.61, 0.43, 0.29, 0.2, 0.12, 0.04, -0.06, -0.16, -0.24, -0.3, -0.29, -0.26, -0.2, -0.11, 0.15, 0.41, 0.62, 0.76, 0.7, 0.54, 0.36, 0.23, 0.13, 0.03, -0.08, -0.2, -0.31, -0.38, -0.38, -0.34, -0.24, -0.03, 0.27, 0.5, 0.68, 0.73, 0.64, 0.49, 0.35, 0.22, 0.11, -0.01, -0.15, -0.28, -0.39, -0.44, -0.43, -0.37, -0.26, -0.1, 0.2, 0.45, 0.64, 0.76, 0.71, 0.58, 0.44, 0.31, 0.18, 0.06, -0.07, -0.19, -0.3, -0.4, -0.45, -0.44, -0.34, -0.05, 0.24, 0.47, 0.66, 0.65, 0.52, 0.36, 0.22, 0.12, 0.03, -0.07, -0.17, -0.27, -0.36, -0.42, -0.44, -0.41, -0.33, -0.13, 0.18, 0.42, 0.62, 0.67, 0.57, 0.39]\" ,\"latlng\":\"[6.83754332 79.87149777, 6.83754332 79.87149777, 6.83754478 79.87149819, 6.83754791 79.87151206, 6.83753231 79.8715086]\" ,\"iriValue\":\"6.129838733529648\" ,\"distanceInMeters\":\"1.7396868765354156\"}]";
//        String URL = "https://my-fyp-1551939769568.appspot.com/data/add";
        String URL = "http://192.168.1.11:8080/data/insertData";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), objres.toString(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })
        {
            public String getBodyContentType() {return "application/json; charset=utf-8";}

            public byte[] getBody() throws AuthFailureError {
                try {
                    return savedata == null ? null : savedata.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee){
                    return null;
                }
            }

        };

        requestQueue.add(stringRequest);
    }
}
