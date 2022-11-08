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
    private final ArrayList<Folder> folders = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        setUpRecyclerView();
        setUpFabButton();
//        adapter.setFolders(folders);

//        foldersRecyclerView.


    }

    private void setUpRecyclerView() {
        //create garbage list for testing
        folders.add(new Folder("First Folder"));
        folders.add(new Folder("Second Category"));
        folders.add(new Folder("Third Group. Name it whatever you want"));
        adapter = new FolderRecyclerViewAdapter(folders, this);
        RecyclerView recyclerView = findViewById(R.id.folderListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each folder will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @SuppressLint("NotifyDataSetChanged")
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
                EditText input = inflater.findViewById(R.id.folderNameEditText);
                String newFolderName = input.getText().toString().trim();
                if (newFolderName.equals(""))
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.invalid_folder_name)
                            .setPositiveButton(R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else {
                    folders.add(new Folder(input.getText().toString()));
                    adapter.notifyDataSetChanged();
                }
            });
            dialog.show();
        });
    }
}
