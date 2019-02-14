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
import com.company.model.statistics.utils.StatisticsUtils;
import com.company.model.system.Controller;
import com.company.model.utils.SimulatorUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private final double START = 0.0;                                   /* initial (open the door) */
    private final double STOP = Configuration.STOP;                     /* terminal (close the door) time */
    private final double INFINITY = 100 * STOP;                         /* infinity, much bigger than STOP */

    private double arrival[] = {START, START};                          /* init arrival time for CLASS1 <- arrival[0] and CLASS2 <- arrival[1]*/

    /* UTILITIES */
    StatisticsUtils statisticsUtils = new StatisticsUtils();
    SimulatorUtils simulatorUtils = new SimulatorUtils();

    /* TIME */
    private Time t;

    /* SYSTEM */
    private Controller controller;

    /* BATCH MEANS */
    long eventCounter = 1;                    /* event processed in batch counter,
                                               if eventCounter mod(batchSize - 1) == 0 -> reset batch statistics */
    long batchSize = Configuration.BATCH_SIZE;                  /* number of event processed in a batch */

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

        this.t = new Time();                    /* init time */

        this.controller = new Controller();     /* init controller */

        /* BATCH STATISTICS */
        BatchStatistics batchStatistics = new BatchStatistics(); /* init batch statistics */

        /* STATIONARY STATISTICS */
        StationaryStatistics stationaryStatistics = new StationaryStatistics();

        controller.plantSeeds(Configuration.SEED);          /* plant seeds */

        this.t.setCurrent(START);                   /* set current time to start */

        this.arrival[0] += this.controller.getArrival(ClassType.CLASS1); /* get first CLASS1 arrival */
        this.arrival[1] += this.controller.getArrival(ClassType.CLASS2); /* get first CLASS2 arrival */

        this.controller.getCloudletEvents()[0].setEventStatus(EventStatus.ACTIVE); /* set first event as active */
        this.controller.computeNextArrival(this.arrival, this.STOP, this.controller.getCloudletEvents()); /* compute first arrival */

        while ((this.controller.getCloudletEvents()[0].getEventStatus() == EventStatus.ACTIVE) || !systemState.systemIsEmpty()) {
            nextEventInfo = this.simulatorUtils.nextEvent(                                     /* compute next event index */
                    this.controller.getCloudletEvents(),
                    this.controller.getCloudEvents(),
                    this.INFINITY                       /* infinity time*/
            );

            /* compute next event time  */
            if (nextEventInfo.getLocation() == EventLocation.CLOUDLET) {
                t.setNext(this.controller.getCloudletEvents()[nextEventInfo.getIndex()].getNextEventTime());
            } else {
                t.setNext(this.controller.getCloudEvents().get(nextEventInfo.getIndex()).getNextEventTime());
            }
            /* --- compute statistics --- */
            if (Configuration.EXEC_BATCH_MEANS) {
                if (eventCounter % (batchSize - 1) == 0) {    /* start new batch mean*/
                    batchStatistics.resetBatch();
                } else {                                    /* update batch statistics */
                    batchStatistics.updateAggregateStatistics();
                }
                batchStatistics.updateStatistics(systemState, t); /* update batch base statistics before set t.currentTime to t.next */
            }
            if (Configuration.EXEC_STATIONARY_STATISTICS) {
                stationaryStatistics.updateStatistics(systemState, t); /* update stationary statistics */
                if (stationaryStatistics.getBaseStatistics().getProcessedSystemJobsNumber() > 0) {
                    stationaryStatistics.updateAggregateStatistics();
                }
            }
            eventCounter++;                                                         /* update event counter */

            t.setCurrent(t.getNext());                                              /* advance the clock to next event*/

            controller.processEvent(nextEventInfo, systemState, this.t,                     /* process next event */
                    this.arrival, this.STOP, batchStatistics, stationaryStatistics);
        }

        /* -------------------------------------------------------------------------------------------------------------
         * ---------------------------------------- PRINT STATISTICS ---------------------------------------------------
         * -------------------------------------------------------------------------------------------------------------
         */

        /* ------------------------------------ Stationary statistics ----------------------------------------------- */
        if (Configuration.EXEC_STATIONARY_STATISTICS) {
            try {
                stationaryStatistics.writeToCSV(new PrintWriter(Configuration.STATIONARY_STATISTICS_CSV_PATH));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /* -------------------------------------- Batch Means and CDH ----------------------------------------------- */
        if (Configuration.EXEC_BATCH_MEANS) {
            DecimalFormat decimalFourZero = new DecimalFormat("###0.0000");

            /* ------------------------------------------- (A.3.1) ----------------------------------------------------- */
            double[] batchSystemRespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getSystemRespTime());
            double[] batchClass1RespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass1RespTime());
            double[] batchClass2RespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass2RespTime());
            double[] batchGlobalThr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getGlobalThr());
            double[] batchClass1Thr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass1Thr());
            double[] batchClass2Thr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass2Thr());

            /* ------------------------------------------- (A.3.2) ----------------------------------------------------- */
            double[] batchCletEffClass1Thr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getCloudletEffectiveClass1Thr());
            double[] batchCletEffClass2Thr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getCloudletEffectiveClass2Thr());

            /* ------------------------------------------- (A.3.3) ----------------------------------------------------- */
            double[] batchCloudClass1Thr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getCloudClass1Thr());
            double[] batchCloudClass2Thr = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getCloudClass2Thr());

            /* ------------------------------------------- (A.3.4) ----------------------------------------------------- */
            double[] batchCletClass1RespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass1CletRespTime());
            double[] batchCletClass2RespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass2CletRespTime());
            double[] batchCloudClass1RespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass1CloudRespTime());
            double[] batchCloudClass2RespTime = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass2CloudRespTime());
            double[] batchCloudletClass1MeanPop = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass1CletMeanPop());
            double[] batchCloudletClass2MeanPop = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass2CletMeanPop());
            double[] batchCloudClass1MeanPop = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass1CloudMeanPop());
            double[] batchCloudClass2MeanPop = this.statisticsUtils.computeMeanAndConfidenceWidth(batchStatistics.getClass2CloudMeanPop());

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
}
