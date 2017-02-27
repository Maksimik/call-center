package com.callcenter.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.callcenter.constants.Constants;

public class CallInterceptionService extends IntentService {

    public CallInterceptionService() {
        super("");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        final NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final int notifyID = 1;

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Incoming call")
                .setContentText(intent.getStringExtra(Constants.KEY_PHONE_NUMBER))
                .setSmallIcon(android.R.drawable.stat_notify_chat);

        mNotificationManager.notify(notifyID, mBuilder.build());

        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

    }

    public void onDestroy() {
        super.onDestroy();
    }

}