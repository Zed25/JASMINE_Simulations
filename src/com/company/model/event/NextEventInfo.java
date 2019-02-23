package com.company.model.event;

import com.company.model.event.enumeration.EventLocation;

public class NextEventInfo {
    private int index;              /* event list index */
    private EventLocation location; /* event location; check it to choose to compute CLOUD or CLOUDLET event*/

    public NextEventInfo(int index, EventLocation location) {
        this.index = index;
        this.location = location;
    }

    public int getIndex() {
        return index;
    }

    public EventLocation getLocation() {
        return location;
    }
}
