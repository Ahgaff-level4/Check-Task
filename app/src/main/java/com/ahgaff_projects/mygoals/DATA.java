package com.ahgaff_projects.mygoals;

import android.content.Context;
import android.widget.Toast;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.file.FileListActivity;
import com.ahgaff_projects.mygoals.folder.Folder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class DATA {//shared functionality that deal with data
    public static void save(ArrayList<Folder> folders, Context context){
        try{
            FileOutputStream fileOutputStream = context.openFileOutput("MyGoals", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(folders);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Error in DATA.save FileNotFoundException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error in DATA.save IOException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public static ArrayList<Folder> retrieveFolders(Context context) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput("MyGoals"));
            ArrayList<Folder> folders = (ArrayList<Folder>) objectInputStream.readObject();
            objectInputStream.close();
            return folders;
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Error in DATA.retrieveFolders FileNotFoundException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {//if app is new  then IOException will throw; because there is no data saved. That normal no need for error message
//            Toast.makeText(context, "Error in DATA.retrieveFolders IOException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Error in DATA.retrieveFolders ClassNotFoundException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    public static void save(Folder folder, FileListActivity context){
        ArrayList<Folder> folders = DATA.retrieveFolders(context);
        for(int i=0;i<folders.size();i++)//to save a folder we need to get all folders and set the folder and then save all folders because internal storage take ArrayList<Folder>
            if(folders.get(i).getId() == folder.getId()){
                folders.set(i,folder);
                DATA.save(folders,context);
                return;
            }
    }

    /**
     *  generate folder id used for new folder
     * @param folders
     * @return max folder id+1
     */
    public static int generateId(ArrayList<Folder> folders){
        int max = Integer.MIN_VALUE;
        for(Folder f : folders)
            if(f.getId() > max)
                max = f.getId();
        return Math.max(1+max,0);//if no item than return 0
    }
    public static int generateId(Folder folder){
        int max = Integer.MIN_VALUE;
        for(File f:folder.getFiles())
            if(f.getId() > max)
                max = f.getId();
        return Math.max(1+max, 0);
    }
}
