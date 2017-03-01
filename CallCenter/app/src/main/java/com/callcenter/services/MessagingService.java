package com.callcenter.services;

import android.content.Intent;
import android.net.Uri;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        final String number = remoteMessage.getData().get("number");

        final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}