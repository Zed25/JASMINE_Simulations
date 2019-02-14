package com.company.model.utils;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Cdh implements CSVPrintable{
    private double mean = 0; /* histogram mean */
    private double stdev = 0; /* histogram stdev */
    private double delta = 0; /* histogram bins width */
    private long lower = 0; /* lower outlier counts */
    private long higher = 0; /* higher outlier counts */
    private int k = 0; /* number of bins */

    private long[] counts; /* bin counts */
    private double[] midpoints; /* bin midpoints */
    private double[] proportions; /* bin proportions */
    private double[] densities; /* bin densities */

    public Cdh(List<Double> values) {
        int n = values.size();
        int k = (int)((Math.floor(Math.log(n) / Math.log(2)) + Math.floor(Math.sqrt(n))) / 2); //log(n) < k < sqrt(n)
        double max = Collections.max(values);
        double min = Collections.min(values);
        this.fit(values, min, max, k);
    }

    public void fit(List<Double> values, double min, double max, int k) {
        this.k = k;
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
            if ((x > min) && (x < max)) {
                int j = (int) ((x - min) / delta);
                this.counts[j]++;
            } else if (x <= min)
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

    @Override
    public void writeToCSV(PrintWriter printer) {
        DecimalFormat f = new DecimalFormat("###0.0000000000000", new DecimalFormatSymbols(Locale.US));

        printer.println(String.join(",", new String[]{
                "mean",
                "stdev",
                "delta",
                "lower",
                "higher"
        }));
        printer.println(String.join(",", new String[]{
                f.format(this.mean),
                f.format(this.stdev),
                f.format(this.delta),
                f.format(this.lower),
                f.format(this.higher)
        }));
        printer.println(String.join(",", new String[]{
                "counts",
                "midpoints",
                "proportions",
                "densities"
        }));
        for (int i = 0; i < this.k; i++)
        {
            printer.println(String.join(",", new String[]{
                    f.format(this.counts[i]),
                    f.format(this.midpoints[i]),
                    f.format(this.proportions[i]),
                    f.format(this.densities[i])
            }));
        }
    }
}
