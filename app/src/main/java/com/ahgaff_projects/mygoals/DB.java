package com.ahgaff_projects.mygoals;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "CheckTasks.db";
    public static final String FOLDER_TABLE_NAME = "folders";
    public static final String ID = "id";
    public static final String FOLDER_ID = FOLDER_TABLE_NAME + "." + ID;
    public static final String NAME = "name";
    public static final String FOLDER_NAME = FOLDER_TABLE_NAME + "." + NAME;
    public static final String CREATED = "created";
    public static final String FOLDER_CREATED = FOLDER_TABLE_NAME + "." + CREATED;

    public static final String FILE_TABLE_NAME = "file";
    public static final String FILE_ID = FILE_TABLE_NAME + "." + ID;
    public static final String FILE_NAME = FILE_TABLE_NAME + "." + NAME;
    public static final String START_REMINDER = "startReminder";
    public static final String FILE_START_REMINDER = FILE_TABLE_NAME + "." + START_REMINDER;
    public static final String REPEAT_EVERY = "repeatEvery";
    public static final String FILE_REPEAT_EVERY = FILE_TABLE_NAME + "." + REPEAT_EVERY;
    public static final String FILE_CREATED = FILE_TABLE_NAME + "." + CREATED;
    public static final String FILE_REFERENCE_FOLDER = "folderId";


    public static final String TASK_TABLE_NAME = "task";
    public static final String TASK_ID = TASK_TABLE_NAME + "." + ID;
    public static final String TEXT = "text";
    public static final String TASK_TEXT = TASK_TABLE_NAME + "." + TEXT;
    public static final String CHECKED = "checked";
    public static final String TASK_CHECKED = TASK_TABLE_NAME + "." + CHECKED;
    public static final String TASK_CREATED = TASK_TABLE_NAME + "." + CREATED;
    public static final String TASK_REFERENCE_FILE = "fileId";


    public DB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + FOLDER_TABLE_NAME + "(" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NAME + " TEXT NOT NULL," +
                        CREATED + " TEXT)"
        );
        db.execSQL(
                "CREATE TABLE " + FILE_TABLE_NAME + "(" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        NAME + " TEXT NOT NULL," +
                        START_REMINDER + " TEXT," +
                        REPEAT_EVERY + " INTEGER," +
                        CREATED + " TEXT," +
                        FILE_REFERENCE_FOLDER + " INTEGER NOT NULL, FOREIGN KEY(" + FILE_REFERENCE_FOLDER + ") REFERENCES " + FOLDER_TABLE_NAME + "(" + ID + ") ON DELETE CASCADE)"
        );
        db.execSQL(
                "CREATE TABLE " + TASK_TABLE_NAME + "(" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TEXT + " TEXT NOT NULL," +
                        CHECKED + " TEXT," +
                        CREATED + " TEXT," +
                        TASK_REFERENCE_FILE + " INTEGER NOT NULL, FOREIGN KEY(" + TASK_REFERENCE_FILE + ") REFERENCES " + FILE_TABLE_NAME + "(" + ID + ") ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FOLDER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Folder> getAllFolders() {
        ArrayList<Folder> arr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        /*
        SELECT description,
       author,
       count(sales.book_id) as "amount of sales"
FROM books
  LEFT JOIN sales ON books.id = sales.book_id
GROUP BY books.id;
         */
        Cursor res = db.rawQuery("SELECT " + FOLDER_ID + "," + FOLDER_NAME + "," + FOLDER_CREATED +
                ", COUNT(" + FILE_REFERENCE_FOLDER + ") AS filesCount FROM " + FOLDER_TABLE_NAME
                + " LEFT JOIN " + FILE_TABLE_NAME + " ON " + FOLDER_ID + " = " + FILE_REFERENCE_FOLDER + " GROUP BY " + FOLDER_ID, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            @SuppressLint("Range") int id = res.getInt(res.getColumnIndex(DB.ID));
            @SuppressLint("Range") String name = res.getString(res.getColumnIndex(DB.NAME));
            @SuppressLint("Range") String createdStr = res.getString(res.getColumnIndex(CREATED));
            @SuppressLint("Range") int filesCount = res.getInt(res.getColumnIndex("filesCount"));
            LocalDateTime created = null;
            if (createdStr.contains("/"))
                created = FACTORY.getDateFrom(createdStr);

            arr.add(new Folder(id, name, created, filesCount));

            res.moveToNext();
        }
        res.close();
        return arr;
    }

    public boolean insertFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(CREATED, LocalDateTime.now().format(FACTORY.dateFormat));
        db.insert(FOLDER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean deleteFolder(int folderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(FOLDER_TABLE_NAME, ID + "=" + folderId, null) > 0;
    }

    public boolean updateFolder(int folderId, String name) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        return db.update(FOLDER_TABLE_NAME, cv, ID + "=" + folderId, null) > 0;
    }

    public ArrayList<File> getFilesOf(int folderId) {
        ArrayList<File> arr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " + FILE_ID + "," + FILE_NAME + "," + FILE_START_REMINDER + "," +
                FILE_REPEAT_EVERY + "," + FILE_CREATED + ", COUNT(" + TASK_REFERENCE_FILE + ") AS tasksCount" +
                " FROM " + FILE_TABLE_NAME + " LEFT JOIN " + TASK_TABLE_NAME + " ON " + FILE_ID + " = " + TASK_REFERENCE_FILE +
                " WHERE " + FILE_REFERENCE_FOLDER + " = " + folderId +
                " GROUP BY " + FILE_ID, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            @SuppressLint("Range") int id = res.getInt(res.getColumnIndex(DB.ID));
            @SuppressLint("Range") String name = res.getString(res.getColumnIndex(DB.NAME));
            @SuppressLint("Range") String createdStr = res.getString(res.getColumnIndex(CREATED));
            @SuppressLint("Range") String startReminderStr = res.getString(res.getColumnIndex(START_REMINDER));
            @SuppressLint("Range") int repeatEvery = res.getInt(res.getColumnIndex(DB.REPEAT_EVERY));
            @SuppressLint("Range") int tasksCount = res.getInt(res.getColumnIndex("tasksCount"));

            LocalDateTime created = null, startReminder = null;
            if (createdStr.contains("/"))
                created = FACTORY.getDateFrom(createdStr);
            if (startReminderStr != null && startReminderStr.contains("/"))
                startReminder = FACTORY.getDateFrom(startReminderStr);
            arr.add(new File(id, name, startReminder, repeatEvery, created, folderId, tasksCount));
            res.moveToNext();
        }
        res.close();
        return arr;
    }

    public boolean insertFile(int folderId, String name, LocalDateTime startReminder, int repeatEvery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        if (startReminder != null)
            contentValues.put(START_REMINDER, startReminder.format(FACTORY.dateFormat));
        contentValues.put(REPEAT_EVERY, repeatEvery);
        contentValues.put(CREATED, LocalDateTime.now().format(FACTORY.dateFormat));
        contentValues.put(FILE_REFERENCE_FOLDER, folderId);
        db.insert(FILE_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean deleteFile(int fileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(FILE_TABLE_NAME, ID + "=" + fileId, null) > 0;
    }

    public boolean updateFile(int fileId, String fileName, LocalDateTime startReminder, int repeatEvery) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, fileName);
        if (startReminder != null)
            cv.put(START_REMINDER, startReminder.format(FACTORY.dateFormat));
        cv.put(REPEAT_EVERY, repeatEvery);
        return db.update(FILE_TABLE_NAME, cv, ID + "=" + fileId, null) > 0;
    }

    public ArrayList<Task> getTasksOf(int fileId) {
        ArrayList<Task> arr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + TASK_REFERENCE_FILE + " = " + fileId, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            @SuppressLint("Range") int id = res.getInt(res.getColumnIndex(ID));
            @SuppressLint("Range") String text = res.getString(res.getColumnIndex(TEXT));
            @SuppressLint("Range") String createdStr = res.getString(res.getColumnIndex(CREATED));
            @SuppressLint("Range") String checkedStr = res.getString(res.getColumnIndex(CHECKED));

            LocalDateTime created = null;
            if (createdStr.contains("/"))
                created = FACTORY.getDateFrom(createdStr);
            boolean checked = checkedStr.equals("1");//"1"->true, "0"->false
            arr.add(new Task(id, text, checked, created));
            res.moveToNext();
        }
        res.close();
        return arr;
    }

    public boolean insertTask(int fileId,String text,boolean checked){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TEXT, text);
        contentValues.put(CREATED, LocalDateTime.now().format(FACTORY.dateFormat));
        contentValues.put(TASK_REFERENCE_FILE, fileId);
        contentValues.put(CHECKED, checked);
        db.insert(TASK_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateTask(int taskId, String text, boolean isChecked) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TEXT, text);
        cv.put(CHECKED,isChecked);
        return db.update(TASK_TABLE_NAME, cv, ID + "=" + taskId, null) > 0;
    }

}











