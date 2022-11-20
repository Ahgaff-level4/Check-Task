package com.ahgaff_projects.mygoals.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.MainActivity;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.file.FileListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class TaskListFragment extends Fragment implements MainActivity.MyOnBackPressed {
    private TaskRecyclerViewAdapter adapter;
    private DB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DB(getActivity());
        setUpFabButton();
        setUpRecyclerView();
    }

    /**
     * whether this fragment opens from AllTasksFragment or from a folder; used to determine behavior of back button
     */
    private boolean isFromAllFiles;

    private void setUpRecyclerView() {
        int fileId = requireArguments().getInt("fileId");
        isFromAllFiles = requireArguments().getBoolean("isFromAllTasks");
        adapter = new TaskRecyclerViewAdapter(fileId, requireActivity(), db);
        RecyclerView recyclerView = requireView().findViewById(R.id.taskListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each task will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = requireView().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
            dialog.setTitle(R.string.add_task_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.dialog_add_edit_task, null);
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.taskTextEditText);//input from dialog
                String newTaskName = input.getText().toString().trim();
                if (!db.insertTask(adapter.fileId, newTaskName, false))
                    FACTORY.showErrorDialog(R.string.something_went_wrong, getActivity());
                adapter.updateTasks();

            });
            dialog.show();
        });
    }


    @Override
    public boolean onBackPressed() {
        //todo when open taskList from allFiles fragment then pass bundle.put("isFromAllFiles",true)
        // then here if isFromAllFiles == true open AllFilesFragment aka folderId=-1 when open FileListFragment

        Bundle bundle = new Bundle();
        int folderId;
        if (isFromAllFiles)
            folderId = -1;//to show AllFiles
        else
            folderId = new DB(getActivity()).getFile(adapter.fileId).getFolderId();
        bundle.putInt("folderId", folderId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, FileListFragment.class, bundle)
                .commit();
        return true;
    }
}