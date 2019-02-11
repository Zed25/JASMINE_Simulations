package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchStatistics implements BatchMeanStatistics, CSVPrintable {

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
        this.batchMeanStatistics.get(lastBatchIndex).updateStatistics(systemState, time);
    }

    public List<AreaStatistics> getBatchMeanStatistics() {
        return batchMeanStatistics;
    }

    public AreaStatistics getLastBatchStatistics() {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        return this.batchMeanStatistics.get(lastBatchIndex);
    }

    @Override
    public void writeToCSV(PrintWriter printer) {
        String[] headers = {
                "systemArea",
                "cloudletArea",
                "cloudArea",
                "n1CletArea",
                "n2CletArea",
                "n1CloudArea",
                "n2CloudArea",
                "processedN1JobsClet",
                "processedN2JobsClet",
                "processedN1JobsCloud",
                "processedN2JobsCloud"
        };
        printer.println(String.join(",", Arrays.asList(headers)));
        for (AreaStatistics areaStatistics : this.batchMeanStatistics)
            areaStatistics.writeToCSV(printer);
    }
}
