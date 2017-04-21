package com.travel.abd.tripplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by albargi on 4/20/2017.
 */

public class DestinationAdapter extends ArrayAdapter<Destination> {
    public DestinationAdapter(Context context, int resource) {
        super(context, resource);
    }

    public DestinationAdapter(Context context, int resource, List<Destination> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Destination item view
        View view = convertView;

        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            layoutInflater.inflate(R.layout.destination_item, null);
        }
        return view;
    }
}
