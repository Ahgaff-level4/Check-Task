package com.ahmadalkaf.basicactivity;

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

import java.util.ArrayList;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Folder> folders = new ArrayList<>();
    private Context mContext;

    public FolderRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
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

    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();//refresh the list
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_folder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("FolderRecyclerView","onBindViewHolder called");
        holder.folderName.setText(folders.get(position).getName());
        holder.folderParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,folders.get(holder.getAbsoluteAdapterPosition()).getName()+" Selected",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }



}
