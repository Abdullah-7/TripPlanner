package com.travel.abd.tripplanner;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by albargi on 4/24/2017.
 */

public class Day extends SugarRecord implements Parcelable {

    private long id;
    private Date date;

    //test
    private ArrayList<Task> tasks = new ArrayList<Task>();

    public Day(){

    }

    public Day(Parcel in){
        date = new Date(in.readLong());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(date.getTime());
    }

    public static final Parcelable.Creator<Day> CREATOR =
            new Parcelable.Creator<Day>() {
                public Day createFromParcel(Parcel in) {
                    return new Day(in);
                }

                public Day[] newArray(int size) {
                    return new Day[size];
                }
            };

    public String toString(){
        return "\n\t\tDate: " + date.toString();
    }
}
