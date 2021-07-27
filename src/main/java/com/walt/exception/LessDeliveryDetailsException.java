package com.walt.exception;

public class LessDeliveryDetailsException extends Exception {
    public LessDeliveryDetailsException() {
        System.out.println("Exception: One or more specifications are missing to complete the order.\n");
    }
}
