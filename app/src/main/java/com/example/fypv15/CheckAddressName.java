package com.example.fypv15;

public class CheckAddressName extends MapsActivity{

    public static void checkAddressName() {
        if (AddressName != null) {
            if (AddressName.equalsIgnoreCase("") || AddressName.equalsIgnoreCase("NoAddress")) {

            } else if ((AddressName.equals(oldAddressName)) || (oldAddressName.equalsIgnoreCase("empty"))
                    || AddressName.equalsIgnoreCase("null")) {

                vibrationTotal = vibrationTotal + vibration;

                String addAccelerometerData = Double.toString(zValueRounded);


                MapsActivity.accDataArrayList.add(addAccelerometerData);


                if (MapsActivity.lastLocation == null) {
                    MapsActivity.lastLocation = MapsActivity.currLocation;
                }
                MapsActivity.distanceInMeters += MapsActivity.currLocation.distanceTo(MapsActivity.lastLocation);
                MapsActivity.lastLocation = MapsActivity.currLocation;

            } else if (!AddressName.equals(oldAddressName)) {
                newAddressName = AddressName;

                if(newAddressName.equals(oldAddressName)){
                    vibrationTotal = vibrationTotal + vibration;

                    String addAccelerometerData = Double.toString(zValueRounded);


                    MapsActivity.accDataArrayList.add(addAccelerometerData);
                    MapsActivity.locDataArrayList.add(addLatLngData);

                    if (MapsActivity.lastLocation == null) {
                        MapsActivity.lastLocation = MapsActivity.currLocation;
                    }
                    MapsActivity.distanceInMeters += MapsActivity.currLocation.distanceTo(MapsActivity.lastLocation);
                    MapsActivity.lastLocation = MapsActivity.currLocation;

                }else if (!newAddressName.equals(oldAddressName)){
                    sendDataToCloud data = new sendDataToCloud();
                    data.sendData();
                    accDataArrayList.clear();
                    locDataArrayList.clear();

                    MapsActivity.vibrationTotal = 0.00;

                    MapsActivity.lastLocation = null;
                    MapsActivity.distanceInMeters = 0.00;
                }

            }
            oldAddressName = AddressName;
        }
    }
}
