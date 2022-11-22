package com.ahgaff_projects.mygoals.file;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.MainActivity;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.folder.FolderListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileListFragment extends Fragment implements MainActivity.MyOnBackPressed {
    private FileRecyclerViewAdapter adapter;
    private DB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list, container, false);
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
        adapter = new FileRecyclerViewAdapter(folderId, getActivity(), db);
        RecyclerView recyclerView = requireView().findViewById(R.id.fileListRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        //set up how each file will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = requireView().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
            dialog.setTitle(R.string.add_file_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.dialog_add_edit_file, null);
            StartReminder.setUp(inflater, getActivity(), null);
            RepeatEvery.setUp(inflater, getActivity(), null);
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
                    int repeatEvery = RepeatEvery.getChosen(inflater);
                    if (!db.insertFile(adapter.folderId, newFileName, startReminder, repeatEvery))
                        FACTORY.showErrorDialog(R.string.error, getActivity());
                    adapter.updateFiles();
                }
            });
            dialog.show();
        });
    }

    @Override
    public boolean onBackPressed() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main,new FolderListFragment())
                .commit();
        return true;
    }


    //class made to divide the functions to its purpose
    public static class StartReminder {
        public static void setUp(View dialogView, Context context, @Nullable String startedReminder) {
            RelativeLayout startReminder = dialogView.findViewById(R.id.startReminderLayout);
            TextView startReminderContent = dialogView.findViewById(R.id.startReminderContent);
            if (startedReminder != null)
                startReminderContent.setText(startedReminder);

            startReminder.setOnClickListener(v -> {
                LocalDateTime defaultSelectedDate = FACTORY.getDateFrom(startReminderContent.getText().toString());
                if (defaultSelectedDate == null)
                    defaultSelectedDate = LocalDateTime.now();

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, yearChosen, monthOfYear, dayOfMonth) -> {
                    String chosenDate = yearChosen + "/" + (monthOfYear + 1) + "/" + dayOfMonth;//this date format is used every where, be careful
                    startReminderContent.setText(chosenDate);
                }, defaultSelectedDate.getYear(), defaultSelectedDate.getMonthValue()-1, defaultSelectedDate.getDayOfMonth());

                datePickerDialog.show();
            });
        }

        @Nullable
        public static LocalDateTime getChosen(View dialogView) {
            String startReminderTxt = ((TextView) dialogView.findViewById(R.id.startReminderContent)).getText().toString();
            if (startReminderTxt.contains("/"))
                return FACTORY.getDateFrom(startReminderTxt);
            else return null;
        }
    }

    //Functions for repeat notification every Never, day...
    public static class RepeatEvery {
        public static void setUp(View dialogView, FragmentActivity context, @Nullable String repeatEveryStr) {
            Spinner spinner = dialogView.findViewById(R.id.repeatEverySpinner);
            if (repeatEveryStr != null)
                spinner.setSelection(getSpinnerPosition(repeatEveryStr));
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
                        dialog.setTitle(R.string.repeat_custom_dialog_title);
                        dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
                        View inflater = context.getLayoutInflater().inflate(R.layout.dialog_add_edit_file_custom_repeat, null);
                        dialog.setView(inflater);
                        dialog.setPositiveButton(R.string.set, (_dialog, blah) -> {
                            EditText editText = inflater.findViewById(R.id.fileCustomDaysEditText);
                            try {
                                Log.d("MyTag", "Enter try block");

                                customDay = Integer.parseInt(editText.getText().toString());
                                Log.d("MyTag", "Set customDay");
//todo set selected custom somehow                                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context)
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

        private static int getSpinnerPosition(String repeatEveryStr) {
            int repeatEvery = Integer.parseInt(repeatEveryStr);
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
            switch (repeatEvery) {
                case -1:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 7:
                    return 4;
                case 14:
                    return 5;
                case 30:
                    return 6;
                default:
                    return 7;//todo custom show the custom days
            }
        }

        public static int getChosen(View dialogView) {
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
                    return -1;
            }
        }
    }
}