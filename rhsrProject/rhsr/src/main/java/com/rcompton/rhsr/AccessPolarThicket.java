package com.rcompton.rhsr;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

class AccessPolarThicket extends AsyncTask<String, Void, String> {

    protected String doInBackground(String[] urlIn) {
        String jsonOut = "json";
        try {
            URL url= new URL(urlIn[0]);
            // get URL content
            //url = new URL("http://polar-thicket-8603.herokuapp.com/services/geocoder/malibu");
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                jsonOut = inputLine;
            }
            br.close();
            return jsonOut;
        } catch (Exception e) {
            return e.toString();
        }
    }


}