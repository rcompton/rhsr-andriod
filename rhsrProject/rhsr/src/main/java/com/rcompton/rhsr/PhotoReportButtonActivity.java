package com.rcompton.rhsr;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class PhotoReportButtonActivity extends Activity {

    private final static String PBUTTON_LOG_TAG="rhsrPhotoButton";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        if(!connectionDetector.isConnectingToInternet()){
            TextView textView = new TextView(this);
            textView.setTextSize(40);
            textView.setText("bruddah I tink you need da internet");

            //setContentView(R.layout.photo_report_button_activity);
            setContentView(textView);
            return;
        }

        Intent intent = getIntent();
        String submittedReport = intent.getStringExtra(MainActivity.SUBMITTED_REPORT_MESSAGE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String CONSUMER_KEY = "DiJ0O1WJSwphr19vuA";
        String CONSUMER_SECRET = "cdOeuS8dcj8wZAhqI2lIG40RgbW7WA4s0idwZ4iYmS8";

        Twitter twitter = TwitterFactory.getSingleton();

        twitter.setOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);

        AccessToken accessToken = new AccessToken("13036062-0QfetCQZxpI5AhF2PXSeO6o7NGPnirRnIXe1xGTLc",
                "PRrgnD49ncyTStSP6VCL1nZkeq0fFQwlwkdWf0s");

        twitter.setOAuthAccessToken(accessToken);

        Status status = null;
        try{
            status = twitter.updateStatus(UUID.randomUUID().toString());
            Log.i(PBUTTON_LOG_TAG, status.getText());
        }catch(Exception e){
            Log.e(PBUTTON_LOG_TAG, e.toString());
        }

        if(status !=null){
            // Create the text view
            TextView textView = new TextView(this);
            textView.setTextSize(40);
            textView.setText(status.getText());

            //setContentView(R.layout.photo_report_button_activity);
            setContentView(textView);
        }

        // Show the Up button in the action bar.
       // setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_report_button, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
