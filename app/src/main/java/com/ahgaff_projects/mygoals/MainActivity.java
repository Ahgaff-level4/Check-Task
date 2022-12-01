package com.ahgaff_projects.mygoals;

import static com.ahgaff_projects.mygoals.FACTORY.TAG;

import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FolderRecyclerViewAdapter.EventFoldersChanged {

    public DrawerLayout drawerLayout;
    private ExpandableListAdapter menuFoldersAdapter;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;


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

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        updateUI(firebaseAuth.getCurrentUser());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
//                Intent intent = new Intent(this,SettingActivity.class);
//                this.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_login) {
            signInHandle();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            return true;
        } else
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
     * @param user object that could be null
     */
    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            Toast.makeText(this, "user NOT null. see log", Toast.LENGTH_LONG).show();
            Log.d("MyTag", "ID:" + uid);
            Log.d("MyTag", "DisplayName:" + user.getDisplayName());
            Log.d("MyTag", "Email:" + user.getEmail());
            Log.d("MyTag", "GivenName:" + user.getPhotoUrl().toString());
//            Log.d("MyTag", "Password:"+user.getPassword());
            Log.d("MyTag", "PhoneNumber:" + user.getPhoneNumber());

        } else {
            Toast.makeText(this, "updateUi user is null :(", Toast.LENGTH_SHORT).show();
        }
    }

    public void signInHandle() {
//        signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .build();
//        oneTapClient = Identity.getSignInClient(this);
//
//        signInRequest = BeginSignInRequest.builder()
//                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(true)
//                .build();


//        oneTapClient.beginSignIn(signInRequest)
//                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
//                    @Override
//                    public void onSuccess(BeginSignInResult result) {
//                        try {
//                            startIntentSenderForResult(
//                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
//                                    null, 0, 0, 0);
//                        } catch (IntentSender.SendIntentException e) {
//                            Toast.makeText(MainActivity.this, "Couldn't start One Tap UI. see log", Toast.LENGTH_SHORT).show();
//                            Log.e("MyTag", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
//                        }
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // No saved credentials found. Launch the One Tap sign-up flow, or
//                        // do nothing and continue presenting the signed-out UI.
//                        Toast.makeText(MainActivity.this, "BeginSignIn onFailure. see log", Toast.LENGTH_SHORT).show();
//                        Log.w("MyTag","BeginSignIn onFailure. see log", e);
//                    }
//                });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("438431947620-ecpi41uk3dhhf4mv8g8q993k3vs49ltm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this
                , googleSignInOptions);
        // Initialize sign in intent
        Intent intent = googleSignInClient.getSignInIntent();
        // Start activity for result
        startActivityForResult(intent, 12354);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 12354)//not our result then get out
            return;
        // Initialize task
        Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                .getSignedInAccountFromIntent(data);

        // check condition
        if (signInAccountTask.isSuccessful()) {
            // When google sign in successful
            Toast.makeText(this, "Google sign in successful", Toast.LENGTH_SHORT).show();
            try {
                // Initialize sign in account
                GoogleSignInAccount googleSignInAccount = signInAccountTask
                        .getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    // When sign in account is not equal to null
                    // Initialize auth credential
                    AuthCredential authCredential = GoogleAuthProvider
                            .getCredential(googleSignInAccount.getIdToken()
                                    , null);
                    // Check credential
                    firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this, task -> {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful
                                    Toast.makeText(MainActivity.this, "Firebase authentication successful", Toast.LENGTH_LONG).show();
                                    updateUI(firebaseAuth.getCurrentUser());
                                } else {
                                    // When task is unsuccessful
                                    // Display Toast
                                    Toast.makeText(MainActivity.this, "Authentication Failed. see log", Toast.LENGTH_SHORT).show();
                                    Log.w("MyTag", "Authentication Failed", task.getException());
                                }
                            });

                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(MainActivity.this, "Google Sign in failed", Toast.LENGTH_LONG).show();
            Log.w(TAG,"Goodle Sign in failed",signInAccountTask.getException());
        }

//        if(requestCode ==12354)
//
//    {
//        try {
//            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
//            String idToken = credential.getGoogleIdToken();
//            if (idToken != null) {
//                // Got an ID token from Google. Use it to authenticate
//                // with Firebase.
//                Toast.makeText(this, "Got ID token. see log", Toast.LENGTH_LONG).show();
//                Log.d("MyTag", "Got ID token:" + idToken);
//                Log.d("MyTag", "ID:" + credential.getId());
//                Log.d("MyTag", "DisplayName:" + credential.getDisplayName());
//                Log.d("MyTag", "FamilyName:" + credential.getFamilyName());
//                Log.d("MyTag", "GivenName:" + credential.getGivenName());
//                Log.d("MyTag", "Password:" + credential.getPassword());
//                Log.d("MyTag", "PhoneNumber:" + credential.getPhoneNumber());
//            }
//        } catch (ApiException e) {
//            switch (e.getStatusCode()) {
//                case CommonStatusCodes.CANCELED:
//                    Toast.makeText(this, "One-tap dialog was closed", Toast.LENGTH_SHORT).show();
//                    break;
//                case CommonStatusCodes.NETWORK_ERROR:
//                    Toast.makeText(this, "One-tp encountered a network error", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    Toast.makeText(this, "Couldn't get credential from result. see log", Toast.LENGTH_SHORT).show();
//                    Log.w("MyTag", e.getLocalizedMessage(), e);
//                    break;
//            }
//
//        }
//    }

    }


}


