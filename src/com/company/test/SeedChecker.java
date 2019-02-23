package com.company.test;


import com.company.Rvms;
import com.company.model.system.Rngs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SeedChecker {
    public static void main(String[] args) {
        try {

            /* ---------------------------------------------------------------------------------------------------------
             * --------------------------------------------- SEED TESTS ------------------------------------------------
             * ---------------------------------------------------------------------------------------------------------
             * */

            /* Sample distribution test */
            PrintWriter printWriter1 = new PrintWriter("./outputs/seed_checker_12345.csv");
            testRngsSeed(12345, 0, 1, 1000, printWriter1);
            printWriter1.close();

            PrintWriter printWriter2 = new PrintWriter("./outputs/seed_checker_98765.csv");
            testRngsSeed(98765, 0, 1, 1000, printWriter2);
            printWriter2.close();

            PrintWriter printWriter3 = new PrintWriter("./outputs/seed_checker_445679.csv");
            testRngsSeed(445679, 0, 1, 1000, printWriter3);
            printWriter3.close();

            /* Chi-squared test */
            PrintWriter printWriter4 = new PrintWriter("./outputs/seed_checker_chi_12345.csv");
            testRngsSeedChiSquare(12345, 1000, 10 * 1000, 0.05, printWriter1);
            printWriter4.close();

            PrintWriter printWriter5 = new PrintWriter("./outputs/seed_checker_chi_98765.csv");
            testRngsSeedChiSquare(98765, 1000, 10 * 1000, 0.05, printWriter2);
            printWriter5.close();

            PrintWriter printWriter6 = new PrintWriter("./outputs/seed_checker_chi_445679.csv");
            testRngsSeedChiSquare(445679, 1000, 10 * 1000, 0.05, printWriter3);
            printWriter6.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void testRngsSeed(int seed, double min, double max, int count, PrintWriter writer) {
        writer.println("xi,xi+1");                                                  /* write csv header */

        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "US"));
        f.applyPattern("###0.0000000000000");

        Rngs rngs = new Rngs();
        rngs.putSeed(seed); /* plant seed */
        double xi = rngs.random();
        for (int i = 0; i < count; i++) { /* test each generation couple and write it on the csv */
            double xi1 = rngs.random();
            if ((xi >= min || xi <= max) && (xi1 >= min || xi1 <= max)) {
                writer.println(f.format(xi) + "," + f.format(xi1));
                writer.flush();
            }
            xi = xi1;
        }
    }

    private static void testRngsSeedChiSquare(int seed, int k, int n, double alpha, PrintWriter writer) {
        writer.println("Stream,v,v1,v2");                                                      /* write csv header */

        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "US"));
        f.applyPattern("###0.0000000000000");

        Rngs rngs = new Rngs();
        rngs.plantSeeds(seed); /* plant seed */

        Rvms rvms = new Rvms();     /* use rvms to compute chi square inverse distribution function */
        double v1 = rvms.idfChiSquare(k - 1, alpha / 2);
        double v2 = rvms.idfChiSquare(k - 1, 1 - alpha / 2);

        for (int i = 0; i < 256; i++) {
            rngs.selectStream(i); /* change stream */
            double[] o = new double[k];
            for (int j = 0; j < k; j++) /* reset observation array */
                o[j] = 0;

            for (int j = 0; j < n; j++) { /* compute beans and store in observation arrays */
                double u = rngs.random();
                int x = (int) Math.floor(u * k);
                o[x]++;
            }
            double v = 0;           /* Compute chi-square statistics */
            for (int x = 0; x <= k - 1; x++) {
                v += Math.pow(o[x] - n / (double) k, 2) / (n / (double) k);
            }
            writer.println(i + "," + f.format(v) + "," + f.format(v1) + "," + f.format(v2));
            writer.flush();
        }

    }
}
