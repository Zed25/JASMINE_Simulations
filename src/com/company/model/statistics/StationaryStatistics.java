package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StationaryStatistics implements AggregateStatistics, CSVPrintable {

    private List<TimeValueStatistics> systemResponseTime;
    private List<TimeValueStatistics> class1RespTime;
    private List<TimeValueStatistics> class2RespTime;
    private List<TimeValueStatistics> globalThr;
    private List<TimeValueStatistics> class1Thr;
    private List<TimeValueStatistics> class2Thr;

    private List<TimeValueStatistics> cloudletClass1EffectiveThr;
    private List<TimeValueStatistics> cloudletClass2EffectiveThr;

    private List<TimeValueStatistics> cloudClass1Thr;
    private List<TimeValueStatistics> cloudClass2Thr;

    private List<TimeValueStatistics> cloudletClass1RespTime;
    private List<TimeValueStatistics> cloudletClass2RespTime;
    private List<TimeValueStatistics> cloudClass1RespTime;
    private List<TimeValueStatistics> cloudClass2RespTime;
    private List<TimeValueStatistics> cloudletClass1MeanPop;
    private List<TimeValueStatistics> cloudletClass2MeanPop;
    private List<TimeValueStatistics> cloudClass1MeanPop;
    private List<TimeValueStatistics> cloudClass2MeanPop;

    private BaseStatistics baseStatistics;

    public StationaryStatistics() {
        this.systemResponseTime = new ArrayList<>();
        this.class1RespTime = new ArrayList<>();
        this.class2RespTime = new ArrayList<>();
        this.globalThr = new ArrayList<>();
        this.class1Thr = new ArrayList<>();
        this.class2Thr = new ArrayList<>();

        this.cloudletClass1EffectiveThr = new ArrayList<>();
        this.cloudletClass2EffectiveThr = new ArrayList<>();

        this.cloudClass1Thr = new ArrayList<>();
        this.cloudClass2Thr = new ArrayList<>();

        this.cloudletClass1RespTime = new ArrayList<>();
        this.cloudletClass2RespTime = new ArrayList<>();
        this.cloudClass1RespTime = new ArrayList<>();
        this.cloudClass2RespTime = new ArrayList<>();
        this.cloudletClass1MeanPop = new ArrayList<>();
        this.cloudletClass2MeanPop = new ArrayList<>();
        this.cloudClass1MeanPop = new ArrayList<>();
        this.cloudClass2MeanPop = new ArrayList<>();

        this.baseStatistics = new BaseStatistics();
    }

    @Override
    public void updateStatistics(SystemState systemState, Time time) {
        baseStatistics.updateStatistics(systemState, time);
    }

    @Override
    public void updateAggregateStatistics() {
        BaseStatistics baseStatistics = this.baseStatistics;

        /* if there are jobs processed record instant values:
         *      1. system response time
         *      2. class 1 response time
         *      3. class 2 response time
         *      4. class 1 cloudlet response time
         *      5. class 2 cloudlet response time
         *      6. class 1 cloud response time
         *      7. class 2 cloud response time
         * */
        if (baseStatistics.getProcessedSystemJobsNumber() > 0) {
            this.systemResponseTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getSystemArea() / (double) baseStatistics.getProcessedSystemJobsNumber()
                    )
            );
        }
        if (baseStatistics.getProcessedN1JobsNumber() > 0) {
            this.class1RespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getN1Area() / (double) baseStatistics.getProcessedN1JobsNumber()
                    )
            );
        }
        if (baseStatistics.getProcessedN2JobsNumber() > 0) {
            this.class2RespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getN2Area() / (double) baseStatistics.getProcessedN2JobsNumber()
                    )
            );
        }
        if (baseStatistics.getProcessedN1JobsClet() > 0) {
            this.cloudletClass1RespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getN1CletArea() / (double) baseStatistics.getProcessedN1JobsClet()
                    )
            );
        }
        if (baseStatistics.getProcessedN2JobsClet() > 0) {
            this.cloudletClass2RespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getN2CletArea() / (double) baseStatistics.getProcessedN2JobsClet()
                    )
            );
        }
        if (baseStatistics.getProcessedN1JobsCloud() > 0) {
            this.cloudClass1RespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getN1CloudArea() / (double) baseStatistics.getProcessedN1JobsCloud()
                    )
            );
        }
        if (baseStatistics.getProcessedN2JobsCloud() > 0) {
            this.cloudClass2RespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getN2CloudArea() / (double) baseStatistics.getProcessedN2JobsCloud()
                    )
            );
        }

        /* if batch current time is grater than 0 update statistics:
         *      1.  global throughput
         *      2.  class 1 throughput
         *      3.  class 2 throughput
         *      4.  cloudlet effective class 1 throughput
         *      5.  cloudlet effective class 2 throughput
         *      6.  cloud class 1 throughput
         *      7.  cloud class 1 throughput
         *      8.  cloudlet class 1 mean population
         *      9.  cloudlet class 2 mean population
         *      10. cloud class 1 mean population
         *      11. cloud class 2 mean population
         *      */
        double currentTime = baseStatistics.getCurrentTime();
        if (currentTime > 0) { /* if batch current time grater than 0,
                                                      it's possible to compute throughput and population mean */

            this.globalThr.add(new TimeValueStatistics(currentTime, baseStatistics.getProcessedSystemJobsNumber() / currentTime));
            this.class1Thr.add(new TimeValueStatistics(currentTime,
                    (baseStatistics.getProcessedN1JobsClet() + baseStatistics.getProcessedN1JobsCloud()) / currentTime)
            );
            this.class2Thr.add(new TimeValueStatistics(currentTime,
                    (baseStatistics.getProcessedN2JobsClet() + baseStatistics.getProcessedN2JobsCloud()) / currentTime)
            );
            this.cloudletClass1EffectiveThr.add(new TimeValueStatistics(currentTime, baseStatistics.getProcessedN1JobsClet() / currentTime));
            this.cloudletClass2EffectiveThr.add(new TimeValueStatistics(currentTime, baseStatistics.getProcessedN2JobsClet() / currentTime));
            this.cloudClass1Thr.add(new TimeValueStatistics(currentTime,baseStatistics.getProcessedN1JobsCloud() / currentTime));
            this.cloudClass2Thr.add(new TimeValueStatistics(currentTime, baseStatistics.getProcessedN2JobsCloud() / currentTime));
            this.cloudletClass1MeanPop.add(new TimeValueStatistics(currentTime, baseStatistics.getN1CletArea() / currentTime));
            this.cloudletClass2MeanPop.add(new TimeValueStatistics(currentTime, baseStatistics.getN2CletArea() / currentTime));
            this.cloudClass1MeanPop.add(new TimeValueStatistics(currentTime,  baseStatistics.getN1CloudArea() / currentTime));
            this.cloudClass2MeanPop.add(new TimeValueStatistics(currentTime, baseStatistics.getN2CloudArea() / currentTime));
        }
    }

    @Override
    public void writeToCSV(PrintWriter printer) {
        DecimalFormat f = new DecimalFormat("###0.0000000000000");
        int systemSize = systemResponseTime.size();

        String[] header = new String[] {
                "Time",
                "MRT",
                "AVG",
                "Time",
                "MRT Class 1",
                "AVG",
                "Time",
                "MRT Class 2",
                "AVG",
                "Time",
                "global Thr",
                "AVG",
                "Time",
                "Thr Class 1",
                "AVG",
                "Time",
                "Thr Class 2",
                "AVG",
                "Time",
                "Clet Class 1 Eff Thr",
                "AVG",
                "Time",
                "Clet Class 2 Eff Thr",
                "AVG",
                "Time",
                "Cloud Class 1 Thr",
                "AVG",
                "Time",
                "Cloud Class 2 Thr",
                "AVG",
                "Time",
                "MRT Class 1 Clet",
                "AVG",
                "Time",
                "MRT Class 2 Clet",
                "AVG",
                "Time",
                "MRT Class 1 Cloud",
                "AVG",
                "Time",
                "MRT Class 2 Cloud",
                "AVG",
                "Time",
                "MP Class 1 Clet",
                "AVG",
                "Time",
                "MP Class 2 Clet",
                "AVG",
                "Time",
                "MP Class 1 Cloud",
                "AVG",
                "Time",
                "MP Class 2 Cloud",
                "AVG"
        };

        printer.println(String.join(",", Arrays.asList(header)));

        for (int i = 0; i < systemSize; i++) {

            printer.print(f.format(systemResponseTime.get(i).getTime()) + "," + f.format(systemResponseTime.get(i).getValue()));
            printer.print(",");
            if (i < class1RespTime.size()) {
                printer.print("," + f.format(class1RespTime.get(i).getTime()) + "," + f.format(class1RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < class2RespTime.size()) {
                printer.print("," + f.format(class2RespTime.get(i).getTime()) + "," + f.format(class2RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < globalThr.size()) {
                printer.print("," + f.format(globalThr.get(i).getTime()) + "," + f.format(globalThr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < class1Thr.size()) {
                printer.print("," + f.format(class1Thr.get(i).getTime()) + "," + f.format(class1Thr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < class2Thr.size()) {
                printer.print("," + f.format(class2Thr.get(i).getTime()) + "," + f.format(class2Thr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudletClass1EffectiveThr.size()) {
                printer.print("," + f.format(cloudletClass1EffectiveThr.get(i).getTime()) + "," + f.format(cloudletClass1EffectiveThr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudletClass2EffectiveThr.size()) {
                printer.print("," + f.format(cloudletClass2EffectiveThr.get(i).getTime()) + "," + f.format(cloudletClass2EffectiveThr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudClass1Thr.size()) {
                printer.print("," + f.format(cloudClass1Thr.get(i).getTime()) + "," + f.format(cloudClass1Thr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudClass2Thr.size()) {
                printer.print("," + f.format(cloudClass2Thr.get(i).getTime()) + "," + f.format(cloudClass2Thr.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudletClass1RespTime.size()) {
                printer.print("," + f.format(cloudletClass1RespTime.get(i).getTime()) + "," + f.format(cloudletClass1RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudletClass2RespTime.size()) {
                printer.print("," + f.format(cloudletClass2RespTime.get(i).getTime()) + "," + f.format(cloudletClass2RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudClass1RespTime.size()) {
                printer.print("," + f.format(cloudClass1RespTime.get(i).getTime()) + "," + f.format(cloudClass1RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudClass2RespTime.size()) {
                printer.print("," + f.format(cloudClass2RespTime.get(i).getTime()) + "," + f.format(cloudClass2RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudletClass1MeanPop.size()) {
                printer.print("," + f.format(cloudletClass1MeanPop.get(i).getTime()) + "," + f.format(cloudletClass1MeanPop.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudletClass2MeanPop.size()) {
                printer.print("," + f.format(cloudletClass2MeanPop.get(i).getTime()) + "," + f.format(cloudletClass2MeanPop.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudClass1MeanPop.size()) {
                printer.print("," + f.format(cloudClass1MeanPop.get(i).getTime()) + "," + f.format(cloudClass1MeanPop.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            if (i < cloudClass2MeanPop.size()) {
                printer.print("," + f.format(cloudClass2MeanPop.get(i).getTime()) + "," + f.format(cloudClass2MeanPop.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print(",");
            printer.print("\n");
        }
        printer.flush();
        printer.close();
    }

    public BaseStatistics getBaseStatistics() {
        return baseStatistics;
    }
}
