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
    private LocalDateTime startReminder;
    /**
     * repeat every day=1,2,3... or -1 for Never repeat
     */
    private int repeatEvery;

    public File(int id,String name,@Nullable LocalDateTime startReminder,int repeatEveryDay){
        this.id = id;
        this.name = name;
        created = LocalDateTime.now();
        this.startReminder = startReminder;
        this.repeatEvery = repeatEveryDay;
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
    public LocalDateTime getStartReminder() {
        return startReminder;
    }

    @Nullable
    public int getRepeatEvery() {
        return repeatEvery;
    }

    @Override
    public File clone() {
        File clone = new File(this.id,this.name,this.startReminder,this.repeatEvery);
        for(Goal goal:goals)
            clone.goals.add(goal.clone());
        clone.created = created;
        return clone;
    }
}
