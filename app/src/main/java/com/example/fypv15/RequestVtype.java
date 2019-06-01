package com.example.fypv15;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import static android.content.ContentValues.TAG;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;



public class RequestVtype extends AppCompatActivity {


    public static String responseStr = "";
    CustomUsersAdapter adapter;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_vtype);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_1);

        NetworkManager.getInstance(this);

        User.DataFetcher dataFetcher = new User.DataFetcher();
        dataFetcher.execute();

        p = new ProgressDialog(RequestVtype.this);
        p.setMessage("Please wait...fetching data");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RequestVtype.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        populateUsersList();
                        p.hide();
                    }
                });
            }
        }, 7000);
    }

    public void populateUsersList() {

        ArrayList<User> arrayOfUsers = User.getUsers();

        Log.d(TAG, "populateUsersList: " + arrayOfUsers);
        // Create the adapter to convert the array to views
        adapter = new CustomUsersAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvUsers);
        listView.setAdapter(adapter);

    }

}
