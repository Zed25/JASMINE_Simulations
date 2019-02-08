package com.company.model;

public class Time {
    private double arrival[];              /* next arrival time, 2 entry : arrival[0] = type 1 arrival, arrival[1] = type 2 arrival */
    private double departure;              /* next departure time */
    private double cloudDeparture;         /* next cloud departure time */
    private double current;                /* current time                        */
    private double next;                   /* next (most imminent) event time     */
    private double last[];                 /* last arrival time, 2 entry : last[0] = last type 1 arrival, last[1] = last type 2 arrival*/

    public Time(){}

    public double[] getArrival() {
        return arrival;
    }

    public void setArrival(double[] arrival) {
        this.arrival = arrival;
    }

    public double getDeparture() {
        return departure;
    }

    public void setDeparture(double departure) {
        this.departure = departure;
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

    public double[] getLast() {
        return last;
    }

    public void setLast(double[] last) {
        this.last = last;
    }

    public void setType1Arrival(double value) {
        this.arrival[0] = value;
    }

    public void setType2Arrival(double value) {
        this.arrival[1] = value;
    }

    public void setType1Last(double value) {
        this.last[0] = value;
    }

    public void setType2Last(double value) {
        this.last[1] = value;
    }

    public double getCloudDeparture() {
        return cloudDeparture;
    }

    public void setCloudDeparture(double cloudDeparture) {
        this.cloudDeparture = cloudDeparture;
    }
}
