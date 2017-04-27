package com.travel.abd.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private DestinationAdapter destinationAdapter;

    //Test Only
    List<Destination> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), AddDestinationActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            }
        });

        initViews();
    }

    private void initViews() {

        //Test
        Calendar calendar = Calendar.getInstance();
        Destination d = new Destination();
        d.setName("Budapest");
        d.setFrom(calendar.getTime());
        calendar.add(calendar.DATE, 10);
        d.setTo(calendar.getTime());
        d.setCost(155);
        d.setCurrency("USD");
        list.add(d);
        //end of test

        // List View
        listView = (ListView) findViewById(R.id.destinationList);
        destinationAdapter = new DestinationAdapter(getBaseContext(), R.layout.destination_item, list);
        listView.setAdapter(destinationAdapter);
        listView.setOnItemClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Destination destination = new Destination();
                destination.setName(data.getStringExtra("name"));
                destination.setFrom(new Date(data.getStringExtra("from")));
                destination.setTo(new Date(data.getStringExtra("to")));
                destination.setCost(data.getDoubleExtra("eCost", 0));
                destination.setCurrency(data.getStringExtra("currency"));

                list.add(destination);
                destinationAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ArrayList<Day> days = new ArrayList<Day>();
        Destination destination = (Destination) destinationAdapter.getItem(i);

        Date startDate = destination.getFrom();
        Date endDate = destination.getTo();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Day day = new Day();

        while (calendar.getTime().before(endDate)){

            day = new Day();
            day.setDate(calendar.getTime());
            days.add(day);
            calendar.add(calendar.DATE, 1);
//            Log.d("Day", calendar.getTime().toString() + " - " + calendar.getTime().before(endDate));
        }

        for (Day d : days) {
//            Log.d("Day", d.getDate().toString() + " - " + d.getDate().before(endDate));
        }

        Intent intent = new Intent(this, DayActivity.class);
        intent.putExtra("city", destination.getName());
        intent.putParcelableArrayListExtra("days", days);
        startActivity(intent);
    }
}
