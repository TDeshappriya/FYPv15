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

    public void sendData() {

        double iriValue = MapsActivity.vibrationTotal / (MapsActivity.distanceInMeters / 1000);

        String data = "[{ " +
                "\"userId\"" + ":" + "\"" + id + "\"," +
                "\"roadName\"" + ":" + "\"" + oldAddressName + "\" ," +
                "\"accelerometer\"" + ":" + "\"" + accDataArrayList + "\" ," +
                "\"latlng\"" + ":" + "\"" +  locDataArrayList.toString().replace("[","").replace("]","") + "\" ," +
                "\"iriValue\"" + ":" + "\"" + iriValue + "\" ," +
                "\"distanceInMeters\"" + ":" + "\"" + distanceInMeters + "\"" +
                "}]";

        Log.d(TAG, "sendData: "+ data);

        NetworkManager.getInstance().somePostRequestReturningString(data);

    }

}
