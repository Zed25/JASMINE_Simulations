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
            /*
            PrintWriter printWriter1 = new PrintWriter("./outputs/seed_checker_12345.csv");
            testRngsSeed(12345, 0, 1, 1000, printWriter1);
            printWriter1.close();

            PrintWriter printWriter2 = new PrintWriter("./outputs/seed_checker_98765.csv");
            testRngsSeed(98765, 0, 1, 1000, printWriter2);
            printWriter2.close();

            PrintWriter printWriter3 = new PrintWriter("./outputs/seed_checker_445679.csv");
            testRngsSeed(445679, 0, 1, 1000, printWriter3);
            printWriter3.close();
            */

            PrintWriter printWriter1 = new PrintWriter("./outputs/seed_checker_chi_12345.csv");
            testRngsSeedChiSquare(12345, 1000, 10 * 1000, 0.05, printWriter1);
            printWriter1.close();

            PrintWriter printWriter2 = new PrintWriter("./outputs/seed_checker_chi_98765.csv");
            testRngsSeedChiSquare(98765, 1000, 10 * 1000, 0.05, printWriter2);
            printWriter2.close();

            PrintWriter printWriter3 = new PrintWriter("./outputs/seed_checker_chi_445679.csv");
            testRngsSeedChiSquare(445679, 1000, 10 * 1000, 0.05, printWriter3);
            printWriter3.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void testRngsSeed(int seed, double min, double max, int count, PrintWriter writer) {
        writer.println("xi,xi+1");

        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "US"));
        f.applyPattern("###0.0000000000000");

        if (count == -1)
            count = 2147483646;

        Rngs rngs = new Rngs();
        rngs.putSeed(seed);
        double xi = rngs.random();
        for (int i = 0; i < count; i++) {
            double xi1 = rngs.random();
            if ((xi >= min || xi <= max) && (xi1 >= min || xi1 <= max)) {
                writer.println(f.format(xi) + "," + f.format(xi1));
                writer.flush();
            }
            xi = xi1;
        }
        writer.close();
    }

    private static void testRngsSeedChiSquare(int seed, int k, int n, double alpha, PrintWriter writer) {
        writer.println("Stream,v,v1,v2");

        DecimalFormat f = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "US"));
        f.applyPattern("###0.0000000000000");

        Rngs rngs = new Rngs();
        rngs.plantSeeds(seed);

        Rvms rvms = new Rvms();
        double v1 = rvms.idfChiSquare(k - 1, alpha / 2);
        double v2 = rvms.idfChiSquare(k - 1, 1 - alpha / 2);

        for (int i = 0; i < 256; i++) {
            rngs.selectStream(i);
            double[] o = new double[k];
            for (int j = 0; j < k; j++)
                o[j] = 0;

            for (int j = 0; j < n; j++) {
                double u = rngs.random();
                int x = (int) Math.floor(u * k);
                o[x]++;
            }
            // Compute v
            double v = 0;
            for (int x = 0; x <= k - 1; x++) {
                v += Math.pow(o[x] - n / (double) k, 2) / (n / (double) k);
            }
            writer.println(i + "," + f.format(v) + "," + f.format(v1) + "," + f.format(v2));
            writer.flush();
        }
        writer.close();

    }
}
