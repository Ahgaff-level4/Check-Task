package com.ahgaff_projects.mygoals.folder;

import com.ahgaff_projects.mygoals.goal.Goal;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Folder {
    private int id;
    private String name;
    private ArrayList<Goal> goals;
    private LocalDateTime dateTime;

    public Folder(String name) {
        this.id = -1;//TODO: last id++
        this.name = name;
        this.goals = new ArrayList<>();
        dateTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }
//
//    public void addGoal(Goal goal) {
//        this.goals.add(goal);
//    }
//    public void removeGoal(Goal goal){
//        this.goals.remove(goal);
//    }
//    public void removeGoal(int index){
//        this.goals.remove(index);
//    }
}
