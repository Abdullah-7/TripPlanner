package com.travel.abd.tripplanner;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DayActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private ViewPager viewPager;
    private ViewPagerAdaper viewPagerAdaper;
    private FloatingActionButton fab;
    private TextView total;

    private ArrayList<Day> days;

    // Test only
    private ArrayList<Task> tasks1 = new ArrayList<Task>();
    private ArrayList<Task> tasks2 = new ArrayList<Task>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        initViews();
    }

    private void initViews() {
        //Test Only
        Task t = new Task();
        t.setName("A1");
        t.setPrice(10);
        tasks1.add(t);
        t = new Task();
        t.setName("A2");
        t.setPrice(15);
        tasks1.add(t);

        Task t2 = new Task();
        t2.setName("B1");
        t2.setPrice(7);
        tasks2.add(t2);
        t2 = new Task();
        t2.setName("B2");
        t2.setPrice(9);
        tasks2.add(t2);
        // end of test

        final ActionBar actionBar = getSupportActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Intent intent = getIntent();
        days = intent.getParcelableArrayListExtra("days");
        Day d = days.get(0);
        d.setTasks(tasks1);
        days.set(0, d);
        d = days.get(1);
        d.setTasks(tasks2);
        days.set(1, d);
//        Toast.makeText(this, days.size()+"", Toast.LENGTH_SHORT).show();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdaper = new ViewPagerAdaper(this, days, tasks1);
        viewPager.setAdapter(viewPagerAdaper);
        viewPager.setOnPageChangeListener(this);
        viewPagerAdaper.notifyDataSetChanged();
        viewPagerAdaper.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                //setTotal(viewPager.getCurrentItem());
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        total = (TextView) findViewById(R.id.total);

        int index = 0;

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < days.size(); i++) {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");

            Calendar c  = Calendar.getInstance();
            Date today = new Date(format.format(c.getTime()));
            c.add(c.DATE, -1);
            Date yesterday = new Date(format.format(c.getTime()));
            c.add(c.DATE, 2);
            Date tomorrow = new Date(format.format(c.getTime()));
            String date = format.format(days.get(i).getDate());

            if(date.equals(format.format(today))) {
                date = "Today";
                index = i;
            }
            else if(date.equals(format.format(yesterday)))
                date = "Yesterday";
            else if(date.equals(format.format(tomorrow)))
                date = "Tomorrow";

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(date)
                            .setTabListener(tabListener));
        }

        viewPager.setCurrentItem(index, true);
        getSupportActionBar().setSelectedNavigationItem(index);
        setTotal(index);
    }

    private void setTotal(int dayIndex){
        double totalValue = 0;
        Day day = days.get(dayIndex);
        ArrayList<Task> tasks = day.getTasks();
        for (Task t: tasks) {
            if(t.isVisited())
                totalValue += t.getPrice();
        }
        total.setText("Total: " + totalValue);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == fab.getId()){
            Intent intent = new Intent(this, AddTask.class);
            intent.putExtra("city", getIntent().getStringExtra("city"));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Task task = new Task();
                task.setName(data.getStringExtra("name"));
                task.setPrice(data.getDoubleExtra("price", 0));
//                Toast.makeText(this, task.getPrice()+"", Toast.LENGTH_SHORT).show();
                Day day = days.get(viewPager.getCurrentItem());
                ArrayList<Task> tasks = day.getTasks();
                tasks.add(task);
                day.setTasks(tasks);
                days.set(viewPager.getCurrentItem(), day);
                viewPagerAdaper.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
        viewPagerAdaper.notifyDataSetChanged();
        setTotal(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // Create a tab listener that is called when the user changes tabs.
    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
            viewPager.setCurrentItem(tab.getPosition(), true);
            setTotal(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

        }
    };
}
