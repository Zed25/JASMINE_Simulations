package com.company.model.statistics.utils;

import com.company.Rvms;
import com.company.configuration.Configuration;

import java.util.List;

public class StatisticsUtils {
    Rvms rvms;

    public StatisticsUtils() {
        this.rvms = new Rvms();                 /* init random variate models */
    }

    public double[] computeMeanAndConfidenceWidth(List<Double> batchStatistics) {
        /* Welford's algorithm mean and standard deviation computation */
        int n = 0;                                                              /* n = 0 */
        double mean = 0.0;                                                      /* x = 0.0 */
        double v = 0.0;                                                         /* v = 0.0 */

        int i = 0;
        while (i < batchStatistics.size()) {                                    /* while (more data) */
            double sample = batchStatistics.get(i);                             /* x = GetData(); */
            n++;                                                                /* n++; */
            double d = sample - mean;                                           /* d = x - mean; */
            v += d * d * (n - 1)/n;                                             /* v = v + (d * d * (n - 1)/n);*/
            mean += d/n;                                                        /* mean = mean + d/n;  */
            i++;                                                                /* step iterator */
        }
        double stDev = Math.sqrt(v/n);                                          /* s = sqrt(v/n); */

        if (n > 1) {
            double u = 1.0 - 0.5 * (1.0 - Configuration.LOC);                   /* interval parameter  */
            double tStudent = rvms.idfStudent(n - 1, u);                     /* critical value of t */
            double w = tStudent * stDev / Math.sqrt(n - 1);                     /* interval half width */
            return new double[]{mean, w};                                       /* return mean and interval half width */
        }

        return new double[]{0,0};                                               /* empty batch statistics default values */

    }
}
