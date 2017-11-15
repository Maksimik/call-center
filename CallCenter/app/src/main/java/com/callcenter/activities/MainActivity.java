package com.callcenter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.callcenter.R;
import com.callcenter.constants.Constants;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sPref;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPref = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
    }

    public void onSignOutClick(View view) {
        sPref.edit().remove(Constants.KEY_REGISTRATION).apply();
        sPref.edit().remove(Constants.KEY_AUTH_TOKEN).apply();
        sPref.edit().remove(Constants.KEY_CLIENT_ID).apply();
        sPref.edit().remove(Constants.KEY_LOGIN).apply();

        final Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

        finish();
    }

}