package com.company.configuration;

public class Configuration {

    public enum Algorithms {
        ALGORITHM_1, ALGORITHM_2
    }

    public static final boolean EXEC_STATIONARY_STATISTICS = false;
    public static final boolean EXEC_BATCH_MEANS = true;
    public static final Algorithms EXECUTION_ALGORITHM = Algorithms.ALGORITHM_2;     /* controller algorithm */
    public static final boolean EXECUTE_CDH = false;

    public static final boolean PRINT_OTHER_STATISTICS = true;

    public static final long SEED = 12345;

    public static final double LOC = 0.95;                      /* level of confidence. 0.95 equals 95% */

    /* ENVIRONMENT */
    public static final boolean CLOUDLET_HYPEREXP_SERVICE = false;
    public static final double STOP = 4000000.0;
    public static final int N = 20;
    public static final int S = 20;
    public static final double LAMBDA_1 = 6.0;                  /* CLASS1 arrival rate */
    public static final double LAMBDA_2 = 6.25;                 /* CLASS2 arrival rate */
    public static final double MU_1_CLET = 0.45;                /* cloudlet CLASS1 service rate */
    public static final double MU_2_CLET = 0.27;                /* cloudlet CLASS2 service rate */
    public static final double MU_1_CLOUD = 0.25;               /* cloud CLASS1 service rate */
    public static final double MU_2_CLOUD = 0.22;               /* cloud CLASS2 service rate */
    public static final double SETUP_TIME = 0.8;                /* cloud interrupted job setup time */
    public static final double HYPEREXP_PROB = 0.2;             /* Hyperexponential probability */


    public static final long BATCH_SIZE = 100000;

    /* OUTPUT PATHS */
    public static final String STATIONARY_STATISTICS_CSV_PATH = "./outputs/stationary_check_" + SEED + ".csv";
    public static final String THRESHOLD_STATISTICS_FILE_PATH = "./outputs/threshold_statistics_" + SEED + ".csv";

}
