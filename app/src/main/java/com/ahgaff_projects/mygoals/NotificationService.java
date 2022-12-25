package com.ahgaff_projects.mygoals;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotificationService extends Service {


    /**
     * Returning START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        FACTORY.notifyNowIfToday(fileId,this);
//        Toast.makeText(this, "NotificationService", Toast.LENGTH_SHORT).show();
        ArrayList<File> files = new DB(this).getAllFiles();
        for(File f:files)
            FACTORY.notifyNowIfToday(f.getId(),this);

        FACTORY.resetAlarmManager(this);
        return START_STICKY;
    }



    /**
     * Simply return null, since our Service will not be communicating with
     * any other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

