package com.travel.abd.tripplanner;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.AsyncTask;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GOOGLE_PLACE_API_KEY = "AIzaSyBIgDpTIPVnzY-unDB3_axxMGLPS4Hl8js";

    private ViewPager viewPager;
    private ViewPagerAdaper viewPagerAdaper;
    private FloatingActionButton fab;
    private TextView total;

    private Intent resultData;

    private Destination destination;
    private ArrayList<Day> days;

    private long destinationId;

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

        getIntentData();

        final ActionBar actionBar = getSupportActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//        Intent intent = getIntent();
//        days = intent.getParcelableArrayListExtra("days");
//        Day d = days.get(0);
//        d.setTasks(tasks1);
//        days.set(0, d);
//        d = days.get(1);
//        d.setTasks(tasks2);
//        days.set(1, d);
//        Toast.makeText(this, days.size()+"", Toast.LENGTH_SHORT).show();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdaper = new ViewPagerAdaper(this, days, tasks1);
        viewPager.setAdapter(viewPagerAdaper);
        viewPager.setOnPageChangeListener(this);
        viewPagerAdaper.notifyDataSetChanged();
        viewPagerAdaper.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                int dayIndex = viewPager.getCurrentItem();
                setTotal(dayIndex);

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

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this, "" + destination.getName(), Toast.LENGTH_SHORT).show();
//        days = (ArrayList<Day>) Day.find(Day.class, "destination_Id = ?", String.valueOf(destinationId));
//        if(days.size() > 0){
//            for (Day day: days) {
////                Log.d("dayId", day.getId() + "");
//                day.setTasks((ArrayList<Task>)Task.find(Task.class, "day_Id = ?", String.valueOf(day.getId())));
//                days.set(days.indexOf(day), day);
//            }
//        }
//        if(viewPagerAdaper != null)
//            viewPagerAdaper.notifyAdapter(viewPager.getCurrentItem());
    }

    private void getIntentData(){
//        Task.deleteAll(Task.class);
        Intent intent = getIntent();
        destinationId = intent.getLongExtra("destination", 0);
        destination = Destination.findById(Destination.class, destinationId);
        setTitle(destination.getName());
        days = (ArrayList<Day>) Day.find(Day.class, "destination_Id = ?", String.valueOf(destinationId));
        if(days.size() > 0){
            for (Day day: days) {
//                Log.d("dayId", day.getId() + "");
                day.setTasks((ArrayList<Task>)Task.find(Task.class, "day_Id = ?", String.valueOf(day.getId())));
                days.set(days.indexOf(day), day);
            }
        }

        Log.d("destination", destination.getName());

//        List<Task> tasks = Task.listAll(Task.class);
//        for (Task t: tasks) {
//            Log.d("Task", t.toString());
//        }
    }

    private void setTotal(int dayIndex){
        double totalValue = 0;
        Day day = days.get(dayIndex);
        List<Task> tasks = Task.find(Task.class, "day_Id = ?", String.valueOf(day.getId()));
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
            intent.putExtra("destination", destinationId);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                resultData = data;
//                Task task = new Task();
//                task.setName(data.getStringExtra("name"));
//                task.setPrice(data.getDoubleExtra("price", 0));
////                Toast.makeText(this, task.getPrice()+"", Toast.LENGTH_SHORT).show();
//                Day day = days.get(viewPager.getCurrentItem());
//                ArrayList<Task> tasks = day.getTasks();
//                tasks.add(task);
//                day.setTasks(tasks);

                new GetPlaceDetails().execute(data.getStringExtra("placeId"));
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

    private class GetPlaceDetails extends AsyncTask<String, Void, Destination> {

        private HttpURLConnection conn = null;
        private StringBuilder jsonResults = new StringBuilder();

        @Override
        protected Destination doInBackground(String... strings) {
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
                sb.append("?placeid=" + URLEncoder.encode(strings[0], "utf8"));
                sb.append("&key=" + GOOGLE_PLACE_API_KEY);

                URL url = new URL(sb.toString());

                System.out.println("URL: "+url);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                try {
                    final JSONObject jsonObject = new JSONObject(jsonResults.toString());
                    JSONObject result = (JSONObject)  jsonObject.get("result");
                    JSONArray address = result.getJSONArray("address_components");
                    String name = result.get("name").toString();
                    String phoneNumber = null;
                    if(result.has("international_phone_number"))
                        phoneNumber = result.get("international_phone_number").toString();
                    String placeId = result.get("place_id").toString();
                    String website = null;
                    if(result.has("website"))
                        website = result.get("website").toString();
                    String formattedAddress = result.get("formatted_address").toString();
                    JSONObject geometry = (JSONObject) result.get("geometry");
                    JSONObject location = (JSONObject) geometry.get("location");
                    JSONObject northeast = (JSONObject)((JSONObject) geometry.get("viewport")).get("northeast");
                    double lat1 = location.getDouble("lat");
                    double lng1 = location.getDouble("lng");
                    double lat2 = northeast.getDouble("lat");
                    double lng2 = northeast.getDouble("lng");

                    Location location1 = new Location("");
                    location1.setLatitude(lat1);
                    location1.setLongitude(lng1);

                    //
                    final Task task = new Task();
                    task.setPlaceId(placeId);
                    task.setName(name);
                    task.setCity(destination.getName());
                    task.setCountry(destination.getCountry());
                    task.setTime(resultData.getStringExtra("time"));
                    task.setWebsite(website);
                    task.setPhoneNumber(phoneNumber);
                    task.setPrice(resultData.getDoubleExtra("price", 0));
                    task.setBudget(resultData.getDoubleExtra("budget", 0));
                    task.setLat(lat1);
                    task.setLng(lng1);
                    task.setAddress(formattedAddress);
                    Log.d("Task", task.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Day day = days.get(viewPager.getCurrentItem());
                            task.setDayId(day.getId());
                            task.save();
                            ArrayList<Task> tasks = day.getTasks();
                            tasks.add(Task.findById(Task.class, task.getId()));
                            day.setTasks(tasks);
                            days.set(viewPager.getCurrentItem(), day);
                            viewPagerAdaper.notifyDataSetChanged();
                        }
                    });

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error processing Places API URL", e);
                return null;
            } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }
    }
}
