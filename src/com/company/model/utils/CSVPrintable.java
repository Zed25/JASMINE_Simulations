package com.company.model.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public interface CSVPrintable {
    void writeToCSV(PrintWriter printer);

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
