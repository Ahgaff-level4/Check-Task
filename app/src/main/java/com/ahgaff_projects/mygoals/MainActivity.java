package com.ahgaff_projects.mygoals;

import static com.ahgaff_projects.mygoals.FACTORY.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.ahgaff_projects.mygoals.task.TaskListFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FolderRecyclerViewAdapter.EventFoldersChanged {

    public DrawerLayout drawerLayout;
    private ExpandableListAdapter menuFoldersAdapter;
    private GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;

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
        FACTORY.updateAllReminders(this);//todo when phone boot call it again

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        updateUI(GoogleSignIn.getLastSignedInAccount(this));
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
        MenuItem login = menu.findItem(R.id.action_login);
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem importAction = menu.findItem(R.id.action_import);
        MenuItem sync = menu.findItem(R.id.action_sync);
        if (account == null) {
            login.setVisible(true);
            logout.setVisible(false);
            sync.setEnabled(false);
            importAction.setEnabled(false);
        } else {
            login.setVisible(false);
            logout.setVisible(true);
            sync.setEnabled(true);
            importAction.setEnabled(true);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
//                Intent intent = new Intent(this,SettingActivity.class);
//                this.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_login) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 12354);
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> Toast.makeText(MainActivity.this, "Logged out Successfully", Toast.LENGTH_LONG).show());
            updateUI(null);
            return true;
        } else//todo sync, import buttons
            return super.onOptionsItemSelected(item);
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


    /*+******************************** Firebase ***************************************/

    /**
     * update UI base on sign-in state if user arg is null. Then user is not signed-in. else singed-in
     *
     * @param account object that could be null
     */
    private void updateUI(@Nullable GoogleSignInAccount account) {
        this.account = account;
        invalidateOptionsMenu();//update actionbar menu items. (login, logout, sync, import)
        if (account != null) {
            // The account's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = account.getId();
            Log.d("MyTag", "ID:" + uid);
            Log.d("MyTag", "DisplayName:" + account.getDisplayName());
            Log.d("MyTag", "Email:" + account.getEmail());
            Log.d("MyTag", "GivenName:" + account.getGivenName());
            Log.d("MyTag", "FamilyName:" + account.getFamilyName());
            Log.d("MyTag", "photo:" + account.getPhotoUrl().toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 12354)//not the expected result
            return;

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Welcome " + account.getGivenName(), Toast.LENGTH_SHORT).show();
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "Failed to Sign-in", Toast.LENGTH_LONG).show();
            Log.w(TAG, "signInResult:failed.", e);
            updateUI(null);
        }
    }
}


