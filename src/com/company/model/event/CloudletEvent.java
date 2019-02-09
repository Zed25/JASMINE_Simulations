package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;

public class CloudletEvent extends Event{

    /* fake event constructor */
    public CloudletEvent() {
        this.nextEventTime = -1.0;
        this.arrivalTime = -1.0;
        this.eventStatus = EventStatus.NOT_ACTIVE;
        //this.eventLocation = EventLocation.NONE;
        this.classType = ClassType.NONE;
    }

    /* real event constructor */
    public CloudletEvent(double nextEventTime, double arrivalTime, EventStatus eventStatus, ClassType classType) {
        this.nextEventTime = nextEventTime;
        this.arrivalTime = arrivalTime;
        this.eventStatus = eventStatus;
        //this.eventLocation = eventLocation;
        this.classType = classType;
    }
}
