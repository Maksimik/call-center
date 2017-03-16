package com.callcenter.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.callcenter.constants.Constants;
import com.callcenter.logs.Logging;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CallInterceptionService extends IntentService {

    public CallInterceptionService() {
        super("");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final SharedPreferences sPref = getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);


        final String authToken = sPref.getString(Constants.KEY_AUTH_TOKEN, "");
        final String dispatcherLogin = sPref.getString(Constants.KEY_LOGIN, "");
        final String clientId = sPref.getString(Constants.KEY_CLIENT_ID, "");
        if (dispatcherLogin != "") {
            if (authToken != "") {
                new Thread() {

                    @Override
                    public void run() {
                        try {
                            final String phone = intent.getStringExtra(Constants.KEY_PHONE_NUMBER);

                            Logging.logInFile("INCOMING CALL: " + phone);
                            Logging.logInFile("REGISTRATION INCOMING CALL: [START]");

                            final OkHttpClient client = new OkHttpClient();

                            final Request request = new Request.Builder()
                                    .addHeader("Accept", "application/json")
                                    .url(Constants.URL_PHONE_NUMBER + clientId + "/" + dispatcherLogin + "/incoming/" + phone.substring(1))
                                    .build();

                            Logging.logInFile("REQUEST FROM SERVER (post method), URL: " + Constants.URL_USER + ", RESPONSE: " + client.newCall(request).execute().toString());
                            Logging.logInFile("REGISTRATION INCOMING CALL: [END]");

                        } catch (final Exception e) {
                            Logging.logInFile("REGISTRATION INCOMING CALL: [END WITH ERROR], error; " + e.toString());
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

}