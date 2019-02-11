package com.company.model.utils;

import java.util.List;

public class Cdh {
    private double mean = 0; /* histogram mean */
    private double stdev = 0; /* histogram stdev */
    private double delta = 0; /* histogram bins width */
    private long lower = 0; /* lower outlier count */
    private long higher = 0; /* higher outlier count */

    private long count[]; /* bin count */
    private double midpoint[]; /* bin midpoint */
    private double proportion[]; /* bin proportion */
    private double density[]; /* bin density */

    public void fit(List<Double> values, double min, double max, int k) {
        this.delta = ((max - min) / k);
        this.count = new long[k];
        this.midpoint = new double[k];
        this.proportion = new double[k];
        this.density = new double[k];
        long size = values.size();

        for (int j = 0; j < k; j++) {
            this.count[j] = 0;
            this.midpoint[j] = min + (j + 0.5) * delta;
        }

        for (Double x : values) {
            if ((x >= min) && (x < max)) {
                int j = (int) ((x - min) / delta);
                this.count[j]++;
            } else if (x < min)
                this.lower++;
            else
                this.higher++;
        }

        for (int j = 0; j < k; j++) {
            proportion[j] = (double) count[j] / size;
            density[j] = count[j] / (size * delta);
        }

        double sum = 0.0;
        for (int j = 0; j < k; j++)
            sum += this.midpoint[j] * this.count[j];
        this.mean = sum / size;

        double sumsqr = 0.0;
        for (int j = 0; j < k; j++)
            sumsqr += Math.pow((this.midpoint[j] - this.mean), 2) * this.count[j];
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

    public long[] getCount() {
        return count;
    }

    public double[] getMidpoint() {
        return midpoint;
    }

    public double[] getProportion() {
        return proportion;
    }

    public double[] getDensity() {
        return density;
    }
}
