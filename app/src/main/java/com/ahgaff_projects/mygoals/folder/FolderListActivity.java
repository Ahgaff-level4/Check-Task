package com.ahgaff_projects.mygoals.folder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FolderListActivity extends AppCompatActivity {

    private FolderRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        setUpRecyclerView();
        setUpFabButton();

    }

    private void setUpRecyclerView() {
        //create garbage list for testing
        adapter = new FolderRecyclerViewAdapter(null, this);
        adapter.addFolder(new Folder("First Folder"));
        adapter.addFolder(new Folder("Second Category"));
        adapter.addFolder(new Folder("Third Group. Name it whatever you want"));
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
            Toast.makeText(this, "hello fab", Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.add_folder_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.add_folder_dialog, null);
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.folderNameEditText);//input from dialog
                String newFolderName = input.getText().toString().trim();
                if (newFolderName.equals(""))
                    new AlertDialog.Builder(this)//show error dialog
                            .setTitle(R.string.error)
                            .setMessage(R.string.invalid_folder_name)
                            .setPositiveButton(R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else if(existFolderName(newFolderName))
                    new AlertDialog.Builder(this)//show error dialog
                            .setTitle(R.string.error)
                            .setMessage(R.string.invalid_folder_name_exist)
                            .setPositiveButton(R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else
                    adapter.addFolder(new Folder(newFolderName));

            });
            dialog.show();
        });
    }

    private boolean existFolderName(String newName){
        for (Folder f: adapter.getCopyFolders())//foreach
            if(f.getName().equals(newName))
                return true;
        return false;
    }
}
