package com.ahgaff_projects.mygoals.file;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ahgaff_projects.mygoals.DB;
import com.ahgaff_projects.mygoals.FACTORY;
import com.ahgaff_projects.mygoals.R;
import com.ahgaff_projects.mygoals.task.TaskListFragment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.ViewHolder> {
    private final FragmentActivity context;
    public final int folderId;
    private ArrayList<File> files;
    private final DB db;

    public FileRecyclerViewAdapter(int folderId, FragmentActivity context, DB db) {
        this.folderId = folderId;
        this.context = context;
        this.db = db;
        if (folderId == -1) {//-1 means all files
            this.files = db.getAllFiles();
            context.setTitle(R.string.all_files);
        } else {
            this.files = db.getFilesOf(folderId);
            context.setTitle(context.getString(R.string.folder_title)+" "+db.getFolder(folderId).getName());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View fileParent;
        private final TextView fileName;
        private final TextView fileStartTime;
        private final View optionBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileParent = itemView.findViewById(R.id.fileItemParent);
            fileName = itemView.findViewById(R.id.fileName);
            fileStartTime = itemView.findViewById(R.id.fileStartReminder);
            optionBtn = itemView.findViewById(R.id.fileItemOptionMenuBtn);
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
        holder.fileName.setText(thisFile.getName());
        holder.fileStartTime.setText(nearestReminder(thisFile));
        holder.fileParent.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("fileId", thisFile.getId());
            if(folderId==-1)
                bundle.putBoolean("isFromAllTasks",true);
            context.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, TaskListFragment.class, bundle)
                    .commit();
        });
        holder.optionBtn.setOnClickListener(handleOnOptionClick(thisFile, holder.optionBtn));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    /**
     * @return array of string of all files name
     */
    public ArrayList<String> getFilesNames() {
        ArrayList<String> arr = new ArrayList<>();
        for (File f : files)
            arr.add(f.getName());
        return arr;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFiles() {
        if (folderId == -1)
            this.files = db.getAllFiles();
        else
            this.files = db.getFilesOf(folderId);
        notifyDataSetChanged();//refresh the list
    }

    private String nearestReminder(File thisFile) {
        if (thisFile.getStartReminder() == null)//no startReminder means no repeatEvery because if user choose only repeatEvery then automatically startReminder will be set at that day
            return "";
        //get days different between now and startReminder (future)
        Duration dur = Duration.between(LocalDateTime.now(), thisFile.getStartReminder());
        long days = Math.round(((double) dur.toHours()) / 24) + 1;//today days became -1 So add one. idk why

        if (days < 0) {
            //startReminder has pass and no repeatEvery!
            if (thisFile.getRepeatEvery() == -1)
                return "";
            //app will reach here if startReminder=old date and repeatDays exist
            //startReminder= old date.
            //repeatDays= n days.
            // So, remain days = (now date - old date) % repeatDays
            days = (-days) % thisFile.getRepeatEvery();//old date is negative
        }
        return FACTORY.toEveryDay((int) days, context);
    }

    private View.OnClickListener handleOnOptionClick(File f, View optionBtn) {
        return view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, optionBtn);
            //inflating menu from xml resource
            popup.inflate(R.menu.edit_delete_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_item) {
                    //handle menu1 click
                    handleEditFile(f);
                    return true;
                }
                if (item.getItemId() == R.id.delete_item) {
                    //handle menu2 click
                    FACTORY.showAreYouSureDialog(context.getString(R.string.file_title) + " " + f.getName() + " " + context.getString(R.string.will_be_deleted) + "\n" + context.getString(R.string.it_has) + " " + f.getTasksCount() + " " + context.getString(R.string.tasks), context, (_dialog, which) -> {
                        if (!db.deleteFile(f.getId()))
                            FACTORY.showErrorDialog(R.string.something_went_wrong, context);
                        else {
                            this.updateFiles();
                            Toast.makeText(context, context.getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                } else
                    return false;

            });
            //displaying the popup
            popup.show();
        };
    }

    private void handleEditFile(File f) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.edit_file_title);
        dialog.setNegativeButton(R.string.cancel, (_dialog, blah) -> _dialog.cancel());
        View inflater = context.getLayoutInflater().inflate(R.layout.dialog_add_edit_file, null);
        if (f.getStartReminder() != null)
            FileListFragment.StartReminder.setUp(inflater, context, f.getStartReminder().format(FACTORY.dateFormat));
        else FileListFragment.StartReminder.setUp(inflater, context, null);
        FileListFragment.RepeatEvery.setUp(inflater, context, String.valueOf(f.getRepeatEvery()));
        dialog.setView(inflater);
        EditText input = inflater.findViewById(R.id.fileNameEditText);//input from dialog
        input.setText(f.getName());
        dialog.setPositiveButton(R.string.edit, (_dialog, blah) -> {
            String editFileName = input.getText().toString().trim();
            if (editFileName.equals(""))
                FACTORY.showErrorDialog(context.getString(R.string.invalid_file_name), context);
            else {
                LocalDateTime startReminder = FileListFragment.StartReminder.getChosen(inflater);
                int repeatEvery = FileListFragment.RepeatEvery.getChosen(inflater);
                if (!db.updateFile(f.getId(), editFileName, startReminder, repeatEvery))
                    FACTORY.showErrorDialog(R.string.error, context);
                updateFiles();
            }
        });
        dialog.show();
    }
}
