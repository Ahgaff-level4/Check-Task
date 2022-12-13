package com.ahgaff_projects.mygoals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "Check Task: All reminders reset after booting", Toast.LENGTH_SHORT).show();
            FACTORY.updateAllReminders(context);
        }
    }
}