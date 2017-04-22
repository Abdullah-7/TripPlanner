package com.travel.abd.tripplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Currency;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.Set;

public class AddDestinationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GOOGLE_PLACE_API_KEY = "AIzaSyCh_jbg0EgPTBG7whTZTrpNu7Eyhjc2D9U";

    private AutoCompleteTextView name;
    private EditText from;
    private EditText to;
    private EditText cost;
    private AutoCompleteTextView currnecy;
    private Button add;

    //values
//    private String name;
//    private Date fromDate;
//    private Date toDate;
//    private double estimatedCost;
//    private String currnecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);

        initViews();
    }

    private void initViews() {

        name = (AutoCompleteTextView) findViewById(R.id.name);
        from = (EditText) findViewById(R.id.from);
        to = (EditText) findViewById(R.id.to);
        cost = (EditText) findViewById(R.id.eCost);
        currnecy = (AutoCompleteTextView) findViewById(R.id.currency);

        from.setInputType(InputType.TYPE_NULL);
        to.setInputType(InputType.TYPE_NULL);
        from.setOnFocusChangeListener(onFocusChangeListener);
        to.setOnFocusChangeListener(onFocusChangeListener);

        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);

        name.setAdapter(new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
        name.setOnItemClickListener(this);
        currnecy.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getCurrencyList()));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == add.getId()){
            Intent result = new Intent();
            result.putExtra("name", name.getText().toString());
            result.putExtra("from", from.getText().toString());
            result.putExtra("to", to.getText().toString());
            result.putExtra("eCost", cost.getText().toString());
            result.putExtra("currency", currnecy.getText().toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String str = (String) adapterView.getItemAtPosition(i);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if(from.isFocused()){
            Date fromDate = getDateFromDatePicker(datePicker);
            from.setText(fromDate.toString());
        } else {
            Date toDate = getDateFromDatePicker(datePicker);
            to.setText(toDate.toString());
        }
    }

    public Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private String [] getCurrencyList(){
        String [] currencyList = new String[Currency.getAvailableCurrencies().size()];
        int i = 0;
        for (Currency cr: Currency.getAvailableCurrencies()) {
            currencyList[i++] = (cr.getCurrencyCode() + " - " +cr.getDisplayName());
        }
        return currencyList;
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddDestinationActivity.this);
                datePickerDialog.setOnDateSetListener(AddDestinationActivity.this);
                datePickerDialog.show();
            }
        }
    };

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + GOOGLE_PLACE_API_KEY);
            sb.append("&components=country:gr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

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
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
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
