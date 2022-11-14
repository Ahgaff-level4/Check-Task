package com.ahgaff_projects.mygoals;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public final class FACTORY {
    /**
     *
     * @param message
     * @param context
     */
    public static void showErrorDialog( String message, Context context){
        new AlertDialog.Builder(context)//show error dialog
                .setTitle( R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public static void showErrorDialog(@StringRes int stringRes,Context context){
        showErrorDialog(context.getString(stringRes),context);
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

    public static void showAreYouSureDialog(String message, Context context,  DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)//show error dialog
                .setTitle(R.string.are_you_sure)
                .setMessage(message)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.delete, listener)
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }
}
