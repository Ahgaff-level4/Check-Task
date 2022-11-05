package com.ahgaff_projects.mygoals.goal;

public class Goal {
    private int id;
    private String name;
    private boolean completed;
    //DateTime
    public Goal(String name, boolean completed){
        id = -1;//get max id +1
        this.name = name;
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", completed=" + completed +
                '}';
    }

    public int getId(){
    return id;
}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
