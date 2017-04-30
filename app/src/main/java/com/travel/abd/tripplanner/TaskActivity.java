package com.travel.abd.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity {

    private EditText name;
    private EditText time;
    private EditText cost;
    private EditText budget;

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initViews();
    }

    private void initViews() {

        getIntentData();

        name = (EditText) findViewById(R.id.name);
        time = (EditText) findViewById(R.id.time);
        cost = (EditText) findViewById(R.id.cost);
        budget = (EditText) findViewById(R.id.budget);

        name.setText(task.getName());
        time.setText(task.getTime());
        cost.setText(String.valueOf(task.getPrice()));
        budget.setText(String.valueOf(task.getBudget()));
    }


    public void getIntentData() {
        Intent intent = getIntent();
        task = intent.getParcelableExtra("task");
    }
}
