package com.example.todolist;

import static com.example.todolist.Database.Database.TABLE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.todolist.Database.Database;
import com.example.todolist.Database.NoteModel;
import com.example.todolist.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NoteAdapter adapter, adapter2;
    ArrayList<NoteModel> list;
    ArrayList<NoteModel> list2;
    ArrayList<NoteModel> p1;
    ArrayList<NoteModel> p2;
    ArrayList<NoteModel> p3;
    ArrayList<NoteModel> p11;
    ArrayList<NoteModel> p12;
    ArrayList<NoteModel> p13;
    Database database;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        p1 = new ArrayList<>();
        p2 = new ArrayList<>();
        p3 = new ArrayList<>();
        p11 = new ArrayList<>();
        p12 = new ArrayList<>();
        p13 = new ArrayList<>();
        database = new Database(MainActivity.this);

        binding.updateNoteRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        list2.clear();
                        display();
                        binding.updateNoteRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        binding.insertItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InsertUpdateItem.class);
                intent.putExtra("type", "Insert Data");
                startActivity(intent);
            }
        });

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setHasFixedSize(true);
        adapter = new NoteAdapter(this, list, new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NoteModel item) {
//                display();
                MainActivity.this.recreate();
            }
        });
        binding.recyclerview.setAdapter(adapter);

        binding.completeTaskRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.completeTaskRecyclerview.setHasFixedSize(true);
        adapter2 = new NoteAdapter(this, list2, new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NoteModel item) {
//                display();
                MainActivity.this.recreate();
            }
        });
        binding.completeTaskRecyclerview.setAdapter(adapter2);

        display();
    }

    public void display() {
        list.clear();
        list2.clear();
        p1.clear();
        p2.clear();
        p3.clear();
        p11.clear();
        p12.clear();
        p13.clear();
        sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + "", null);

        while (cursor.moveToNext()) {
            int i = 0;
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            int isDone = cursor.getInt(3);
            String date = cursor.getString(4);
            int priority = cursor.getInt(5);
            if (isDone == 1) {
                if (priority == 2) {
                    p11.add(new NoteModel(id, title, content, isDone, date, priority));
                } else if (priority == 1) {
                    p12.add(new NoteModel(id, title, content, isDone, date, priority));
                } else {
                    p13.add(new NoteModel(id, title, content, isDone, date, priority));
                }
            } else {
                if (priority == 2) {
                    p1.add(new NoteModel(id, title, content, isDone, date, priority));
                } else if (priority == 1) {
                    p2.add(new NoteModel(id, title, content, isDone, date, priority));
                } else {
                    p3.add(new NoteModel(id, title, content, isDone, date, priority));
                }
            }
        }
        list.addAll(p1);
        list.addAll(p2);
        list.addAll(p3);
        list2.addAll(p11);
        list2.addAll(p12);
        list2.addAll(p13);
        cursor.close();
        adapter.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        if (list.isEmpty() && list2.isEmpty()) {
            binding.toDoText.setVisibility(View.GONE);
            binding.workDoneText.setVisibility(View.GONE);
            binding.emptyList.setVisibility(View.VISIBLE);
        } else if (list.isEmpty()) {
            binding.toDoText.setVisibility(View.GONE);
            binding.workDoneText.setVisibility(View.VISIBLE);
            binding.emptyList.setVisibility(View.GONE);
        } else if (list2.isEmpty()) {
            binding.toDoText.setVisibility(View.VISIBLE);
            binding.workDoneText.setVisibility(View.GONE);
            binding.emptyList.setVisibility(View.GONE);
        } else {
            binding.toDoText.setVisibility(View.VISIBLE);
            binding.workDoneText.setVisibility(View.VISIBLE);
            binding.emptyList.setVisibility(View.GONE);
        }
        binding.updateNoteRefresh.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        display();
    }
}