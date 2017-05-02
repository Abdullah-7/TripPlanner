package com.travel.abd.tripplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albargi on 4/20/2017.
 */

public class DestinationAdapter extends ArrayAdapter<Destination> {


    Context context;
    public DestinationAdapter(Context context, int resource) {
        super(context, resource);
    }

    public DestinationAdapter(Context context, int resource, List<Destination> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Destination item view
        View view = convertView;

        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.destination_item, parent, false);
        }
        Log.v("Destination", getItem(position).getName() + position);
        if(view != null) {
            final TextView name = (TextView) view.findViewById(R.id.destination);
            name.setText(getItem(position).getName() + ", " + getItem(position).getCountry());

//            Log.d("Cost: ", "Value: " +getItem(position).getCost()+"");
//            Log.d("Currency: ", "Value: " +getItem(position).getCurrency().toString());
//            Log.d("Compare", Double.compare(0, getItem(position).getCost()) + " " + !getItem(position).getCurrency().equals(""));
            if(Double.compare(0, getItem(position).getCost()) != 0 && !getItem(position).getCurrency().equals("")) {
                TextView cost = (TextView) view.findViewById(R.id.cost);
                cost.setText(getItem(position).getCost()+"");
                TextView currency = (TextView) view.findViewById(R.id.currency);
                currency.setText(getItem(position).getCurrency().substring(0, 3));
            }
            ImageView delete = (ImageView) view.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getRootView().getContext())
                            .setTitle("Delete, are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    List<Day> days = Day.find(Day.class, "destination_Id = ?", String.valueOf(getItem(position).getId()));
                                    Log.d("DeleteItems", "\n");
                                    for (Day day: days) {
                                        Log.d("DeleteItems", day.getId().toString());
                                        List<Task> tasks = Task.find(Task.class, "day_Id = ?", String.valueOf(day.getId()));
                                        for (Task task: tasks) {
                                            Log.d("DeleteItems", "\t\t" + task.getId().toString());
                                            Task.delete(task);
                                        }
                                        Day.delete(day);
                                    }
                                    Destination.delete(getItem(position));
                                    DestinationAdapter.this.remove(getItem(position));
                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();
                }
            });
        }
        return view;
    }
}
