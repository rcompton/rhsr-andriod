package com.rcompton.rhsr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rcompton.rhsr.backend.RHSRQuery;
import com.rcompton.rhsr.location.LocationHelper;

import java.util.Calendar;

public class MainActivity extends Activity {

    String RHSR_MAIN_LOG_TAG = "rhsrMain";
    private RHSRBackendHandler rhsr;
    private String tweetDefault = "default report";
    private String closestSpots;

    private double usersLat;
    private double usersLng;

    private LocationControl locationControlTask ;
    private boolean hasLocation = false;
    private LocationHelper locHelper;

    private boolean gotLocalSurfInfo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(RHSR_MAIN_LOG_TAG, "Oncreate lat " + usersLat);
        Log.i(RHSR_MAIN_LOG_TAG,"Oncreate lng "+usersLng);
        setContentView(R.layout.activity_main);

        locHelper = new LocationHelper();
        locHelper.getLocation(this.getApplicationContext(), locationResult);
        locationControlTask = new LocationControl();
        locationControlTask.execute(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(RHSR_MAIN_LOG_TAG,"Onstart lat "+usersLat);
        Log.i(RHSR_MAIN_LOG_TAG,"Onstart lng "+usersLng);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(RHSR_MAIN_LOG_TAG, "onResume");

        //ok... got gps and downloaded everything...
        if(gotLocalSurfInfo){
            EditText editText = (EditText)findViewById(R.id.editText0);
            editText.setText(tweetDefault, TextView.BufferType.EDITABLE);

            TextView foundSpot = (TextView) findViewById(R.id.foundSpot);
            foundSpot.setText(closestSpots);

        }else{
            EditText editText = (EditText)findViewById(R.id.editText0);
            editText.setText("no data yet", TextView.BufferType.EDITABLE);
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(RHSR_MAIN_LOG_TAG, "on pause");
        locHelper.stopLocationUpdates();
        locationControlTask.cancel(true);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(RHSR_MAIN_LOG_TAG, "on stop");
        locHelper.stopLocationUpdates();
        locationControlTask.cancel(true);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(RHSR_MAIN_LOG_TAG, "on destroy");
        locHelper.stopLocationUpdates();
        locationControlTask.cancel(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** Called when the user clicks the Photo Report button */
    public final static String SUBMITTED_REPORT_MESSAGE = "com.rcompton.rhsr.SUBMITTED_REPORT_MESSAGE";
    private final static int TAKE_PHOTO_CODE = 123456;
    public void photoReportButton(View view) {
        Intent intent = new Intent(this, PhotoReportButtonActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText0);
        String submittedReport = editText.getText().toString();
        intent.putExtra(SUBMITTED_REPORT_MESSAGE, submittedReport);
        startActivity(intent);
    }




//    /**
//     * gets the user's location information
//     */
//    private void getUsersLocation() {
//        Log.i(RHSR_MAIN_LOG_TAG,"getting location...");
//
//        // Get the location manager
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_HIGH);
//
//        List<String> allProviders = locationManager.getAllProviders();
//        Log.i(RHSR_MAIN_LOG_TAG, "all location Provider "+allProviders.toString());
//        String bestProvider = locationManager.getBestProvider(criteria, false);
//        Log.i(RHSR_MAIN_LOG_TAG,"bestProvider "+ bestProvider);
//
//        // Location location = locationManager.getLastKnownLocation(bestProvider);
//        LocationListener loc_listener = new LocationListener() {
//
//            public void onLocationChanged(Location l) {}
//
//            public void onProviderEnabled(String p) {}
//
//            public void onProviderDisabled(String p) {}
//
//            public void onStatusChanged(String p, int status, Bundle extras) {}
//        };
//
//        locationManager.requestLocationUpdates(bestProvider, 0, 0, loc_listener);
//
//        Location newLoc = null;
//
//        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if(!gpsEnabled){
//            Log.i(RHSR_MAIN_LOG_TAG,"no GPS!");
//        }else{
//            newLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            gpsEnabled = true;
//        }
//        Log.i(RHSR_MAIN_LOG_TAG, "new loc: "+newLoc.toString());
//
//        try {
//            usersLat = newLoc.getLatitude();
//            usersLng = newLoc.getLongitude();
//        } catch (NullPointerException e) {
//            usersLat = -1.0;
//            usersLng = -1.0;
//        }
//    }


    /**
     * connect to my ap
     */
    private boolean herokuConnect(){
        Log.i(RHSR_MAIN_LOG_TAG,"gpt a gps");
        /**Get the data from rhsr backend on an async task**/
        String responseStr = "no data";
        try{
            responseStr  = new PolarThicketCaller().execute(usersLat,usersLng).get();
            rhsr = new RHSRBackendHandler(responseStr);
            Toast.makeText(getApplicationContext(),rhsr.getClosestSpotsAndDistances().get(0)+"km", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.i(RHSR_MAIN_LOG_TAG, "failed to download heroku app response");
        }

        /** parse the heroku response **/
        try{
            tweetDefault = rhsr.getClosestSpotsAndDistances().get(0).split("\t")[0] + "\n"+
                    "swell: "+rhsr.getSwellInfo() + "\n\n" +
                    "wind: "+rhsr.getWindInfo() + "\n\n" +
                    "tide: "+rhsr.getTideInfo() + "\n\n" +
                    "temp: "+rhsr.getClosestBuoyTemp();
            closestSpots = "closest known spots: \n" + rhsr.getClosestSpotsAndDescriptions().get(0) + "\n" +
                    rhsr.getClosestSpotsAndDescriptions().get(1) + "\n" +
                    rhsr.getClosestSpotsAndDescriptions().get(2);
            return true;
        }catch (Exception e){
            Log.i(RHSR_MAIN_LOG_TAG, "failed to parse heroku app response");
        }
        return false;

    }

    /**
     * Background task to handle connection to heroku
     */
    private class PolarThicketCaller extends AsyncTask<Double, Integer, String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tDownloading local surf data...");
            pdLoading.show();
        }

        @Override
        protected String doInBackground(Double... latlng) {
            try {
                return RHSRQuery.apiRequest(latlng[0], latlng[1]);
            } catch (Exception e) {
                Log.i(RHSR_MAIN_LOG_TAG, "failed background heroku download");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pdLoading.setMessage("\tGot local surf info");
            pdLoading.dismiss();
        }
    }


    /**
     * Wait GPS fix
     */
    private class LocationControl extends AsyncTask<Context, Void, Void>{
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            this.dialog.setMessage("waiting for location...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Context... params){
            Long t = Calendar.getInstance().getTimeInMillis();
            while (!hasLocation && Calendar.getInstance().getTimeInMillis() - t < 15000) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            return null;
        }

        @Override
        protected void onPostExecute(Void unused){
            super.onPostExecute(unused);
            this.dialog.dismiss();

            Log.i(RHSR_MAIN_LOG_TAG, "heroku connect with "+usersLat + " "+ usersLng);
            gotLocalSurfInfo = herokuConnect(); //does the stuff that requires current location
            Log.i(RHSR_MAIN_LOG_TAG, "done with gps connect, "+tweetDefault);

            //back to UI...
            onResume();
        }

    }

    public LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult()
    {
        @Override
        public void gotLocation(final Location location)
        {
            usersLat = location.getLatitude();
            usersLng = location.getLongitude();

        }
    };

}
