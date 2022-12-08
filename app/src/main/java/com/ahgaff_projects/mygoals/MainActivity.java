package com.ahgaff_projects.mygoals;

import static com.ahgaff_projects.mygoals.FACTORY.TAG;

import android.content.DialogInterface;
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

import com.ahgaff_projects.mygoals.file.File;
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
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
//        FACTORY.updateAllReminders(this);

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
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
            logout.setEnabled(!syncInProgress);
            sync.setEnabled(!syncInProgress);
            if (syncInProgress) sync.setTitle(getString(R.string.sync) + "...");
            else sync.setTitle(R.string.sync);
            importAction.setEnabled(!syncInProgress);
        }
        return true;
    }

    boolean syncInProgress = false;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Main Fragment");
        if (drawerLayout.isDrawerOpen(GravityCompat.START))//if drawer navigation is open then just close it
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (fragment instanceof MyOnBackPressed) { //if user inside a tasks than backPress will be handle in TaskListFragment. So, this if condition will call the handler and result false
            ((MyOnBackPressed) fragment).onBackPressed();
        } else super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
//                Intent intent = new Intent(this,SettingActivity.class);
//                this.startActivity(intent);
        } else if (item.getItemId() == R.id.action_login) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 12354);
        } else if (item.getItemId() == R.id.action_logout) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> Toast.makeText(MainActivity.this, "Logged out Successfully", Toast.LENGTH_SHORT).show());
            updateUI(null);
        } else if (item.getItemId() == R.id.action_sync) {
            sync();
        } else if (item.getItemId() == R.id.action_import) {
            importData();
        } else return super.onOptionsItemSelected(item);
        return true;
    }


    @Override
    public void onFoldersChanged() {
        menuFoldersAdapter.update();
    }

    public interface MyOnBackPressed {
        /**
         * make other fragments handle the backPressed event instead of MainActivity
         */
        void onBackPressed();
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
            Log.d("MyTag", "photo:" + account.getPhotoUrl());
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
            Toast.makeText(this, getString(R.string.welcome)+" " + account.getGivenName(), Toast.LENGTH_SHORT).show();
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, getString(R.string.login_failed)+"\n" + e, Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }


    private void sync() {
        if (account == null || account.getId() == null) {
            Toast.makeText(this, getString(R.string.error_try_relogin), Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Create a new user with a first, middle, and last name
        DB db = new DB(this);
        Map<String, Object> data = new HashMap<>();
        data.put("Display Name", account.getDisplayName());
        data.put("Given Name", account.getGivenName());
        data.put("Family Name", account.getFamilyName());
        data.put("Email", account.getEmail());
        data.put("Profile Image", account.getPhotoUrl());
        data.put("AccountId", account.getId());
        data.put(DB.FOLDER_TABLE_NAME, db.firebaseFolders());
        data.put(DB.FILE_TABLE_NAME, db.firebaseFiles());
        data.put(DB.TASK_TABLE_NAME, db.firebaseTasks());
        syncInProgress = true;
        invalidateOptionsMenu();//update actionbar menu items. (login, logout, sync, import)
//        Toast.makeText(this, "accountId: "+account.getId(), Toast.LENGTH_SHORT).show();
// Add a new document with a generated ID
        firestore.collection("/users/")
                .document(account.getId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "success with object(don't ask me what's this) o=" + o);
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.sync_finish_successfully), Toast.LENGTH_LONG).show();
                        syncInProgress = false;
                        invalidateOptionsMenu();
                    }
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.sync_failed)+"\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    syncInProgress = false;
                    invalidateOptionsMenu();
                });
    }

    private void importData() {
        if (account == null || account.getId() == null) {
            Toast.makeText(this, getString(R.string.error_try_relogin), Toast.LENGTH_LONG).show();
            return;
        }
        syncInProgress = true;
        invalidateOptionsMenu();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .document(account.getId()).get()
                .addOnSuccessListener(doc -> {
                    Toast.makeText(MainActivity.this, getString(R.string.import_success), Toast.LENGTH_LONG).show();
                    syncInProgress = false;
                    invalidateOptionsMenu();
                    if (doc.exists()) {
                        Log.d(TAG, "data= " + doc.getData());
                        if (doc.getData() != null) {
                            DB db = new DB(this);
                            FirebaseData data = doc.toObject(FirebaseData.class);
                            if (data != null) {
                            Log.d(TAG, "firebaseData= " + data);
                            FACTORY.showAreYouSureDialog(getString(R.string.founded)+":\n"
                                    +data.folders.size()+" "+getString(R.string.folders_title)+"\n"
                                    +data.file.size()+" "+getString(R.string.files_title)+"\n"
                                    +data.task.size()+" "+getString(R.string.tasks_title)+"\n"
                                    +getString(R.string.are_you_sure_reset_all_data), this,R.string.reset, (dialog, which) -> {
                                db.firebaseFiles(data.file);
                                db.firebaseFolders(data.folders);
                                db.firebaseTasks(data.task);
                                startActivity(new Intent(this,MainActivity.class));
                                finish();
                            });

                            }else Log.d(TAG,"data is null");
                        }
                    } else
                        Log.d(TAG, "doc doesn't exists!");
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting document", e);
                    Toast.makeText(MainActivity.this, getString(R.string.import_failed)+"\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    syncInProgress = false;
                    invalidateOptionsMenu();
                });
    }
}


