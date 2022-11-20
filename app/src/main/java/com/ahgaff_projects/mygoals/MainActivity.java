package com.ahgaff_projects.mygoals;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FolderRecyclerViewAdapter.EventFoldersChanged {

    private DrawerLayout drawerLayout;
    private ExpandableListAdapter menuFoldersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();
        if (savedInstanceState == null) //app first open
            openFragment(HomeFragment.class, null);

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
            openFragment(FileListFragment.class, "folderId", (int) folderId);
            return false;
        });

        expandableList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (id == 0) //home fragment item
                openFragment(HomeFragment.class, null);
            else if (id == 2)
                openFragment(FileListFragment.class, "folderId", -1);//-1 means files of all folders(all files)
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
        else if (!(fragment instanceof MyOnBackPressed) || !((MyOnBackPressed) fragment).onBackPressed()) //if user inside a tasks than backPress will be handle in TaskListFragment
            super.onBackPressed();

    }

    /**
     * <ul>
     * <li>
     * Open a the main and only fragment panel.
     * </li><li>
     * Close the nav drawer.
     * </li><li>
     * todo Select (highlight) the drawer item that opened.
     * </li>
     * </ul>
     */
    public void openFragment(Class<? extends androidx.fragment.app.Fragment> fragmentClass, @Nullable Bundle bundle) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragmentClass, bundle)
                .commit();
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openFragment(Class<? extends androidx.fragment.app.Fragment> fragmentClass, String key, int value) {
        Bundle b = new Bundle();
        b.putInt(key, value);
        openFragment(fragmentClass, b);
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


