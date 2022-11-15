package com.ahgaff_projects.mygoals.file;

import androidx.annotation.Nullable;

import com.ahgaff_projects.mygoals.goal.Goal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class File implements Cloneable, Serializable {
    private final int id;
    private String name;
    private final LocalDateTime created;
    /**
    remindStart instead of remindBefore because LocalDateTime store the date not the period. So, if user enter before 3 days you should calculate when it start because LocalDateTime can't store 3 days period.
    */
     @Nullable
    private LocalDateTime startReminder;
    /**
     * repeat every day=1,2,3... or -1 for Never repeat
     */
    private int repeatEvery;
    private final int folderId;

    public File(int id,String name,@Nullable LocalDateTime startReminder,int repeatEveryDay,LocalDateTime created,int folderId){
        this.id = id;
        this.name = name;
        this.startReminder = startReminder;
        this.repeatEvery = repeatEveryDay;
        this.created = created;
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

    public LocalDateTime getCreated() {
        return created;
    }

    @Nullable
    public LocalDateTime getStartReminder() {
        return startReminder;
    }
    @Nullable
    public void setStartReminder(LocalDateTime startReminder){
        this.startReminder = startReminder;
    }

    public int getRepeatEvery() {
        return repeatEvery;
    }
    public void setRepeatEvery(int repeatEvery){this.repeatEvery = repeatEvery;}
    public int getFolderId() {
        return folderId;
    }


}
