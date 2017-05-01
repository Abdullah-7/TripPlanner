package com.travel.abd.tripplanner;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by albargi on 4/25/2017.
 */

public class Task implements Parcelable{

    private long id;
    private String placeId;
    private String name;
    private String time;
    private String city;
    private String country;
    private String currency;
    private String phoneNumber;
    private String website;
    private double budget;
    private double price;
    private boolean isVisited;
    private Location location;

    public Task(){

    }

    public Task(Parcel in){
        placeId = in.readString();
        name = in.readString();
        time = in.readString();
        city = in.readString();
        country = in.readString();
        currency = in.readString();
        phoneNumber = in.readString();
        website = in.readString();
        budget = in.readDouble();
        price = in.readDouble();
        isVisited = (in.readInt() == 1 ? true : false);
        location = Location.CREATOR.createFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeId);
        parcel.writeString(name);
        parcel.writeString(time);
        parcel.writeString(city);
        parcel.writeString(country);
        parcel.writeString(currency);
        parcel.writeString(phoneNumber);
        parcel.writeString(website);
        parcel.writeDouble(budget);
        parcel.writeDouble(price);
        parcel.writeInt((isVisited == true ? 1 : 0));
        location.writeToParcel(parcel, i);
    }

    public static final Parcelable.Creator<Task> CREATOR =
            new Parcelable.Creator<Task>() {
                public Task createFromParcel(Parcel in) {
                    return new Task(in);
                }

                public Task[] newArray(int size) {
                    return new Task[size];
                }
            };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String toString(){
        return placeId + "\n" +
                name + "\n" +
                time + "\n" +
                phoneNumber + "\n" +
                website + "\n" +
                budget + "\n" +
                price + "\n" +
                isVisited + "\n" +
                location.toString();
    }
}
