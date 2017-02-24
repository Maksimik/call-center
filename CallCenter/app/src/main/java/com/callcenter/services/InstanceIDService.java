package com.callcenter.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.i(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(final String token) {

    }
}
