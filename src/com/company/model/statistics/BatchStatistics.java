package com.company.model.statistics;

import com.company.configuration.Configuration;
import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchStatistics implements BatchMeanStatistics, CSVPrintable {

    private List<BaseStatistics> batchMeanStatistics;

    /* --------------------------- A.3.1/C.2.1 Statistics --------------------------- */
    private List<Double> systemRespTime;                        /* system response time*/
    private List<Double> class1RespTime;                        /* class 1 response time*/
    private List<Double> class2RespTime;                        /* class 2 response time */
    private List<Double> globalThr;                             /* system global throughput */
    private List<Double> class1Thr;                             /* system class 1 throughput */
    private List<Double> class2Thr;                             /* system class 2 throughput */

    /* --------------------------- A.3.2/C.2.2 Statistics --------------------------- */
    private List<Double> cloudletEffectiveClass1Thr;            /* cloudlet effective class 1 throughput */
    private List<Double> cloudletEffectiveClass2Thr;            /* cloudlet effective class 2 throughput */

    /* --------------------------- A.3.3/C.2.3 Statistics --------------------------- */
    private List<Double> cloudClass1Thr;                        /* cloud class 1 throughput */
    private List<Double> cloudClass2Thr;                        /* cloud class 2 throughput */

    /* --------------------------- A.3.4/C.2.4 Statistics --------------------------- */
    private List<Double> class1CletRespTime;                    /* class 1 cloudlet response time */
    private List<Double> class2CletRespTime;                    /* class 2 cloudlet response time */
    private List<Double> class1CloudRespTime;                   /* class 1 cloud response time */
    private List<Double> class2CloudRespTime;                   /* class 2 cloud response time */
    private List<Double> class1CletMeanPop;                     /* class 1 cloudlet mean population */
    private List<Double> class2CletMeanPop;                     /* class 2 cloudlet mean population */
    private List<Double> class1CloudMeanPop;                    /* class 1 cloud mean population */
    private List<Double> class2CloudMeanPop;                    /* class 2 cloud mean population */

    /* ------------------------------ C.2.6 Statistics ------------------------------ */
    private List<Double> class2InterruptedPercentage;           /* class 2 interrupted jobs percentage */
    private List<Double> class2InterruptedRespTime;             /* class 2 interrupted jobs response time */

    /* ------------------------------ Hyperexp phases ------------------------------ */
    private List<Double> processedN1F1RespTime;                 /* class 1 phase 1 response time */
    private List<Double> processedN1F2RespTime;                 /* class 1 phase 2 response time */
    private List<Double> processedN2F1RespTime;                 /* class 2 phase 1 response time */
    private List<Double> processedN2F2RespTime;                 /* class 2 phase 2 response time */
    private List<Double> processedN1F1MeanPop;                  /* class 1 phase 1 mean population */
    private List<Double> processedN1F2MeanPop;                  /* class 1 phase 2 mean population */
    private List<Double> processedN2F1MeanPop;                  /* class 2 phase 1 mean population */
    private List<Double> processedN2F2MeanPop;                  /* class 2 phase 2 mean population */


    public BatchStatistics() {
        /* ---------------------- init batch statistics ---------------------- */
        this.batchMeanStatistics = new ArrayList<>();

        /* ---------------------- init A.3.1/C.2.1 statistics ---------------------- */
        this.systemRespTime = new ArrayList<>();

        this.class1RespTime = new ArrayList<>();

        this.class2RespTime = new ArrayList<>();

        this.globalThr = new ArrayList<>();

        this.class1Thr = new ArrayList<>();

        this.class2Thr = new ArrayList<>();

        /* ---------------------- init A.3.2/C.2.2 statistics ---------------------- */
        this.cloudletEffectiveClass1Thr = new ArrayList<>();

        this.cloudletEffectiveClass2Thr = new ArrayList<>();

        /* ---------------------- init A.3.3/C.2.3 statistics ---------------------- */
        this.cloudClass1Thr = new ArrayList<>();

        this.cloudClass2Thr = new ArrayList<>();

        /* ---------------------- init A.3.4/C.2.4 statistics ---------------------- */
        this.class1CletRespTime = new ArrayList<>();

        this.class2CletRespTime = new ArrayList<>();

        this.class1CloudRespTime = new ArrayList<>();

        this.class2CloudRespTime = new ArrayList<>();

        this.class1CletMeanPop = new ArrayList<>();

        this.class2CletMeanPop = new ArrayList<>();

        this.class1CloudMeanPop = new ArrayList<>();

        this.class2CloudMeanPop = new ArrayList<>();

        /* ------------------------- init C.2.6 statistics ------------------------- */
        this.class2InterruptedPercentage = new ArrayList<>();

        this.class2InterruptedRespTime = new ArrayList<>();

        /* ------------------------- Hyperexp phases ------------------------- */
        this.processedN1F1RespTime = new ArrayList<>();
        this.processedN1F2RespTime = new ArrayList<>();
        this.processedN2F1RespTime = new ArrayList<>();
        this.processedN2F2RespTime = new ArrayList<>();
        this.processedN1F1MeanPop = new ArrayList<>();
        this.processedN1F2MeanPop = new ArrayList<>();
        this.processedN2F1MeanPop = new ArrayList<>();
        this.processedN2F2MeanPop = new ArrayList<>();

        /* ----------------- reset batch one time to init it ------------------ */
        this.resetBatch();
    }

    @Override
    public void resetBatch() {
        /* ---------------------- reset batch statistics ---------------------- */
        this.batchMeanStatistics.add(new BaseStatistics());

        /* ---------------------- reset A.3.1/C.2.1 statistics ---------------------- */
        this.systemRespTime.add(0.0);

        this.class1RespTime.add(0.0);

        this.class2RespTime.add(0.0);

        this.globalThr.add(0.0);

        this.class1Thr.add(0.0);

        this.class2Thr.add(0.0);

        /* ---------------------- reset A.3.2/C.2.2 statistics ---------------------- */

        this.cloudletEffectiveClass1Thr.add(0.0);

        this.cloudletEffectiveClass2Thr.add(0.0);

        /* ---------------------- reset A.3.3/C.2.3 statistics ---------------------- */
        this.cloudClass1Thr.add(0.0);

        this.cloudClass2Thr.add(0.0);

        /* ---------------------- reset A.3.4/C.2.4 statistics ---------------------- */
        this.class1CletRespTime.add(0.0);

        this.class2CletRespTime.add(0.0);

        this.class1CloudRespTime.add(0.0);

        this.class2CloudRespTime.add(0.0);

        this.class1CletMeanPop.add(0.0);

        this.class2CletMeanPop.add(0.0);

        this.class1CloudMeanPop.add(0.0);

        this.class2CloudMeanPop.add(0.0);

        /* ------------------------- reset C.2.6 statistics ------------------------- */
        this.class2InterruptedPercentage.add(0.0);

        this.class2InterruptedRespTime.add(0.0);

        /* ------------------------- Hyperexp phases ------------------------- */
        this.processedN1F1RespTime.add(0.0);
        this.processedN1F2RespTime.add(0.0);
        this.processedN2F1RespTime.add(0.0);
        this.processedN2F2RespTime.add(0.0);
        this.processedN1F1MeanPop.add(0.0);
        this.processedN1F2MeanPop.add(0.0);
        this.processedN2F1MeanPop.add(0.0);
        this.processedN2F2MeanPop.add(0.0);

    }

    @Override
    public void updateStatistics(SystemState systemState, Time time) {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        this.batchMeanStatistics.get(lastBatchIndex).updateStatistics(systemState, time);
    }

    @Override
    public void updateAggregateStatistics() {
        int lastBatchIndex = this.batchMeanStatistics.size() - 1;
        BaseStatistics baseStatistics = this.batchMeanStatistics.get(lastBatchIndex); /* get current batch statistics */

        /* if batch current time is grater than 0 update statistics:
         *      1.  global throughput                       (A.3.1/C.2.1)
         *      2.  class 1 throughput                      (A.3.1/C.2.1)
         *      3.  class 2 throughput                      (A.3.1/C.2.1)
         *      4.  cloudlet effective class 1 throughput   (A.3.2/C.2.2)
         *      5.  cloudlet effective class 2 throughput   (A.3.2/C.2.2)
         *      6.  cloud class 1 throughput                (A.3.3/C.2.3)
         *      7.  cloud class 1 throughput                (A.3.3/C.2.3)
         *      8.  cloudlet class 1 mean population        (A.3.4/C.2.4)
         *      9.  cloudlet class 2 mean population        (A.3.4/C.2.4)
         *      10. cloud class 1 mean population           (A.3.4/C.2.4)
         *      11. cloud class 2 mean population           (A.3.4/C.2.4)
         *      */
        double currentTime = baseStatistics.getCurrentTime();
        if (currentTime > 0) { /* if batch current time grater than 0,
                                                      it's possible to compute throughput and population mean */

            this.globalThr.set(lastBatchIndex, baseStatistics.getProcessedSystemJobsNumber() / currentTime);
            this.class1Thr.set(
                    lastBatchIndex,
                    (baseStatistics.getProcessedN1JobsClet() + baseStatistics.getProcessedN1JobsCloud()) / currentTime
            );
            this.class2Thr.set(
                    lastBatchIndex,
                    (baseStatistics.getProcessedN2JobsClet() + baseStatistics.getProcessedN2JobsCloud()) / currentTime
            );
            this.cloudletEffectiveClass1Thr.set(lastBatchIndex, baseStatistics.getProcessedN1JobsClet() / currentTime);
            this.cloudletEffectiveClass2Thr.set(lastBatchIndex, baseStatistics.getProcessedN2JobsClet() / currentTime);
            this.cloudClass1Thr.set(lastBatchIndex, baseStatistics.getProcessedN1JobsCloud() / currentTime);
            this.cloudClass2Thr.set(lastBatchIndex, baseStatistics.getProcessedN2JobsCloud() / currentTime);
            this.class1CletMeanPop.set(lastBatchIndex, baseStatistics.getN1CletArea() / currentTime);
            this.class2CletMeanPop.set(lastBatchIndex, baseStatistics.getN2CletArea() / currentTime);
            this.class1CloudMeanPop.set(lastBatchIndex, baseStatistics.getN1CloudArea() / currentTime);
            this.class2CloudMeanPop.set(lastBatchIndex, baseStatistics.getN2CloudArea() / currentTime);
        }

        /* if there are jobs processed update statistics:
         *      1. system response time             (A.3.1/C.2.1)
         *      2. class 1 response time            (A.3.1/C.2.1)
         *      3. class 2 response time            (A.3.1/C.2.1)
         *      4. class 1 cloudlet response time   (A.3.4/C.2.4)
         *      5. class 2 cloudlet response time   (A.3.4/C.2.4)
         *      6. class 1 cloud response time      (A.3.4/C.2.4)
         *      7. class 2 cloud response time      (A.3.4/C.2.4)
         * */
        if (baseStatistics.getProcessedSystemJobsNumber() > 0) {
            this.systemRespTime.set(lastBatchIndex, baseStatistics.getSystemArea() / (double) baseStatistics.getProcessedSystemJobsNumber());
        }
        if (baseStatistics.getProcessedN1JobsNumber() > 0) {
            this.class1RespTime.set(lastBatchIndex, baseStatistics.getN1Area() / (double) baseStatistics.getProcessedN1JobsNumber());
        }
        if (baseStatistics.getProcessedN2JobsNumber() > 0) {
            this.class2RespTime.set(lastBatchIndex, baseStatistics.getN2Area() / (double) baseStatistics.getProcessedN2JobsNumber());
        }
        if (baseStatistics.getProcessedN1JobsClet() > 0) {
            this.class1CletRespTime.set(lastBatchIndex, baseStatistics.getN1CletArea() / (double) baseStatistics.getProcessedN1JobsClet());
        }
        if (baseStatistics.getProcessedN2JobsClet() > 0) {
            this.class2CletRespTime.set(lastBatchIndex, baseStatistics.getN2CletArea() / (double) baseStatistics.getProcessedN2JobsClet());
        }
        if (baseStatistics.getProcessedN1JobsCloud() > 0) {
            this.class1CloudRespTime.set(lastBatchIndex, baseStatistics.getN1CloudArea() / (double) baseStatistics.getProcessedN1JobsCloud());
        }
        if (baseStatistics.getProcessedN2JobsCloud() > 0) {
            this.class2CloudRespTime.set(lastBatchIndex, baseStatistics.getN2CloudArea() / (double) baseStatistics.getProcessedN2JobsCloud());
        }

        /* if algorithm 2 policy is been using:
         *      1. percentage of class 2 interrupted jobs                       (C.3.6)
         *      2. system response time of class 2 interrupted jobs             (C.3.6)
         * */
        if (Configuration.EXECUTION_ALGORITHM == Configuration.Algorithms.ALGORITHM_2) {
            if (baseStatistics.getProcessedN2JobsNumber() > 0) {    /* if there are jobs processed on cloudlet or cloud */
                this.class2InterruptedPercentage.set(
                        lastBatchIndex,
                        baseStatistics.getInterruptedN2Jobs() / (double) baseStatistics.getProcessedN2JobsNumber()
                );
            }
            if (baseStatistics.getInterruptedN2Jobs() > 0) {
                this.class2InterruptedRespTime.set(
                        lastBatchIndex,
                        (baseStatistics.getInterruptedN2JobsServiceTimeOnClet() +
                                baseStatistics.getInterruptedN2JobsServiceTimeOnCloud()) / (double) baseStatistics.getInterruptedN2Jobs()
                );
            }
        }

        /* if cloudlet service is hyperexponential update:
         *      1. class 1 phase 1 response time
         *      2. class 1 phase 2 response time
         *      3. class 2 phase 1 response time
         *      3. class 2 phase 2 response time
         *      3. class 1 phase 1 mean population
         *      3. class 1 phase 2 mean population
         *      3. class 2 phase 1 mean population
         *      3. class 2 phase 2 mean population
         * */
        if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {
            if (baseStatistics.getProcessedN1F1() > 0) {
                this.processedN1F1RespTime.set(lastBatchIndex, baseStatistics.getN1F1Area() / (double) baseStatistics.getProcessedN1F1());
            }
            if (baseStatistics.getProcessedN1F2() > 0) {
                this.processedN1F2RespTime.set(lastBatchIndex, baseStatistics.getN1F2Area() / (double) baseStatistics.getProcessedN1F2());
            }
            if (baseStatistics.getProcessedN2F1() > 0) {
                this.processedN2F1RespTime.set(lastBatchIndex, baseStatistics.getN2F1Area() / (double) baseStatistics.getProcessedN2F1());
            }
            if (baseStatistics.getProcessedN2F2() > 0) {
                this.processedN2F2RespTime.set(lastBatchIndex, baseStatistics.getN2F2Area() / (double) baseStatistics.getProcessedN2F2());
            }
            if (currentTime > 0) {
                this.processedN1F1MeanPop.set(lastBatchIndex, baseStatistics.getN1F1Area() / currentTime);
                this.processedN1F2MeanPop.set(lastBatchIndex, baseStatistics.getN1F2Area() / currentTime);
                this.processedN2F1MeanPop.set(lastBatchIndex, baseStatistics.getN2F1Area() / currentTime);
                this.processedN2F2MeanPop.set(lastBatchIndex, baseStatistics.getN2F2Area() / currentTime);
            }
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
        for (BaseStatistics baseStatistics : this.batchMeanStatistics) {
            baseStatistics.writeToCSV(printer);
        }
    }

    public List<Double> getSystemRespTime() {
        return systemRespTime;
    }

    public List<Double> getClass1RespTime() {
        return class1RespTime;
    }

    public List<Double> getClass2RespTime() {
        return class2RespTime;
    }

    public List<Double> getGlobalThr() {
        return globalThr;
    }

    public List<Double> getClass1Thr() {
        return class1Thr;
    }

    public List<Double> getClass2Thr() {
        return class2Thr;
    }

    public List<Double> getCloudletEffectiveClass1Thr() {
        return cloudletEffectiveClass1Thr;
    }

    public List<Double> getCloudletEffectiveClass2Thr() {
        return cloudletEffectiveClass2Thr;
    }

    public List<Double> getCloudClass1Thr() {
        return cloudClass1Thr;
    }

    public List<Double> getCloudClass2Thr() {
        return cloudClass2Thr;
    }

    public List<Double> getClass1CletRespTime() {
        return class1CletRespTime;
    }

    public List<Double> getClass2CletRespTime() {
        return class2CletRespTime;
    }

    public List<Double> getClass1CloudRespTime() {
        return class1CloudRespTime;
    }

    public List<Double> getClass2CloudRespTime() {
        return class2CloudRespTime;
    }

    public List<Double> getClass1CletMeanPop() {
        return class1CletMeanPop;
    }

    public List<Double> getClass2CletMeanPop() {
        return class2CletMeanPop;
    }

    public List<Double> getClass1CloudMeanPop() {
        return class1CloudMeanPop;
    }

    public List<Double> getClass2CloudMeanPop() {
        return class2CloudMeanPop;
    }

    public List<Double> getClass2InterruptedPercentage() {
        return class2InterruptedPercentage;
    }

    public List<Double> getClass2InterruptedRespTime() {
        return class2InterruptedRespTime;
    }

    public List<Double> getProcessedN1F1RespTime() {
        return processedN1F1RespTime;
    }

    public List<Double> getProcessedN1F2RespTime() {
        return processedN1F2RespTime;
    }

    public List<Double> getProcessedN2F1RespTime() {
        return processedN2F1RespTime;
    }

    public List<Double> getProcessedN2F2RespTime() {
        return processedN2F2RespTime;
    }

    public List<Double> getProcessedN1F1MeanPop() {
        return processedN1F1MeanPop;
    }

    public List<Double> getProcessedN1F2MeanPop() {
        return processedN1F2MeanPop;
    }

    public List<Double> getProcessedN2F1MeanPop() {
        return processedN2F1MeanPop;
    }

    public List<Double> getProcessedN2F2MeanPop() {
        return processedN2F2MeanPop;
    }
}
