package com.company.model.statistics;

public class TimeValueStatistics {
    private double time;
    private double value;

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
