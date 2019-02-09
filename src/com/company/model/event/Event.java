package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;

public abstract class Event {
    protected double nextEventTime; /* next event time */
    protected double arrivalTime; /* time of arrival in system */
    protected EventStatus eventStatus; /* event status, ACTIVE, NOT_ACTIVE */
    //protected EventLocation eventLocation; /* event location, CLOUDLET, CLOUD, NONE */
    protected ClassType classType; /* job class type, CLASS1, CLASS2, NONE */

    public double getNextEventTime() {
        return nextEventTime;
    }

    public void setNextEventTime(double nextEventTime) {
        this.nextEventTime = nextEventTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
}
