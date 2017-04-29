package com.travel.abd.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GOOGLE_PLACE_API_KEY = "AIzaSyBIgDpTIPVnzY-unDB3_axxMGLPS4Hl8js";

    private ListView listView;
    private DestinationAdapter destinationAdapter;

    private Destination destinationAdd;

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
                destinationAdd = new Destination();
                destinationAdd.setName(data.getStringExtra("name"));
                destinationAdd.setFrom(new Date(data.getStringExtra("from")));
                destinationAdd.setTo(new Date(data.getStringExtra("to")));
                destinationAdd.setCost(data.getDoubleExtra("eCost", 0));
                destinationAdd.setCurrency(data.getStringExtra("currency"));

                new GetPlaceDetails().execute(data.getStringExtra("placeId"));
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
                    String city = address.getJSONObject(0).getString("long_name");
                    String country = address.getJSONObject(2).getString("long_name");
                    Log.d("City", address.getJSONObject(0).getString("long_name"));
                    Log.d("City", address.getJSONObject(2).getString("long_name"));
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

                    destinationAdd.setName(city);
                    destinationAdd.setCountry(country);
                    destinationAdd.setLocation(location1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            list.add(destinationAdd);
                            destinationAdapter.notifyDataSetChanged();
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
