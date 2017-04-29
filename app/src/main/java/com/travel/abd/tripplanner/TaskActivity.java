package com.travel.abd.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity {

    private TextView name;
    private TextView time;
    private TextView cost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();

        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        cost = (TextView) findViewById(R.id.cost);

        name.setText(intent.getStringExtra("name"));
        time.setText(intent.getStringExtra("time"));
        cost.setText(intent.getStringExtra("cost"));
    }
}
