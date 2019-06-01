package com.example.fypv15;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class User {

    static ArrayList<User> listdata = new ArrayList<User>();

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoadName() {
        return roadName;
    }

    private int id;
    private String userId;
    private String roadName;

    public User(int id, String userId, String roadName) {
        this.id = id;
        this.userId = userId;
        this.roadName = roadName;
    }

    public static class DataFetcher extends AsyncTask<Void, String, String> {
        String responseStr = "";
        String res = "";
        JSONArray jArray = new JSONArray();


        CustomUsersAdapter customUsersAdapter;

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            String url = "https://my-fyp-1551939769568.appspot.com/data/getDataToListView";
//            String url = "http://192.168.1.11:8080/data/getDataToListView";


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                responseStr =  response.body().string();
                Log.d(TAG, "doInBackground: " + responseStr);
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
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                for ( int i = 0; i < jArray.length(); i++) {
                    JSONArray json = jArray.getJSONArray(i);
                    if(json.getString(0).equalsIgnoreCase(MapsActivity.id)) {
                        listdata.add(new User(json.getInt(1), json.getString(0), json.getString(2)));
                    }
                }

            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }


    public static ArrayList<User> getUsers() {

        ArrayList<User> users = (ArrayList<User>)listdata.clone();

        return users;
    }



}

