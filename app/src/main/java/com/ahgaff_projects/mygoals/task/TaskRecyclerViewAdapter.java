package com.ahgaff_projects.mygoals.task;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;

import java.util.ArrayList;
import java.util.Comparator;

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
        this.tasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if ((o1.isChecked() && o1.isChecked()) || (!o1.isChecked() && !o2.isChecked()))
                    return o1.getCreated().compareTo(o2.getCreated());
                else if(o1.isChecked())
                    return -1;
                else return 1;
            }
        });
        context.setTitle(db.getFile(fileId).getName());
        if (this.tasks.size() <= 0)
            context.findViewById(R.id.emptyList).setVisibility(View.VISIBLE);
        else context.findViewById(R.id.emptyList).setVisibility(View.INVISIBLE);
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
        setStrike(holder.taskText, thisTask.isChecked());
        setBackgroundColor(holder.card, thisTask.isChecked());
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setStrike(holder.taskText, isChecked);
            thisTask.setChecked(isChecked);
            setBackgroundColor(holder.card, thisTask.isChecked());
            if (!db.updateTask(thisTask.getId(), thisTask.getText(), isChecked))
                FACTORY.showErrorDialog(R.string.something_went_wrong, context);
        });
        holder.card.setOnLongClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(R.string.edit_task_title);
            dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
            View inflater = context.getLayoutInflater().inflate(R.layout.dialog_add_edit_task, null);
            EditText input = inflater.findViewById(R.id.taskTextEditText);
            input.setText(thisTask.getText());
            dialog.setView(inflater);
            dialog.setPositiveButton(R.string.edit, (_dialog, blah) -> {
                String newTaskName = input.getText().toString().trim();
                if (newTaskName.equals(thisTask.getText()))
                    return;
                if (!db.updateTask(thisTask.getId(), newTaskName, thisTask.isChecked()))
                    FACTORY.showErrorDialog(R.string.something_went_wrong, context);
                updateTasks();

            });
            dialog.show();
            return true;
        });
    }

    /**
     * change card background color base on isChecked
     */
    private void setBackgroundColor(CardView cardView, boolean isChecked) {
        cardView.setCardBackgroundColor(context.getColor(isChecked ? R.color.cardCheckedBackgroundColor : R.color.cardBackgroundColor));
    }

    private void setStrike(TextView textView, boolean isChecked) {
        if (isChecked)
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
        if (this.tasks.size() <= 0)
            context.findViewById(R.id.emptyList).setVisibility(View.VISIBLE);
        else context.findViewById(R.id.emptyList).setVisibility(View.INVISIBLE);
    }
}
