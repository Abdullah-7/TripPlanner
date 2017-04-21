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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
