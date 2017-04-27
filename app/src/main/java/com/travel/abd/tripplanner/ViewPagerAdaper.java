package com.travel.abd.tripplanner;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albargi on 4/25/2017.
 */

public class ViewPagerAdaper extends PagerAdapter {

    private ArrayList<Day> items;
    private ArrayList<Task> tasks;
    private Context context;
    private ListView listView;

    public ViewPagerAdaper(Activity activity, ArrayList<Day> items, ArrayList<Task> tasks){
        this.items = items;
        this.context = activity;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        listView = (ListView) LayoutInflater.from(context).inflate(R.layout.task_listview, null);

        ListViewAdapter adapter = new ListViewAdapter(context, R.layout.day_item, items.get(position).getTasks());
        listView.setAdapter(adapter);
        container.addView(listView);
        return listView;
    }

    private class ListViewAdapter extends ArrayAdapter<Task>{

        public ListViewAdapter(Context context, int resource) {
            super(context, resource);
        }

        public ListViewAdapter(Context context, int resource, List<Task> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.day_item, null);
            }
            if(view != null){
//                TextView name = (TextView) view.findViewById(R.id.name);
//                name.setText(getItem(position).getName());
                TextView price = (TextView) view.findViewById(R.id.price);
                price.setText(getItem(position).getPrice()+"");
            }
            return view;
        }
    }
}
