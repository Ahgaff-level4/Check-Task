package com.ahgaff_projects.mygoals.folder;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.R;

import java.util.ArrayList;

public class FolderListActivity extends AppCompatActivity {

    private RecyclerView foldersRecyclerView;
    private FolderRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FolderRecyclerViewAdapter(this);
        foldersRecyclerView = findViewById(R.id.folderListRecyclerView);
        foldersRecyclerView.setAdapter(adapter);
        foldersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create a garbage list for testing
        ArrayList<Folder> folders = new ArrayList<>();
        folders.add( new Folder("dksjfls"));
        folders.add( new Folder("cnkvx,v"));
        folders.add( new Folder("werrerw"));
//        adapter.setFolders(folders);

    }
}
