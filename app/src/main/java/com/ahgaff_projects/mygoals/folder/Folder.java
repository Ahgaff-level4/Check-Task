package com.ahgaff_projects.mygoals.folder;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.goal.Goal;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Folder implements Cloneable {
    private int id;
    private String name;
    private ArrayList<File> files;
    private LocalDateTime created;

    public Folder(String name) {
        this.id = -1;//TODO: last id++
        this.name = name;
        this.files = new ArrayList<>();
        created = LocalDateTime.now();
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

    public ArrayList<File> getFiles() {
        return files;
    }

    @Override
    public Folder clone() {
        Folder clone = new Folder(this.name);
        clone.id = this.id;
        for (File file : this.files)
            clone.files.add(file.clone());
        clone.created = this.created;
        return clone;
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
