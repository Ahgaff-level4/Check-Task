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
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        // do the actual work, in a separate thread
        Toast.makeText(this, "onCreate Service called", Toast.LENGTH_SHORT).show();

        int fileId = intent.getIntExtra("fileId", -1);
        if (fileId < 0) {
            Toast.makeText(this, "Invalid fileId! expected positive integer got=" + fileId, Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Invalid fileId! expected positive integer got=" + fileId);
        }
        File file = new DB(this).getFile(fileId);
        if (getUncheckedTasksCount(file) == 0)
            return START_NOT_STICKY;//if all tasks are checked then exist(no notification)
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("fileId", fileId);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel("Check Tasks", getString(R.string.notification_title), NotificationManager.IMPORTANCE_HIGH));
        Notification noti = new Notification.Builder(this, "12354")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, fileId, mainIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT))
                .setContentTitle(getString(R.string.check_your_tasks))
                .setContentText(getString(R.string.file_title) + " " + file.getName() + " " + getString(R.string.has) + " " + getUncheckedTasksCount(file) + " " + getString(R.string.unchecked_tasks))
                .setSmallIcon(R.drawable.task_alt)
                .setLargeIcon(Icon.createWithResource(this, R.drawable.icon))
                .build();
        notificationManager.notify(12354, noti);
        return START_NOT_STICKY;
    }

    private int getUncheckedTasksCount(File file) {
        ArrayList<Task> tasks = new DB(this).getTasksOf(file.getId());
        int unchecked = 0;
        for (Task t : tasks)
            if (!t.isChecked())
                unchecked++;
        return unchecked;
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

