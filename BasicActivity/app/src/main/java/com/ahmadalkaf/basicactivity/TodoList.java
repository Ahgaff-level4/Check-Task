package com.ahmadalkaf.basicactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class TodoList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
//        TextView title = findViewById(R.id.textViewTitle);
//        TextView desc = findViewById(R.id.textViewDesc);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String t = extras.getString("title").toString();
            String d = extras.getString("description").toString();
//            title.setText(t);
//            desc.setText(d);
        }
    }
}