package com.ahgaff_projects.mygoals.folder;

import android.content.Context;
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

    public ArrayList<Folder> getCopyFolders(){//return clone because folders should not be changed outside adapter
        ArrayList<Folder> clone = new ArrayList<>();
        for(Folder f : this.folders)
            clone.add(f.clone());
        return clone;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void addFolder(Folder folder) {
        this.folders.add(folder);
        notifyDataSetChanged();//refresh the list
    }

    public void setFolders(ArrayList<Folder> folders){
        this.folders = folders;
        notifyDataSetChanged();//refresh the list
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_folder,parent,false);
//        return new ViewHolder(view);
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Log.d("FolderRecyclerView","onBindViewHolder called");
        holder.folderName.setText(folders.get(position).getName());
        holder.folderParent.setOnClickListener(v -> Toast.makeText(context, folders.get(holder.getAbsoluteAdapterPosition()).getName() + " Selected", Toast.LENGTH_LONG).show());
//        ViewHolder viewHolder = (ViewHolder) holder;
//here you can set your own conditions based on your arraylist using position parameter
//        viewHolder. .itemNameTextView.setText(locationsArrayList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


}
