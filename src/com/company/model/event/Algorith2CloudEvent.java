package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;

public class Algorith2CloudEvent extends CloudEvent {
    private boolean interrupted; /* set to true if it represent a job interrupted in the cloudlet */

    /* fake event constructor */
    public Algorith2CloudEvent() {
        super();
        this.interrupted = false;
    }

    /* real event constructor */
    public Algorith2CloudEvent(double nextEventTime, double arrivalTime, EventStatus eventStatus, ClassType classType, boolean interrupted) {
        super(nextEventTime, arrivalTime,eventStatus, classType);
        this.interrupted = interrupted;
    }
}
