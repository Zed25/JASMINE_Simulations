package com.company.model.utils;

import com.company.configuration.Configuration;
import com.company.model.event.CloudEvent;
import com.company.model.event.CloudletEvent;
import com.company.model.event.NextEventInfo;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;

import java.util.List;

/** ----------------------------------------------------------------------------------------------------------------
 *  ------------------------------------------------   UTILS   -----------------------------------------------------
 *  ----------------------------------------------------------------------------------------------------------------
 *  */
public class SimulatorUtils {
    public SimulatorUtils() {
    }

    /** ---------------------------------------
     * return the index of the next event type
     * ---------------------------------------
     */
    public NextEventInfo nextEvent(CloudletEvent[] cloudletEvents, List<CloudEvent> cloudEvents, double infinityTime) {

        int eventIndex;                                                             /* next event index to return */
        int cloudletScan = 0;                                                       /* cloudlet scan index */
        int cloudScan = 0;                                                          /* cloud scan index */
        int cloudletIndex;                                                          /* cloudlet chosen index */
        int cloudIndex;                                                             /* cloud chosen index */
        double cloudletNextEventTime;
        double cloudNextEventTime;

        EventLocation eventLocation;

        /* find index cloudlet first event */
        while (cloudletEvents[cloudletScan].getEventStatus() == EventStatus.NOT_ACTIVE &&
                cloudletScan < (cloudletEvents.length - 1)) {                      /* find the index of the first 'active'
                                                                               element in the event list */
            cloudletScan++;
        }
        cloudletIndex = cloudletScan;
        while (cloudletScan < (cloudletEvents.length - 1)) {      /* now, check the others to
                                                                    find which event type is most imminent */
            cloudletScan++;
            if ((cloudletEvents[cloudletScan].getEventStatus() == EventStatus.ACTIVE) &&
                    (cloudletEvents[cloudletScan].getNextEventTime() < cloudletEvents[cloudletIndex].getNextEventTime())
                    )
                cloudletIndex = cloudletScan;
        }

        /* compute cloudlet next event time */
        if (cloudletEvents[cloudletIndex].getEventStatus() == EventStatus.NOT_ACTIVE) {
            cloudletNextEventTime = infinityTime;
        } else {
            cloudletNextEventTime = cloudletEvents[cloudletIndex].getNextEventTime();
        }

        if (cloudEvents.size() > 0) {
            /* find index cloud first event */
            while (cloudEvents.get(cloudScan).getEventStatus() == EventStatus.NOT_ACTIVE &&
                    cloudScan < cloudEvents.size() - 1) {
                cloudScan++;
            }
            cloudIndex = cloudScan;
            while (cloudScan < cloudEvents.size() - 1) {
                cloudScan++;
                if (cloudEvents.get(cloudScan).getEventStatus() == EventStatus.ACTIVE &&
                        cloudEvents.get(cloudScan).getNextEventTime() < cloudEvents.get(cloudIndex).getNextEventTime()
                        ) {
                    cloudIndex = cloudScan;
                }
            }

            /* compute cloud next event time */
            if (cloudEvents.get(cloudIndex).getEventStatus() == EventStatus.NOT_ACTIVE) {
                cloudNextEventTime = infinityTime;
            } else {
                cloudNextEventTime = cloudEvents.get(cloudIndex).getNextEventTime();
            }
        } else {
            cloudNextEventTime = infinityTime;
            cloudIndex = -1;
        }

        if (cloudletNextEventTime < cloudNextEventTime) {
            eventIndex = cloudletIndex;
            eventLocation = EventLocation.CLOUDLET;
        } else {
            eventIndex = cloudIndex;
            eventLocation = EventLocation.CLOUD;
        }
        return new NextEventInfo(eventIndex, eventLocation);
    }


}
