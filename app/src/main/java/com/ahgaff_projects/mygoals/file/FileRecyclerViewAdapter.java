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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
            switch ((int) days){
                case 0: return context.getString(R.string.today);
                case 1: return context.getString(R.string.tomorrow);
                case 2: return context.getString(R.string.after_tomorrow);
                case 3://empty case will fall to below case. So all empty cases will execute case 10 code.
                case 4:// 10 ايام
                case 5:// 11 يوم
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:return context.getString(R.string.after)+" "+days+" "+context.getString(R.string.days);
                default:return context.getString(R.string.after)+" "+days+" "+context.getString(R.string.arabic_days);
            }
    }
}
