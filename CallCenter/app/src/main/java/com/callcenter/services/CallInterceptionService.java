package com.callcenter.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.callcenter.HttpClient;
import com.callcenter.constants.Constants;

import java.util.HashMap;
import java.util.Map;

public class CallInterceptionService extends IntentService {

    public CallInterceptionService() {
        super("");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final HttpClient httpClient = new HttpClient();

        final SharedPreferences sPref = getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);

        final String authToken = sPref.getString(Constants.KEY_AUTH_TOKEN, "");
        final String dispatcherLogin = sPref.getString(Constants.KEY_LOGIN, "");
        final String clientId = sPref.getString(Constants.KEY_CLIENT_ID, "");

        if (dispatcherLogin != "") {
            if (authToken != "") {
                try {
                    final Map<String, String> header = new HashMap<>();

                    header.put("Accept", "application/json");

                    final String phone = intent.getStringExtra(Constants.KEY_PHONE_NUMBER);

                    httpClient.post(Constants.URL_PHONE_NUMBER + clientId + "/" + dispatcherLogin + "/incoming/" + phone.substring(1), null, null);

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

}