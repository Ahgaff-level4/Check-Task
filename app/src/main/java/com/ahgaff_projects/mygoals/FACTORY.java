package com.ahgaff_projects.mygoals;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public final class FACTORY {
    /**
     *
     * @param title if null will show error title
     * @param message
     * @param context
     */
    public static void showErrorDialog(@Nullable String title, String message, Context context){
        if(title == null)
            title = context.getString(R.string.error);
        new AlertDialog.Builder(context)//show error dialog
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * in short it will return "Every "+days+" Day/s";
     * @param days
     * @param context
     * @return
     */
    public static String toEveryDay(int days,Context context) {
        switch (days){
            case 0: return context.getString(R.string.today);
            case 1: return context.getString(R.string.tomorrow);
            case 2: return context.getString(R.string.after_tomorrow);
            case 3://empty case will fall to below case. So all empty cases will execute case 10 code.
            case 4:// 10 ايام
            case 5:// 11 يوم
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:return context.getString(R.string.after)+" "+days+" "+context.getString(R.string.days);
            default:return context.getString(R.string.after)+" "+days+" "+context.getString(R.string.arabic_days);
        }
    }
}
