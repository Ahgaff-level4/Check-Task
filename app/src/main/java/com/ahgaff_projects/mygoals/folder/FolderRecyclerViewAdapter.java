package com.ahgaff_projects.mygoals.folder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.R;

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Folder> folders;
    private Context mContext;

    public FolderRecyclerViewAdapter(ArrayList<Folder> folders,Context mContext) {
        this.mContext = mContext;
        this.folders = folders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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

    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();//refresh the list
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_folder,parent,false);
//        return new ViewHolder(view);
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_folder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Log.d("FolderRecyclerView","onBindViewHolder called");
        holder.folderName.setText(folders.get(position).getName());
        holder.folderParent.setOnClickListener(v -> Toast.makeText(mContext,folders.get(holder.getAbsoluteAdapterPosition()).getName()+" Selected",Toast.LENGTH_LONG).show());
//        ViewHolder viewHolder = (ViewHolder) holder;
//here you can set your own conditions based on your arraylist using position parameter
//        viewHolder. .itemNameTextView.setText(locationsArrayList.get(position).getName());

    }

    public Context getContext() {
        return mContext;
    }
    @Override
    public int getItemCount() {
        return folders.size();
    }



}
