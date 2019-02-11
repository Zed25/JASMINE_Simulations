package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;

import java.util.ArrayList;
import java.util.List;

public class BatchStatistics implements BatchMeanStatistics {

    private List<AreaStatistics> batchMeanStatistics;

    public BatchStatistics() {
        this.batchMeanStatistics = new ArrayList<>();
        this.batchMeanStatistics.add(new AreaStatistics());
    }

    @Override
    public void resetBatch() {
        this.batchMeanStatistics.add(new AreaStatistics());
    }

    @Override
    public void updateStatistics(SystemState systemState, Time time) {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        this.batchMeanStatistics.get(lastBatchIndex).updateStatistics(systemState,time);
    }

    public List<AreaStatistics> getBatchMeanStatistics() {
        return batchMeanStatistics;
    }
    public AreaStatistics getLastBatchStatistics() {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        return this.batchMeanStatistics.get(lastBatchIndex);
    }
}
