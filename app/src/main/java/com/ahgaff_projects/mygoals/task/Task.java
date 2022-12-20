package com.ahgaff_projects.mygoals.task;

import androidx.annotation.NonNull;

import com.ahgaff_projects.mygoals.FACTORY;

import java.time.LocalDateTime;

public class Task {
    private  int id;
    private String text;
    private boolean checked;
    private LocalDateTime created;
    public String createdStr;
    public int fileId;


    public Task(int id, String text, boolean checked,LocalDateTime created, int fileId){
        this.id = id;//get max id +1
        this.text = text;
        this.checked = checked;
        this.created = created;
        this.fileId = fileId;
    }
    public Task(){}//firebase
    //firebase constructor
    public Task(int id, String text, boolean checked, String created,int fileId){
        this.id = id;
        this.text = text;
        this.checked = checked;
        this.createdStr = created;
        this.fileId = fileId;
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

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getCreated(){return created;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (checked != task.checked) return false;
        if (fileId != task.fileId) return false;
        if (!text.equals(task.text)) return false;
        if(task.createdStr != null && task.createdStr.equals(""))
            task.createdStr = null;
        return created.format(FACTORY.dateFormat).equals(task.createdStr);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + text.hashCode();
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + created.hashCode();
        result = 31 * result + fileId;
        return result;
    }
}
