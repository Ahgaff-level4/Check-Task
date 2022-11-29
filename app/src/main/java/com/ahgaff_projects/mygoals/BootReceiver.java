package com.ahgaff_projects.mygoals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        FACTORY.updateAllReminders(context);
    }
}