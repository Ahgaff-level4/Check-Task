package com.ahgaff_projects.mygoals;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public final class FACTORY {
    public static final String TAG = "MyTag";
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    /**
     * when notification will be shown at the day. from 0 to 23 hours
     */
    public static final int hourOfDay = 6;//todo user can change when notification shown. in setting page

    /**
     * @param date expected format: yyyy/MM/dd
     * @return date object of LocalDateTime instance at specified date. If date is incorrect format
     */
    public static LocalDateTime getDateFrom(String date) {
        String[] s = date.split("/");
        if (s.length == 3)
            return LocalDateTime.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), hourOfDay, 0);
        return null;
    }

    public static void showErrorDialog(String message, Context context) {
        new AlertDialog.Builder(context)//show error dialog
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(R.drawable.warning)
                .show();
    }

    public static void showErrorDialog(@StringRes int stringRes, Context context) {
        showErrorDialog(context.getString(stringRes), context);
    }

    /**
     * @param days number of days. 0 is today, 1 tomorrow...
     * @return string formatted as "Every "+days+" Day/s"
     */
    public static String toEveryDay(int days, Context context) {
        switch (days) {
            case 0:
                return context.getString(R.string.today);
            case 1:
                return context.getString(R.string.tomorrow);
            case 2:
                return context.getString(R.string.after_tomorrow);
            case 3://empty case will fall to below case. So all empty cases will execute case 10 code.
            case 4:// 10 ايام
            case 5:// 11 يوم
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return context.getString(R.string.after) + " " + days + " " + context.getString(R.string.days);
            default:
                return context.getString(R.string.after) + " " + days + " " + context.getString(R.string.arabic_days);
        }
    }

    /**
     * @param message  message that will be shown to the user
     * @param context  context
     * @param listener the positive button click listener, negative button won't call the listener
     */
    public static void showAreYouSureDialog(String message, Context context, @StringRes int positiveText, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)//show error dialog
                .setTitle(R.string.are_you_sure)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(positiveText, listener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * <ul>
     * <li>
     * Open the main and only fragment panel.
     * </li><li>
     * Close the nav drawer.
     * </li><li>
     * </li>
     * </ul>
     */
    public static void openFragment(FragmentActivity context, Class<? extends androidx.fragment.app.Fragment> fragmentClass, @Nullable Bundle bundle) {
        context.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragmentClass, bundle, "Main Fragment")
                .commit();
        if (context instanceof MainActivity)
            if (((MainActivity) context).drawerLayout.isDrawerOpen(GravityCompat.START))
                ((MainActivity) context).drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static void openFragment(FragmentActivity context, Class<? extends androidx.fragment.app.Fragment> fragmentClass, String key, int value) {
        Bundle b = new Bundle();
        b.putInt(key, value);
        openFragment(context, fragmentClass, b);
    }

    /**
     * @return number of days the nearest reminder will be shown. Base on File.startReminder, File.repeatEvery. or -1 for never
     */
    public static int nearestReminder(Context context, int fileId) {
        File file = new DB(context).getFile(fileId);
        if (file.getStartReminder() == null)//no startReminder means no repeatEvery because if user choose only repeatEvery then automatically startReminder will be set at that day
            return -1;

        //get days different between now and startReminder (future)
        Duration dur = Duration.between(LocalDateTime.now().toLocalDate().atStartOfDay(), file.getStartReminder().toLocalDate().atStartOfDay());

        int days = (int) Math.round(((double) dur.toMinutes() / 60 / 24));

        if (days < 0) {
            //startReminder has pass and no repeatEvery!
            if (file.getRepeatEvery() <= 0)//repeatEvery is -1 if user chosen Never
                return -1;
            //app will reach here if startReminder is old date and repeatDays exist
            //startReminder is old date.
            //repeatDays is n days.
            //So, remain days = (now date - old date) % repeatDays
            days = days % file.getRepeatEvery();
            //days is negative
            if (days < 0)
                days += file.getRepeatEvery();
        }
        return days;
    }

    /**
     * reset alarm that run today and reschedule tomorrow... so on so forth; to notify all files that nearest reminder is the today.
     */
    public static void resetAlarmManager(Context context) {
        PendingIntent pendingService = PendingIntent.getBroadcast(context, 212488, new Intent(context, NotificationReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingService);
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + getRemainingMillisGoOff(), pendingService);
    }

    /**
     * calculate by the current hours and minutes. how much remain millis to go off at 12 O'Clock (Noon)
     *
     * @return millis seconds remaining to go off base on current time.
     */
    private static long getRemainingMillisGoOff() {
        int nowHours = LocalDateTime.now().getHour();//0 - 23 hours
        int nowMinutes = LocalDateTime.now().getMinute();
        int nowSeconds = LocalDateTime.now().getSecond();
        double nowMilli = ((((nowHours * 60) + nowMinutes) * 60) + nowSeconds) * 1000;
        //  0  => 12    y=(12 - x) if y<0: y+24
        //  1  => 11
        //  2  => 10
        //  8  => 4
        //  11 => 1
        //  12 => 0
        //  13 => 23
        //  14 => 22
        //  16 => 20
        //  18 => 18
        //  20 => 16
        //  22 => 14
        //  23 => 13
        double y = (12 * 60 * 60 * 1000) - nowMilli;//12 O'Clock is when it go off.


        if (y < 0)
            y = y + (24 * 60 * 60 * 1000);
        return (long) y;
    }

    /**
     * Note: if file.created is today then and it should notify today then this func won't notify and return false. dah..
     *
     * @param fileId
     * @return whether the file is notified by this call. true if notify called. false if file is not today
     */
    public static boolean notifyNowIfToday(int fileId, Context context) {
        File file = new DB(context).getFile(fileId);
        int nearest = nearestReminder(context, fileId);
        if (nearest != 0 || file.getCreated().getDayOfYear() == LocalDateTime.now().getDayOfYear())//created today
            return false;//no startReminder nor repeatEvery. Or nearest had passed. So, there won't be any notification for this file

        int uncheckedCount = FACTORY.getUncheckedTasksCount(context, file.getId());
        if (uncheckedCount == 0)
            return false;//if all tasks are checked then exist(no notification)
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("fileId", fileId);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel("Check Tasks", context.getString(R.string.notification_title), NotificationManager.IMPORTANCE_HIGH));
        Notification noti = new Notification.Builder(context, "12354")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, fileId, mainIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT))
                .setContentTitle(context.getString(R.string.check_your_tasks))
                .setContentText(file.getName() + " " + context.getString(R.string.has) + " " + uncheckedCount + " " + context.getString(R.string.unchecked_tasks))
                .setSmallIcon(R.drawable.task_alt)
                .setLargeIcon(Icon.createWithResource(context, R.drawable.ct))
                .build();
        notificationManager.notify(12354 + fileId, noti);
        return true;
    }

    public static int getUncheckedTasksCount(Context context, int fileId) {
        ArrayList<Task> tasks = new DB(context).getTasksOf(fileId);
        int unchecked = 0;
        for (Task t : tasks)
            if (!t.isChecked())
                unchecked++;
        return unchecked;
    }

    /**
     * it will iterate to all files and cancel current notifications and recreate it again.
     * NOTE: if there is a today notification it will be triggered and if it triggered and you call this it will trigger again!
     */
//    public static void updateAllReminders(Context context) {
//        ArrayList<File> files = new DB(context).getAllFiles();
//        for (File f : files)
//            createNotify(context, f.getId());
//
//    }
    public static int getTheme(SharedPreferences sharedPreferences) {
        String themeValue = sharedPreferences.getString("Theme", "Theme_MyGoals");
        switch (themeValue) {
            //teal is default theme
            case "teal":
                return R.style.Teal;
            case "yellow":
                return R.style.Yellow;
            case "indigo":
                return R.style.Indigo;
            case "brown":
                return R.style.Brown;
            case "red":
                return R.style.Red;
            case "purple":
                return R.style.Purple;


            default:
                return R.style.Teal;
        }
    }
}






