package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
public class BaseStatistics implements Statistics, CSVPrintable {
    /* BASE STATISTICS */
    private double systemArea;               /* mean job number in system */
    private double cloudletArea;            /* mean job number in cloudlet */
    private double cloudArea;           /* mean job number in cloud */
    private double n1CletArea;          /* mean n1 job number in cloulet */
    private double n2CletArea;          /* mean n2 job number in cloudlet */
    private double n1CloudArea;         /* mean n1 job number in cloud */
    private double n2CloudArea;         /* mean n2 job number in cloud */

    private double currentTime;         /* this statistics current time */

    private long processedN1JobsClet;
    private long processedN2JobsClet;
    private long processedN1JobsCloud;
    private long processedN2JobsCloud;


    public BaseStatistics() {
        this.systemArea = 0.0;
        this.cloudletArea = 0.0;
        this.cloudArea = 0.0;
        this.n1CletArea = 0.0;
        this.n2CletArea = 0.0;
        this.n1CloudArea = 0.0;
        this.n2CloudArea = 0.0;

        this.currentTime = 0.0;

        this.processedN1JobsClet = 0;
        this.processedN2JobsClet = 0;
        this.processedN1JobsCloud = 0;
        this.processedN2JobsCloud = 0;
    }

    @Override
    public void updateStatistics(SystemState systemState, Time time) {
        if (time.getNext() > time.getCurrent()) {
            double deltaT = time.getNext() - time.getCurrent();
            this.systemArea += deltaT * (systemState.getCloudletJobsNumber() + systemState.getCloudJobsNumber());
            this.cloudletArea += deltaT * systemState.getCloudletJobsNumber();
            this.cloudArea += deltaT * systemState.getCloudJobsNumber();
            this.n1CletArea += deltaT * systemState.getN1Clet();
            this.n2CletArea += deltaT * systemState.getN2Clet();
            this.n1CloudArea += deltaT * systemState.getN1Cloud();
            this.n2CloudArea += deltaT * systemState.getN2Cloud();

            this.currentTime += deltaT;
        }
    }

    public void incrementProcJobsN1Clet() {
        this.processedN1JobsClet++;
    }

    public void incrementProcJobsN2Clet() {
        this.processedN2JobsClet++;
    }

    public void incrementProcJobsN1Cloud() {
        this.processedN1JobsCloud++;
    }

    public void incrementProcJobsN2Cloud() {
        this.processedN2JobsCloud++;
    }

    public double getSystemArea() {
        return systemArea;
    }

    public double getCloudletArea() {
        return cloudletArea;
    }

    public double getCloudArea() {
        return cloudArea;
    }

    public double getN1CletArea() {
        return n1CletArea;
    }

    public double getN2CletArea() {
        return n2CletArea;
    }

    public double getN1CloudArea() {
        return n1CloudArea;
    }

    public double getN2CloudArea() {
        return n2CloudArea;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public long getProcessedN1JobsClet() {
        return processedN1JobsClet;
    }

    public long getProcessedN2JobsClet() {
        return processedN2JobsClet;
    }

    public long getProcessedN1JobsCloud() {
        return processedN1JobsCloud;
    }

    public long getProcessedN2JobsCloud() {
        return processedN2JobsCloud;
    }

    public long getProcessedSystemJobsNumber() {
        return this.processedN1JobsClet + this.processedN2JobsClet +
                this.processedN1JobsCloud + this.processedN2JobsCloud;
    }

    @Override
    public void writeToCSV(PrintWriter printer) {
        DecimalFormat f = new DecimalFormat("###0.0000000000000");
        String[] strings = {
                f.format(this.systemArea),
                f.format(this.cloudletArea),
                f.format(this.cloudArea),
                f.format(this.n1CletArea),
                f.format(this.n2CletArea),
                f.format(this.n1CloudArea),
                f.format(this.n2CloudArea),
                f.format(this.processedN1JobsClet),
                f.format(this.processedN2JobsClet),
                f.format(this.processedN1JobsCloud),
                f.format(this.processedN2JobsCloud)
        };

        printer.println(String.join(",", Arrays.asList(strings)));
    }
}
