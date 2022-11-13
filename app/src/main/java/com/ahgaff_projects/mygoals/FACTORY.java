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
}
