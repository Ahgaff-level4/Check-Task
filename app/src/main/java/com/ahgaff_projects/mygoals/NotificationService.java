package com.ahgaff_projects.mygoals;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "onCreate Service called", Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(this, MainActivity.class);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel("12354","CheckTasksName",NotificationManager.IMPORTANCE_HIGH));

        //todo set text base on correspond file notification
        Notification noti = new Notification.Builder(this,"12354")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 12354, mainIntent,
                        PendingIntent.FLAG_IMMUTABLE))
                .setContentTitle("CHECK YOUR TASKS!")
                .setContentText("Your file x need you now")
//                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.icon)
                .setTicker("ticker message")
                .setWhen(System.currentTimeMillis())
                .build();
        notificationManager.notify(12354, noti);

    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind in NotificationService called", Toast.LENGTH_SHORT).show();
        return null;
    }
}