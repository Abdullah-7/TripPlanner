package com.travel.abd.tripplanner;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TimePicker;
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
import java.sql.Time;
import java.util.ArrayList;

public class AddTask extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GOOGLE_PLACE_API_KEY = "AIzaSyBIgDpTIPVnzY-unDB3_axxMGLPS4Hl8js";

    private AutoCompleteTextView name;
    private EditText time;
    private EditText budget;
    private EditText price;
    private Button add;

    private static Destination destination;

    private JSONArray countriesCodes;

    private static JSONArray predsJsonArray;

    private static String cityname;
    private static String countryname;
    private static String countryCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
    }

    private void initViews() {

        getIntentData();

        name = (AutoCompleteTextView) findViewById(R.id.name);
        time = (EditText) findViewById(R.id.time);
        price = (EditText) findViewById(R.id.price);
        budget = (EditText) findViewById(R.id.budget);
        add = (Button) findViewById(R.id.add);

        name.setAdapter(new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
        time.setInputType(InputType.TYPE_NULL);
        time.setOnFocusChangeListener(this);
        add.setOnClickListener(this);

        //
        try{
            countriesCodes = FileReader.getJSONArray(this);
            for (int i = 0 ; i < countriesCodes.length() ; i++){
                JSONObject code = countriesCodes.getJSONObject(i);
                if(code.get("Name").equals(countryname)){
                    Log.d("countryCode", countryname);
                    Log.d("countryCode", code.get("Name").toString() + " - " + code.get("Code").toString());
                    countryCode = code.get("Code").toString();
                    break;
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void getIntentData(){
        Intent intent = getIntent();

        destination = intent.getParcelableExtra("destination");
        cityname = intent.getStringExtra("city");
        countryname = intent.getStringExtra("country");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == add.getId()){
            Intent intent = new Intent();
            intent.putExtra("name", name.getText().toString());
            intent.putExtra("time", time.getText().toString());
            intent.putExtra("placeId", getPlaceId());
            double priceValue = 0;
            if(!price.getText().toString().equals(""))
                priceValue = Double.parseDouble(price.getText().toString());
            double budgetValue = 0;
            if(!budget.getText().toString().equals(""))
                budgetValue = Double.parseDouble(budget.getText().toString());
            intent.putExtra("price", priceValue);
            intent.putExtra("budget", budgetValue);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddTask.this, this, 0, 0, true);
            timePickerDialog.show();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        int hour = timePicker.getHour();
        int min = timePicker.getMinute();
        time.setText(hour+":"+min);
//        Toast.makeText(this, timePicker.getHour() + ":" + timePicker.getMinute(), Toast.LENGTH_SHORT).show();
    }

    private String getPlaceId(){
        for (int i = 0 ; i < predsJsonArray.length() ; i++) {
            try {
                if(predsJsonArray.getJSONObject(i).getString("description").equals(name.getText().toString())){
                    String placeID = predsJsonArray.getJSONObject(i).getString("place_id");
                    Log.d("PlaceID", placeID);
                    return placeID;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?input=" + URLEncoder.encode(input, "utf8") + destination.getName());
//            sb.append("&types=(cities)");
//            sb.append("&components=country:hu");
            if(countryCode != null)
                sb.append("&components=country:" + countryCode);
            sb.append("&language=en_US");
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
        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
//                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
//                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
//            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
