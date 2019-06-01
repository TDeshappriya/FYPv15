package com.example.fypv15;

import java.util.ArrayList;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import static android.content.ContentValues.TAG;


public class CustomUsersAdapter extends ArrayAdapter<User> {
    public CustomUsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        // Get the data item for this position
        final User user = getItem(position);

//        System.out.println(user);

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHometown);
        // Populate the data into the template view using the data object

//        System.out.println("test" + user.getUserId());

        ImageButton btnTwoWheel = (ImageButton) convertView.findViewById(R.id.twoWheel);
        ImageButton btnFourWheel = (ImageButton) convertView.findViewById(R.id.fourWheel);
        final ImageButton btnClear = (ImageButton) convertView.findViewById(R.id.delete);


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getView: " + position);
                removeListItem(position);

                Snackbar.make(btnClear, "Record Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        btnTwoWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkManager.getInstance().update(user.getId(), "1");
            }
        });

        btnFourWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkManager.getInstance().update(user.getId(), "2");
            }
        });



        String test  = String.valueOf(user.getId());
        tvName.setText(user.getRoadName());
//        tvHome.setText(test);


        // Return the completed view to render on screen
        return convertView;
    }

    public void removeListItem(int position) {

        int idd = getItem(position).getId();
        this.remove(getItem(position));
        NetworkManager.getInstance().Submit(idd);
        this.notifyDataSetChanged();
    }





}
