package com.ahgaff_projects.mygoals;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ahgaff_projects.mygoals.file.File;
import com.ahgaff_projects.mygoals.folder.Folder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    private static final String DOT=".";
    private static final String COM=",";

    public static final String DATABASE_NAME = "CheckGoals.db";
    public static final String FOLDER_TABLE_NAME = "folders";
    public static final String FOLDER_COLUMN_ID = "id";
    public static final String FOLDER_COLUMN_NAME = "name";
    public static final String FOLDER_COLUMN_CREATED = "created";

    public static final String FILE_TABLE_NAME = "file";
    public static final String FILE_COLUMN_ID = "id";
    public static final String FILE_COLUMN_NAME = "name";
    public static final String FILE_COLUMN_START_REMINDER = "startReminder";
    public static final String FILE_COLUMN_START_REPEAT_EVERY = "repeatEvery";
    public static final String FILE_COLUMN_CREATED = "created";
    public static final String FILE_COLUMN_REFERENCE_FOLDER = "folderId";


    public static final String GOAL_TABLE_NAME = "id";
    public static final String GOAL_COLUMN_ID = "id";
    public static final String GOAL_COLUMN_NAME = "name";
    public static final String GOAL_COLUMN_COMPLETED = "completed";
    public static final String GOAL_COLUMN_CREATED = "created";
    public static final String GOAL_COLUMN_REFERENCE_FILE = "fileId";


    public DB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + FOLDER_TABLE_NAME + "(" +
                        FOLDER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FOLDER_COLUMN_NAME + " TEXT NOT NULL," +
                        FOLDER_COLUMN_CREATED + " TEXT)"
        );
        db.execSQL(
                "CREATE TABLE " + FILE_TABLE_NAME + "(" +
                        FILE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FILE_COLUMN_NAME + " TEXT NOT NULL," +
                        FILE_COLUMN_START_REMINDER + " TEXT," +
                        FILE_COLUMN_START_REPEAT_EVERY + " INTEGER," +
                        FILE_COLUMN_CREATED + " TEXT," +
                        FILE_COLUMN_REFERENCE_FOLDER+" INTEGER NOT NULL, FOREIGN KEY("+FILE_COLUMN_REFERENCE_FOLDER+") REFERENCES " + FOLDER_TABLE_NAME + "(" + FOLDER_COLUMN_ID + ") ON DELETE CASCADE)"
        );
        db.execSQL(
                "CREATE TABLE " + GOAL_TABLE_NAME + "(" +
                        GOAL_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        GOAL_COLUMN_NAME + " TEXT NOT NULL," +
                        GOAL_COLUMN_COMPLETED + " TEXT," +
                        GOAL_COLUMN_CREATED + " TEXT," +
                        GOAL_COLUMN_REFERENCE_FILE+" INTEGER NOT NULL, FOREIGN KEY("+GOAL_COLUMN_REFERENCE_FILE+") REFERENCES " + FILE_TABLE_NAME + "(" + FILE_COLUMN_ID + ") ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FOLDER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOLDER_COLUMN_NAME, name);
        contentValues.put(FOLDER_COLUMN_CREATED, LocalDateTime.now().format(FACTORY.dateFormat));
        db.insert(FOLDER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertFile(int folderId, String name,LocalDateTime startReminder,int repeatEvery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FILE_COLUMN_NAME, name);
        if (startReminder != null)
            contentValues.put(FILE_COLUMN_START_REMINDER, startReminder.format(FACTORY.dateFormat));
        contentValues.put(FILE_COLUMN_START_REPEAT_EVERY, repeatEvery);
        contentValues.put(FILE_COLUMN_CREATED, LocalDateTime.now().format(FACTORY.dateFormat));
        contentValues.put(FILE_COLUMN_REFERENCE_FOLDER, folderId);
        db.insert(FILE_TABLE_NAME, null, contentValues);
        return true;
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
        Cursor res = db.rawQuery("select "+FOLDER_TABLE_NAME+"."+FOLDER_COLUMN_ID+","+FOLDER_TABLE_NAME+DOT+FOLDER_COLUMN_NAME+","+FOLDER_TABLE_NAME+DOT+FOLDER_COLUMN_CREATED+
                ", COUNT("+FILE_TABLE_NAME+"."+FILE_COLUMN_REFERENCE_FOLDER+") as \"filesCount\" from " + FOLDER_TABLE_NAME
                +" LEFT JOIN "+FILE_TABLE_NAME+" ON "+FOLDER_TABLE_NAME+"."+FOLDER_COLUMN_ID+" = "+FILE_TABLE_NAME+"."+FILE_COLUMN_REFERENCE_FOLDER+" GROUP BY "+FOLDER_TABLE_NAME+"."+FOLDER_COLUMN_ID, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            @SuppressLint("Range") int id = res.getInt(res.getColumnIndex(FOLDER_COLUMN_ID));
            @SuppressLint("Range") String name = res.getString(res.getColumnIndex(FOLDER_COLUMN_NAME));
            @SuppressLint("Range") String createdStr = res.getString(res.getColumnIndex(FOLDER_COLUMN_CREATED));
            @SuppressLint("Range") int filesCount = res.getInt(res.getColumnIndex("filesCount"));
            LocalDateTime created = null;
            if (createdStr.contains("/"))
                created = FACTORY.getDateFrom(createdStr);

            arr.add(new Folder(id, name, created,filesCount));

            res.moveToNext();
        }
        res.close();
        return arr;
    }

    public ArrayList<File> getFilesOf(int folderId) {
        ArrayList<File> arr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + FILE_TABLE_NAME+" WHERE "+FILE_COLUMN_REFERENCE_FOLDER+" = "+folderId, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            @SuppressLint("Range") int id = res.getInt(res.getColumnIndex(FILE_COLUMN_ID));
            @SuppressLint("Range") String name = res.getString(res.getColumnIndex(FILE_COLUMN_NAME));
            @SuppressLint("Range") String createdStr = res.getString(res.getColumnIndex(FILE_COLUMN_CREATED));
            @SuppressLint("Range") String startReminderStr = res.getString(res.getColumnIndex(FILE_COLUMN_START_REMINDER));
            @SuppressLint("Range") int repeatEvery = res.getInt(res.getColumnIndex(FILE_COLUMN_START_REPEAT_EVERY));
            LocalDateTime created = null,startReminder=null;
            if (createdStr.contains("/"))
                created = FACTORY.getDateFrom(createdStr);
            if(startReminderStr!=null&&startReminderStr.contains("/"))
                startReminder = FACTORY.getDateFrom(startReminderStr);
            arr.add(new File(id, name,startReminder,repeatEvery,created,folderId));
            res.moveToNext();
        }
        res.close();
        return arr;
    }

    public boolean deleteFolder(int folderId) {
        SQLiteDatabase db =this.getReadableDatabase();
        return db.delete(FOLDER_TABLE_NAME,FOLDER_COLUMN_ID+"="+folderId,null)>0;
    }

    public boolean updateFolder(int folderId,String name){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FOLDER_COLUMN_NAME,name);
        return db.update(FOLDER_TABLE_NAME,cv,FOLDER_COLUMN_ID+"="+folderId,null)>0;
    }
}












