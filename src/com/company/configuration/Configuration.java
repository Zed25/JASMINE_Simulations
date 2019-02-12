package com.company.configuration;

public class Configuration {
    public static final boolean PRINT_STATIONARY_STATISTICS = false;
    public static final boolean PRINT_BATCH_MEANS = true;
    public static final boolean EXECUTE_ALGORITHM_1 = true;
    public static final boolean EXECUTE_ALGORITHM_2 = false;

    public static final long SEED = 12345;

    public static final double LOC = 0.95;      /* level of confidence. 0.95 equals 95% */

    /* OUTPUT PATHS */
    public static final String STATIONARY_STATISTICS_CSV_PATH = "./outputs/stationary_check_" + SEED + ".csv";

}
