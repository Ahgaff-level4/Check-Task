package com.ahgaff_projects.mygoals.file;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.task.TaskListFragment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.ViewHolder> {
    private final FileListActivity context;
    public final int folderId;
    private ArrayList<File> files;
    private final DB db;

    public FileRecyclerViewAdapter(int folderId, FileListActivity context, DB db) {
        this.folderId = folderId;
        this.context = context;
        this.db = db;
        this.files = db.getFilesOf(folderId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView fileParent;
        private final TextView fileName;
        private final TextView fileStartTime;

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
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File thisFile = files.get(position);
        String fileName = thisFile.getId() + "- " + thisFile.getName();
        holder.fileName.setText(fileName);
        holder.fileStartTime.setText(nearestReminder(thisFile));
        holder.fileParent.setOnClickListener(v -> {
            Intent i = new Intent(context, TaskListFragment.class);
            i.putExtra("fileId", thisFile.getId());
            context.startActivity(i);
        });
//        ViewHolder viewHolder = (ViewHolder) holder;
//here you can set your own conditions based on your arraylist using position parameter
//        viewHolder. .itemNameTextView.setText(locationsArrayList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    /**
     *
     * @return array of string of all files name
     */
    public ArrayList<String> getFilesNames(){
        ArrayList<String> arr = new ArrayList<>();
        for(File f: files)
            arr.add(f.getName());
        return arr;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFiles() {
        this.files = db.getFilesOf(folderId);
        notifyDataSetChanged();//refresh the list
    }

    private String nearestReminder(File thisFile) {
        if (thisFile.getStartReminder() == null)//no startReminder means no repeatEvery because if user choose only repeatEvery then automatically startReminder will be set at that day
            return "";
        //get days different between now and startReminder (future)
        Duration dur = Duration.between(LocalDateTime.now(), thisFile.getStartReminder());
        long days = Math.round(((double) dur.toHours()) / 24)+1;//today days became -1 So add one. idk why

        if(days < 0){
            //startReminder has pass and no repeatEvery!
            if(thisFile.getRepeatEvery() == -1)
                return "";
            //app will reach here if startReminder=old date and repeatDays exist
            //startReminder= old date.
            //repeatDays= n days.
            // So, remain days = (now date - old date) % repeatDays
            days =(-days) % thisFile.getRepeatEvery();//old date is negative
        }
            return FACTORY.toEveryDay((int)days,context);
    }
}
