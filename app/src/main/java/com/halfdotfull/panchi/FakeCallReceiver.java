package com.halfdotfull.panchi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.halfdotfull.panchi.Activity.FakeRingingActivity;

/**
 * Created by 15103068 on 28-03-2017.
 */

public class FakeCallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String getFakeName = intent.getStringExtra("FAKENAME");
        String getFakePhoneNumber = intent.getStringExtra("FAKENUMBER");

        Intent intentObject = new Intent(context.getApplicationContext(), FakeRingingActivity.class);
        intentObject.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentObject.putExtra("myfakename", getFakeName);
        intentObject.putExtra("myfakenumber", getFakePhoneNumber);
        context.startActivity(intentObject);
    }
}
