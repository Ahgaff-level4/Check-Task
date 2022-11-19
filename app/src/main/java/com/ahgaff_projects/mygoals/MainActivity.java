package com.ahgaff_projects.mygoals;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ahgaff_projects.mygoals.file.FileListFragment;
import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.folder.FolderListFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ExpandableListAdapter menuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();
        if (savedInstanceState == null) {//app first open
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, new FolderListFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_folders);
        }
    }

    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        expandableList = findViewById(R.id.navigationmenu);//
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        prepareListData();
        menuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);
        //setting list adapter
        expandableList.setAdapter(menuAdapter);
        expandableList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

//            int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
//            parent.setItemChecked(index, true);
            //id is folder index from db.getAllFolders()todo open the correspond folder fragment
            return false;
        });

        expandableList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if(id == 0) {//home fragment item
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, new HomeFragment())
                        .commit();
            }
//            else if(id == 2)todo open All Task fragment
        else return false;
            drawerLayout.closeDrawer(GravityCompat.START);
        return true;
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add(new ExpandedMenuModel("Home",R.drawable.home));
        listDataHeader.add(new ExpandedMenuModel("Folders",R.drawable.folder));//NOTE: changing the order require change ExpandableListAdapter.FoldersPos value!!!
        listDataHeader.add(new ExpandedMenuModel("All Tasks",R.drawable.file_description));
        // Adding child data
        List<String> headerFolders = new ArrayList<String>();
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, new FolderListFragment())
                    .commit();
        else//close the app (default behavior)
            super.onBackPressed();
    }

    //we don't need this function anymore because we now use expandableListView
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.nav_home)
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment_content_main, new HomeFragment())
//                    .commit();
//        else if (id == R.id.nav_folders)
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment_content_main, new FolderListFragment())
//                    .commit();
//        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
