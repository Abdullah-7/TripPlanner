package com.travel.abd.tripplanner;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by albargi on 4/26/2017.
 */

public class FileReader {

    public static JSONArray getJSONArray(Activity activity) throws JSONException {
        String result = loadJSONFromAsset(activity);
        return new JSONArray(result);
    }

    public static String loadJSONFromAsset(Activity activity) {
        String json = null;
        try {

            InputStream is = activity.getAssets().open("countries_iso_list.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
