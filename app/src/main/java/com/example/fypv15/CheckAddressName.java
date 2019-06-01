package com.example.fypv15;

import android.app.Dialog;
import android.util.Log;

import static android.content.ContentValues.TAG;


public class CheckAddressName extends MapsActivity {

    public static void checkAddressName() {
        if (AddressName != null) {
            if (AddressName.equalsIgnoreCase("") || AddressName.equalsIgnoreCase("NoAddress")) {

            } else if ((AddressName.equals(oldAddressName)) || (oldAddressName.equals("empty"))
                    || AddressName.equals("null") || oldAddressName.equals("null")) {
                addDataToArray();

            } else if (!AddressName.equalsIgnoreCase(oldAddressName)) {
                if (AddressName.equals("null")) {
                    addDataToArray();

                } else if (locDataArrayList.size() > 10) {
                    sendDataToBackend();

                } else {
                    addDataToArray();

                }
            }

            oldAddressName = AddressName;
        }
    }


    public static void addDataToArray() {
        vibrationTotal = vibrationTotal + vibration;

        String addAccelerometerData = Double.toString(zValueRounded);

        Log.d(TAG, "checkAddressName: " + locDataArrayList.size());
        MapsActivity.accDataArrayList.add(addAccelerometerData);


        if (MapsActivity.lastLocation == null) {
            MapsActivity.lastLocation = MapsActivity.currLocation;
        }
        MapsActivity.distanceInMeters += MapsActivity.currLocation.distanceTo(MapsActivity.lastLocation);
        MapsActivity.lastLocation = MapsActivity.currLocation;
    }

    public static void sendDataToBackend() {
        sendDataToCloud data = new sendDataToCloud();
        data.sendData();

        Log.d(TAG, "AddressName: " + AddressName);
        Log.d(TAG, "oldAddressName: " + oldAddressName);
        Log.d(TAG, "Arrray: " + locDataArrayList.size());


        accDataArrayList.clear();
        locDataArrayList.clear();

        MapsActivity.vibrationTotal = 0.00;

        MapsActivity.lastLocation = null;
        MapsActivity.distanceInMeters = 0.00;
    }
}


//                newAddressName = AddressName;
//
//                if(AddressName.equals(newAddressName)){
//                    vibrationTotal = vibrationTotal + vibration;
//
//                    String addAccelerometerData = Double.toString(zValueRounded);
//
//
//                    MapsActivity.accDataArrayList.add(addAccelerometerData);
//                    MapsActivity.locDataArrayList.add(addLatLngData);
//
//                    if (MapsActivity.lastLocation == null) {
//                        MapsActivity.lastLocation = MapsActivity.currLocation;
//                    }
//                    MapsActivity.distanceInMeters += MapsActivity.currLocation.distanceTo(MapsActivity.lastLocation);
//                    MapsActivity.lastLocation = MapsActivity.currLocation;
//
//                }else if (!AddressName.equals(newAddressName)){
//                    sendDataToCloud data = new sendDataToCloud();
//                    data.sendData();
//                    accDataArrayList.clear();
//                    locDataArrayList.clear();
//
//                    MapsActivity.vibrationTotal = 0.00;
//
//                    MapsActivity.lastLocation = null;
//                    MapsActivity.distanceInMeters = 0.00;
//                }