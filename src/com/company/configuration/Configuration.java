package com.company.configuration;

public class Configuration {
    public static final boolean EXEC_STATIONARY_STATISTICS = false;
    public static final boolean EXEC_BATCH_MEANS = true;
    public static final boolean EXECUTE_ALGORITHM_1 = true;
    public static final boolean EXECUTE_ALGORITHM_2 = false;

    public static final long SEED = 12345;

    public static final double LOC = 0.95;      /* level of confidence. 0.95 equals 95% */

    /* ENVIRONMENT */
    public static final boolean CLOUDLET_HYPEREXP_SERVICE = true;
    public static final double STOP = 200000.0;
    public static final int N = 20;
    public static final double LAMBDA_1 = 6.0;             /* CLASS1 arrival rate */
    public static final double LAMBDA_2 = 6.25;            /* CLASS2 arrival rate */
    public static final double MU_1_CLET = 0.45;        /* cloudlet CLASS1 service rate */
    public static final double MU_2_CLET = 0.27;        /* cloudlet CLASS2 service rate */
    public static final double MU_1_CLOUD = 0.25;           /* cloud CLASS1 service rate */
    public static final double MU_2_CLOUD = 0.22;           /* cloud CLASS2 service rate */
    public static final double HYPEREXP_PROB = 0.2;        /* Hyperexponential probability */


    public static final long BATCH_SIZE = 100000;

    /* OUTPUT PATHS */
    public static final String STATIONARY_STATISTICS_CSV_PATH = "./outputs/stationary_check_" + SEED + ".csv";


}
