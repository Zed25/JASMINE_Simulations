package com.company;

import com.company.configuration.Configuration;
import com.company.model.HyperexpSystemState;
import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.event.NextEventInfo;
import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;
import com.company.model.statistics.BatchStatistics;
import com.company.model.statistics.StationaryStatistics;
import com.company.model.statistics.utils.StatisticsUtils;
import com.company.model.system.Controller;
import com.company.model.utils.SimulatorUtils;

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

    /**
     * ----------------------------------------------------------------------------------------------------------------
     * --------------------------------------------------   MAIN   ----------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------
     */
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.simulate();
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------   SIMULATION   --------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------
     */
    private void simulate() {

        NextEventInfo nextEventInfo;            /* next event info :
                                                   nextEventInfo[0] <- index,
                                                   nextEventInfo <- Location (CLOUDLET, CLOUD) */

        SystemState systemState;                /* init system state (N1Clet, N2Clet, N1Cloud, N2Cloud) <- (0,0,0,0) */
        if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {  /* if hyperexponential cloudlet service
                                                           add other system state (N1F1, N1F2, N2F1, N2F2) <- (0,0,0,0) */
            systemState = new HyperexpSystemState();
        } else {
            systemState = new SystemState();
        }

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

        while (((this.controller.getCloudletEvents()[0].getEventStatus() == EventStatus.ACTIVE) || !systemState.systemIsEmpty()) /*&&
                eventCounter < 100*/) {
            /*if (eventCounter == 301) {
                batchStatistics = new BatchStatistics();
            }*/
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

        this.statisticsUtils.printStatistics(stationaryStatistics, batchStatistics);

    }
}
