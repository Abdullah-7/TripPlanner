package com.travel.abd.tripplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Currency;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddDestinationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GOOGLE_PLACE_API_KEY = "AIzaSyBIgDpTIPVnzY-unDB3_axxMGLPS4Hl8js";

    private AutoCompleteTextView name;
    private EditText from;
    private EditText to;
    private EditText cost;
    private AutoCompleteTextView currency;
    private Button add;

    private static JSONArray predsJsonArray;
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
        currency = (AutoCompleteTextView) findViewById(R.id.currency);

        from.setInputType(InputType.TYPE_NULL);
        to.setInputType(InputType.TYPE_NULL);
        from.setOnFocusChangeListener(onFocusChangeListener);
        to.setOnFocusChangeListener(onFocusChangeListener);

        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);

        name.setAdapter(new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
        name.setOnItemClickListener(this);
        currency.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getCurrencyList()));

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == add.getId()){
            if (!validateInput())
                return;
            Intent result = new Intent();
            result.putExtra("name", name.getText().toString());
            result.putExtra("placeId", getPlaceId());
            result.putExtra("from", from.getText().toString());
            result.putExtra("to", to.getText().toString());
            double costValue = 0;
            if(!cost.getText().toString().equals(""))
                costValue = Double.parseDouble(cost.getText().toString());
            result.putExtra("eCost", costValue);
            result.putExtra("currency", currency.getText().toString());
            setResult(Activity.RESULT_OK, result);
            finish();

        }
    }

    private boolean validateInput() {
        if(name.getText().length() == 0){
            Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (from.getText().length() == 0){
            Toast.makeText(this, "Please enter start date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (to.getText().length() == 0){
            Toast.makeText(this, "please enter end date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String str = (String) adapterView.getItemAtPosition(i);
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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
            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&types=(cities)");
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
