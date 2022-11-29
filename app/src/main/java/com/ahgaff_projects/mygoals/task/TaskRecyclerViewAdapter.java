package com.ahgaff_projects.mygoals.task;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
        context.setTitle(db.getFile(fileId).getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskText;
        private final CheckBox taskCheckBox;
        private final CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.taskTextView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            card = itemView.findViewById(R.id.taskParentCard);
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
        holder.taskText.setText(thisTask.getText());
        holder.taskCheckBox.setChecked(thisTask.isChecked());
        setStrike(holder.taskText,thisTask.isChecked());
        setBackgroundColor(holder.card,thisTask.isChecked());
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setStrike(holder.taskText,isChecked);
            thisTask.setChecked(isChecked);
            setBackgroundColor(holder.card,thisTask.isChecked());
            if (!db.updateTask(thisTask.getId(), thisTask.getText(), isChecked))
                FACTORY.showErrorDialog(R.string.something_went_wrong, context);
        });
    }

    /**
     * Strike the text of the TextView if isChecked. Un-strike if not isChecked
     */
    private void setBackgroundColor (CardView cardView, boolean isChecked) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(isChecked?android.R.attr.shadowColor:android.R.attr.textColorPrimary, typedValue, true);
        int color = ContextCompat.getColor(context, typedValue.resourceId);
//        if(isChecked)
            cardView.setCardBackgroundColor(color);
//        else cardView.setCardBackgroundColor(context.getColor(R.color));
    }
    private void setStrike(TextView textView, boolean isChecked){
        if(isChecked)
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
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
