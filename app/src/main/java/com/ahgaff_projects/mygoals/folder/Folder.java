package com.ahgaff_projects.mygoals.folder;

import com.ahgaff_projects.mygoals.FACTORY;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Folder folder = (Folder) o;

        if (id != folder.id) return false;
        if (name != null ? !name.equals(folder.name) : folder.name != null) return false;
        return created != null ? created.equals(folder.created) : folder.created == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }
}
