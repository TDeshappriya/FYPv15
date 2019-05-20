package com.example.fypv15;

public class highPassFilter extends  MapsActivity{

    static final float alpha = 0.8f;
    public static float [] gravity = {0.00f,0.00f,0.00f};
    public static float [] linear_acceleration = {0.00f,0.00f,0.00f};
    static double zValueRounded;

    protected static double highPass(float[] gravSensorVals) {


        gravity[0] = (alpha * gravity[0] + (1 - alpha) * gravSensorVals[0]);
        gravity[1] = (alpha * gravity[1] + (1 - alpha) * gravSensorVals[1]);
        gravity[2] = (alpha * gravity[2] + (1 - alpha) * gravSensorVals[2]);

        linear_acceleration[0] = gravSensorVals[0] - gravity[0];
        linear_acceleration[1] = gravSensorVals[1] - gravity[1];
        linear_acceleration[2] = gravSensorVals[2] - gravity[2];

//        xValueRounded = Math.round(linear_acceleration[0] * 100.00) / 100.00;
//        yValueRounded = Math.round(linear_acceleration[1] * 100.00) / 100.00;
        zValueRounded = Math.round(linear_acceleration[2] * 100.00) / 100.00;

        return zValueRounded;
    }
}
