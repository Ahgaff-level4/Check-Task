package com.ahgaff_projects.mygoals.task;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;

public class Task {
    private final int id;
    private String text;
    private boolean checked;
    private final LocalDateTime created;

    public Task(int id, String text, boolean checked,LocalDateTime created){
        this.id = id;//get max id +1
        this.text = text;
        this.checked = checked;
        this.created = created;
    }


    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + text + '\'' +
                ", completed=" + checked +
                '}';
    }

    public int getId(){
    return id;
}
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public LocalDateTime getCreated(){return created;}

}
