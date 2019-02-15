package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventStatus;

public class CloudEvent extends CloudletEvent {

    boolean interrupdetJob = false; /* check if is an interrupted job event
                                       default value is false */

    /* fake event constructor */
    public CloudEvent() {
        super();
    }

    /* real event constructor */
    public CloudEvent(double nextEventTime, EventStatus eventStatus, ClassType classType, double arrivalTime) {
        super(nextEventTime, eventStatus, classType, arrivalTime);
    }

    public boolean isInterrupdetJob() {
        return interrupdetJob;
    }

    public void setInterrupdetJob(boolean interrupdetJob) {
        this.interrupdetJob = interrupdetJob;
    }
}
