package com.ahgaff_projects.mygoals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ahgaff_projects.mygoals.file.File;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            FACTORY.resetAlarmManager(context);
            return;
        }
        ArrayList<File> files = new DB(context).getAllFiles();
        for (File f : files)
            FACTORY.notifyNowIfToday(f.getId(), context);
        FACTORY.resetAlarmManager(context);
    }
}