package com.company.model.event;

public abstract class Job {
    protected ClassType classType;    //the job class type (1 or 2)
    //protected EventType eventType;    //the event type (arrival or departure)
    protected double arrivalTime;     //When the job arrives in the system
    protected double scheduledTime;   //When the event is scheduled in next event simulation
                                    //If it is an arrival event this time would be the same of arrival time
                                    //If it is a departure event it would be the time which the job leaves the system

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(double scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
