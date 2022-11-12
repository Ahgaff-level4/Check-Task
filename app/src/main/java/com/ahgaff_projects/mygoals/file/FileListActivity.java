package com.ahgaff_projects.mygoals.file;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.method.DateKeyListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahgaff_projects.mygoals.DATA;
import com.ahgaff_projects.mygoals.MainActivity;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.folder.FolderRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FileListActivity extends AppCompatActivity {
    private FileRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        setUpRecyclerView();
        setUpFabButton();
    }

    private void setUpRecyclerView() {
        //the folder that has this files list
        Folder folder = (Folder) getIntent().getSerializableExtra("folderObj");
        adapter = new FileRecyclerViewAdapter(folder, this);
        RecyclerView recyclerView = findViewById(R.id.fileListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each file will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.add_file_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.add_edit_delete_file_dialog, null);
            StartReminder.setUp(inflater, this);

            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.fileNameEditText);//input from dialog
                String newFileName = input.getText().toString().trim();
                if (newFileName.equals(""))
                    new AlertDialog.Builder(this)//show error dialog
                            .setTitle(R.string.error)
                            .setMessage(R.string.invalid_file_name)
                            .setPositiveButton(R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else if (existFileName(newFileName))
                    new AlertDialog.Builder(this)//show error dialog
                            .setTitle(R.string.error)
                            .setMessage(R.string.invalid_file_name_exist)
                            .setPositiveButton(R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else {
                    LocalDateTime startReminder = StartReminder.getChosen(inflater);
                    int repeatEvery = RepeatEvery.getChosen(inflater, this);
                    Toast.makeText(this, "repeat every:" + repeatEvery, Toast.LENGTH_LONG).show();
                    adapter.addFile(new File(DATA.generateId(adapter.getCopyFolder()), newFileName, startReminder, repeatEvery));
                }
            });
            dialog.show();
        });
    }


    private boolean existFileName(String newName) {
        for (File f : adapter.getCopyFolder().getFiles())//foreach
            if (f.getName().equals(newName))
                return true;
        return false;
    }

    //class made to divide the factions to its purpose
    private static class StartReminder {
        private static void setUp(View dialogView, Context context) {
            RelativeLayout startReminder = dialogView.findViewById(R.id.startReminderLayout);
            TextView startReminderContent = dialogView.findViewById(R.id.startReminderContent);
            startReminder.setOnClickListener(v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context);
                datePickerDialog.setOnDateSetListener((view, year, monthOfYear, dayOfMonth) -> {
                    String chosenDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                    startReminderContent.setText(chosenDate);
                });
                datePickerDialog.show();
            });
        }

        @Nullable
        private static LocalDateTime getChosen(View dialogView) {
            String startReminderTxt = ((TextView) dialogView.findViewById(R.id.startReminderContent)).getText().toString();
            String[] arr = startReminderTxt.split("/");
            if (arr.length > 1)
                return LocalDateTime.of(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), 6, 0);
            else return null;
        }
    }

    //Functions for repeat notification every Never, day...
    public static class RepeatEvery {
        static int getChosen(View dialogView, Context context) {
            Spinner spinner = dialogView.findViewById(R.id.repeatEverySpinner);
            switch (spinner.getSelectedItemPosition()) {
                /**
                 * position base on:
                 *   0- Never
                 *   1- Every Day
                 *   2- Every 2 Days
                 *   3- Every 3 Days
                 *   4- Every Week
                 *   5- Every 2 Weeks
                 *   6- Every Month
                 */
                case 0:
                    return -1;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 7;//week has 7 days
                case 5:
                    return 14;
                case 6:
                    return 30;
                default:
                    Toast.makeText(context, "Repeat Every: Unexpected Chosen got=" + spinner.getSelectedItemPosition(), Toast.LENGTH_LONG).show();
                    return -1;
            }
        }
    }
}