package com.ahgaff_projects.mygoals.task;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskListFragment extends AppCompatActivity {
    private TaskRecyclerViewAdapter adapter;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        db = new DB(this);
        setUpFabButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        int fileId = getIntent().getIntExtra("fileId",-1);
        if(fileId==-1)
            Toast.makeText(this,"ERROR: fileId==-1",Toast.LENGTH_LONG).show();
        adapter = new TaskRecyclerViewAdapter(fileId,this,db);
        RecyclerView recyclerView = findViewById(R.id.taskListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each task will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.add_task_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.dialog_add_edit_task, null);
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.taskTextEditText);//input from dialog
                String newTaskName = input.getText().toString().trim();

                    if(!db.insertTask(adapter.fileId,newTaskName,false))
                        FACTORY.showErrorDialog(R.string.something_went_wrong,this);
                    adapter.updateTasks();

            });
            dialog.show();
        });
    }



}