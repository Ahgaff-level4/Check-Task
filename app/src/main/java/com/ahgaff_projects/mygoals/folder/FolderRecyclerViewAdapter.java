package com.ahgaff_projects.mygoals.folder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.file.FileListFragment;

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Folder> folders;
    private final FragmentActivity context;
    private final DB db;

    public FolderRecyclerViewAdapter(FragmentActivity context, DB db) {
        this.context = context;
        this.folders = db.getAllFolders();
        this.db = db;
        context.setTitle(R.string.folders_title);
        if (this.folders.size() <= 0)
            context.findViewById(R.id.emptyList).setVisibility(View.VISIBLE);
        else context.findViewById(R.id.emptyList).setVisibility(View.INVISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View folderParent;
        private final TextView folderName;
        private final TextView filesCount;
        private final View optionBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderParent = itemView.findViewById(R.id.folderItemParent);
            folderName = itemView.findViewById(R.id.folderName);
            filesCount = itemView.findViewById(R.id.folderFilesCount);
            optionBtn = itemView.findViewById(R.id.folderItemOptionMenuBtn);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//each item
        Folder f = folders.get(position);
        holder.folderName.setText(f.getName());
        holder.filesCount.setText(numberOfFilesToString(f.getFilesCount()));
        holder.folderParent.setOnClickListener((v) -> FACTORY.openFragment(context, FileListFragment.class, "folderId", f.getId()));
        holder.optionBtn.setOnClickListener(handleOnOptionClick(f, holder.optionBtn));
    }

    private String numberOfFilesToString(int n) {
        switch (n) {
            case 1:
                return n + " " + context.getString(R.string.file);
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                return n + " " + context.getString(R.string.files);
            default:
                return n + " " + context.getString(R.string.arabic_files);
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    private View.OnClickListener handleOnOptionClick(Folder f, View optionBtn) {
        return view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, optionBtn);
            //inflating menu from xml resource
            popup.inflate(R.menu.edit_delete_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_item) {
                    //handle menu1 click
                    handleEditFolder(f);
                    return true;
                }
                if (item.getItemId() == R.id.delete_item) {
                    //handle menu2 click
                    FACTORY.showAreYouSureDialog(context.getString(R.string.folder_title) + " " + f.getName() + " " + context.getString(R.string.will_be_deleted) + "\n" + context.getString(R.string.it_has) + " " + f.getFilesCount() + " " + context.getString(R.string.files), context,R.string.delete, (_dialog, which) -> {
                        if (!db.deleteFolder(f.getId()))
                            FACTORY.showErrorDialog(R.string.something_went_wrong, context);
                        else {
                            this.updateFolders();
                            Toast.makeText(context, context.getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                } else
                    return false;

            });
            //displaying the popup
            popup.show();
        };
    }

    private void handleEditFolder(Folder f) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.edit_folder_title);
        dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
        View inflater = context.getLayoutInflater().inflate(R.layout.dialog_add_edit_folder, null);
        dialog.setView(inflater);
        EditText input = inflater.findViewById(R.id.folderNameEditText);//input from dialog
        input.setText(f.getName());//set existing folder name to be changed by user

        dialog.setPositiveButton(R.string.edit, (_dialog, blah) -> {
            String newFolderName = input.getText().toString().trim();
            if (newFolderName.equals(""))
                FACTORY.showErrorDialog(R.string.invalid_folder_name, context);
            else if (!db.updateFolder(f.getId(), newFolderName))
                FACTORY.showErrorDialog(R.string.something_went_wrong, context);
            else {
                this.updateFolders();
            }

        });
        dialog.show();

    }


    /**
     * assign adapter folders from database and notifyDataSetChanged()
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateFolders() {
        this.folders = db.getAllFolders();
        notifyDataSetChanged();//refresh the list
        foldersChangedCallback.onFoldersChanged();
        if (this.folders.size() <= 0)
            context.findViewById(R.id.emptyList).setVisibility(View.VISIBLE);
        else context.findViewById(R.id.emptyList).setVisibility(View.INVISIBLE);
    }

    public static EventFoldersChanged foldersChangedCallback;

    /**
     * we made this event to pass updated folders from FolderRecyclerViewAdapter to MainActivity
     */
    public interface EventFoldersChanged {
        /**
         * called when updateFolders() in FolderRecyclerViewAdapter called
         */
        void onFoldersChanged();
    }

    public ArrayList<String> getAllFolderNames() {
        ArrayList<String> arr = new ArrayList<>();
        for (Folder f : folders)
            arr.add(f.getName());
        return arr;
    }

}
