package com.developer.arsltech.covid_19tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReveiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent){
       String message = intent.getStringExtra("toastMessage");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }
}
