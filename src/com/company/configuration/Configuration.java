package com.company.configuration;

public class Configuration {

    /* CONTROLLER ALGORITHM CONFIGURATIONS */
    public enum Algorithms {
        ALGORITHM_1, ALGORITHM_2
    }
    public static final Algorithms EXECUTION_ALGORITHM = Algorithms.ALGORITHM_1;     /* controller algorithm */

    /* STATISTICS CONFIGURATIONS */
    public static final boolean PRINT_OTHER_STATISTICS = false; /* print less relevant statistics */
    public static final double LOC = 0.95;                      /* level of confidence. 0.95 equals 95% */
    public static final long BATCH_SIZE = 100000;               /* batch size */

    /* SIMULATION CONFIGURATIONS */
    public static final boolean FINITE_HORIZON = false;         /* set it to false if run is INFINITE HORIZON simulation */
    public static final double BATCH_SIZE_MULTIPLIER = 2;       /* batch size increment among finite horizon simulations  */

    public static final int SIMULATION_REPEAT_TIMES = 3;        /* number of performed simulations */
    public static final int BATCH_REPEAT_TIMES = 15;            /* number of batch size's increments for each simulation */
    public static final long OBSERVATIONS = 20;                 /* number of observations for each batch size */

    /* RNGS CONFIGURATIONS */
    public static final long SEED = 445679;                     /* initial seed */

    /* ENVIRONMENT CONFIGURATIONS */
    public static final boolean CLOUDLET_HYPEREXP_SERVICE = false;   /* if true -> hyperexp service
                                                                        else exponential service */
    public static final int N = 20;                                  /* number of cloudlet server */
    public static final int S = 10;                                  /* threshold S */
    public static final double LAMBDA_1 = 6.0;                       /* CLASS1 arrival rate */
    public static final double LAMBDA_2 = 6.25;                      /* CLASS2 arrival rate */
    public static final double MU_1_CLET = 0.45;                     /* cloudlet CLASS1 service rate */
    public static final double MU_2_CLET = 0.27;                     /* cloudlet CLASS2 service rate */
    public static final double MU_1_CLOUD = 0.25;                    /* cloud CLASS1 service rate */
    public static final double MU_2_CLOUD = 0.22;                    /* cloud CLASS2 service rate */
    public static final double SETUP_TIME = 0.8;                     /* cloud interrupted job setup time */
    public static final double HYPEREXP_PROB = 0.2;                  /* Hyperexponential probability */
    public static final double STOP = 4000000.0;                     /* stop time */

    /* OUTPUT CONFIGURATIONS */
    public static final boolean VERBOSE = false;                     /* verbose mode */
    public static final String STATISTICS_FILE_PATH_PREFIX = "./outputs/statistics_";
    public static final String STATISTICS_FILE_FORMAT = ".csv";


}
