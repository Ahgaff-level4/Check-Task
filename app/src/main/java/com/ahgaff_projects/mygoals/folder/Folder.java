package com.ahgaff_projects.mygoals.folder;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.goal.Goal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
