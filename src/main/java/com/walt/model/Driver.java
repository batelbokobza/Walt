package com.walt.model;

import javax.persistence.*;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Entity
public class Driver extends NamedEntity {

    @ManyToOne
    City city;

    @OneToMany(fetch = FetchType.EAGER)
    private Map<Date, Delivery> infoDeliveries;

    private long distance;

    public Driver() {
    }

    public Driver(String name, City city) {
        super(name);
        this.city = city;
        infoDeliveries = new TreeMap<>();
        distance = 0;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setDeliveriesToDriver(Delivery delivery) {
        infoDeliveries.put(delivery.getDeliveryTime(), delivery);
    }

    public Map<Date, Delivery> getDeliveriesToDriver() {
        return infoDeliveries;
    }

    public int getDeliveriesNumber() {
        return infoDeliveries.size();
    }

    // set/update total driver distance
    public void setDistance(double newDistance) {
        distance += newDistance;
    }

    public double getDistance() {
        return this.distance;
    }

    public boolean isAvailableToDelivery(Date deliveryTime) {
        int HOUR = 1;
        for (Map.Entry<Date, Delivery> entry : infoDeliveries.entrySet()) {
            Date DeliveryEndTimeByDriver = Date.from(entry.getKey().toInstant().plus(Duration.ofHours(HOUR)));
            boolean result = entry.getKey().compareTo(deliveryTime) < 0 && DeliveryEndTimeByDriver.compareTo(deliveryTime) < 0;
            if (!result) {
                return false;
            }
        }
        return true;
    }
}