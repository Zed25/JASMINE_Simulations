package com.company;

import com.company.configuration.Configuration;
import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.event.CloudEvent;
import com.company.model.event.NextEventInfo;
import com.company.model.event.enumeration.ClassType;
import com.company.model.event.CloudletEvent;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;
import com.company.model.statistics.BaseStatistics;
import com.company.model.statistics.BatchStatistics;
import com.company.model.statistics.StationaryStatistics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private final double START = 0.0;               /* initial (open the door) */
    private final double STOP = Configuration.STOP;           /* terminal (close the door) time */
    private final double INFINITY = 100 * STOP;       /* infinity, much bigger than STOP */

    /* INPUT VARIABLES */
    private final int N = Configuration.N;                       /* cloudlet threshold (cloudlet servers number) */
    private final double lambda1 = Configuration.LAMBDA_1;             /* CLASS1 arrival rate */
    private final double lambda2 = Configuration.LAMBDA_2;            /* CLASS2 arrival rate */
    private final double mu1Cloudlet = Configuration.MU_1_CLET;        /* cloudlet CLASS1 service rate */
    private final double mu2Cloudlet = Configuration.MU_2_CLET;        /* cloudlet CLASS2 service rate */
    private final double mu1Cloud = Configuration.MU_1_CLOUD;           /* cloud CLASS1 service rate */
    private final double mu2Cloud = Configuration.MU_2_CLOUD;           /* cloud CLASS2 service rate */
    private final double hyperexpProb = Configuration.HYPEREXP_PROB;        /* Hyperexponential probability */


    private double arrival[] = {START, START};      /* init arrival time for CLASS1 <- arrival[0] and CLASS2 <- arrival[1]*/

    /* UTILITIES */
    private Rngs rngs;
    private Rvgs rvgs;
    private Rvms rvms;
    private Time t;

    /* BATCH MEANS */
    long eventCounter = 1;                    /* event processed in batch counter,
                                               if eventCounter mod(batchSize - 1) == 0 -> reset batch statistics */
    long batchSize = Configuration.BATCH_SIZE;                  /* number of event processed in a batch */

    /** -----------------------------------------------------------------
     * generate the next arrival value, it must be added to current time
     * ------------------------------------------------------------------
     */
    public double getArrival(ClassType classType) {
        switch (classType) {
            case CLASS1:                                    /* get CLASS1 next exponential value */
                rvgs.rngs.selectStream(0);
                return rvgs.exponential(1/lambda1);
            case CLASS2:                                    /* get CLASS2 next exponential value */
                rvgs.rngs.selectStream(1);
                return rvgs.exponential(1/lambda2);
            default:                                        /* error, return 0.0 */
                return 0.0;
        }
    }

    /** -----------------------------------------------------------------------------------------------
     * generate cloudlet the next service time, it must be added to current time to get departure time
     * ------------------------------------------------------------------------------------------------
     */
    public double getServiceCloudlet(ClassType classType) {
        if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {
            double randomVal;
            switch (classType) {
                case CLASS1:                                    /* get CLASS1 next hyperexponential value */
                    rvgs.rngs.selectStream(3);
                    randomVal = rvgs.rngs.random();
                    rvgs.rngs.selectStream(4);
                    if (randomVal <= hyperexpProb) {
                        return rvgs.exponential(1 / (2 * hyperexpProb * mu1Cloudlet));
                    } else {
                        return rvgs.exponential(1 / (2 * (1 - hyperexpProb) * mu1Cloudlet));
                    }
                case CLASS2:                                    /* get CLASS2 next hyperexponential value */
                    rvgs.rngs.selectStream(5);
                    randomVal = rvgs.rngs.random();
                    rvgs.rngs.selectStream(6);
                    if (randomVal <= hyperexpProb) {
                        return rvgs.exponential(1 / (2 * hyperexpProb * mu2Cloudlet));
                    } else {
                        return rvgs.exponential(1 / (2 * (1 - hyperexpProb) * mu2Cloudlet));
                    }
                default:                                        /* error, return 0.0 */
                    return 0.0;
            }
        }else {
            switch (classType) {
                case CLASS1:                                    /* get CLASS1 next exponential value */
                    rvgs.rngs.selectStream(4);
                        return rvgs.exponential(1 / mu1Cloudlet);
                case CLASS2:                                    /* get CLASS2 next exponential value */
                    rvgs.rngs.selectStream(6);
                        return rvgs.exponential(1/ mu2Cloudlet);

                default:                                        /* error, return 0.0 */
                    return 0.0;
            }
        }
    }


    /** --------------------------------------------------------------------------------------------
     * generate cloud the next service time, it must be added to current time to get departure time
     * ---------------------------------------------------------------------------------------------
     */
    public double getServiceCloud(ClassType classType) {
        switch (classType) {
            case CLASS1:                                    /* get CLASS1 next exponential value */
                rvgs.rngs.selectStream(7);
                return rvgs.exponential(1/mu1Cloud);
            case CLASS2:                                    /* get CLASS2 next exponential value */
                rvgs.rngs.selectStream(8);
                return rvgs.exponential(1/mu2Cloud);
            default:                                        /* error, return 0.0 */
                return 0.0;
        }
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  --------------------------------------------------   MAIN   ----------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */
    public static void main(String[] args) {
	    Simulator simulator = new Simulator();
	    simulator.simulate();
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  ----------------------------------------------   SIMULATION   --------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */
    private void simulate() {

        NextEventInfo nextEventInfo;            /* next event info :
                                                   nextEventInfo[0] <- index,
                                                   nextEventInfo <- Location (CLOUDLET, CLOUD) */

        SystemState systemState = new SystemState();   /* init system state (0,0,0,0) */

        this.rngs = new Rngs();                 /* init Lehmer random number generators with streams */
        this.rvgs = new Rvgs(rngs);             /* init random variate generators */
        this.rvms = new Rvms();                 /* init random variate models */
        this.t = new Time();                    /* init time */

        /* BATCH STATISTICS */
        BatchStatistics batchStatistics = new BatchStatistics(); /* init batch statistics */

        /* STATIONARY STATISTICS */
        StationaryStatistics stationaryStatistics = new StationaryStatistics();

        rngs.plantSeeds(Configuration.SEED);          /* plan seeds */

        /* init cloudlet event list, cloudEvents[0] <- new arrival, other entries ar node servers */
        CloudletEvent[] cloudletEvents = new CloudletEvent[N + 1];

        for (int s = 0; s < N + 1; s++) {
            cloudletEvents[s] = new CloudletEvent(); /* fill server with a fake event, to set server as
                                                        idle and init arrival as empty event */
        }

        /* init cloud event list, every entry is a server */
        List<CloudEvent> cloudEvents = new ArrayList<>();

        this.t.setCurrent(START);                   /* set current time to start */

        this.arrival[0] += this.getArrival(ClassType.CLASS1); /* get first CLASS1 arrival */
        this.arrival[1] += this.getArrival(ClassType.CLASS2); /* get first CLASS2 arrival */

        cloudletEvents[0].setEventStatus(EventStatus.ACTIVE); /* set first event as active */
        computeNextArrival(cloudletEvents);                   /* compute first arrival */

        while ((cloudletEvents[0].getEventStatus() == EventStatus.ACTIVE) || !systemState.systemIsEmpty()) {
            nextEventInfo = this.nextEvent(cloudletEvents, cloudEvents);     /* compute next event index */

            /* compute next event time  */
            if (nextEventInfo.getLocation() == EventLocation.CLOUDLET) {
                t.setNext(cloudletEvents[nextEventInfo.getIndex()].getNextEventTime());
            } else {
                t.setNext(cloudEvents.get(nextEventInfo.getIndex()).getNextEventTime());
            }
            /* --- compute statistics --- */
            if (eventCounter % (batchSize - 1) == 0) {    /* start new batch mean*/
                batchStatistics.resetBatch();
            } else {                                    /* update batch statistics */
                batchStatistics.updateAggregateStatistics();
            }
            batchStatistics.updateStatistics(systemState, t); /* update batch base statistics before set t.currentTime to t.next */

            if (Configuration.EXEC_STATIONARY_STATISTICS) {
                stationaryStatistics.updateStatistics(systemState, t); /* update stationary statistics */
                if (stationaryStatistics.getBaseStatistics().getProcessedSystemJobsNumber() > 0) {
                    stationaryStatistics.updateAggregateStatistics();
                }
            }
            eventCounter++;                               /* update job counter */

            t.setCurrent(t.getNext());                      /* advance the clock to next event*/

            if (nextEventInfo.getIndex() == 0 &&
                    nextEventInfo.getLocation() == EventLocation.CLOUDLET) {  /* process cloudlet arrival*/
                this.execAlgorithm1(cloudletEvents, cloudEvents, systemState, t);  /* exec algorithm 1 */
            }
            else if (nextEventInfo.getIndex() != 0
                    && nextEventInfo.getLocation() == EventLocation.CLOUDLET) { /* process cloudlet departure */
                this.processCloudletDeparture(nextEventInfo.getIndex(), cloudletEvents, systemState, batchStatistics.getLastBatchStatistics(), stationaryStatistics.getBaseStatistics());
            } else { /* process cloud departure */
                this.processCloudDeparture(nextEventInfo.getIndex(), cloudEvents, systemState, batchStatistics.getLastBatchStatistics(), stationaryStatistics.getBaseStatistics());
            }
        }

        //TODO print statistics
        /* --------------------------- Stationary statistics ------------------------ */
        if (Configuration.PRINT_STATIONARY_STATISTICS) {
            try {
                stationaryStatistics.writeToCSV(new PrintWriter(Configuration.STATIONARY_STATISTICS_CSV_PATH));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (Configuration.PRINT_BATCH_MEANS) {
            DecimalFormat decimalFourZero = new DecimalFormat("###0.0000");
            /*for (int i = 0; i < batchStatistics.getBatchMeanStatistics().size(); i++) {
                System.out.println("\n-------------------------------------------------------------");
                System.out.println("\t\t\t\t\t\tBatch " + (i + 1) + " Statistics");
                System.out.println("-------------------------------------------------------------\n");

                System.out.println("system response time ................. = " + decimalFourZero.format(batchStatistics.getSystemRespTime().get(i)));
                System.out.println("class 1 response time ................ = " + decimalFourZero.format(batchStatistics.getClass1RespTime().get(i)));
                System.out.println("class 2 response time ................ = " + decimalFourZero.format(batchStatistics.getClass2RespTime().get(i)));
                System.out.println("global thr ........................... = " + decimalFourZero.format(batchStatistics.getGlobalThr().get(i)));
                System.out.println("class 1 thr .......................... = " + decimalFourZero.format(batchStatistics.getClass1Thr().get(i)));
                System.out.println("class 2 thr .......................... = " + decimalFourZero.format(batchStatistics.getClass2Thr().get(i)));
                System.out.println("cloudlet eff class 1 thr ............. = " + decimalFourZero.format(batchStatistics.getCloudletEffectiveClass1Thr().get(i)));
                System.out.println("cloudlet eff class 2 thr ............. = " + decimalFourZero.format(batchStatistics.getCloudletEffectiveClass2Thr().get(i)));
                System.out.println("cloud class 1 thr .................... = " + decimalFourZero.format(batchStatistics.getCloudClass1Thr().get(i)));
                System.out.println("cloud class 2 thr .................... = " + decimalFourZero.format(batchStatistics.getCloudClass2Thr().get(i)));
                System.out.println("cloudlet class 1 resp time ........... = " + decimalFourZero.format(batchStatistics.getClass1CletRespTime().get(i)));
                System.out.println("cloudlet class 2 resp time ........... = " + decimalFourZero.format(batchStatistics.getClass2CletRespTime().get(i)));
                System.out.println("cloud class 1 resp time .............. = " + decimalFourZero.format(batchStatistics.getClass1CloudRespTime().get(i)));
                System.out.println("cloud class 2 resp time .............. = " + decimalFourZero.format(batchStatistics.getClass2CloudRespTime().get(i)));
                System.out.println("cloudlet class 1 mean population ..... = " + decimalFourZero.format(batchStatistics.getClass1CletMeanPop().get(i)));
                System.out.println("cloudlet class 2 mean population ..... = " + decimalFourZero.format(batchStatistics.getClass2CletMeanPop().get(i)));
                System.out.println("cloud class 1 mean population ........ = " + decimalFourZero.format(batchStatistics.getClass1CloudMeanPop().get(i)));
                System.out.println("cloud class 2 mean population ........ = " + decimalFourZero.format(batchStatistics.getClass2CloudMeanPop().get(i)));

            }*/

            /* ------------------------------------------- (A.3.1) ----------------------------------------------------- */
            double[] batchSystemRespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getSystemRespTime());
            double[] batchClass1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1RespTime());
            double[] batchClass2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2RespTime());
            double[] batchGlobalThr = this.computeMeanAndConfidenceWidth(batchStatistics.getGlobalThr());
            double[] batchClass1Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1Thr());
            double[] batchClass2Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2Thr());

            /* ------------------------------------------- (A.3.2) ----------------------------------------------------- */
            double[] batchCletEffClass1Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudletEffectiveClass1Thr());
            double[] batchCletEffClass2Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudletEffectiveClass2Thr());

            /* ------------------------------------------- (A.3.3) ----------------------------------------------------- */
            double[] batchCloudClass1Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudClass1Thr());
            double[] batchCloudClass2Thr = this.computeMeanAndConfidenceWidth(batchStatistics.getCloudClass2Thr());

            /* ------------------------------------------- (A.3.4) ----------------------------------------------------- */
            double[] batchCletClass1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CletRespTime());
            double[] batchCletClass2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CletRespTime());
            double[] batchCloudClass1RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CloudRespTime());
            double[] batchCloudClass2RespTime = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CloudRespTime());
            double[] batchCloudletClass1MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CletMeanPop());
            double[] batchCloudletClass2MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CletMeanPop());
            double[] batchCloudClass1MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass1CloudMeanPop());
            double[] batchCloudClass2MeanPop = this.computeMeanAndConfidenceWidth(batchStatistics.getClass2CloudMeanPop());

            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.1");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("System Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchSystemRespTime[0]) + " ± " + decimalFourZero.format(batchSystemRespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchSystemRespTime[0] - batchSystemRespTime[1]) + " , "
                    + decimalFourZero.format(batchSystemRespTime[0] + batchSystemRespTime[1]) + "]\n");
            System.out.println("Class 1 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass1RespTime[0]) + " ± " + decimalFourZero.format(batchClass1RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass1RespTime[0] - batchClass1RespTime[1]) + " , "
                    + decimalFourZero.format(batchClass1RespTime[0] + batchClass1RespTime[1]) + "]\n");
            System.out.println("Class 2 Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass2RespTime[0]) + " ± " + decimalFourZero.format(batchClass2RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass2RespTime[0] - batchClass2RespTime[1]) + " , "
                    + decimalFourZero.format(batchClass2RespTime[0] + batchClass2RespTime[1]) + "]\n");
            System.out.println("Global Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchGlobalThr[0]) + " ± " + decimalFourZero.format(batchGlobalThr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchGlobalThr[0] - batchGlobalThr[1]) + " , "
                    + decimalFourZero.format(batchGlobalThr[0] + batchGlobalThr[1]) + "]\n");
            System.out.println("Class 1 Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass1Thr[0]) + " ± " + decimalFourZero.format(batchClass1Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass1Thr[0] - batchClass1Thr[1]) + " , "
                    + decimalFourZero.format(batchClass1Thr[0] + batchClass1Thr[1]) + "]\n");
            System.out.println("Class 2 Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchClass2Thr[0]) + " ± " + decimalFourZero.format(batchClass2Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchClass2Thr[0] - batchClass2Thr[1]) + " , "
                    + decimalFourZero.format(batchClass2Thr[0] + batchClass2Thr[1]) + "]\n");

            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.2");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Class 1 Cloudlet Effective Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletEffClass1Thr[0]) + " ± " + decimalFourZero.format(batchCletEffClass1Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletEffClass1Thr[0] - batchCletEffClass1Thr[1]) + " , "
                    + decimalFourZero.format(batchCletEffClass1Thr[0] + batchCletEffClass1Thr[1]) + "]\n");
            System.out.println("Class 2 Cloudlet Effective Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletEffClass2Thr[0]) + " ± " + decimalFourZero.format(batchCletEffClass2Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletEffClass2Thr[0] - batchCletEffClass2Thr[1]) + " , "
                    + decimalFourZero.format(batchCletEffClass2Thr[0] + batchCletEffClass2Thr[1]) + "]\n");


            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.3");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Class 1 Cloud Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass1Thr[0]) + " ± " + decimalFourZero.format(batchCloudClass1Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass1Thr[0] - batchCloudClass1Thr[1]) + " , "
                    + decimalFourZero.format(batchCloudClass1Thr[0] + batchCloudClass1Thr[1]) + "]\n");
            System.out.println("Class 2 Cloud Throughput " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass2Thr[0]) + " ± " + decimalFourZero.format(batchCloudClass2Thr[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass2Thr[0] - batchCloudClass2Thr[1]) + " , "
                    + decimalFourZero.format(batchCloudClass2Thr[0] + batchCloudClass2Thr[1]) + "]\n");

            System.out.println("\n-------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\tA.3.4");
            System.out.println("-------------------------------------------------------------\n");
            System.out.println("Class 1 Cloudlet Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletClass1RespTime[0]) + " ± " + decimalFourZero.format(batchCletClass1RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletClass1RespTime[0] - batchCletClass1RespTime[1]) + " , "
                    + decimalFourZero.format(batchCletClass1RespTime[0] + batchCletClass1RespTime[1]) + "]\n");
            System.out.println("Class 2 Cloudlet Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCletClass2RespTime[0]) + " ± " + decimalFourZero.format(batchCletClass2RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCletClass2RespTime[0] - batchCletClass2RespTime[1]) + " , "
                    + decimalFourZero.format(batchCletClass2RespTime[0] + batchCletClass2RespTime[1]) + "]\n");
            System.out.println("Class 1 Cloud Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass1RespTime[0]) + " ± " + decimalFourZero.format(batchCloudClass1RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass1RespTime[0] - batchCloudClass1RespTime[1]) + " , "
                    + decimalFourZero.format(batchCloudClass1RespTime[0] + batchCloudClass1RespTime[1]) + "]\n");
            System.out.println("Class 2 Cloud Response Time " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass2RespTime[0]) + " ± " + decimalFourZero.format(batchCloudClass2RespTime[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass2RespTime[0] - batchCloudClass2RespTime[1]) + " , "
                    + decimalFourZero.format(batchCloudClass2RespTime[0] + batchCloudClass2RespTime[1]) + "]\n");
            System.out.println("Class 1 Cloudlet Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudletClass1MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudletClass1MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudletClass1MeanPop[0] - batchCloudletClass1MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudletClass1MeanPop[0] + batchCloudletClass1MeanPop[1]) + "]\n");
            System.out.println("Class 2 Cloudlet Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudletClass2MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudletClass2MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudletClass2MeanPop[0] - batchCloudletClass2MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudletClass2MeanPop[0] + batchCloudletClass2MeanPop[1]) + "]\n");
            System.out.println("Class 1 Cloud Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass1MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudClass1MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass1MeanPop[0] - batchCloudClass1MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudClass1MeanPop[0] + batchCloudClass1MeanPop[1]) + "]\n");
            System.out.println("Class 2 Cloud Mean Population " + (Configuration.LOC * 100) + "% Confidence Interval");
            System.out.println(decimalFourZero.format(batchCloudClass2MeanPop[0]) + " ± " + decimalFourZero.format(batchCloudClass2MeanPop[1]) +
                    " ----> ["
                    + decimalFourZero.format(batchCloudClass2MeanPop[0] - batchCloudClass2MeanPop[1]) + " , "
                    + decimalFourZero.format(batchCloudClass2MeanPop[0] + batchCloudClass2MeanPop[1]) + "]\n");
        }

    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  ----------------------------------------------   ALGORITHM 1   -------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */
    private void execAlgorithm1(CloudletEvent[] cloudletEvents,
                                List<CloudEvent> cloudEvents, SystemState systemState, Time time) {
        if ((systemState.getN1Clet() + systemState.getN2Clet()) == N) {     /* check (n1 + n2 = N)*/
            this.acceptJobToCloud(cloudletEvents[0], cloudEvents, systemState, time);    /* accept job on cloud */
        } else {
            this.acceptJobToCloudlet(cloudletEvents, systemState, time);    /* accept job on cloudlet */
        }
        computeNextArrival(cloudletEvents);                                 /* compute next job arrival */
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  ------------------------------------------------   CLOUDLET   --------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */

    /**
     *  accept job to cloudlet
     *
     *  */
    /** ----------------------
     * accept job to cloudlet
     * -----------------------
     */
    private void acceptJobToCloudlet(CloudletEvent[] cloudletEvents, SystemState systemState, Time time) {
        if (cloudletEvents[0].getClassType() == ClassType.CLASS1) {            /* process CLASS1 arrival */
            this.processClass1Arrival(cloudletEvents, systemState, time);
        } else {                                                               /* process CLASS2 arrival */
            this.processClass2Arrival(cloudletEvents, systemState, time);
        }
    }

    /** ----------------------
     * process CLASS1 arrival
     * -----------------------
     */
    private void processClass1Arrival(CloudletEvent[] cloudletEvents, SystemState systemState, Time time) {
        systemState.incrementN1Clet();                                      /* increment n1 cloudlet state variable */
        this.arrival[0] += getArrival(ClassType.CLASS1);                    /* compute next arrival of CLASS1 */
        int s = findCloudletIdleServer(cloudletEvents);                     /* find longest idle server */
        double service = getServiceCloudlet(ClassType.CLASS1);              /* compute service time */
        cloudletEvents[s].setNextEventTime(time.getCurrent() + service);    /* set job departure time */
        cloudletEvents[s].setEventStatus(EventStatus.ACTIVE);               /* set event active */
        cloudletEvents[s].setClassType(ClassType.CLASS1);                   /* set event class 1 */
    }

    /** ----------------------
     * process CLASS2 arrival
     * -----------------------
     */
    private void processClass2Arrival(CloudletEvent[] cloudletEvents, SystemState systemState, Time time) {
        systemState.incrementN2Clet();                                      /* increment n2 cloudlet state variable */
        this.arrival[1] += getArrival(ClassType.CLASS2);                    /* compute next arrival of CLASS2 */
        int s = findCloudletIdleServer(cloudletEvents);                     /* find longest idle server */
        double service = getServiceCloudlet(ClassType.CLASS2);              /* compute service time */
        cloudletEvents[s].setNextEventTime(time.getCurrent() + service);    /* set job departure time */
        cloudletEvents[s].setEventStatus(EventStatus.ACTIVE);               /* set event active */
        cloudletEvents[s].setClassType(ClassType.CLASS2);                   /* set event class 2 */
    }

    /** --------------------------
     * process cloudlet departure
     * ---------------------------
     */
    private void processCloudletDeparture(int eventIndex, CloudletEvent[] cloudletEvents, SystemState systemState, BaseStatistics baseStatistics, BaseStatistics stationaryStatistics) {
        CloudletEvent event = cloudletEvents[eventIndex];       /* get event to process */
        if (event.getClassType() == ClassType.CLASS1) {         /* process class 1 departure */
            systemState.decrementN1Clet();                      /* decrement class 1 state */
            baseStatistics.incrementProcJobsN1Clet();
            stationaryStatistics.incrementProcJobsN1Clet();
        } else if (event.getClassType() == ClassType.CLASS2) {  /* process class 2 departure */
            systemState.decrementN2Clet();                      /* decrement class 2 state */
            baseStatistics.incrementProcJobsN2Clet();
            stationaryStatistics.incrementProcJobsN2Clet();
        }

        /* set server idle */
        cloudletEvents[eventIndex].setClassType(ClassType.NONE);
        cloudletEvents[eventIndex].setEventStatus(EventStatus.NOT_ACTIVE);
    }

    /** --------------------------------------------------------------
     * return the index of the available cloudlet server idle longest
     * ---------------------------------------------------------------
     */
    int findCloudletIdleServer(CloudletEvent[] cloudletEvents) {
        int s;
        int i = 1;

        while (cloudletEvents[i].getEventStatus() == EventStatus.ACTIVE) {    /* find the
                                                                                 index of the first available */
            i++;                                                              /* (idle) server*/
        }
        s = i;
        while (i < N) {         /* now, check the others to find which   */
            i++;                        /* has been idle longest                 */
            if (cloudletEvents[i].getEventStatus() == EventStatus.NOT_ACTIVE &&
                    cloudletEvents[i].getNextEventTime() < cloudletEvents[s].getNextEventTime()) {
                s = i;
            }
        }
        return s;
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  --------------------------------------------------   CLOUD   ---------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */

    /** -------------------
     * accept job to cloud
     * --------------------
     */
    private void acceptJobToCloud(CloudletEvent arrivalEvent,
                                  List<CloudEvent> cloudEvents, SystemState systemState, Time time) {
        int s = findCloudIdleServer(cloudEvents);                           /* find the longest idle server on cloud */
        ClassType arrivalType = arrivalEvent.getClassType();                /* get arrival class type */

        double service = -1.0;
        switch (arrivalType) {
            case CLASS1:
                this.arrival[0] += getArrival(ClassType.CLASS1);            /* compute next CLASS1 arrival */
                systemState.incrementN1Cloud();                             /* increment cloud CLASS1 state */
                service = getServiceCloud(ClassType.CLASS1);                /* compute cloud CLASS1 service time */
                cloudEvents.get(s).setClassType(ClassType.CLASS1);          /* set job of CLASS1 */
                break;
            case CLASS2:
                this.arrival[1] += getArrival(ClassType.CLASS2);            /* compute next CLASS2 arrival */
                systemState.incrementN2Cloud();                             /* increment cloud CLASS2 state */
                service = getServiceCloud(ClassType.CLASS2);                /* compute cloud CLASS2 service time */
                cloudEvents.get(s).setClassType(ClassType.CLASS2);          /* set job of CLASS2 */
                break;
            case NONE:
                return;                                                     /* error, return; don't add job to cloud */
        }
        if (service != -1.0) {                                              /* if it's all ok, schedule job */
            cloudEvents.get(s).setNextEventTime(time.getCurrent() + service);
            cloudEvents.get(s).setEventStatus(EventStatus.ACTIVE);
        } else {                                                            /* else, disable server and go on*/
            cloudEvents.get(s).setEventStatus(EventStatus.NOT_ACTIVE);
        }
    }

    /** -----------------------------------------------------------
     * return the index of the available cloud server idle longest
     * ------------------------------------------------------------
     */
    private int findCloudIdleServer(List<CloudEvent> cloudEvents) {
        for (int i = 0; i < cloudEvents.size(); i++) {
            if (cloudEvents.get(i).getEventStatus() == EventStatus.NOT_ACTIVE) {    /* if this server is idle */
                return i;                                                           /* return it */
            }
        }

        /* no server idle */
        cloudEvents.add(new CloudEvent());                                          /* add new server and */
        return cloudEvents.size() - 1;                                              /* return it */
    }

    /** -----------------------
     * process cloud departure
     * ------------------------
     */
    private void processCloudDeparture(int eventIndex, List<CloudEvent> cloudEvents, SystemState systemState, BaseStatistics baseStatistics, BaseStatistics stationaryStatistics) {
        if (cloudEvents.get(eventIndex).getClassType() == ClassType.CLASS1) {           /* process cloud
                                                                                           class1 departure */
            systemState.decrementN1Cloud();                                             /* decrement cloud
                                                                                           class1 state */
            baseStatistics.incrementProcJobsN1Cloud();
            stationaryStatistics.incrementProcJobsN1Cloud();
        } else if (cloudEvents.get(eventIndex).getClassType() == ClassType.CLASS2) {    /* process cloud
                                                                                           class2 departure */
            systemState.decrementN2Cloud();                                             /* decrement cloud
                                                                                           class2 state */
            baseStatistics.incrementProcJobsN2Cloud();
            stationaryStatistics.incrementProcJobsN2Cloud();
        }

        /* set server idle */
        cloudEvents.get(eventIndex).setClassType(ClassType.NONE);
        cloudEvents.get(eventIndex).setEventStatus(EventStatus.NOT_ACTIVE);
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  ------------------------------------------------   UTILS   -----------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */

    /** ---------------------------------------
     * return the index of the next event type
     * ---------------------------------------
     */
    private NextEventInfo nextEvent(CloudletEvent[] cloudletEvents, List<CloudEvent> cloudEvents) {

        int eventIndex;                                                             /* next event index to return */
        int cloudletScan = 0;                                                       /* cloudlet scan index */
        int cloudScan = 0;                                                          /* cloud scan index */
        int cloudletIndex;                                                          /* cloudlet chosen index */
        int cloudIndex;                                                             /* cloud chosen index */
        double cloudletNextEventTime;
        double cloudNextEventTime;

        EventLocation eventLocation;

        /* find index cloudlet first event */
        while (cloudletEvents[cloudletScan].getEventStatus() == EventStatus.NOT_ACTIVE &&
                cloudletScan < N) {                                         /* find the index of the first 'active'
                                                                               element in the event list */
            cloudletScan++;
        }
        cloudletIndex = cloudletScan;
        while (cloudletScan < N) {         /* now, check the others to find which event type is most imminent */
            cloudletScan++;
            if ((cloudletEvents[cloudletScan].getEventStatus() == EventStatus.ACTIVE) &&
                    (cloudletEvents[cloudletScan].getNextEventTime() < cloudletEvents[cloudletIndex].getNextEventTime())
                    )
                cloudletIndex = cloudletScan;
        }

        /* compute cloudlet next event time */
        if (cloudletEvents[cloudletIndex].getEventStatus() == EventStatus.NOT_ACTIVE) {
            cloudletNextEventTime = INFINITY;
        } else {
            cloudletNextEventTime = cloudletEvents[cloudletIndex].getNextEventTime();
        }

        if (cloudEvents.size() > 0) {
            /* find index cloud first event */
            while (cloudEvents.get(cloudScan).getEventStatus() == EventStatus.NOT_ACTIVE &&
                    cloudScan < cloudEvents.size() - 1) {
                cloudScan++;
            }
            cloudIndex = cloudScan;
            while (cloudScan < cloudEvents.size() - 1) {
                cloudScan++;
                if (cloudEvents.get(cloudScan).getEventStatus() == EventStatus.ACTIVE &&
                        cloudEvents.get(cloudScan).getNextEventTime() < cloudEvents.get(cloudIndex).getNextEventTime()
                        ) {
                    cloudIndex = cloudScan;
                }
            }

            /* compute cloud next event time */
            if (cloudEvents.get(cloudIndex).getEventStatus() == EventStatus.NOT_ACTIVE) {
                cloudNextEventTime = INFINITY;
            } else {
                cloudNextEventTime = cloudEvents.get(cloudIndex).getNextEventTime();
            }
        } else {
            cloudNextEventTime = INFINITY;
            cloudIndex = -1;
        }

        if (cloudletNextEventTime < cloudNextEventTime) {
            eventIndex = cloudletIndex;
            eventLocation = EventLocation.CLOUDLET;
        } else {
            eventIndex = cloudIndex;
            eventLocation = EventLocation.CLOUD;
        }
        return new NextEventInfo(eventIndex, eventLocation);
    }

    /** --------------------
     * compute next arrival
     * ---------------------
     */
    private void computeNextArrival(CloudletEvent[] cloudletEvents) {
        if (cloudletEvents[0].getEventStatus() == EventStatus.ACTIVE) {
            if (this.arrival[0] < this.arrival[1]) {                            /* next arrival will be a CLASS1 job */
                cloudletEvents[0].setNextEventTime(this.arrival[0]);
                cloudletEvents[0].setClassType(ClassType.CLASS1);
            } else {                                                            /* next arrival will be a CLASS2 job */
                cloudletEvents[0].setNextEventTime(this.arrival[1]);
                cloudletEvents[0].setClassType(ClassType.CLASS2);
            }
            if (cloudletEvents[0].getNextEventTime() > STOP) {                  /* if arrival time out
                                                                                   of simulation range */
                cloudletEvents[0].setEventStatus(EventStatus.NOT_ACTIVE);       /* disable arrival event */
            }
        }
    }

    private double[] computeMeanAndConfidenceWidth(List<Double> batchStatistics) {
        /* Welford's algorithm mean and standard deviation computation */
        int n = 0;                                                              /* n = 0 */
        double mean = 0.0;                                                      /* x = 0.0 */
        double v = 0.0;                                                         /* v = 0.0 */

        int i = 0;
        while (i < batchStatistics.size()) {                                    /* while (more data) */
            double sample = batchStatistics.get(i);                             /* x = GetData(); */
            n++;                                                                /* n++; */
            double d = sample - mean;                                           /* d = x - mean; */
            v += d * d * (n - 1)/n;                                             /* v = v + (d * d * (n - 1)/n);*/
            mean += d/n;                                                        /* mean = mean + d/n;  */
            i++;                                                                /* step iterator */
        }
        double stDev = Math.sqrt(v/n);                                          /* s = sqrt(v/n); */

        if (n > 1) {
            double u = 1.0 - 0.5 * (1.0 - Configuration.LOC);                   /* interval parameter  */
            double tStudent = rvms.idfStudent(n - 1, u);                     /* critical value of t */
            double w = tStudent * stDev / Math.sqrt(n - 1);                     /* interval half width */
            return new double[]{mean, w};                                       /* return mean and interval half width */
        }

        return new double[]{0,0};                                               /* empty batch statistics default values */

    }
}
