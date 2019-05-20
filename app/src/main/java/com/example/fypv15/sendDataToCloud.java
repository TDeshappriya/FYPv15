package com.example.fypv15;

import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.example.fypv15.MapsActivity.vibrationTotal;

public class sendDataToCloud extends CheckAddressName {

    private static String TAG;
    private RequestQueue requestQueue;

    public void sendData() {

        double iriValue = MapsActivity.vibrationTotal / (MapsActivity.distanceInMeters / 1000);

        String data = "[{ " +
                "\"userId\"" + ":" + "\"" + id + "\"," +
                "\"roadName\"" + ":" + "\"" + oldAddressName + "\" ," +
                "\"accelerometer\"" + ":" + "\"" + accDataArrayList + "\" ," +
                "\"latlng\"" + ":" + "\"" + locDataArrayList + "\" ," +
                "\"iriValue\"" + ":" + "\"" + iriValue + "\" ," +
                "\"distanceInMeters\"" + ":" + "\"" + distanceInMeters + "\"" +
                "}]";

        Log.d(TAG, "sendData: "+ data);
        Submit(data);
    }

    private void Submit(String data){
        final String savedata = data;
//        String URL = "https://my-fyp-1551939769568.appspot.com/data/add";
        String URL = "http://192.168.1.11:8080/data/add";

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
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
