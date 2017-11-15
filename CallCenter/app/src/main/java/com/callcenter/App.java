package com.callcenter;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.callcenter.activities.MainActivity;
import com.callcenter.activities.SignInActivity;
import com.callcenter.constants.Constants;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final SharedPreferences sPref = getSharedPreferences(Constants.PREF, MODE_PRIVATE);

        final boolean registration = sPref.getBoolean(Constants.KEY_REGISTRATION, false);

        final Intent intent = new Intent(this, registration ? MainActivity.class : SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}