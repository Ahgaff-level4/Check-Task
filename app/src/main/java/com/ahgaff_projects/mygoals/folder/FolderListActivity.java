package com.ahgaff_projects.mygoals.folder;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FolderListActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FolderRecyclerViewAdapter adapter;
    private final DB db = new DB(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        setUpNavigationDrawer();
        setUpFabButton();
//        getActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void setUpNavigationDrawer() {
        setSupportActionBar(findViewById(R.id.toolbar));
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void handleNavigateButton(MenuItem item){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}
