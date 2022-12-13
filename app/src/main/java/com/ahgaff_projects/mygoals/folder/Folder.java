package com.ahgaff_projects.mygoals.folder;

import java.time.LocalDateTime;

public class Folder {
    private int id;
    private String name;
    private LocalDateTime created;
    public String createdStr;
    private int filesCount;

    public Folder(int id,String name, LocalDateTime created,int filesCount){
        this.id = id;
        this.name = name;
        this.created = created;
        this.filesCount=filesCount;
    }
    public Folder(){
//firebase
    }
//firebase
    public Folder(int id, String name, String createdStr){
        this.id = id;
        this.name = name;
        this.createdStr = createdStr;
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

    public int getFilesCount(){return filesCount;}

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
