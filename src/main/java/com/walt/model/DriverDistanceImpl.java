package com.walt.model;

import javax.persistence.ManyToOne;

public class DriverDistanceImpl implements DriverDistance{

    @ManyToOne
    Driver driver;
    long totalDistance;

    public DriverDistanceImpl(Driver d){
        driver = d;
        totalDistance = (long) d.getDistance();
    }

    public Driver getDriver(){
        return driver;
    }

    public Long getTotalDistance(){
        return totalDistance;
    }
}

