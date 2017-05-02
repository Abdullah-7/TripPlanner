package com.travel.abd.tripplanner;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener{

    private static final String GOOGLE_MAP_PACKAGE = "com.google.android.apps.maps";
    private static final String GOOGLE_MAP_REQUEST = "";
    private EditText name;
    private EditText time;
    private EditText cost;
    private EditText budget;

    private Task task;
    private long taskId;

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

        time.setInputType(InputType.TYPE_NULL);
        time.setOnFocusChangeListener(this);

        if(task.getPhoneNumber() == null)
            ((Button) findViewById(R.id.call)).setVisibility(View.INVISIBLE);
        if(task.getWebsite() == null)
            ((Button) findViewById(R.id.website)).setVisibility(View.INVISIBLE);
    }


    public void getIntentData() {
        Intent intent = getIntent();
        taskId = intent.getLongExtra("task", 0);
        task = Task.findById(Task.class, taskId);
        Log.d("Task", task.toString());
    }

    @Override
    public void onBackPressed() {
        if(!task.getTime().equals(time.getText().toString()) ||
                Double.compare(Double.parseDouble(cost.getText().toString()), task.getPrice()) != 0 ||
                Double.compare(Double.parseDouble(budget.getText().toString()), task.getBudget()) != 0){
            new AlertDialog.Builder(this)
                    .setTitle("Save changes?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            task.setTime(time.getText().toString());
                            task.setPrice(Double.parseDouble(cost.getText().toString()));
                            task.setBudget(Double.parseDouble(budget.getText().toString()));
                            task.save();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .create()
                    .show();

        } else {
            finish();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            int h = 0;
            if(!TextUtils.isEmpty(time.getText()))
                h = Integer.parseInt(time.getText().toString().substring(0, time.getText().toString().indexOf(":")));
            int m = 0;
            if(!TextUtils.isEmpty(time.getText()))
                m = Integer.parseInt(time.getText().toString().substring(time.getText().toString().indexOf(":")+1));
            TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this, this, h, m, true);
            timePickerDialog.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        int hour = timePicker.getHour();
        int min = timePicker.getMinute();
        String h = String.valueOf(hour);
        if(h.length() == 1)
            h = "0" + h;
        String m = String.valueOf(min);
        if(m.length() == 1)
            m = "0" + m;
        time.setText(h+":"+m);
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
            Location location = new Location(task.getName());
            location.setLatitude(task.getLat());
            location.setLongitude(task.getLng());
            Log.d("Location", location.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("google.navigation:q=" + task.getAddress()));
            intent.setPackage(GOOGLE_MAP_PACKAGE);
            if(intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        }
    }
}
