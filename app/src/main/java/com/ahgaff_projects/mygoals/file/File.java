package com.ahgaff_projects.mygoals.file;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public class File {
    private int id;
    private String name;
    private LocalDateTime created;
    public String createdStr;
    /**
    remindStart instead of remindBefore because LocalDateTime store the date not the period. So, if user enter before 3 days you should calculate when it start because LocalDateTime can't store 3 days period.
    */
     @Nullable
    private LocalDateTime startReminder;
     @Nullable
    public String startReminderStr;
    /**
     * repeat every day=1,2,3... or -1 for Never repeat
     */
    private int repeatEvery;
    private int folderId;
    private int tasksCount;
public File(){}//for firebase
    public File(int id,String name,@Nullable LocalDateTime startReminder,int repeatEveryDay,LocalDateTime created,int folderId,int tasksCount){
        this.id = id;
        this.name = name;
        this.startReminder = startReminder;
        this.repeatEvery = repeatEveryDay;
        this.created = created;
        this.folderId = folderId;
        this.tasksCount = tasksCount;
    }
    //firebase
    public File(int id, String name, @Nullable String startReminderStr, int repeatEveryDay, String createdStr, int folderId){
        this.id = id;
        this.name = name;
        this.startReminderStr = startReminderStr;
        this.repeatEvery = repeatEveryDay;
        this.createdStr = createdStr;
        this.folderId = folderId;
    }
    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    @Nullable
    public LocalDateTime getStartReminder() {
        return startReminder;
    }

    public int getRepeatEvery() {
        return repeatEvery;
    }

    /**
     *
     * @return may returns -1 for files comes from DB.getAllFiles()
     */
    public int getFolderId() {
        return folderId;
    }
    public int getTasksCount(){return tasksCount;}

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setStartReminder(@Nullable LocalDateTime startReminder) {
        this.startReminder = startReminder;
    }

    public LocalDateTime getCreated(){return created;}
    @NonNull
    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", startReminder=" + startReminder +
                ", repeatEvery=" + repeatEvery +
                ", folderId=" + folderId +
                ", tasksCount=" + tasksCount +
                '}';
    }
}
