package com.callcenter.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;

import com.callcenter.constants.Constants;
import com.callcenter.logs.Logging;
import com.onesignal.OSNotificationReceivedResult;

public class NotificationService extends com.onesignal.NotificationExtenderService {

    private BroadcastReceiver receiver;

    @Override
    protected boolean onNotificationProcessing(final OSNotificationReceivedResult receivedResult) {
        final SharedPreferences sPref = getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);

        final boolean registration = sPref.getBoolean(Constants.KEY_REGISTRATION, false);

        if (registration) {

            Logging.logInFile("PUSH NOTIFICATION : " + receivedResult.toString());

            final String phone = receivedResult.payload.body;

            final PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            boolean isScreenOn = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isScreenOn = powerManager.isInteractive();
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
                isScreenOn = powerManager.isScreenOn();
            }

            if (isScreenOn) {
                dialTheNumber(phone);

            } else {
                receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(final Context context, final Intent intent) {

                        dialTheNumber(phone);

                        getApplicationContext().unregisterReceiver(receiver);
                    }
                };

                final IntentFilter screenStateFilter = new IntentFilter();

                screenStateFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
                screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
                getApplicationContext().registerReceiver(receiver, screenStateFilter);
            }

            final long[] pattern = {100, 400, 200, 400};
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, -1);
        }
        return true;
    }

    public void dialTheNumber(final String phone) {
        if (phone != null) {

            final Intent newIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+" + phone));

            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(newIntent);
        }
    }
}