package com.ahgaff_projects.mygoals;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.ahgaff_projects.mygoals.file.FileListFragment;
import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.folder.FolderListFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();
        if (savedInstanceState == null) {//app first open
            openFragment(new FolderListFragment());
        }
    }

    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        expandableList = findViewById(R.id.navigationmenu);//
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        prepareListData();
        ExpandableListAdapter menuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);
        //setting list adapter
        expandableList.setAdapter(menuAdapter);
        expandableList.setOnChildClickListener((parent, v, groupPosition, childPosition, folderPos) -> {
            int folderId = new DB(this).getAllFolders().get((int) folderPos).getId();
            openFragment(FileListFragment.class,"folderId", (int) folderId);
            return false;
        });

        expandableList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (id == 0) //home fragment item
                openFragment(new HomeFragment());

            else if(id == 2)
                openFragment(FileListFragment.class,"folderId",-1);//-1 means files of all folders(all files)
            else return false;
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add(new ExpandedMenuModel(getString(R.string.home), R.drawable.home));
        listDataHeader.add(new ExpandedMenuModel(getString(R.string.folders_title), R.drawable.folder));//NOTE: changing the order require change ExpandableListAdapter.FoldersPos value!!!
        listDataHeader.add(new ExpandedMenuModel(getString(R.string.all_files), R.drawable.file_description));
        // Adding child data
        List<String> headerFolders = new ArrayList<>();
        List<Folder> folders = new DB(this).getAllFolders();
        for (Folder f : folders)
            headerFolders.add(f.getName());

        listDataChild.put(listDataHeader.get(ExpandableListAdapter.FoldersPos), headerFolders);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))//if drawer navigation is open then just close it
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (getSupportFragmentManager().getFragments().get(0) instanceof FileListFragment)//if user inside a folder then get back to folders list
            openFragment(new FolderListFragment());
        else//close the app (default behavior)
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
    public void openFragment(Class<? extends androidx.fragment.app.Fragment> fragmentClass, Bundle bundle) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragmentClass, bundle)
                .commit();
        _openCommonStuff();
    }
    private void _openCommonStuff(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main,fragment)
                .commit();
        _openCommonStuff();
    }

    public void openFragment(Class<? extends androidx.fragment.app.Fragment> fragmentClass, String key,int value){
        Bundle b = new Bundle();
        b.putInt(key,value);
        openFragment(fragmentClass,b);
    }
}
