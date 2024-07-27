package com.example.todolist;

import static com.example.todolist.Database.Database.TABLE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.todolist.Database.Database;
import com.example.todolist.databinding.ActivityInsertUpdateItemBinding;

import java.time.Month;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class InsertUpdateItem extends AppCompatActivity {
    ActivityInsertUpdateItemBinding binding;
    String operationName;
    Database database;
    SQLiteDatabase sqLiteDatabase;
    int id = 0;
    String selectedDate = "";
    int priorityId = 0;
    String[] arr = {"Low", "Normal", "High"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityInsertUpdateItemBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        database = new Database(InsertUpdateItem.this);

        operationName = getIntent().getStringExtra("type");
        binding.operationName.setText(operationName);
        if (Objects.equals(operationName, "Insert Data")) {
            binding.updateInsertBtn.setText("Add Data");
            insertData();
        } else {
            binding.updateInsertBtn.setText("Update Data");
            binding.selectDate.setText(getIntent().getStringExtra("date"));
            priorityId = getIntent().getIntExtra("priority", 0);
            binding.selectPriority.post(new Runnable() {
                @Override
                public void run() {
                    binding.selectPriority.setSelection(priorityId);
                }
            });
            updateData();
        }

        binding.enterItemTitle.requestFocus();

        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        binding.selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog(day, month, year);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, arr);
        binding.selectPriority.setAdapter(adapter);

        binding.selectPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priorityId = binding.selectPriority.getSelectedItemPosition();
                Log.d("Check", String.valueOf(priorityId));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void openDateDialog(int d, int mo, int y) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = String.valueOf(dayOfMonth + " " + getMonthName(month + 1) + " " + year);
                binding.selectDate.setText(String.valueOf(dayOfMonth + " " + getMonthName(month + 1) + " " + year));
            }
        }, y, mo, d);
        datePickerDialog.show();
    }

    private String getMonthName(int month) {
        return Month.of(month).getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }

    public void insertData() {
        binding.updateInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidDetails()) {
                    ContentValues cv = new ContentValues();
                    cv.put("title", binding.enterItemTitle.getText().toString());
                    cv.put("content", binding.enterItemContent.getText().toString());
                    cv.put("isDone", 0);
                    cv.put("date", binding.selectDate.getText().toString());
                    cv.put("priority", priorityId);

                    sqLiteDatabase = database.getWritableDatabase();
                    Long checkInsert = sqLiteDatabase.insert(TABLE_NAME, null, cv);
                    if (checkInsert != null) {
                        Toast.makeText(InsertUpdateItem.this, "Inserted!", Toast.LENGTH_SHORT).show();
                        binding.enterItemTitle.setText("");
                        binding.enterItemContent.setText("");
                        finish();
                    } else {
                        Toast.makeText(InsertUpdateItem.this, "Unexpected!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateData() {
        id = getIntent().getIntExtra("ID", 0);
        binding.enterItemTitle.setText(getIntent().getStringExtra("title"));
        binding.enterItemContent.setText(getIntent().getStringExtra("content"));
        selectedDate = getIntent().getStringExtra("date");
        binding.updateInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidDetails()) {
                    ContentValues cv = new ContentValues();
                    cv.put("title", binding.enterItemTitle.getText().toString());
                    cv.put("content", binding.enterItemContent.getText().toString());
                    cv.put("isDone", getIntent().getIntExtra("isDone", 0));
                    cv.put("date", selectedDate);
                    cv.put("priority", priorityId);

                    sqLiteDatabase = database.getReadableDatabase();
                    long checkUpdate = sqLiteDatabase.update(TABLE_NAME, cv, "id=" + id, null);
                    if (checkUpdate != -1) {
                        Toast.makeText(InsertUpdateItem.this, "Data Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(InsertUpdateItem.this, "Unexpected Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkValidDetails() {
        String title = binding.enterItemTitle.getText().toString();
        String content = binding.enterItemContent.getText().toString();
        String date = binding.selectDate.getText().toString();

        if (title.isEmpty() && content.isEmpty() && (date.isEmpty() || date.equals("Select Date"))) {
            Toast.makeText(this, "Empty details can not acceptable!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (title.isEmpty() && content.isEmpty()) {
            binding.enterItemTitle.setError("Can not be empty!");
            binding.enterItemContent.setError("Can not be empty!");
            return false;
        } else if (title.isEmpty()) {
            binding.enterItemTitle.setError("Can not be empty!");
            return false;
        } else if (content.isEmpty()) {
            binding.enterItemContent.setError("Can not be empty!");
            return false;
        } else if (date.isEmpty() || date.equals("Select Date")) {
            Toast.makeText(this, "Please select Date by clicking -Select Date- button!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}