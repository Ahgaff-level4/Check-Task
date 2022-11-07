package com.ahgaff_projects.mygoals.folder;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.R;

import java.util.ArrayList;

public class FolderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FolderRecyclerViewAdapter adapter;
    private ArrayList<Folder> folders = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        setUpRecyclerView();
//        adapter.setFolders(folders);

//        foldersRecyclerView.




    }
    private void setUpRecyclerView() {
        //create garbage list for testing
        folders.add( new Folder("First Folder"));
        folders.add( new Folder("Second Category"));
        folders.add( new Folder("Third Group. Name it whatever you want"));
        adapter = new FolderRecyclerViewAdapter(folders,this);
        recyclerView = findViewById(R.id.folderListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each folder will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}
