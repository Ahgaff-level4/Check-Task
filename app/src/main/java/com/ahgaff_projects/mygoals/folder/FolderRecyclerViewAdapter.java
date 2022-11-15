package com.ahgaff_projects.mygoals.folder;

import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.file.FileListActivity;

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Folder> folders;
    private final FolderListActivity context;
    private final DB db;

    public FolderRecyclerViewAdapter(FolderListActivity context, DB db) {
        this.context = context;
        this.folders = db.getAllFolders();
        this.db = db;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView folderParent;
        private TextView folderName;
        private View optionBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderParent = itemView.findViewById(R.id.folderParentCard);
            folderName = itemView.findViewById(R.id.folderName);
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
                .inflate(R.layout.list_item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//each item
        Folder f = folders.get(position);
        String name = f.getId() + "- " + f.getName();
        holder.folderName.setText(name);
        holder.folderParent.setOnClickListener(handleOnFolderClick(f));
        holder.optionBtn.setOnClickListener(handleOnOptionClick(f, holder.optionBtn));
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
            popup.inflate(R.menu.menu_crud_folder_file);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit_crud_folder_file_item:
                        //handle menu1 click
                        handleEditFolder(f);
                        return true;
                    case R.id.delete_crud_folder_file_item:
                        //handle menu2 click
                        FACTORY.showAreYouSureDialog(context.getString(R.string.folder) + " " + f.getName() + " " + context.getString(R.string.will_be_deleted) + "\n" + context.getString(R.string.it_has) + " " + f.getFilesCount() + " " + context.getString(R.string.files), context, (_dialog, which) -> {
                            if (!db.deleteFolder(f.getId()))
                                FACTORY.showErrorDialog(R.string.something_went_wrong, context);
                            else {
                                this.updateFolders();
                                Toast.makeText(context, context.getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    default:
                        return false;
                }
            });
            //displaying the popup
            popup.show();
        };
    }

    private void handleEditFolder(Folder f) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.edit_folder_title);
        dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
        View inflater = context.getLayoutInflater().inflate(R.layout.add_edit_delete_folder_dialog, null);
        dialog.setView(inflater);
        EditText input = inflater.findViewById(R.id.folderNameEditText);//input from dialog
        input.setText(f.getName());//set existing folder name to be changed by user

        dialog.setPositiveButton(R.string.edit, (_dialog, blah) -> {
            String newFolderName = input.getText().toString().trim();
            if (newFolderName.equals(""))
                FACTORY.showErrorDialog(R.string.invalid_folder_name, context);
            else if (getAllFolderNames().contains(newFolderName))
                FACTORY.showErrorDialog(R.string.invalid_folder_name_exist, context);
            else if(!db.updateFolder(f.getId(),newFolderName))
                    FACTORY.showErrorDialog(R.string.something_went_wrong,context);
                else{
                    this.updateFolders();
                }

        });
        dialog.show();

    }

    private View.OnClickListener handleOnFolderClick(Folder f) {
        return v -> {
            Intent i = new Intent(context, FileListActivity.class);
            i.putExtra("folderId", f.getId());
            context.startActivity(i);
        };
    }

    /**
     * assign adapter folders from database and notifyDataSetChanged()
     */
    public void updateFolders() {
        this.folders = db.getAllFolders();
        notifyDataSetChanged();//refresh the list
    }

    public ArrayList<String> getAllFolderNames() {
        ArrayList<String> arr = new ArrayList<>();
        for (Folder f : folders)
            arr.add(f.getName());
        return arr;
    }

}
