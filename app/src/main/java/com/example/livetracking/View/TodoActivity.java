package com.example.livetracking.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.livetracking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    private EditText editTextTask;
    private FloatingActionButton buttonAddTask;
    private ListView listViewTasks;

    // Static list to store tasks
    private static ArrayList<String> taskList;
    private static ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Initialize UI elements
        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        listViewTasks = findViewById(R.id.listViewTasks);

        // Initialize task list
        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        // Add task button click listener
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        // Edit or delete task on item click
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog(position);
            }
        });



    }


    // Function to add a task
    private void addTask() {
        String task = editTextTask.getText().toString();
        if (!task.isEmpty()) {
            taskList.add(task);
            adapter.notifyDataSetChanged();
            editTextTask.setText("");
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to show a dialog to edit or delete a task
    private void showEditDeleteDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Edit or Delete Task");

        final EditText editText = new EditText(this);
        editText.setText(taskList.get(position));
        dialogBuilder.setView(editText);

        dialogBuilder.setPositiveButton("Edit", (dialog, which) -> {
            editTask(position, editText.getText().toString());
        });

        dialogBuilder.setNegativeButton("Delete", (dialog, which) -> {
            deleteTask(position);
        });

        dialogBuilder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    // Function to edit a task
    private void editTask(int position, String newTask) {
        if (!newTask.isEmpty()) {
            taskList.set(position, newTask);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to delete a task
    private void deleteTask(int position) {
        taskList.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
    }
}