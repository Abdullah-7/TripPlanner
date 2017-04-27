package com.travel.abd.tripplanner;

import android.content.Context;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Destination item view
        View view = convertView;

        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.destination_item, parent, false);
        }
        Log.v("Destination", getItem(position).getName() + position);
        if(view != null) {
            TextView name = (TextView) view.findViewById(R.id.destination);
            name.setText(getItem(position).getName());

//            Log.d("Cost: ", "Value: " +getItem(position).getCost()+"");
//            Log.d("Currency: ", "Value: " +getItem(position).getCurrency().toString());
//            Log.d("Compare", Double.compare(0, getItem(position).getCost()) + " " + !getItem(position).getCurrency().equals(""));
            if(Double.compare(0, getItem(position).getCost()) != 0 && !getItem(position).getCurrency().equals("")) {
                TextView cost = (TextView) view.findViewById(R.id.cost);
                cost.setText(getItem(position).getCost()+"");
                TextView currency = (TextView) view.findViewById(R.id.currency);
                currency.setText(getItem(position).getCurrency().substring(0, 3));
            }
        }
        return view;
    }
}
