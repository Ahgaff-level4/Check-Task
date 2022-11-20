package com.ahgaff_projects.mygoals;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ahgaff_projects.mygoals.folder.FolderListFragment;

import java.util.Objects;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity()//todo Now home fragment will direct to FolderList fragment todo make home page
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, new FolderListFragment())
                .commit();
    }

}
