package com.ahgaff_projects.mygoals.folder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.databinding.FragmentFolderListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FolderListFragment extends Fragment {

    private FolderRecyclerViewAdapter adapter;
    private DB db;
    private FragmentFolderListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFolderListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = new DB(getActivity());
        Toast.makeText(getActivity(), "folder createView", Toast.LENGTH_SHORT).show();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
//        setUpFabButton();
    }


    private void setUpRecyclerView() {
        //create garbage list for testing
        adapter = new FolderRecyclerViewAdapter(getActivity(), db);
        RecyclerView recyclerView = getView().findViewById(R.id.folderListRecyclerView);
        recyclerView.setAdapter(adapter);

        //set up how each folder will be arrange
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle(R.string.add_folder_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = getLayoutInflater().inflate(R.layout.dialog_add_edit_folder, null);
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.add, (_dialog, blah) -> {
                EditText input = inflater.findViewById(R.id.folderNameEditText);//input from dialog
                String newFolderName = input.getText().toString().trim();
                if (newFolderName.equals(""))
                    FACTORY.showErrorDialog(R.string.invalid_folder_name, getContext());
                else if (adapter.getAllFolderNames().contains(newFolderName))
                    FACTORY.showErrorDialog(R.string.invalid_folder_name_exist, getContext());
                else {
                    if (!db.insertFolder(newFolderName))
                        FACTORY.showErrorDialog(R.string.error, getContext());
                    adapter.updateFolders();
                }

            });
            dialog.show();
        });
    }

}
