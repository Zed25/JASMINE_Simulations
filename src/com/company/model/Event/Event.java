package com.company.model.Event;

public abstract class Event {
    private ClassType classType;    //the job class type (1 or 2)
    private EventType eventType;    //the event type (arrival or departure)
    private double arrivalTime;     //When the job arrives in the system
    private double scheduledTime;   //When the event is scheduled in next event simulation
                                    //If it is an arrival event this time would be the same of arrival time
                                    //If it is a departure event it would be the time which the job leaves the system
}
