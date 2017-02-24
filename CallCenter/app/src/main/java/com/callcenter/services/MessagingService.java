package com.callcenter.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.callcenter.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        sendNotification(remoteMessage.getData().get("number"));
    }

    private void sendNotification(final String number) {

        final Context context = getApplicationContext();

        final Intent notificationIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        final Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Call")
                .setContentText(number)
                .setTicker("Call!").setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }
}