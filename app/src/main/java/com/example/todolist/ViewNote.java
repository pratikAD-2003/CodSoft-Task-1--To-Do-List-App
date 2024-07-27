package com.example.todolist;

import static com.example.todolist.Database.Database.TABLE_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.todolist.Database.Database;
import com.example.todolist.Database.NoteModel;
import com.example.todolist.databinding.ActivityViewNoteBinding;

import java.util.Objects;

public class ViewNote extends AppCompatActivity {
    ActivityViewNoteBinding binding;
    String title, content,date,priority;
    int id, isDone;
    SQLiteDatabase sqLiteDatabase;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityViewNoteBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        database = new Database(ViewNote.this);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        id = getIntent().getIntExtra("ID", -1);
        isDone = getIntent().getIntExtra("isDone", -1);
        date = getIntent().getStringExtra("date");
        priority = setPriority(getIntent().getIntExtra("priority",0));

        if (isDone == 1){
            binding.editItemVM.setVisibility(View.GONE);
            binding.markItemDoneVN.setVisibility(View.GONE);
        }else{
            binding.editItemVM.setVisibility(View.VISIBLE);
            binding.markItemDoneVN.setVisibility(View.VISIBLE);
        }

        binding.itemTitleVN.setText(title);
        binding.itemContentVM.setText(content);
        binding.itemDate.setText("Date : "+date);
        binding.itemPriority.setText("Priority : "+priority);

        binding.deleteItemVN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(id);
            }
        });

        binding.editItemVM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNote.this, InsertUpdateItem.class);
                intent.putExtra("type", "Update Data");
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("isDone", isDone);
                intent.putExtra("ID", id);
                intent.putExtra("date", date);
                intent.putExtra("priority", getIntent().getIntExtra("priority",0));
                startActivity(intent);
            }
        });

        binding.markItemDoneVN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(id, title, content);
            }
        });

    }

    public void updateData(int id, String title, String content) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("isDone", 1);

        sqLiteDatabase = database.getReadableDatabase();
        long checkUpdate = sqLiteDatabase.update(TABLE_NAME, cv, "id=" + id, null);
        if (checkUpdate != -1) {
            Toast.makeText(ViewNote.this, "Mark as Done!", Toast.LENGTH_SHORT).show();
            ViewNote.this.recreate();
        } else {
            Toast.makeText(ViewNote.this, "Unexpected Error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteData(int id) {
        sqLiteDatabase = database.getReadableDatabase();
        long delete = sqLiteDatabase.delete(TABLE_NAME, "id=" + id, null);
        if (delete != -1) {
            Toast.makeText(ViewNote.this, "Deleted!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(ViewNote.this, "Unexpected Error!", Toast.LENGTH_SHORT).show();
        }
    }

    private String setPriority(int priority) {
        switch (priority) {
            case 0:
                return "Low";
            case 1:
                return "Normal";
            case 2:
                return "High";
            default:
                return "Normal";
        }
    }
}