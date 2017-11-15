package com.callcenter.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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
        final String lineNember = sPref.getString(Constants.KEY_LINE_NUMBER, "");
        final String clientId = sPref.getString(Constants.KEY_CLIENT_ID, "");

        if (lineNember != "" && authToken != "") {
            new Thread() {

                @Override
                public void run() {
                    try {
                        final String phone = intent.getStringExtra(Constants.KEY_PHONE_NUMBER);
                        final String url = Constants.URL_PHONE_NUMBER + clientId + "/" + lineNember + "/incoming/" + phone.substring(1);
                        Logging.logInFile("INCOMING CALL: " + phone);
                        Logging.logInFile("REGISTRATION INCOMING CALL: [START]");

                        final OkHttpClient client = new OkHttpClient();

                        final Request request = new Request.Builder()
                                .addHeader("Accept", "application/json")
                                .url(url)
                                .build();
                        Logging.logInFile("REQUEST FROM SERVER (post method), URL: " + url + ", RESPONSE: " + client.newCall(request).execute().toString());
                        Logging.logInFile("REGISTRATION INCOMING CALL: [END]");

                    } catch (final Exception e) {
                        Logging.logInFile("REGISTRATION INCOMING CALL: [END WITH ERROR], error; " + e.toString());
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

}