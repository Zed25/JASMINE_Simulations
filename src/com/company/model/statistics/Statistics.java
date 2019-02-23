package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;

/**
 * ---------------------------------------------------------------------------------------------------------------------
 * ------------------------------------------ Statistics Interface -----------------------------------------------------
 * ---------------------------------------------------------------------------------------------------------------------
 * */
public interface Statistics {

    void updateStatistics(SystemState systemState, Time time);

}
