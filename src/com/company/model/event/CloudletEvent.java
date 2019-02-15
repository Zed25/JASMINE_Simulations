package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventStatus;

public class CloudletEvent extends Event {

    /* fake event constructor */
    public CloudletEvent() {
        this.nextEventTime = -1.0;
        this.eventStatus = EventStatus.NOT_ACTIVE;
        this.classType = ClassType.NONE;
        this.arrivalTime = -1;
    }

    /* real event constructor */
    public CloudletEvent(double nextEventTime, EventStatus eventStatus, ClassType classType, double arrivalTime) {
        this.nextEventTime = nextEventTime;
        this.eventStatus = eventStatus;
        this.classType = classType;
        this.arrivalTime = arrivalTime;
    }

}
