package com.travel.abd.tripplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.media.audiofx.Visualizer;
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
import android.widget.DatePicker;
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
    private ListViewAdapter adapter;
    private ArrayList<ListViewAdapter> adapters = new ArrayList<ListViewAdapter>();

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
        View view = (View) LayoutInflater.from(context).inflate(R.layout.task_listview, null);
        listView = (ListView) view.findViewById(R.id.taskListView);
        adapter = new ListViewAdapter(context, R.layout.day_item, items.get(position).getTasks());
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
//                Log.d("Changed", "Data Changed");
//                Day day = items.get(position);
//                day.setTasks(tasks);
//                items.set(position, day);
                notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);
        adapters.add(adapter);
        container.addView(view);
        return view;
    }

    public void notifyAdapter(int position){
        if (adapters.size() > 0)
            adapters.get(position).notifyDataSetChanged();
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
        public View getView(final int position, final View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.day_item, null);
            }
            if(view != null){
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setText(getItem(position).getName());
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TaskActivity.class);
                        intent.putExtra("task", getItem(position).getId());
                        context.startActivity(intent);
                    }
                });
                name.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Delete, are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Task task = (Task) getItem(position);
                                        Log.d("TaskName", task.getName());
                                        Task.delete(task);
                                        ListViewAdapter.this.remove(task);
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create()
                                .show();
                        return false;
                    }
                });
                TextView time = (TextView) view.findViewById(R.id.time);
                time.setText(getItem(position).getTime());
                TextView price = (TextView) view.findViewById(R.id.price);
                if(Double.compare(getItem(position).getPrice(), 0) != 0)
                    price.setText(getItem(position).getPrice()+"");

                CheckBox isVisited = (CheckBox) view.findViewById(R.id.isVisited);
                isVisited.setChecked(getItem(position).isVisited());
                isVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Task task = getItem(position);
                        task.setVisited(b);
                        task.save();
//                        tasks.set(position, task);
                        notifyDataSetChanged();
                    }
                });
            }
            return view;
        }
    }
}
