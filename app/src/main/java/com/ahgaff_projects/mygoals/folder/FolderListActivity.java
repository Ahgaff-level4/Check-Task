package com.ahgaff_projects.mygoals.folder;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FolderListActivity extends AppCompatActivity {

    private FolderRecyclerViewAdapter adapter;
    private final DB db = new DB(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        setUpFabButton();
    }
    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();//in onStart() because back button won't call OnCreate but we need to refresh data. example: user add files in fileListActivity we should have that data.
    }


    private void setUpRecyclerView() {
        //create garbage list for testing
        adapter = new FolderRecyclerViewAdapter( this,db);
        RecyclerView recyclerView = findViewById(R.id.folderListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each folder will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.add_folder_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.add_edit_folder_dialog, null);
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.folderNameEditText);//input from dialog
                String newFolderName = input.getText().toString().trim();
                if (newFolderName.equals(""))
                    FACTORY.showErrorDialog(R.string.invalid_folder_name,this);
                else if(adapter.getAllFolderNames().contains(newFolderName))
                    FACTORY.showErrorDialog(R.string.invalid_folder_name_exist,this);
                else {
                    if(!db.insertFolder(newFolderName))
                        FACTORY.showErrorDialog(R.string.error,this);
                    adapter.updateFolders();
                }

            });
            dialog.show();
        });
    }




}
