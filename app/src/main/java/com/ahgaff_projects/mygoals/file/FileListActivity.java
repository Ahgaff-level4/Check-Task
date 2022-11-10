package com.ahgaff_projects.mygoals.file;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ahgaff_projects.mygoals.DATA;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.folder.FolderRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        Folder folder = (Folder)getIntent().getSerializableExtra("folderObj");
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
            Toast.makeText(this, "hello fab", Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.add_file_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.add_edit_delete_file_dialog, null);
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
                else if(existFileName(newFileName))
                    new AlertDialog.Builder(this)//show error dialog
                            .setTitle(R.string.error)
                            .setMessage(R.string.invalid_file_name_exist)
                            .setPositiveButton(R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                else
                    adapter.addFile(new File(DATA.generateId(adapter.getCopyFolder()),newFileName,null,null));//TODO

            });
            dialog.show();
        });
    }

    private boolean existFileName(String newName){
        for (File f: adapter.getCopyFolder().getFiles())//foreach
            if(f.getName().equals(newName))
                return true;
        return false;
    }
}