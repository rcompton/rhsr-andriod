package com.rcompton.rhsr;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        EditText editText = (EditText) findViewById(R.id.submitted_report);
        String submittedReport = editText.getText().toString();
        intent.putExtra(SUBMITTED_REPORT_MESSAGE, submittedReport);
        startActivity(intent);
    }

}
