package com.ahgaff_projects.mygoals;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.ahgaff_projects.mygoals.file.FileListFragment;
import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.folder.FolderListFragment;
import com.ahgaff_projects.mygoals.folder.FolderRecyclerViewAdapter;
import com.ahgaff_projects.mygoals.task.TaskListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FolderRecyclerViewAdapter.EventFoldersChanged {

    public DrawerLayout drawerLayout;
    private ExpandableListAdapter menuFoldersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();

        int fileId = getIntent().getIntExtra("fileId", -1);
        if (fileId != -1) {
            Toast.makeText(this, "fileId= " + fileId, Toast.LENGTH_SHORT).show();
            FACTORY.openFragment(this, TaskListFragment.class, "fileId", fileId);
            getIntent().putExtra("fileId", -1);//if user exit the app don't reopen this fragment
        } else if (savedInstanceState == null)  //app first open
            FACTORY.openFragment(this, FolderListFragment.class, null);//Home Page
        FACTORY.updateAllReminders(this);
    }


    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ExpandableListView expandableList = findViewById(R.id.navigationmenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        menuFoldersAdapter = new ExpandableListAdapter(this, expandableList, this);
        //setting list adapter
        expandableList.setAdapter(menuFoldersAdapter);
        expandableList.setOnChildClickListener((parent, v, groupPosition, childPosition, folderPos) -> {
            int folderId = new DB(this).getAllFolders().get((int) folderPos).getId();
            FACTORY.openFragment(this, FileListFragment.class, "folderId", folderId);
            return false;
        });
        expandableList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (id == 0) //home fragment item
                FACTORY.openFragment(this, FolderListFragment.class, null);
            else if (id == 2)
                FACTORY.openFragment(this, FileListFragment.class, "folderId", -1);//-1 means files of all folders(all files)
            else return false;
            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().getFragments().get(0);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))//if drawer navigation is open then just close it
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (!(fragment instanceof MyOnBackPressed) || !((MyOnBackPressed) fragment).onBackPressed()) //if user inside a tasks than backPress will be handle in TaskListFragment. So, this if condition will call the handler and result false
            super.onBackPressed();
    }


    @Override
    public void onFoldersChanged(ArrayList<Folder> folders) {
        menuFoldersAdapter.update();
    }

    public interface MyOnBackPressed {
        /**
         * @return true if handled. false to make MainActivity handle it.
         */
        boolean onBackPressed();
    }

}


