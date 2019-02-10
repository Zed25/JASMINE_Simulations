package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;

public interface Statistics {

    void updateStatistics(SystemState systemState, Time time);
}
