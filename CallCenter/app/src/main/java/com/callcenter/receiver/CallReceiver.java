package com.callcenter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.callcenter.constants.Constants;
import com.callcenter.services.CallInterceptionService;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {

            final String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                final String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                final Intent newIntent = new Intent(context, CallInterceptionService.class);

                newIntent.putExtra(Constants.KEY_PHONE_NUMBER, phoneNumber);

                context.startService(newIntent);

            }
        }
    }
}