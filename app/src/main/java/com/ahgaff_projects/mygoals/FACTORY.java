package com.ahgaff_projects.mygoals;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FACTORY {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * @param date expected: yyyy/MM/dd
     * @return date or null if there is format error
     */
    public static LocalDateTime getDateFrom(String date) {
        String[] s = date.split("/");
        if (s.length == 3)
            return LocalDateTime.of(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2]),6,0);
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
}
