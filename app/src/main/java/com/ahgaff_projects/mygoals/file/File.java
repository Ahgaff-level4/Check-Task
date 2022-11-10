package com.ahgaff_projects.mygoals.file;

import androidx.annotation.Nullable;

import com.ahgaff_projects.mygoals.goal.Goal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class File implements Cloneable, Serializable {
    private final int id;
    private String name;
    private ArrayList<Goal> goals = new ArrayList<>();
    private LocalDateTime created;
    //remindStart instead of remindBefore because LocalDateTime store the date not the period. So, if user enter before 3 days you should calculate when it start because LocalDateTime can't store 3 days period.
    @Nullable
    private LocalDateTime remindStart;
    @Nullable
    private LocalDateTime remindPeriodic;

    public File(int id,String name,LocalDateTime remindStart,LocalDateTime remindPeriodic){
        this.id = id;
        this.name = name;
        created = LocalDateTime.now();
        this.remindStart = remindStart;
        this.remindPeriodic = remindPeriodic;
    }
    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Nullable
    public LocalDateTime getRemindStart() {
        return remindStart;
    }

    @Nullable
    public LocalDateTime getRemindPeriodic() {
        return remindPeriodic;
    }

    @Override
    public File clone() {
        File clone = new File(this.id,this.name,this.remindStart,this.remindPeriodic);
        for(Goal goal:goals)
            clone.goals.add(goal.clone());
        clone.created = created;
        return clone;
    }
}