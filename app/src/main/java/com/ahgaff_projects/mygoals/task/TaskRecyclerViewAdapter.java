package com.ahgaff_projects.mygoals.task;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;

import java.util.ArrayList;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {
    private final FragmentActivity context;
    public final int fileId;
    private ArrayList<Task> tasks;
    private final DB db;

    public TaskRecyclerViewAdapter(int fileId, FragmentActivity context, DB db) {
        this.fileId = fileId;
        this.context = context;
        this.db = db;
        this.tasks = db.getTasksOf(fileId);
        context.setTitle(context.getString(R.string.file_title)+" "+db.getFile(fileId).getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskText;
        private final CheckBox taskCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.taskText);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
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
                .inflate(R.layout.item_list_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task thisTask = tasks.get(position);
        String text = thisTask.getId() + "- " + thisTask.getText();
        holder.taskText.setText(text);
        holder.taskCheckBox.setChecked(thisTask.isChecked());
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(context, "checkedChanged ID="+thisTask.getId(), Toast.LENGTH_SHORT).show();
            holder.taskText.setAllCaps(isChecked);
            thisTask.setChecked(isChecked);
            if (!db.updateTask(thisTask.getId(), thisTask.getText(), isChecked))
                FACTORY.showErrorDialog(R.string.something_went_wrong, context);
        });
//        holder.taskParent.setOnClickListener(v -> Toast.makeText(context, tasks.get(holder.getAbsoluteAdapterPosition()).getText() + " Selected", Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateTasks() {
        this.tasks = db.getTasksOf(fileId);
        notifyDataSetChanged();//refresh the list
    }
}
