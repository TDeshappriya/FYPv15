package com.example.fypv15;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager
{
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;


    //for Volley API
    public RequestQueue requestQueue;

    private NetworkManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //other stuf if you need
    }

    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }

    //this is so you don't need to pass context each time
    public static synchronized NetworkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void somePostRequestReturningString(String data) {

        final String savedata = data;
        String URL = "https://my-fyp-1551939769568.appspot.com/data/insertData";
//        String URL = "http://192.168.1.11:8080/data/insertData";

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

    public void update(int id, String vTpye) {

//        public void updateVtype(int id, String vTpye){
        final int idData = id;
        String vTypeData = vTpye;
        String URL = "https://my-fyp-1551939769568.appspot.com/data/updateVtype";
//        String URL = "http://192.168.1.13:8080/data/add";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL +"/"+ idData + "/"+ vTypeData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }


    public void Submit(int id){
        final int savedata = id;
        String URL = "https://my-fyp-1551939769568.appspot.com/data/deleteRecord/";
//        String URL = "http://192.168.1.13:8080/data/add";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL +"/"+ savedata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
//                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
//
        requestQueue.add(stringRequest);
    }
}


