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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
        private TextView fileStartTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileParent = itemView.findViewById(R.id.fileParentCard);
            fileName = itemView.findViewById(R.id.fileName);
            fileStartTime = itemView.findViewById(R.id.fileStartReminder);
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
        File thisFile = folder.getFiles().get(position);
        holder.fileName.setText(thisFile.getId() + "- " + thisFile.getName());
        holder.fileStartTime.setText(nearestReminder(thisFile));
        holder.fileParent.setOnClickListener(v -> Toast.makeText(context, folder.getFiles().get(holder.getAbsoluteAdapterPosition()).getName() + " Selected", Toast.LENGTH_LONG).show());
//        ViewHolder viewHolder = (ViewHolder) holder;
//here you can set your own conditions based on your arraylist using position parameter
//        viewHolder. .itemNameTextView.setText(locationsArrayList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return folder.getFiles().size();
    }

    public Folder getCopyFolder() {//return clone because this.folder should not be changed outside adapter(Encapsulation)
        return folder.clone();
    }


    public void addFile(File file) {
        this.folder.getFiles().add(file);
        notifyDataSetChanged();//refresh the list
        DATA.save(this.folder, context);
    }

    public void deleteFile(File file) {
        this.folder.getFiles().remove(file);
        notifyDataSetChanged();
        DATA.save(folder, context);
    }

    private String nearestReminder(File thisFile) {
        if (thisFile.getStartReminder() == null)
            return "repeat:" + thisFile.getRepeatEvery();
        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = thisFile.getStartReminder();

        LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);
//
//        long years = tempDateTime.until( toDateTime, ChronoUnit.YEARS );
//        tempDateTime = tempDateTime.plusYears( years );
//
//        long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS );
//        tempDateTime = tempDateTime.plusMonths( months );

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
//        tempDateTime = tempDateTime.plusDays( days );
//        long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS );
//        tempDateTime = tempDateTime.plusHours( hours );
//
//        long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES );
//        tempDateTime = tempDateTime.plusMinutes( minutes );
//
//        long seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS );

        return "repeat:" + thisFile.getRepeatEvery() + " days:" + LocalDateTime.now().until(thisFile.getStartReminder(), ChronoUnit.DAYS);
    }
}
