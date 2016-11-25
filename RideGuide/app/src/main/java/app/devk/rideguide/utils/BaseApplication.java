package app.devk.rideguide.utils;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.splunk.mint.Mint;

import app.devk.rideguide.R;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Mint.initAndStartSession(this, "94731616");
    }
}
