package com.company.configuration;

public class Configuration {

    /* CONTROLLER ALGORITHM CONFIGURATIONS */
    public static final Algorithms EXECUTION_ALGORITHM = Algorithms.ALGORITHM_2;     /* controller algorithm */
    /* STATISTICS CONFIGURATIONS */
    public static final boolean PRINT_OTHER_STATISTICS = false;
    public static final double LOC = 0.95;                      /* level of confidence. 0.95 equals 95% */
    public static final long BATCH_SIZE = 32;               /* batch size */


    public static final boolean FINITE_HORIZON = true;
    public static final double BATCH_SIZE_MULTIPLIER = 2;

    public static final int SIMULATION_REPEAT_TIMES = 3;
    public static final int BATCH_REPEAT_TIMES = 15;
    public static final long OBSERVATIONS = 20;

    /* RNGS CONFIGURATIONS */
    public static final long SEED = 445679;                     /* initial seed */
    /* ENVIRONMENT CONFIGURATION */
    public static final boolean CLOUDLET_HYPEREXP_SERVICE = true;   /* if true -> hyperexp service
                                                                        else exponential service */
    public static final int N = 20;                                 /* number of cloudlet server */
    public static final int S = 10;                                 /* threshold S */
    public static final double LAMBDA_1 = 6.0;                      /* CLASS1 arrival rate */
    public static final double LAMBDA_2 = 6.25;                     /* CLASS2 arrival rate */
    public static final double MU_1_CLET = 0.45;                    /* cloudlet CLASS1 service rate */
    public static final double MU_2_CLET = 0.27;                    /* cloudlet CLASS2 service rate */
    public static final double MU_1_CLOUD = 0.25;                   /* cloud CLASS1 service rate */
    public static final double MU_2_CLOUD = 0.22;                   /* cloud CLASS2 service rate */
    public static final double SETUP_TIME = 0.8;                    /* cloud interrupted job setup time */
    public static final double HYPEREXP_PROB = 0.2;                 /* Hyperexponential probability */
    public static final double STOP = 4000000.0;                       /* stop time */

    public static final boolean VERBOSE = false;

    /* OUTPUT PATHS CONFIGURATION */
    public static final String STATISTICS_FILE_PATH_PREFIX = "./outputs/statistics_";
    public static final String STATISTICS_FILE_FORMAT = ".csv";

    public enum Algorithms {
        ALGORITHM_1, ALGORITHM_2
    }

}
