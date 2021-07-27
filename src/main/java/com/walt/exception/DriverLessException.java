package com.walt.exception;

import com.walt.model.City;

public class DriverLessException extends Exception{
    public DriverLessException() { }

    public DriverLessException(City city, String deliveryTime) {
        System.out.println("Exception: The order failed.\n"+ "No driver available for delivery on - "+deliveryTime+"" +
                "in the city of - "+city.getName()+".\n");
    }
}
