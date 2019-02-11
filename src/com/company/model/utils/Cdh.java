package com.company.model.utils;

import java.util.List;

public class Cdh {
    private double mean = 0; /* histogram mean */
    private double stdev = 0; /* histogram stdev */
    private double delta = 0; /* histogram bins width */
    private long lower = 0; /* lower outlier counts */
    private long higher = 0; /* higher outlier counts */

    private long counts[]; /* bin counts */
    private double midpoints[]; /* bin midpoints */
    private double proportions[]; /* bin proportions */
    private double densities[]; /* bin densities */

    public void fit(List<Double> values, double min, double max, int k) {
        this.delta = ((max - min) / k);
        this.counts = new long[k];
        this.midpoints = new double[k];
        this.proportions = new double[k];
        this.densities = new double[k];
        long size = values.size();

        for (int j = 0; j < k; j++) {
            this.counts[j] = 0;
            this.midpoints[j] = min + (j + 0.5) * delta;
        }

        for (Double x : values) {
            if ((x >= min) && (x < max)) {
                int j = (int) ((x - min) / delta);
                this.counts[j]++;
            } else if (x < min)
                this.lower++;
            else
                this.higher++;
        }

        for (int j = 0; j < k; j++) {
            proportions[j] = (double) counts[j] / size;
            densities[j] = counts[j] / (size * delta);
        }

        double sum = 0.0;
        for (int j = 0; j < k; j++)
            sum += this.midpoints[j] * this.counts[j];
        this.mean = sum / size;

        double sumsqr = 0.0;
        for (int j = 0; j < k; j++)
            sumsqr += Math.pow((this.midpoints[j] - this.mean), 2) * this.counts[j];
        this.stdev = Math.sqrt(sumsqr / size);
    }

    public double getMean() {
        return mean;
    }

    public double getStdev() {
        return stdev;
    }

    public double getDelta() {
        return delta;
    }

    public long getLower() {
        return lower;
    }

    public long getHigher() {
        return higher;
    }

    public long[] getCounts() {
        return counts;
    }

    public double[] getMidpoints() {
        return midpoints;
    }

    public double[] getProportions() {
        return proportions;
    }

    public double[] getDensities() {
        return densities;
    }
}
