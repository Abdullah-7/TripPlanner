package com.travel.abd.tripplanner;

import java.util.Date;

/**
 * Created by albargi on 4/20/2017.
 */

public class Destination {

    private int id;
    private String name;
    private Date from;
    private Date to;
    private Date [] days;
    private double cost;
    private String currency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date[] getDays() {
        return days;
    }

    public void setDays(Date[] days) {
        this.days = days;
    }

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
}
