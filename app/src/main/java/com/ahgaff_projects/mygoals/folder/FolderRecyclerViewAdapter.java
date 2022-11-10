package com.ahgaff_projects.mygoals.folder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DATA;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.file.FileListActivity;

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Folder> folders;
    private final Context context;

    public FolderRecyclerViewAdapter(ArrayList<Folder> folders, Context context) {
        this.context = context;
        if (folders == null)
            this.folders = new ArrayList<>();
        else
            this.folders = folders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView folderParent;
        private TextView folderName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderParent = itemView.findViewById(R.id.folderParentCard);
            folderName = itemView.findViewById(R.id.folderName);
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
        String name = f.getId()+"- "+f.getName();
        holder.folderName.setText(name);
        holder.folderParent.setOnClickListener(v -> {
            Intent i = new Intent(context, FileListActivity.class);
            i.putExtra("folderObj",f);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public ArrayList<Folder> getCopyFolders(){//return clone because folders should not be changed outside adapter
        ArrayList<Folder> clone = new ArrayList<>();
        for(Folder f : this.folders)
            clone.add(f.clone());
        return clone;
    }


    public void addFolder(Folder folder) {
        this.folders.add(folder);
        notifyDataSetChanged();//refresh the list
        DATA.save(folders,context);
    }

    public void deleteFolder(Folder folder){
        this.folders.remove(folder);
        notifyDataSetChanged();
        DATA.save(folders,context);
    }

}
