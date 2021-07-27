package com.walt.exception;

import com.walt.model.City;

public class NotSameCityException extends Exception {
    public NotSameCityException() { }

    public NotSameCityException(City city1, City city2)
    {
        System.out.println("Exception: Cities are not the same.\n" +
                "The customer is from " + city1.getName() +
                " and restaurant locate at " + city2.getName() + " ."+
                " order failed!\n");
    }
}