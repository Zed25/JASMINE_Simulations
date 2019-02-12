package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventStatus;

public class CloudEvent extends CloudletEvent {

    /* fake event constructor */
    public CloudEvent() {
        super();
    }

    /* real event constructor */
    public CloudEvent(double nextEventTime, EventStatus eventStatus, ClassType classType) {
        super(nextEventTime, eventStatus, classType);
    }
}
