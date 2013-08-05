package com.rcompton.rhsr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.UUID;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class PhotoReportButtonActivity extends Activity {

    private final static String PBUTTON_LOG_TAG="rhsrPhotoButton";
    private final static int CAMERA_REQ_CODE = 123456;

    String tweetText = "default text "+UUID.randomUUID().toString();

    String tweetLink = "twitter.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mainIntent = getIntent();
        tweetText = mainIntent.getStringExtra(MainActivity.SUBMITTED_REPORT_MESSAGE);

        //start camera and take photo
        capturePhoto();

        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText(tweetLink);
        setContentView(textView);

        setContentView(R.layout.photo_report_button_activity);
        //Show the Up button in the action bar.
        setupActionBar();
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

    //simple call to t4j, checks for internet connection and sends a tweet.
    protected boolean sendTweetToRyansAccount(String tweetText, Uri anImageUri){

        //assert we've got internet
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        if(!connectionDetector.isConnectingToInternet()){
            TextView textView = new TextView(this);
            textView.setTextSize(40);
            textView.setText("bruddah I tink you need da internet");

            //setContentView(R.layout.photo_report_button_activity);
            setContentView(textView);
            return false;
        }

        //lazy fix for network on main
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String CONSUMER_KEY = "DiJ0O1WJSwphr19vuA";
        String CONSUMER_SECRET = "cdOeuS8dcj8wZAhqI2lIG40RgbW7WA4s0idwZ4iYmS8";
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);
        AccessToken accessToken = new AccessToken("13036062-0QfetCQZxpI5AhF2PXSeO6o7NGPnirRnIXe1xGTLc",
                "PRrgnD49ncyTStSP6VCL1nZkeq0fFQwlwkdWf0s");
        twitter.setOAuthAccessToken(accessToken);

        StatusUpdate statusUpdate = null;
        try{
            statusUpdate = new StatusUpdate(tweetText);
            statusUpdate.setMedia(new File(anImageUri.getPath()));
            Status status = twitter.updateStatus(statusUpdate);
            Log.i(PBUTTON_LOG_TAG, "success! "+status.getText());
            tweetLink = "https://twitter.com/"+status.getId()+
                        "/status/"+ status.getId();
            return true;
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Log.e(PBUTTON_LOG_TAG, sw.toString());
        }
        return false;
    }

    //start camera
    Uri imageUri;
    public void capturePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photoFile = new File(Environment.getExternalStorageDirectory(),  "rhsrPhoto"+UUID.randomUUID().toString()+".png");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photoFile));
        imageUri = Uri.fromFile(photoFile);
        startActivityForResult(intent, CAMERA_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQ_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = imageUri;
                    boolean tweetSuccess = sendTweetToRyansAccount(tweetText,selectedImageUri);
                    if(tweetSuccess){
                        Log.i(PBUTTON_LOG_TAG, "tweet with photo success!!!!!!!");
                    }
                }else{
                    imageUri = Uri.parse("fuck.");
                }
        }
    }

}

