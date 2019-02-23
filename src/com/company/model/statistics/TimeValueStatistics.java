package com.company.model.statistics;

public class TimeValueStatistics {
    private double time;                /* simulation time when statistics is sampled  */
    private double value;               /* sampled statistic value  */

    public TimeValueStatistics(double time, double value) {
        this.time = time;
        this.value = value;
    }

    public double getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }
}
