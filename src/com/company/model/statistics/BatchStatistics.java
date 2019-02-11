package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchStatistics implements BatchMeanStatistics, CSVPrintable {

    private List<BaseStatistics> batchMeanStatistics;

    /* --------------------------- A.3.1 Statistics --------------------------- */
    private List<Double> systemRespTime;
    private List<Double> class1RespTime;
    private List<Double> class2RespTime;
    private List<Double> globalThr;                             /* system global throughput */
    private List<Double> class1Thr;                             /* system class 1 throughput */
    private List<Double> class2Thr;                             /* system class 2 throughput */

    /* --------------------------- A.3.2 Statistics --------------------------- */
    /* --------------------------- A.3.3 Statistics --------------------------- */
    /* --------------------------- A.3.4 Statistics --------------------------- */
    private List<Double> cloudClass1Thr;                        /* cloud class 1 throughput */
    private List<Double> cloudClass2lThr;                       /* cloud class 2 throughput */
    private List<Double> cloudletEffectiveClass1Thr;            /* cloudlet effective class 1 throughput */
    private List<Double> cloudletEffectiveClass2Thr;            /* cloudlet effective class 2 throughput */

    //private List<Double> class1RespTime;

    public BatchStatistics() {
        this.batchMeanStatistics = new ArrayList<>();
        this.batchMeanStatistics.add(new BaseStatistics());
    }

    @Override
    public void resetBatch() {
        this.batchMeanStatistics.add(new BaseStatistics());
    }

    @Override
    public void updateStatistics(SystemState systemState, Time time) {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        this.batchMeanStatistics.get(lastBatchIndex).updateStatistics(systemState, time);
    }

    @Override
    public void updateBatchStatistics() {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        BaseStatistics baseStatistics = this.batchMeanStatistics.get(lastBatchIndex); /* get current batch statistics */

        if (baseStatistics.getCurrentTime() > 0) { /* if batch current time grater than 0,
                                                      it's possible to compute throughput and population mean */

        }
    }

    public List<BaseStatistics> getBatchMeanStatistics() {
        return batchMeanStatistics;
    }
    public BaseStatistics getLastBatchStatistics() {
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
        for (BaseStatistics baseStatistics : this.batchMeanStatistics)
            baseStatistics.writeToCSV(printer);
    }
}
