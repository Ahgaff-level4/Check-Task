package com.ahgaff_projects.mygoals.folder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.ahgaff_projects.mygoals.DATA;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.file.FileListActivity;

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Folder> folders;
    private final FolderListActivity context;

    public FolderRecyclerViewAdapter(ArrayList<Folder> folders, FolderListActivity context) {
        this.context = context;
        if (folders == null)
            this.folders = new ArrayList<>();
        else
            this.folders = folders;
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
                        FACTORY.showAreYouSureDialog(context.getString(R.string.folder) + " " + f.getName() + " " + context.getString(R.string.will_be_deleted) + "\n" + context.getString(R.string.it_has) + " " + f.getFiles().size() + " " + context.getString(R.string.files), context, (_dialog, which) -> {
                            deleteFolder(f);
                            Toast.makeText(context, context.getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show();
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
            else if (existFolderName(newFolderName))
                FACTORY.showErrorDialog(R.string.invalid_folder_name_exist, context);
            else {
                editFolder(new Folder(f.getId(),newFolderName));
            }
        });
        dialog.show();

    }

    private View.OnClickListener handleOnFolderClick(Folder f) {
        return v -> {
            Intent i = new Intent(context, FileListActivity.class);
            i.putExtra("folderObj", f);
            context.startActivity(i);
        };
    }

    public ArrayList<Folder> getCopyFolders() {//return clone because folders should not be changed outside adapter
        ArrayList<Folder> clone = new ArrayList<>();
        for (Folder f : this.folders)
            clone.add(f.clone());
        return clone;
    }


    public void addFolder(Folder folder) {
        this.folders.add(folder);
        DATA.save(folders, context);
        notifyDataSetChanged();//refresh the list
    }

    public void deleteFolder(Folder folder) {
        this.folders.remove(folder);
        DATA.save(folders, context);
        notifyDataSetChanged();
    }

    /**
     * @param folder update folder name in folders that has same @param folder's id
     */
    public void editFolder(Folder folder) {
        for (int i = 0; i < folders.size(); i++)
            if (folders.get(i).getId() == folder.getId()) {
                folders.get(i).setName(folder.getName());
                DATA.save(folders, context);
                notifyDataSetChanged();
                return;
            }
        Toast.makeText(context,"You are asshole! providing folder with not exist id folder!",Toast.LENGTH_LONG).show();
    }

    private boolean existFolderName(String newName) {
        for (Folder f : folders)
            if (f.getName().equals(newName))
                return true;
        return false;
    }

}
