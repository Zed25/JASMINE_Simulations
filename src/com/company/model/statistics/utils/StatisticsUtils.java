package com.company.model.statistics.utils;

import com.company.Rvms;
import com.company.configuration.Configuration;
import com.company.model.statistics.BatchStatistics;
import com.company.model.utils.CSVPrintable;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StatisticsUtils implements CSVPrintable {
    Rvms rvms;

    public StatisticsUtils() {
        this.rvms = new Rvms();                 /* init random variate models */
    }

    private double[] computeMeanAndConfidenceWidth(List<Double> batchStatistics) {
        /* Welford's algorithm mean and standard deviation computation */
        int n = 0;                                                              /* n = 0 */
        double mean = 0.0;                                                      /* x = 0.0 */
        double v = 0.0;                                                         /* v = 0.0 */

        int i = 0;
        while (i < batchStatistics.size()) {                                    /* while (more data) */
            double sample = batchStatistics.get(i);                             /* x = GetData(); */
            n++;                                                                /* n++; */
            double d = sample - mean;                                           /* d = x - mean; */
            v += d * d * (n - 1) / n;                                             /* v = v + (d * d * (n - 1)/n);*/
            mean += d / n;                                                        /* mean = mean + d/n;  */
            i++;                                                                /* step iterator */
        }
        double stDev = Math.sqrt(v / n);                                          /* s = sqrt(v/n); */

        if (n > 1) {
            double u = 1.0 - 0.5 * (1.0 - Configuration.LOC);                   /* interval parameter  */
            double tStudent = rvms.idfStudent(n - 1, u);                     /* critical value of t */
            double w = tStudent * stDev / Math.sqrt(n - 1);                     /* interval half width */
            return new double[]{mean, w};                                       /* return mean and interval half width */
        }

        return new double[]{0, 0};                                               /* empty batch statistics default values */

    }

    /**
     * -------------------------------------------------------------------------------------------------------------
     * ---------------------------------------- PRINT STATISTICS ---------------------------------------------------
     * -------------------------------------------------------------------------------------------------------------
     */
    public void printStatistics(BatchStatistics batchStatistics, long batchSize, long seed) {

        /* ------------------------------------ Other statistics ----------------------------------------------- */
        if (Configuration.PRINT_OTHER_STATISTICS) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tOther Statistics");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Batch number " + batchStatistics.getBatchMeanStatistics().size());
        }

        /* -------------------------------------- Batch Means ----------------------------------------------- */

        this.writeHeaderIfNecessary(seed);

        List<String> batchValues = new ArrayList<>();

        if (Configuration.EXECUTION_ALGORITHM == Configuration.Algorithms.ALGORITHM_2) {
            batchValues.add(String.valueOf(Configuration.S));
        }
        batchValues.add(String.valueOf(seed));
        batchValues.add(String.valueOf(batchSize));

        DecimalFormat decimalFourZero = new DecimalFormat("###0.000000");

        /* --------------------------------------- (A.3.1 / C.3.1) ------------------------------------------------- */
        double[] batchSystemRespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getSystemRespTime());
        double[] batchClass1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1RespTime());
        double[] batchClass2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2RespTime());
        double[] batchGlobalThr = this.computeMeanAndConfidenceWidth(batchStatistics.getGlobalThr());
        double[] batchClass1Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1Thr());
        double[] batchClass2Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2Thr());

        /* --------------------------------------- (A.3.2 / C.3.2) ------------------------------------------------- */
        double[] batchCletEffClass1Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudletEffectiveClass1Thr());
        double[] batchCletEffClass2Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudletEffectiveClass2Thr());

        /* --------------------------------------- (A.3.3 / C.3.3) ------------------------------------------------- */
        double[] batchCloudClass1Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudClass1Thr());
        double[] batchCloudClass2Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudClass2Thr());

        /* --------------------------------------- (A.3.4 / C.3.4) ------------------------------------------------- */
        double[] batchCletClass1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CletRespTime());
        double[] batchCletClass2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CletRespTime());
        double[] batchCloudClass1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CloudRespTime());
        double[] batchCloudClass2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CloudRespTime());
        double[] batchCloudletClass1MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CletMeanPop());
        double[] batchCloudletClass2MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CletMeanPop());
        double[] batchCloudClass1MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CloudMeanPop());
        double[] batchCloudClass2MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CloudMeanPop());

        if (Configuration.VERBOSE) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.1 / C.2.1");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("System Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchSystemRespTime[0]) + " ± " + decimalFourZero.format(batchSystemRespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchSystemRespTime[0] - batchSystemRespTime[1]) + " , "
                    + decimalFourZero.format(batchSystemRespTime[0] + batchSystemRespTime[1]) + "]\n");
            System.out.println("Class 1 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass1RespTime[0]) + " ± " + decimalFourZero.format(batchClass1RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass1RespTime[0] - batchClass1RespTime[1]) + " , "
                    + decimalFourZero.format(batchClass1RespTime[0] + batchClass1RespTime[1]) + "]\n");
            System.out.println("Class 2 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass2RespTime[0]) + " ± " + decimalFourZero.format(batchClass2RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass2RespTime[0] - batchClass2RespTime[1]) + " , "
                    + decimalFourZero.format(batchClass2RespTime[0] + batchClass2RespTime[1]) + "]\n");
            System.out.println("Global Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchGlobalThr[0]) + " ± " + decimalFourZero.format(batchGlobalThr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchGlobalThr[0] - batchGlobalThr[1]) + " , "
                    + decimalFourZero.format(batchGlobalThr[0] + batchGlobalThr[1]) + "]\n");
            System.out.println("Class 1 Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass1Thr[0]) + " ± " + decimalFourZero.format(batchClass1Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass1Thr[0] - batchClass1Thr[1]) + " , "
                    + decimalFourZero.format(batchClass1Thr[0] + batchClass1Thr[1]) + "]\n");
            System.out.println("Class 2 Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass2Thr[0]) + " ± " + decimalFourZero.format(batchClass2Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass2Thr[0] - batchClass2Thr[1]) + " , "
                    + decimalFourZero.format(batchClass2Thr[0] + batchClass2Thr[1]) + "]\n");
        }

        /* print to csv */
        batchValues.add(decimalFourZero.format(batchSystemRespTime[0]));
        batchValues.add(decimalFourZero.format(batchSystemRespTime[1]));
        batchValues.add(decimalFourZero.format(batchSystemRespTime[0] - batchSystemRespTime[1]));
        batchValues.add(decimalFourZero.format(batchSystemRespTime[0] + batchSystemRespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchClass1RespTime[0]));
        batchValues.add(decimalFourZero.format(batchClass1RespTime[1]));
        batchValues.add(decimalFourZero.format(batchClass1RespTime[0] - batchClass1RespTime[1]));
        batchValues.add(decimalFourZero.format(batchClass1RespTime[0] + batchClass1RespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchClass2RespTime[0]));
        batchValues.add(decimalFourZero.format(batchClass2RespTime[1]));
        batchValues.add(decimalFourZero.format(batchClass2RespTime[0] - batchClass2RespTime[1]));
        batchValues.add(decimalFourZero.format(batchClass2RespTime[0] + batchClass2RespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchGlobalThr[0]));
        batchValues.add(decimalFourZero.format(batchGlobalThr[1]));
        batchValues.add(decimalFourZero.format(batchGlobalThr[0] - batchGlobalThr[1]));
        batchValues.add(decimalFourZero.format(batchGlobalThr[0] + batchGlobalThr[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchClass1Thr[0]));
        batchValues.add(decimalFourZero.format(batchClass1Thr[1]));
        batchValues.add(decimalFourZero.format(batchClass1Thr[0] - batchClass1Thr[1]));
        batchValues.add(decimalFourZero.format(batchClass1Thr[0] + batchClass1Thr[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchClass2Thr[0]));
        batchValues.add(decimalFourZero.format(batchClass2Thr[1]));
        batchValues.add(decimalFourZero.format(batchClass2Thr[0] - batchClass2Thr[1]));
        batchValues.add(decimalFourZero.format(batchClass2Thr[0] + batchClass2Thr[1]));

        if (Configuration.VERBOSE) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.2 / C.2.2");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Class 1 Cloudlet Effective Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletEffClass1Thr[0]) + " ± " + decimalFourZero.format(batchCletEffClass1Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletEffClass1Thr[0] - batchCletEffClass1Thr[1]) + " , "
                    + decimalFourZero.format(batchCletEffClass1Thr[0] + batchCletEffClass1Thr[1]) + "]\n");
            System.out.println("Class 2 Cloudlet Effective Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletEffClass2Thr[0]) + " ± " + decimalFourZero.format(batchCletEffClass2Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletEffClass2Thr[0] - batchCletEffClass2Thr[1]) + " , "
                    + decimalFourZero.format(batchCletEffClass2Thr[0] + batchCletEffClass2Thr[1]) + "]\n");
        }

        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCletEffClass1Thr[0]));
        batchValues.add(decimalFourZero.format(batchCletEffClass1Thr[1]));
        batchValues.add(decimalFourZero.format(batchCletEffClass1Thr[0] - batchCletEffClass1Thr[1]));
        batchValues.add(decimalFourZero.format(batchCletEffClass1Thr[0] + batchCletEffClass1Thr[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCletEffClass2Thr[0]));
        batchValues.add(decimalFourZero.format(batchCletEffClass2Thr[1]));
        batchValues.add(decimalFourZero.format(batchCletEffClass2Thr[0] - batchCletEffClass2Thr[1]));
        batchValues.add(decimalFourZero.format(batchCletEffClass2Thr[0] + batchCletEffClass2Thr[1]));

        if (Configuration.VERBOSE) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.3 / C.2.3");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Class 1 Cloud Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass1Thr[0]) + " ± " + decimalFourZero.format(batchCloudClass1Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass1Thr[0] - batchCloudClass1Thr[1]) + " , "
                    + decimalFourZero.format(batchCloudClass1Thr[0] + batchCloudClass1Thr[1]) + "]\n");
            System.out.println("Class 2 Cloud Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass2Thr[0]) + " ± " + decimalFourZero.format(batchCloudClass2Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass2Thr[0] - batchCloudClass2Thr[1]) + " , "
                    + decimalFourZero.format(batchCloudClass2Thr[0] + batchCloudClass2Thr[1]) + "]\n");
        }

        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudClass1Thr[0]));
        batchValues.add(decimalFourZero.format(batchCloudClass1Thr[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass1Thr[0] - batchCloudClass1Thr[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass1Thr[0] + batchCloudClass1Thr[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudClass2Thr[0]));
        batchValues.add(decimalFourZero.format(batchCloudClass2Thr[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass2Thr[0] - batchCloudClass2Thr[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass2Thr[0] + batchCloudClass2Thr[1]));

        if (Configuration.VERBOSE) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.4 / C.2.4");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Class 1 Cloudlet Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletClass1RespTime[0]) + " ± " + decimalFourZero.format(batchCletClass1RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletClass1RespTime[0] - batchCletClass1RespTime[1]) + " , "
                    + decimalFourZero.format(batchCletClass1RespTime[0] + batchCletClass1RespTime[1]) + "]\n");
            System.out.println("Class 2 Cloudlet Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletClass2RespTime[0]) + " ± " + decimalFourZero.format(batchCletClass2RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletClass2RespTime[0] - batchCletClass2RespTime[1]) + " , "
                    + decimalFourZero.format(batchCletClass2RespTime[0] + batchCletClass2RespTime[1]) + "]\n");
            System.out.println("Class 1 Cloud Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass1RespTime[0]) + " ± " + decimalFourZero.format(batchCloudClass1RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass1RespTime[0] - batchCloudClass1RespTime[1]) + " , "
                    + decimalFourZero.format(batchCloudClass1RespTime[0] + batchCloudClass1RespTime[1]) + "]\n");
            System.out.println("Class 2 Cloud Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass2RespTime[0]) + " ± " + decimalFourZero.format(batchCloudClass2RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass2RespTime[0] - batchCloudClass2RespTime[1]) + " , "
                    + decimalFourZero.format(batchCloudClass2RespTime[0] + batchCloudClass2RespTime[1]) + "]\n");
            System.out.println("Class 1 Cloudlet Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudletClass1MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudletClass1MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudletClass1MeanPop[0] - batchCloudletClass1MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudletClass1MeanPop[0] + batchCloudletClass1MeanPop[1]) + "]\n");
            System.out.println("Class 2 Cloudlet Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudletClass2MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudletClass2MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudletClass2MeanPop[0] - batchCloudletClass2MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudletClass2MeanPop[0] + batchCloudletClass2MeanPop[1]) + "]\n");
            System.out.println("Class 1 Cloud Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass1MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudClass1MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass1MeanPop[0] - batchCloudClass1MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudClass1MeanPop[0] + batchCloudClass1MeanPop[1]) + "]\n");
            System.out.println("Class 2 Cloud Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass2MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudClass2MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass2MeanPop[0] - batchCloudClass2MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudClass2MeanPop[0] + batchCloudClass2MeanPop[1]) + "]\n");
        }

        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCletClass1RespTime[0]));
        batchValues.add(decimalFourZero.format(batchCletClass1RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCletClass1RespTime[0] - batchCletClass1RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCletClass1RespTime[0] + batchCletClass1RespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCletClass2RespTime[0]));
        batchValues.add(decimalFourZero.format(batchCletClass2RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCletClass2RespTime[0] - batchCletClass2RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCletClass2RespTime[0] + batchCletClass2RespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudClass1RespTime[0]));
        batchValues.add(decimalFourZero.format(batchCloudClass1RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass1RespTime[0] - batchCloudClass1RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass1RespTime[0] + batchCloudClass1RespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudClass2RespTime[0]));
        batchValues.add(decimalFourZero.format(batchCloudClass2RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass2RespTime[0] - batchCloudClass2RespTime[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass2RespTime[0] + batchCloudClass2RespTime[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudletClass1MeanPop[0]));
        batchValues.add(decimalFourZero.format(batchCloudletClass1MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudletClass1MeanPop[0] - batchCloudletClass1MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudletClass1MeanPop[0] + batchCloudletClass1MeanPop[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudletClass2MeanPop[0]));
        batchValues.add(decimalFourZero.format(batchCloudletClass2MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudletClass2MeanPop[0] - batchCloudletClass2MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudletClass2MeanPop[0] + batchCloudletClass2MeanPop[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudClass1MeanPop[0]));
        batchValues.add(decimalFourZero.format(batchCloudClass1MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass1MeanPop[0] - batchCloudClass1MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass1MeanPop[0] + batchCloudClass1MeanPop[1]));
        /* print to csv */
        batchValues.add(decimalFourZero.format(batchCloudClass2MeanPop[0]));
        batchValues.add(decimalFourZero.format(batchCloudClass2MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass2MeanPop[0] - batchCloudClass2MeanPop[1]));
        batchValues.add(decimalFourZero.format(batchCloudClass2MeanPop[0] + batchCloudClass2MeanPop[1]));


        /* ------------------------------------------- (C.3.6) ----------------------------------------------------- */
        if (Configuration.EXECUTION_ALGORITHM == Configuration.Algorithms.ALGORITHM_2) {
            double[] batchClass2InterruptedPercentage = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2InterruptedPercentage());
            double[] batchClass2InterruptedRespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2InterruptedRespTime());

            if (Configuration.VERBOSE) {
                System.out.println("\n-------------------------------------------------------------");
                System.out.println("\t\t\t\t\t\tC.2.6");
                System.out.println("-------------------------------------------------------------\n");
                System.out.println("Class 2 Interrupted Percentage " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchClass2InterruptedPercentage[0]) + " ± " + decimalFourZero.format(batchClass2InterruptedPercentage[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchClass2InterruptedPercentage[0] - batchClass2InterruptedPercentage[1]) + " , "
                        + decimalFourZero.format(batchClass2InterruptedPercentage[0] + batchClass2InterruptedPercentage[1]) + "] ======> "
                        + decimalFourZero.format(batchClass2InterruptedPercentage[0] * 100) + "% ± "
                        + decimalFourZero.format(batchClass2InterruptedPercentage[1] * 100) +
                        "% ----> ["
                        + decimalFourZero.format((batchClass2InterruptedPercentage[0] - batchClass2InterruptedPercentage[1]) * 100)
                        + "% , "
                        + decimalFourZero.format((batchClass2InterruptedPercentage[0] + batchClass2InterruptedPercentage[1]) * 100) + "%]\n");
                System.out.println("Class 2 Interrupted Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchClass2InterruptedRespTime[0]) + " ± " + decimalFourZero.format(batchClass2InterruptedRespTime[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchClass2InterruptedRespTime[0] - batchClass2InterruptedRespTime[1]) + " , "
                        + decimalFourZero.format(batchClass2InterruptedRespTime[0] + batchClass2InterruptedRespTime[1]) + "]\n");
            }

            /* print to csv */
            batchValues.add(decimalFourZero.format(batchClass2InterruptedPercentage[0]));
            batchValues.add(decimalFourZero.format(batchClass2InterruptedPercentage[1]));
            batchValues.add(decimalFourZero.format(batchClass2InterruptedPercentage[0] - batchClass2InterruptedPercentage[1]));
            batchValues.add(decimalFourZero.format(batchClass2InterruptedPercentage[0] + batchClass2InterruptedPercentage[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchClass2InterruptedRespTime[0]));
            batchValues.add(decimalFourZero.format(batchClass2InterruptedRespTime[1]));
            batchValues.add(decimalFourZero.format(batchClass2InterruptedRespTime[0] - batchClass2InterruptedRespTime[1]));
            batchValues.add(decimalFourZero.format(batchClass2InterruptedRespTime[0] + batchClass2InterruptedRespTime[1]));
        }

        /* ------------------------------------------- Hyperexp phases ----------------------------------------------------- */
        if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {
            double[] batchN1F1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN1F1RespTime());
            double[] batchN1F2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN1F2RespTime());
            double[] batchN2F1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN2F1RespTime());
            double[] batchN2F2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN2F2RespTime());
            double[] batchN1F1MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN1F1MeanPop());
            double[] batchN1F2MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN1F2MeanPop());
            double[] batchN2F1MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN2F1MeanPop());
            double[] batchN2F2MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getProcessedN2F2MeanPop());

            if (Configuration.VERBOSE) {
                System.out.println("\n-------------------------------------------------------------");
                System.out.println("\t\t\t\t\t\tHyperexponential phases");
                System.out.println("-------------------------------------------------------------\n");
                System.out.println("N1 F1 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN1F1RespTime[0]) + " ± " + decimalFourZero.format(batchN1F1RespTime[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN1F1RespTime[0] - batchN1F1RespTime[1]) + " , "
                        + decimalFourZero.format(batchN1F1RespTime[0] + batchN1F1RespTime[1]) + "]\n");
                System.out.println("N1 F2 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN1F2RespTime[0]) + " ± " + decimalFourZero.format(batchN1F2RespTime[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN1F2RespTime[0] - batchN1F2RespTime[1]) + " , "
                        + decimalFourZero.format(batchN1F2RespTime[0] + batchN1F2RespTime[1]) + "]\n");
                System.out.println("N2 F1 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN2F1RespTime[0]) + " ± " + decimalFourZero.format(batchN2F1RespTime[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN2F1RespTime[0] - batchN2F1RespTime[1]) + " , "
                        + decimalFourZero.format(batchN2F1RespTime[0] + batchN2F1RespTime[1]) + "]\n");
                System.out.println("N2 F2 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN2F2RespTime[0]) + " ± " + decimalFourZero.format(batchN2F2RespTime[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN2F2RespTime[0] - batchN2F2RespTime[1]) + " , "
                        + decimalFourZero.format(batchN2F2RespTime[0] + batchN2F2RespTime[1]) + "]\n");
                System.out.println("N1 F1 Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN1F1MeanPop[0]) + " ± " + decimalFourZero.format(batchN1F1MeanPop[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN1F1MeanPop[0] - batchN1F1MeanPop[1]) + " , "
                        + decimalFourZero.format(batchN1F1MeanPop[0] + batchN1F1MeanPop[1]) + "]\n");
                System.out.println("N1 F2 Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN1F2MeanPop[0]) + " ± " + decimalFourZero.format(batchN1F2MeanPop[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN1F2MeanPop[0] - batchN1F2MeanPop[1]) + " , "
                        + decimalFourZero.format(batchN1F2MeanPop[0] + batchN1F2MeanPop[1]) + "]\n");
                System.out.println("N2 F1 Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN2F1MeanPop[0]) + " ± " + decimalFourZero.format(batchN2F1MeanPop[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN2F1MeanPop[0] - batchN2F1MeanPop[1]) + " , "
                        + decimalFourZero.format(batchN2F1MeanPop[0] + batchN2F1MeanPop[1]) + "]\n");
                System.out.println("N2 F2 Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
                System.out.println(decimalFourZero.format(batchN2F2MeanPop[0]) + " ± " + decimalFourZero.format(batchN2F2MeanPop[1]) +
                        " ----> ["
                        + decimalFourZero.format(batchN2F2MeanPop[0] - batchN2F2MeanPop[1]) + " , "
                        + decimalFourZero.format(batchN2F2MeanPop[0] + batchN2F2MeanPop[1]) + "]\n");
            }

            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN1F1RespTime[0]));
            batchValues.add(decimalFourZero.format(batchN1F1RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN1F1RespTime[0] - batchN1F1RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN1F1RespTime[0] + batchN1F1RespTime[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN1F2RespTime[0]));
            batchValues.add(decimalFourZero.format(batchN1F2RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN1F2RespTime[0] - batchN1F2RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN1F2RespTime[0] + batchN1F2RespTime[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN2F1RespTime[0]));
            batchValues.add(decimalFourZero.format(batchN2F1RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN2F1RespTime[0] - batchN2F1RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN2F1RespTime[0] + batchN2F1RespTime[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN2F2RespTime[0]));
            batchValues.add(decimalFourZero.format(batchN2F2RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN2F2RespTime[0] - batchN2F2RespTime[1]));
            batchValues.add(decimalFourZero.format(batchN2F2RespTime[0] + batchN2F2RespTime[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN1F1MeanPop[0]));
            batchValues.add(decimalFourZero.format(batchN1F1MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN1F1MeanPop[0] - batchN1F1MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN1F1MeanPop[0] + batchN1F1MeanPop[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN1F2MeanPop[0]));
            batchValues.add(decimalFourZero.format(batchN1F2MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN1F2MeanPop[0] - batchN1F2MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN1F2MeanPop[0] + batchN1F2MeanPop[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN2F1MeanPop[0]));
            batchValues.add(decimalFourZero.format(batchN2F1MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN2F1MeanPop[0] - batchN2F1MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN2F1MeanPop[0] + batchN2F1MeanPop[1]));
            /* print to csv */
            batchValues.add(decimalFourZero.format(batchN2F2MeanPop[0]));
            batchValues.add(decimalFourZero.format(batchN2F2MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN2F2MeanPop[0] - batchN2F2MeanPop[1]));
            batchValues.add(decimalFourZero.format(batchN2F2MeanPop[0] + batchN2F2MeanPop[1]));
        }

        /* write results to CSV */
        this.writeToCSV(Configuration.STATISTICS_FILE_PATH_PREFIX + seed + Configuration.STATISTICS_FILE_FORMAT, batchValues.toArray(new String[0]));
    }

    private void writeHeaderIfNecessary(long seed) {
        /* Write csv header */
        if (!new File(Configuration.STATISTICS_FILE_PATH_PREFIX + seed + Configuration.STATISTICS_FILE_FORMAT).exists()) {
            LinkedList<String> headerList = new LinkedList<>(Arrays.asList(
                    "Seed",
                    "Batch Size",
                    "MRT", "±", "min", "max",
                    "MRT Class 1", "±", "min", "max",
                    "MRT Class 2", "±", "min", "max",
                    "Thr", "±", "min", "max",
                    "Thr Class 1", "±", "min", "max",
                    "Thr Class 2", "±", "min", "max",
                    "Thr Cloudlet Class 1", "±", "min", "max",
                    "Thr Cloudlet Class 2", "±", "min", "max",
                    "Thr Cloud Class 1", "±", "min", "max",
                    "Thr Cloud Class 2", "±", "min", "max",
                    "MRT Cloudlet Class 1", "±", "min", "max",
                    "MRT Cloudlet Class 2", "±", "min", "max",
                    "MRT Cloud Class 1", "±", "min", "max",
                    "MRT Cloud Class 2", "±", "min", "max",
                    "MP Cloudlet Class 1", "±", "min", "max",
                    "MP Cloudlet Class 2", "±", "min", "max",
                    "MP Cloud Class 1", "±", "min", "max",
                    "MP Cloud Class 2", "±", "min", "max"
            ));

            if (Configuration.EXECUTION_ALGORITHM == Configuration.Algorithms.ALGORITHM_2) {
                headerList.add(0, "S");
            }

            if (Configuration.EXECUTION_ALGORITHM == Configuration.Algorithms.ALGORITHM_2) {
                headerList.addAll(Arrays.asList(
                        "MRT Interrupted", "±", "min", "max",
                        "% Interrupted", "±", "min", "max"
                ));
            }

            if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {
                headerList.addAll(Arrays.asList(
                        "MRT N1F1", "±", "min", "max",
                        "MRT N1F2", "±", "min", "max",
                        "MRT N2F1", "±", "min", "max",
                        "MRT N2F2", "±", "min", "max",
                        "MP N1F1", "±", "min", "max",
                        "MP N1F2", "±", "min", "max",
                        "MP N2F1", "±", "min", "max",
                        "MP N2F2", "±", "min", "max"
                        ));
            }

            String[] header = new String[headerList.size()];
            for (int i = 0; i < header.length; i++) {
                header[i] = headerList.get(i);
            }

            this.writeToCSV(Configuration.STATISTICS_FILE_PATH_PREFIX + seed + Configuration.STATISTICS_FILE_FORMAT, header);
        }
    }
}
