package com.travel.abd.tripplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albargi on 4/25/2017.
 */

public class ViewPagerAdaper extends PagerAdapter implements AdapterView.OnItemClickListener {

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
    public Object instantiateItem(ViewGroup container, final int position) {
        listView = (ListView) LayoutInflater.from(context).inflate(R.layout.task_listview, null);
        ListViewAdapter adapter = new ListViewAdapter(context, R.layout.day_item, items.get(position).getTasks());
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
//                Log.d("Changed", "Data Changed");
//                Day day = items.get(position);
//                day.setTasks(tasks);
//                items.set(position, day);
//                notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        container.addView(listView);
        return listView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(context, TaskActivity.class);
        context.startActivity(intent);
    }

    private class ListViewAdapter extends ArrayAdapter<Task>{

        private List<Task> tasks;

        public ListViewAdapter(Context context, int resource) {
            super(context, resource);
        }

        public ListViewAdapter(Context context, int resource, List<Task> objects) {
            super(context, resource, objects);
            this.tasks = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.day_item, null);
            }
            if(view != null){
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setText(getItem(position).getName());
                TextView price = (TextView) view.findViewById(R.id.price);
                price.setText(getItem(position).getPrice()+"");

                CheckBox isVisited = (CheckBox) view.findViewById(R.id.isVisited);
                isVisited.setChecked(getItem(position).isVisited());
                isVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Task task = getItem(position);
                        task.setVisited(b);
                        tasks.set(position, task);
                        notifyDataSetChanged();
                    }
                });
            }
            return view;
        }
    }
}
