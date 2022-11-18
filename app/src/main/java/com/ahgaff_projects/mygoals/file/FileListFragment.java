package com.ahgaff_projects.mygoals.file;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileListFragment extends Fragment {
    private FileRecyclerViewAdapter adapter;
    private DB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DB(getActivity());
        setUpRecyclerView();
        setUpFabButton();
    }

    private void setUpRecyclerView() {
        //the folderId that has this files list
        int folderId = requireArguments().getInt("folderId");
        adapter = new FileRecyclerViewAdapter(folderId,getActivity(),db);
        RecyclerView recyclerView = getView().findViewById(R.id.fileListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each file will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.add_file_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.dialog_add_edit_file, null);
            StartReminder.setUp(inflater, getActivity());
            RepeatEvery.setUp(inflater, getActivity());
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.fileNameEditText);//input from dialog
                String newFileName = input.getText().toString().trim();
                if (newFileName.equals(""))
                    FACTORY.showErrorDialog(getString(R.string.invalid_file_name), getActivity());
                else if (adapter.getFilesNames().contains(newFileName))
                    FACTORY.showErrorDialog(getString(R.string.invalid_file_name_exist), getActivity());
                else {
                    LocalDateTime startReminder = StartReminder.getChosen(inflater);
                    int repeatEvery = RepeatEvery.getChosen(inflater, getActivity());
                    if(!db.insertFile(adapter.folderId,newFileName,startReminder,repeatEvery))
                        FACTORY.showErrorDialog(R.string.error,getActivity());
                    adapter.updateFiles();
                }
            });
            dialog.show();
        });
    }




    //class made to divide the functions to its purpose
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
            if (startReminderTxt.contains("/"))
                return FACTORY.getDateFrom(startReminderTxt);
            else return null;
        }
    }

    //Functions for repeat notification every Never, day...
    public static class RepeatEvery {
        static void setUp(View dialogView, FragmentActivity context) {
            Spinner spinner = dialogView.findViewById(R.id.repeatEverySpinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0)
                        return;
                    TextView startReminder = dialogView.findViewById(R.id.startReminderContent);
                    if (startReminder.getText().toString().equals(context.getString(R.string.start_reminder_none))) {
                        String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                        startReminder.setText(nowDate);
                    }

                    String[] arr = context.getResources().getStringArray(R.array.repeat_every_options);
                    if (context.getString(R.string.custom).equals(arr[position])) {//todo when type num of days set it into spinner
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle(R.string.add_file_title);
                        dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
                        View inflater = context.getLayoutInflater().inflate(R.layout.dialog_add_edit_file_custom_repeat, null);
                        dialog.setView(inflater);
                        dialog.setPositiveButton(R.string.set, (_dialog, blah) -> {
                            EditText editText = inflater.findViewById(R.id.fileCustomDaysEditText);
                            try {
                                Log.d("MyTag", "Enter try block");

                                customDay = Integer.parseInt(editText.getText().toString());
                                Log.d("MyTag", "Set customDay");
//                                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context)
//                                Log.d("MyTag","created new arrayAdapter");
//                                CharSequence custom = context.getString(R.string.custom);
//                                Log.d("MyTag","created custom charSequence var");
//                                adapter.remove(custom);
//                                Log.d("MyTag","remove custom item from arr adapter");
//                                adapter.add(FACTORY.toEveryDay(customDay,context));
//                                adapter.add(context.getString(R.string.custom));
//                                Log.d("MyTag","added two items in adapter");
//                                spinner.setAdapter(adapter);
//                                Log.d("MyTag","set adapter in the spinner");
//                                spinner.setSelection(adapter.getCount()-2);
//                                Log.d("MyTag","set selection in the spinner");
                            } catch (Exception e) {
//                                Log.d("MyTag","Entered exception block");
                                FACTORY.showErrorDialog(context.getString(R.string.invalid_custom_days), context);
                            }
                        });
                        dialog.show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        private static int customDay = -1;

        static int getChosen(View dialogView, Context context) {
            Spinner spinner = dialogView.findViewById(R.id.repeatEverySpinner);

            switch (spinner.getSelectedItemPosition()) {
                /*
                 * position base on:
                 *   0- Never
                 *   1- Every Day
                 *   2- Every 2 Days
                 *   3- Every 3 Days
                 *   4- Every Week
                 *   5- Every 2 Weeks
                 *   6- Every Month
                 *   7- Custom
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
                case 7:
                    return customDay;
                default:
//                    Toast.makeText(context, "Repeat Every: Unexpected Chosen got=" + spinner.getSelectedItemPosition(), Toast.LENGTH_LONG).show();
                    return -1;
            }
        }
    }
}