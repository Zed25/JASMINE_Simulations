package com.company.model.statistics;

import com.company.configuration.Configuration;
import com.company.model.HyperexpSystemState;
import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.HyperexpPhaseType;
import com.company.model.utils.CSVPrintable;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

public class BaseStatistics implements Statistics, CSVPrintable {
    /* BASE STATISTICS */
    private double systemArea;              /* mean job number in system */
    private double cloudletArea;            /* mean job number in cloudlet */
    private double cloudArea;               /* mean job number in cloud */
    private double n1CletArea;              /* mean n1 job number in cloulet */
    private double n2CletArea;              /* mean n2 job number in cloudlet */
    private double n1CloudArea;             /* mean n1 job number in cloud */
    private double n2CloudArea;             /* mean n2 job number in cloud */
    private double n1Area;                  /* mean n2 job number */
    private double n2Area;                  /* mean n2 job number */

    private double currentTime;             /* this statistics current time */

    private long interruptedN2Jobs;                       /* number of class 2 jobs interrupted */
    private double interruptedN2JobsServiceTimeOnClet;      /* class 2 job time spent on cloudlet */
    private double interruptedN2JobsServiceTimeOnCloud;     /* class 2 job time spent on cloud (setup time + cloud service time) */

    private long processedN1JobsClet;
    private long processedN2JobsClet;
    private long processedN1JobsCloud;
    private long processedN2JobsCloud;

    /* HYPEREXP PHASES */
    private double n1F1Area;
    private double n1F2Area;
    private double n2F1Area;
    private double n2F2Area;

    private long processedN1F1;
    private long processedN1F2;
    private long processedN2F1;
    private long processedN2F2;


    public BaseStatistics() {
        this.systemArea = 0.0;
        this.cloudletArea = 0.0;
        this.cloudArea = 0.0;
        this.n1CletArea = 0.0;
        this.n2CletArea = 0.0;
        this.n1CloudArea = 0.0;
        this.n2CloudArea = 0.0;
        this.n1Area = 0.0;
        this.n2Area = 0.0;

        this.currentTime = 0.0;

        this.interruptedN2Jobs = 0;
        this.interruptedN2JobsServiceTimeOnClet = 0.0;
        this.interruptedN2JobsServiceTimeOnCloud = 0.0;

        this.processedN1JobsClet = 0;
        this.processedN2JobsClet = 0;
        this.processedN1JobsCloud = 0;
        this.processedN2JobsCloud = 0;

        this.n1F1Area = 0.0;
        this.n1F2Area = 0.0;
        this.n2F1Area = 0.0;
        this.n2F2Area = 0.0;

        this.processedN1F1 = 0;
        this.processedN1F2 = 0;
        this.processedN2F1 = 0;
        this.processedN2F2 = 0;
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
            this.n1Area += deltaT * (systemState.getN1Clet() + systemState.getN1Cloud());
            this.n2Area += deltaT * (systemState.getN2Clet() + systemState.getN2Cloud());

            if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {
                HyperexpSystemState hyperexpSystemState = (HyperexpSystemState) systemState;
                this.n1F1Area += deltaT * hyperexpSystemState.getN1F1();
                this.n1F2Area += deltaT * hyperexpSystemState.getN1F2();
                this.n2F1Area += deltaT * hyperexpSystemState.getN2F1();
                this.n2F2Area += deltaT * hyperexpSystemState.getN2F2();
            }

            this.currentTime += deltaT;
        }
    }

    public void incrementProcessedJobPerPhase(ClassType classType, HyperexpPhaseType phaseType) {
        switch (classType) {
            case CLASS1:
                if (phaseType == HyperexpPhaseType.PHASE_1) {
                    this.incrementProcessedN1F1();
                } else if (phaseType == HyperexpPhaseType.PHASE_2) {
                    this.incrementProcessedN1F2();
                }
                break;
            case CLASS2:
                if (phaseType == HyperexpPhaseType.PHASE_1) {
                    this.incrementProcessedN2F1();
                } else if (phaseType == HyperexpPhaseType.PHASE_2) {
                    this.incrementProcessedN2F2();
                }
                break;
        }
    }

    public void incrementInterruptedN2Jobs() {
        this.interruptedN2Jobs++;
    }

    public void incrementInterruptedN2JobsServiceTimeOnClet(double timeToAdd) {
        this.interruptedN2JobsServiceTimeOnClet += timeToAdd;
    }

    public void incrementInterruptedN2JobsServiceTimeOnCloud(double timeToAdd) {
        this.interruptedN2JobsServiceTimeOnCloud += timeToAdd;
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

    public void incrementProcessedN1F1() {
        this.processedN1F1++;
    }

    public void incrementProcessedN1F2() {
        this.processedN1F2++;
    }

    public void incrementProcessedN2F1() {
        this.processedN2F1++;
    }

    public void incrementProcessedN2F2() {
        this.processedN2F2++;
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

    public double getN1Area() {
        return n1Area;
    }

    public double getN2Area() {
        return n2Area;
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

    public long getProcessedN1JobsNumber() {
        return this.processedN1JobsClet + this.processedN1JobsCloud;
    }

    public long getProcessedN2JobsNumber() {
        return this.processedN2JobsClet + this.processedN2JobsCloud;
    }

    public long getProcessedCletJobsNumber() {
        return this.processedN1JobsClet + this.processedN2JobsClet;
    }

    public long getProcessedCloudJobsNumber() {
        return this.processedN1JobsCloud + this.processedN2JobsCloud;
    }

    public long getProcessedN1F1() {
        return processedN1F1;
    }

    public long getProcessedN1F2() {
        return processedN1F2;
    }

    public long getProcessedN2F1() {
        return processedN2F1;
    }

    public long getProcessedN2F2() {
        return processedN2F2;
    }

    public double getN1F1Area() {
        return n1F1Area;
    }

    public double getN1F2Area() {
        return n1F2Area;
    }

    public double getN2F1Area() {
        return n2F1Area;
    }

    public double getN2F2Area() {
        return n2F2Area;
    }

    public long getInterruptedN2Jobs() {
        return interruptedN2Jobs;
    }

    public double getInterruptedN2JobsServiceTimeOnClet() {
        return interruptedN2JobsServiceTimeOnClet;
    }

    public double getInterruptedN2JobsServiceTimeOnCloud() {
        return interruptedN2JobsServiceTimeOnCloud;
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
