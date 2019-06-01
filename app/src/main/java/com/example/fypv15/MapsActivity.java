package com.example.fypv15;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

import com.android.volley.RequestQueue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import com.google.android.gms.maps.model.MarkerOptions;

import com.example.fypv15.lowPassFilter;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.Manifest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapsActivity extends AppCompatActivity implements AppCompatCallback, LocationListener, SensorEventListener, OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {


    private RequestQueue requestQueue;
    private GoogleMap mMap;
    View mapView;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    protected LocationManager locationManager;
    private SensorManager sensorManager;
    Sensor accelerometer;
    static double longitude, latitude;
    static String addLatLngData;
    public float[] gravSensorVals;
    public static double zValueRounded;
    static double vibration;
    static String AddressName;
    static String oldAddressName = "empty";
    static String newAddressName;
    static double distanceInMeters;
    static Location lastLocation = null;
    static Location currLocation;
    static double vibrationTotal;
    public static ArrayList<String> accDataArrayList = new ArrayList<String>();
    public static ArrayList<String> locDataArrayList = new ArrayList<String>();
    static String id;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    ProgressDialog p;
    String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
//        FloatingActionButton fab = findViewById(R.id.imageButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MapsActivity.this, RequestVtype.class);
//                startActivity(intent);
//
//
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MapsActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
//            ActivityCompat.requestPermissions(this,
//
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

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
//        Submit();

        NetworkManager.getInstance(this);

        FloatingActionButton fab = findViewById(R.id.imageButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, RequestVtype.class);
                startActivity(intent);

            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 100, 0, 0);


        DataFetcher fetcher = new DataFetcher();
        fetcher.execute();
    }



    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    //
// Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

//    public void drawPolyLines(String data, String res) {
////        System.out.print(data);
//        Log.d(TAG, "drawPolyLines: " + data);
//        String[] dataList = data.split(", ");
////        String[] array = new String[]{"79.87017185 6.83683996, 0.75 79.87017185 6.83749156","79.87017185 6.83683996, 0.75 79.87017185 6.8374915"};
//        String[] array = new String[]{res};
//        System.out.println(array);
//        List<LatLng> latLngList = new ArrayList<LatLng>();
//        PolylineOptions polyOptions = new PolylineOptions();
//        polyOptions.clickable(true);
//
//        for(String row : dataList) {
//            row.split(" ");
//            polyOptions.add(new LatLng(Double.parseDouble(row.split(" ")[2]), Double.parseDouble(row.split(" ")[1])));
//
//        }
////        polyOptions.color(5);
////        Log.d(TAG, "drawPolyLines: " + polyOptions);
//        Polyline polyline1 = mMap.addPolyline(polyOptions);
//        polyline1.setTag("A");
//        // Position the map's camera near Alice Springs in the center of Australia,
//        // and set the zoom factor so most of Australia shows on the screen.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.83683996, 79.86984405), 20));
//
////         Set listeners for click events.
//        mMap.setOnPolylineClickListener(this);
//
//        PolylineOptions op = new PolylineOptions();
//        op.clickable(true);
//        op.add(new LatLng(6.83683996, 79.87017185));
//        op.add(new LatLng(6.83749156, 79.86985665));
//        polyline1 = mMap.addPolyline(op);
//
//    }

    public void drawPolyLines(String data, String status) {

        String[] dataList = data.split(", ");

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.clickable(true);

        for(String row : dataList) {
            row.split(" ");
            polyOptions.add(new LatLng(Double.parseDouble(row.split(" ")[0]), Double.parseDouble(row.split(" ")[1])));
        }

        switch(status)
        {
            case "Good":
                polyOptions.color(Color.GREEN);
                break;
            case "Average":
                polyOptions.color(Color.CYAN);
                break;
            case "Bad":
                polyOptions.color(Color.RED);
                break;
        }


//        polyOptions.color(android.R.color.holo_blue_dark);
//        Log.d(TAG, "drawPolyLines: " + polyOptions);
        Polyline polyline1 = mMap.addPolyline(polyOptions);
        polyline1.setTag("A");
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
//
            mMap.animateCamera(CameraUpdateFactory.scrollBy(3 / 8, 0));
//         Set listeners for click events.
        mMap.setOnPolylineClickListener(this);


    }

    public class DataFetcher extends AsyncTask<Void, String, String> {
        String responseStr = "";
        String res = "";
        JSONArray jArray = new JSONArray();
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
        String url = "https://my-fyp-1551939769568.appspot.com/data/getDataToMap";
//            String url = "http://192.168.43.2:8080/data/getDataToMap";


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseStr =  response.body().string();

                try {
                    jArray = new JSONArray(responseStr);

                    System.out.println(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return responseStr;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                for ( int i = 0; i < jArray.length(); i++) {
                    JSONArray json = jArray.getJSONArray(i);
                    drawPolyLines(json.getString(1), json.getString(0));
                }
                p.hide();


            } catch(Exception e){
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MapsActivity.this);
            p.setMessage("Please wait...populating the map");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }
    }

    public  void displaymsg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Search is empty")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address>addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }



            Address address = addressList.get(0);

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));

//            mMap.animateCamera(CameraUpdateFactory.scrollBy(3 / 8, 0));
        }
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
            Log.d(TAG, "handleMessage: " + AddressName);
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
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 100));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
//
//            mMap.animateCamera(CameraUpdateFactory.scrollBy(3 / 8, 0));
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
//        Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " "+"Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
//      Filtering data with low pass filter
        gravSensorVals = lowPassFilter.lowPass(sensorEvent.values.clone(), gravSensorVals);

//        Log.d(TAG, "ontesterChanged: X: " + gravSensorVals[0] + "Y: " + gravSensorVals[1] + "Z: " + gravSensorVals[2]);
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
}
