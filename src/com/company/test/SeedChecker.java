package com.company.test;


import com.company.model.system.Rngs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SeedChecker {
    public static void main(String[] args) {
        try {
            PrintWriter printWriter1 = new PrintWriter("./outputs/seed_checker_12345.csv");
            testRngsSeed(12345, 0, 1, 1000, printWriter1);
            printWriter1.close();

            PrintWriter printWriter2 = new PrintWriter("./outputs/seed_checker_98765.csv");
            testRngsSeed(98765, 0, 1, 1000, printWriter2);
            printWriter2.close();

            PrintWriter printWriter3 = new PrintWriter("./outputs/seed_checker_445679.csv");
            testRngsSeed(445679, 0, 1, 1000, printWriter3);
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
    }
}
