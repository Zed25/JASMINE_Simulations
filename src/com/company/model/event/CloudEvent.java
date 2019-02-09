package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;

public class CloudEvent extends CloudletEvent {

    /* fake event constructor */
    public CloudEvent() {
        super();
    }

    /* real event constructor */
    public CloudEvent(double nextEventTime, double arrivalTime, EventStatus eventStatus, ClassType classType) {
        super(nextEventTime, arrivalTime, eventStatus, classType);
    }
}
