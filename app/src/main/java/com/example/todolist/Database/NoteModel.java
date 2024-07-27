package com.example.todolist.Database;

public class NoteModel {
    String title,content;
    int id,isDone,priority;
    String date;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NoteModel(int id,String title, String content, int isDone,String date,int priority) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isDone = isDone;
        this.date = date;
        this.priority = priority;
    }

    public NoteModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }
}
