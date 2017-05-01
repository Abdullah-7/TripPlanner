package com.travel.abd.tripplanner;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by albargi on 4/20/2017.
 */

public class Destination extends SugarRecord implements Parcelable {

    private int id;
    private String placeId = "";
    private String name;
    private String country;
    private Location location;
    private String countryCode = "";
    private Date fromDate;
    private Date toDate;
//    private ArrayList<Day> days = new ArrayList<Day>();
    private double budget;
    private double cost;
    private String currency = "";

    public Destination(){}

    public Destination(Parcel in){
        placeId = in.readString();
        name = in.readString();
        country = in.readString();
        location = Location.CREATOR.createFromParcel(in);
        countryCode = in.readString();
        fromDate = new Date(in.readLong());
        toDate = new Date(in.readLong());
//        in.readTypedList(days, Day.CREATOR);
        budget = in.readDouble();
        cost = in.readDouble();
        currency = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeId);
        parcel.writeString(name);
        parcel.writeString(country);
        location.writeToParcel(parcel, i);
        parcel.writeString(countryCode);
        parcel.writeLong(fromDate.getTime());
        parcel.writeLong(toDate.getTime());
//        parcel.writeTypedList(days);
        parcel.writeDouble(budget);
        parcel.writeDouble(cost);
        parcel.writeString(currency);
    }

    public static final Parcelable.Creator<Destination> CREATOR =
            new Parcelable.Creator<Destination>() {
                public Destination createFromParcel(Parcel in) {
                    return new Destination(in);
                }

                public Destination[] newArray(int size) {
                    return new Destination[size];
                }
            };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFrom() {
        return fromDate;
    }

    public void setFrom(Date from) {
        this.fromDate = from;
    }

    public Date getTo() {
        return toDate;
    }

    public void setTo(Date to) {
        this.toDate = to;
    }

//    public ArrayList<Day> getDays() {
//        return days;
//    }
//
//    public void setDays(ArrayList<Day> days) {
//        this.days = days;
//    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String toString(){
        return  "Place ID: " + placeId +
                "\nName: " + name +
                "\nCountry: " + country +
                "\nCountry Code: " + countryCode +
                "\nFrom: " + fromDate +
                "\nTo: " + toDate +
                "\nCurrency: " + currency +
                "\nCost: " + cost +
                "\nBudget: " + budget +
//                "\nDays: " + android.text.TextUtils.join(",", days) +
                "\nLocation: " +location.toString();
    }
}
