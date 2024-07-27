package com.example.todolist;

import static com.example.todolist.Database.Database.TABLE_NAME;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Database.Database;
import com.example.todolist.Database.NoteModel;
import com.example.todolist.databinding.ItemsLayoutBinding;

import java.util.ArrayList;
import java.util.Objects;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {
    Context context;
    ArrayList<NoteModel> list = new ArrayList<>();
    ItemsLayoutBinding binding;
    OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(NoteModel item);
    }

    public NoteAdapter(Context context, ArrayList<NoteModel> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemsLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (list.get(position).getIsDone() == 0) {
            binding.itemDoneText.setText("Pending");
            binding.itemDoneBtn.setImageDrawable(context.getDrawable(R.drawable.red_circle));
            binding.markItemDone.setVisibility(View.VISIBLE);
            binding.editItem.setVisibility(View.VISIBLE);
        } else {
            binding.itemDoneText.setText("Done");
            binding.itemDoneBtn.setImageDrawable(context.getDrawable(R.drawable.green_circle));
            binding.markItemDone.setVisibility(View.GONE);
            binding.editItem.setVisibility(View.GONE);
        }
        if (list.get(position).getTitle().length() > 45) {
            binding.itemTitle.setText(list.get(position).getTitle().substring(0, 42) + "...");
        } else {
            binding.itemTitle.setText(list.get(position).getTitle());
        }

        if (list.get(position).getContent().length() > 95) {
            binding.itemContent.setText(list.get(position).getContent().substring(0, 92) + "...");
        } else {
            binding.itemContent.setText(list.get(position).getContent());
        }

        binding.itemDate.setText("Date : " + list.get(position).getDate());

        binding.priorityItem.setText(holder.setPriority(list.get(position).getPriority()));
        binding.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InsertUpdateItem.class);
                intent.putExtra("type", "Update Data");
                intent.putExtra("title", list.get(position).getTitle());
                intent.putExtra("content", list.get(position).getContent());
                intent.putExtra("isDone", list.get(position).getIsDone());
                intent.putExtra("ID", list.get(position).getId());
                intent.putExtra("date", list.get(position).getDate());
                intent.putExtra("priority", list.get(position).getPriority());
                context.startActivity(intent);
            }
        });

        binding.markItemDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.updateData(list.get(position).getId(), list.get(position).getTitle(), list.get(position).getContent(), list.get(position).getDate(), list.get(position).getPriority(), listener, holder.getAdapterPosition());
            }
        });

        binding.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.deleteData(list.get(position).getId(), holder.getAdapterPosition(), listener);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewNote.class);
                intent.putExtra("title", list.get(position).getTitle());
                intent.putExtra("content", list.get(position).getContent());
                intent.putExtra("isDone", list.get(position).getIsDone());
                intent.putExtra("ID", list.get(position).getId());
                intent.putExtra("date", list.get(position).getDate());
                intent.putExtra("priority", list.get(position).getPriority());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ItemsLayoutBinding binding;
        SQLiteDatabase sqLiteDatabase;
        Database database;
        Handler handler;

        public MyHolder(@NonNull ItemsLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void updateData(int id, String title, String content, String date, int priority, OnItemClickListener listener, int position) {
            database = new Database(context);
            ContentValues cv = new ContentValues();
            cv.put("title", title);
            cv.put("content", content);
            cv.put("isDone", 1);
            cv.put("date", date);
            cv.put("priority", priority);

            sqLiteDatabase = database.getReadableDatabase();
            long checkUpdate = sqLiteDatabase.update(TABLE_NAME, cv, "id=" + id, null);
            if (checkUpdate != -1) {
                Toast.makeText(context, "Mark as Done!", Toast.LENGTH_SHORT).show();
                listener.onItemClick(new NoteModel());
            } else {
                Toast.makeText(context, "Unexpected Error!", Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteData(int id, int position, OnItemClickListener listener) {
            database = new Database(context);
            sqLiteDatabase = database.getReadableDatabase();
            long delete = sqLiteDatabase.delete(TABLE_NAME, "id=" + id, null);
            if (delete != -1) {
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
//                list.remove(position);
                listener.onItemClick(new NoteModel());
            } else {
                Toast.makeText(context, "Unexpected Error!", Toast.LENGTH_SHORT).show();
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
}
