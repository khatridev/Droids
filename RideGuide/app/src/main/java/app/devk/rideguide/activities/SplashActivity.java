package app.devk.rideguide.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.devk.rideguide.R;

public class SplashActivity extends AppCompatActivity {

    private static int TIME=3000;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {




                Intent i = new Intent(SplashActivity.this, VehicleActivity.class);
                startActivity(i);


                finish();
            }
        }, TIME);
    }
}
