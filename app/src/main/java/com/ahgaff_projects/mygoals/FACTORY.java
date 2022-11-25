package com.ahgaff_projects.mygoals;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;

import com.ahgaff_projects.mygoals.file.File;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class FACTORY {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * @param date expected format: yyyy/MM/dd
     * @return date object of LocalDateTime instance at specified date
     */
    public static LocalDateTime getDateFrom(String date) {
        String[] s = date.split("/");
        if (s.length == 3)
            return LocalDateTime.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), 12, 0);
        throw new IllegalArgumentException("Expected: yyyy/MM/dd format. Got="+date);
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
     * @param days number of days
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

    public static void showAreYouSureDialog(String message, Context context, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)//show error dialog
                .setTitle(R.string.are_you_sure)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, listener)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    /**
     * <ul>
     * <li>
     * Open a the main and only fragment panel.
     * </li><li>
     * Close the nav drawer.
     * </li><li>
     * todo Select (highlight) the drawer item that opened.
     * </li>
     * </ul>
     */
    public static void openFragment(FragmentActivity context, Class<? extends androidx.fragment.app.Fragment> fragmentClass, @Nullable Bundle bundle) {
        context.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragmentClass, bundle)
                .commit();
        if (context instanceof MainActivity)
            if (((MainActivity) context).drawerLayout.isDrawerOpen(GravityCompat.START))
                ((MainActivity) context).drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static void openFragment(FragmentActivity context,Class<? extends androidx.fragment.app.Fragment> fragmentClass, String key, int value) {
        Bundle b = new Bundle();
        b.putInt(key, value);
        openFragment(context, fragmentClass, b);
    }
    public static void setNotify(FragmentActivity context, int fileId) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("fileId",fileId);
        PendingIntent contentIntent = PendingIntent.getService(context, 12354+fileId, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT );

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(contentIntent);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 1000*60 * 2, contentIntent);
    }
}
