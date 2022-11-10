package com.ahgaff_projects.mygoals;

import android.content.Context;
import android.widget.Toast;

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
        } catch (IOException e) {
            Toast.makeText(context, "Error in DATA.retrieveFolders IOException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Error in DATA.retrieveFolders ClassNotFoundException:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }
}
