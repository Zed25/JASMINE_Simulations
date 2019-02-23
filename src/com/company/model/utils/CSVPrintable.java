package com.company.model.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * ---------------------------------------------------------------------------------------------------------------------
 * --------------------------------------- CSV Printable Interface -----------------------------------------------------
 * ---------------------------------------------------------------------------------------------------------------------
 * */
public interface CSVPrintable {
    default void writeToCSV(PrintWriter printer){}

    default void writeToCSV(String path, String[] values) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(path), true));
            printWriter.println(String.join(",", Arrays.asList(values)));
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    default void writeToCSVFile(String path) {
        try {
            PrintWriter printWriter = new PrintWriter(path);
            this.writeToCSV(printWriter);
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
