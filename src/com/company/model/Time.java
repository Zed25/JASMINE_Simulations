package com.company.model;

public class Time {

    private double current;                /* current time */
    private double next;                   /* next (most imminent) event time */

    public Time() {
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getNext() {
        return next;
    }

    public void setNext(double next) {
        this.next = next;
    }
}
