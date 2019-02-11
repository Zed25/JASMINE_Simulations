package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;

public interface BatchMeanStatistics extends Statistics {

    void resetBatch();

    void updateBatchStatistics();

}
