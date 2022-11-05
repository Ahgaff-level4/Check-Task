package com.ahmadalkaf.basicactivity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Folder {
    private int id;
    private String name;
    private ArrayList<Goal> goals;
    private LocalDateTime dateTime;

    public Folder(String name) {
        this.id = -1;//last id +1
        this.name = name;
        this.goals = new ArrayList<>();
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
