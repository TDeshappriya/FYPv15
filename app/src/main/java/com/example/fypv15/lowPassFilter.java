package com.example.fypv15;

public class lowPassFilter extends MapsActivity {

    static final float ALPHA = 0.05f;

    protected static float[] lowPass(float[] input, float[] output) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
