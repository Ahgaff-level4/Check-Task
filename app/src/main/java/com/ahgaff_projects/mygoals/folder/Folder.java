package com.ahgaff_projects.mygoals.folder;

import java.time.LocalDateTime;

public class Folder {
    private final int id;
    private String name;
    private final LocalDateTime created;
    private final int filesCount;

    public Folder(int id,String name, LocalDateTime created,int filesCount){
        this.id = id;
        this.name = name;
        this.created = created;
        this.filesCount=filesCount;
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


    public LocalDateTime getCreated(){return created;}
}
