package com.ahgaff_projects.mygoals.file;

import androidx.annotation.Nullable;

import com.ahgaff_projects.mygoals.goal.Goal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class File implements Cloneable, Serializable {
    private int id;
    private String name;
    private ArrayList<Goal> goals = new ArrayList<>();
    private LocalDateTime created;
    //remindStart instead of remindBefore because LocalDateTime store the date not the period. So, if user enter before 3 days you should calculate when it start because LocalDateTime can't store 3 days period.
    @Nullable
    private LocalDateTime remindStart;
    @Nullable
    private LocalDateTime remindPeriodic;

    public File(String name){
        this.id = -1;
        this.name = name;
        created = LocalDateTime.now();
    }

    @Override
    public File clone() {
        File clone = new File(this.name);
        clone.id = id;
        for(Goal goal:goals)
            clone.goals.add(goal.clone());
        clone.created = created;
        clone.remindStart = remindStart;
        clone.remindPeriodic = remindPeriodic;
        return clone;
    }
}
