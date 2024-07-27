package com.example.todolist.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ToDoTable";
    public static final String TABLE_NAME = "NoteTable";
    public static final int VERSION = 1;
    String query;

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        query = "create table " + TABLE_NAME + "(id integer primary key ,title text,content text,isDone integer,date text,priority integer)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        query = "drop table if exists "+TABLE_NAME+"";
        db.execSQL(query);
    }
}
