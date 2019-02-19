package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StationaryStatistics implements AggregateStatistics, CSVPrintable {

    private List<TimeValueStatistics> systemResponseTime;
    private List<TimeValueStatistics> cloudletRespTime;
    private List<TimeValueStatistics> cloudRespTime;
    private List<TimeValueStatistics> class1RespTime;
    private List<TimeValueStatistics> class2RespTime;
    private List<TimeValueStatistics> cloudletClass1RespTime;
    private List<TimeValueStatistics> cloudletClass2RespTime;
    private List<TimeValueStatistics> cloudClass1RespTime;
    private List<TimeValueStatistics> cloudClass2RespTime;

    private BaseStatistics baseStatistics;

    public StationaryStatistics() {
        this.systemResponseTime = new ArrayList<>();
        this.cloudletRespTime = new ArrayList<>();
        this.cloudRespTime = new ArrayList<>();
        this.class1RespTime = new ArrayList<>();
        this.class2RespTime = new ArrayList<>();
        this.cloudletClass1RespTime = new ArrayList<>();
        this.cloudletClass2RespTime = new ArrayList<>();
        this.cloudClass1RespTime = new ArrayList<>();
        this.cloudClass2RespTime = new ArrayList<>();

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
         *      8. cloudlet reponse time
         *      9. cloud response time
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
        if (baseStatistics.getProcessedCletJobsNumber() > 0) {
            this.cloudletRespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getCloudletArea() / (double) baseStatistics.getProcessedCletJobsNumber()
                    )
            );
        }
        if (baseStatistics.getProcessedCloudJobsNumber() > 0) {
            this.cloudRespTime.add(new TimeValueStatistics(
                            baseStatistics.getCurrentTime(),
                            baseStatistics.getCloudletArea() / (double) baseStatistics.getProcessedCloudJobsNumber()
                    )
            );
        }
    }

    @Override
    public void writeToCSV(PrintWriter printer) {
        DecimalFormat f = new DecimalFormat("###0.0000000000000");
        int systemSize = systemResponseTime.size();

        printer.print("Time,Mean Wait Time,Time,CloudletRespTime,Time,CloudRespTime,Time,Class1,Time,Class2," +
                "Time,CloudletClass1RespTime,Time,CloudletClass2RespTime,Time,CloudClass1RespTime,Time,CloudClass2RespTime" +
                "");

        for (int i = 0; i < systemSize; i++) {
            printer.print(f.format(systemResponseTime.get(i).getTime()) + "," + f.format(systemResponseTime.get(i).getValue()));
            if (i < cloudletRespTime.size()) {
                printer.print("," + f.format(cloudletRespTime.get(i).getTime()) + "," + f.format(cloudletRespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < cloudRespTime.size()) {
                printer.print("," + f.format(cloudRespTime.get(i).getTime()) + "," + f.format(cloudRespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < class1RespTime.size()) {
                printer.print("," + f.format(class1RespTime.get(i).getTime()) + "," + f.format(class1RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < class2RespTime.size()) {
                printer.print("," + f.format(class2RespTime.get(i).getTime()) + "," + f.format(class2RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < cloudletClass1RespTime.size()) {
                printer.print("," + f.format(cloudletClass1RespTime.get(i).getTime()) + "," + f.format(cloudletClass1RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < cloudletClass2RespTime.size()) {
                printer.print("," + f.format(cloudletClass2RespTime.get(i).getTime()) + "," + f.format(cloudletClass2RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < cloudClass1RespTime.size()) {
                printer.print("," + f.format(cloudClass1RespTime.get(i).getTime()) + "," + f.format(cloudClass1RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            if (i < cloudClass2RespTime.size()) {
                printer.print("," + f.format(cloudClass2RespTime.get(i).getTime()) + "," + f.format(cloudClass2RespTime.get(i).getValue()));
            } else {
                printer.print(",,");
            }
            printer.print("\n");
        }
        printer.flush();
        printer.close();
    }

    public List<TimeValueStatistics> getSystemResponseTime() {
        return systemResponseTime;
    }

    public List<TimeValueStatistics> getCloudletRespTime() {
        return cloudletRespTime;
    }

    public List<TimeValueStatistics> getCloudRespTime() {
        return cloudRespTime;
    }

    public List<TimeValueStatistics> getClass1RespTime() {
        return class1RespTime;
    }

    public List<TimeValueStatistics> getClass2RespTime() {
        return class2RespTime;
    }

    public List<TimeValueStatistics> getCloudletClass1RespTime() {
        return cloudletClass1RespTime;
    }

    public List<TimeValueStatistics> getCloudletClass2RespTime() {
        return cloudletClass2RespTime;
    }

    public List<TimeValueStatistics> getCloudClass1RespTime() {
        return cloudClass1RespTime;
    }

    public List<TimeValueStatistics> getCloudClass2RespTime() {
        return cloudClass2RespTime;
    }

    public BaseStatistics getBaseStatistics() {
        return baseStatistics;
    }
}
