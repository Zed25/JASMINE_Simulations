package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventStatus;
import com.company.model.event.enumeration.HyperexpPhaseType;

public class CloudEvent extends CloudletEvent {

    boolean interruptedJob = false;         /* check if is an interrupted job event
                                            default value is false */

    /* fake event constructor */
    public CloudEvent() {
        super();
    }

    /* real event constructor */
    public CloudEvent(double nextEventTime, EventStatus eventStatus, ClassType classType, double arrivalTime,
                      HyperexpPhaseType hyperexpPhaseType) {
        super(nextEventTime, eventStatus, classType, arrivalTime, hyperexpPhaseType);
    }

    public boolean isInterruptedJob() {
        return interruptedJob;
    }

    public void setInterruptedJob(boolean interruptedJob) {
        this.interruptedJob = interruptedJob;
    }
}
