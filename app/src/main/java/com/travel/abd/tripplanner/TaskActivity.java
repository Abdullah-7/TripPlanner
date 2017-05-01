package com.travel.abd.tripplanner;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String GOOGLE_MAP_PACKAGE = "com.google.android.apps.maps";
    private static final String GOOGLE_MAP_REQUEST = "";
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.call){
            Log.d("PhoneNumber", task.getPhoneNumber());
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + task.getPhoneNumber()));
            startActivity(intent);

        } else if(view.getId() == R.id.website){
            Log.d("Website", task.getWebsite());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(task.getWebsite()));
            if(intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);

        } else if(view.getId() == R.id.direction){
            Log.d("Location", task.getLocation().toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("google.navigation:q=" + task.getName() + ", Budapest+Hungary"));
            intent.setPackage(GOOGLE_MAP_PACKAGE);
            if(intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        }
    }
}
