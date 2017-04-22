package com.travel.abd.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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


        Destination d = new Destination();
        d.setName("Budapest");
        d.setCost(155);
        d.setCurrency("$");
        list.add(d);

        // List View
        listView = (ListView) findViewById(R.id.destinationList);
        destinationAdapter = new DestinationAdapter(getBaseContext(), R.layout.destination_item, list);
        listView.setAdapter(destinationAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Destination destination = new Destination();
                destination.setName(data.getStringExtra("name"));
//                destination.setFrom(date.);
//                String name = data.getStringExtra("name");
                list.add(destination);
                destinationAdapter.notifyDataSetChanged();
//                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
