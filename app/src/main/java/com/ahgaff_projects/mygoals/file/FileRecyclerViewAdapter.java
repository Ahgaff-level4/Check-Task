package com.ahgaff_projects.mygoals.file;

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
import com.ahgaff_projects.mygoals.folder.Folder;

import java.util.ArrayList;

public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.ViewHolder> {
    private final FileListActivity context;
    private final Folder folder;
    public FileRecyclerViewAdapter(Folder folder, FileListActivity context) {
        this.folder = folder;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView fileParent;
        private TextView fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileParent = itemView.findViewById(R.id.fileParentCard);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_folder,parent,false);
//        return new ViewHolder(view);
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fileName.setText(folder.getFiles().get(position).getName());
        holder.fileParent.setOnClickListener(v -> Toast.makeText(context, folder.getFiles().get(holder.getAbsoluteAdapterPosition()).getName() + " Selected", Toast.LENGTH_LONG).show());
//        ViewHolder viewHolder = (ViewHolder) holder;
//here you can set your own conditions based on your arraylist using position parameter
//        viewHolder. .itemNameTextView.setText(locationsArrayList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return folder.getFiles().size();
    }

    public Folder getCopyFolder(){//return clone because this.folder should not be changed outside adapter(Encapsulation)
        return folder.clone();
    }


    public void addFile(File file) {
        this.folder.getFiles().add(file);
        notifyDataSetChanged();//refresh the list
        DATA.save(this.folder,context);
    }

    public void deleteFile(File file){
        this.folder.getFiles().remove(file);
        notifyDataSetChanged();
        DATA.save(folder,context);
    }

}
