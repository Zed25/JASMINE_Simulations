package com.company.model.job;

public abstract class Job {
    protected ClassType classType;    //the job class type (1 or 2)
    //protected EventType eventType;    //the job type (arrival or departure)
    protected double arrivalTime;     //When the job arrives in the node
    protected double scheduledTime;   //When the job is scheduled in next job simulation
                                    //If it is an arrival job this time would be the same of arrival time
                                    //If it is a departure job it would be the time which the job leaves the node

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
