package com.ahgaff_projects.mygoals.folder;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FolderListFragment extends Fragment {

    private FolderRecyclerViewAdapter adapter;
    private DB db;

    public FolderListFragment(){
        super(R.layout.fragment_folder_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DB(getActivity());
        setUpRecyclerView();
        setUpFabButton();
    }


    private void setUpRecyclerView() {
        adapter = new FolderRecyclerViewAdapter(getActivity(), db);
        RecyclerView recyclerView = requireView().findViewById(R.id.folderListRecyclerView);
        recyclerView.setAdapter(adapter);
//        if(adapter.getItemCount()<=0)
//            getView().findViewById(R.id.emptyListTextview).setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setUpFabButton() {
        FloatingActionButton fab = requireView().findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
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
