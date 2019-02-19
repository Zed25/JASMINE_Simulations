package com.company.model.event;

import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventStatus;
import com.company.model.event.enumeration.HyperexpPhaseType;

public class CloudletEvent extends Event {

    private HyperexpPhaseType hyperexpPhase; /* hyperexponential servise phase,
                                                relevant only if cloudlet service is hyperexponential */

    /* fake event constructor */
    public CloudletEvent() {
        this.nextEventTime = -1.0;
        this.eventStatus = EventStatus.NOT_ACTIVE;
        this.classType = ClassType.NONE;
        this.arrivalTime = -1;
        this.hyperexpPhase = HyperexpPhaseType.NONE;
    }

    /* real event constructor */
    public CloudletEvent(double nextEventTime, EventStatus eventStatus, ClassType classType, double arrivalTime, HyperexpPhaseType hyperexpPhase) {
        this.nextEventTime = nextEventTime;
        this.eventStatus = eventStatus;
        this.classType = classType;
        this.arrivalTime = arrivalTime;
        this.hyperexpPhase = hyperexpPhase;
    }

    public HyperexpPhaseType getHyperexpPhase() {
        return hyperexpPhase;
    }

    public void setHyperexpPhase(double phase) {
        if (phase == 1) {
            this.hyperexpPhase = HyperexpPhaseType.PHASE_1;
        } else if (phase == 2) {
            this.hyperexpPhase = HyperexpPhaseType.PHASE_2;
        } else {
            this.hyperexpPhase = HyperexpPhaseType.NONE;
        }
    }
}
