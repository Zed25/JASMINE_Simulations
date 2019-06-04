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
import com.company.model.statistics.utils.StatisticsUtils;
import com.company.model.system.Controller;
import com.company.model.utils.SimulatorUtils;

public class Simulator {

    private final double START = 0.0;                                   /* initial (open the door) time */
    private final double STOP = Configuration.STOP;                     /* terminal (close the door) time */
    private final double INFINITY = 100 * STOP;                         /* infinity, much bigger than STOP */
    /* UTILITIES */
    private StatisticsUtils statisticsUtils = new StatisticsUtils();
    private SimulatorUtils simulatorUtils = new SimulatorUtils();
    /* BATCH MEANS */
    private long eventCounter;                    /* event processed in batch counter,
                                               if eventCounter mod(batchSize - 1) == 0 -> reset batch statistics */
    private long batchSize;                  /* number of event processed in a batch */
    private double arrival[];
    private long seed;

    /* TIME */
    private Time t;
    /* SYSTEM */
    private Controller controller;

    /* SIMULATION TIMES */
    private int simulationRepeatTimes;
    private int batchRepeatTimes;
    private long observations; /* number of simulation observations */

    private NextEventInfo nextEventInfo;            /* next event info :
                                                       nextEventInfo[0] <- list or array index,
                                                       nextEventInfo <- Location (CLOUDLET, CLOUD) */

    private SystemState systemState;                /* init system state (N1Clet, N2Clet, N1Cloud, N2Cloud) <- (0,0,0,0) */

    /* BATCH STATISTICS */
    private BatchStatistics batchStatistics; /* init batch statistics */

    /**
     * ----------------------------------------------------------------------------------------------------------------
     * --------------------------------------------------   MAIN   ----------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------
     */
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.init();
        simulator.simulate();
    }


    /**
     * ----------------------------------------------------------------------------------------------------------------
     * --------------------------------------------------   INIT   ----------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------
     */
    private void init() {
        // init simulation repeat time parameters
        this.simulationRepeatTimes = Configuration.SIMULATION_REPEAT_TIMES;
        if (Configuration.FINITE_HORIZON) {
            this.batchRepeatTimes = Configuration.BATCH_REPEAT_TIMES;
            this.observations = Configuration.OBSERVATIONS;
        } else {
            this.batchRepeatTimes = 1;
            this.observations = 1;
        }
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------
     * ----------------------------------------------   SIMULATION   --------------------------------------------------
     * ----------------------------------------------------------------------------------------------------------------
     */
    private void simulate() {


        this.controller = new Controller();     /* init controller */
        controller.plantSeeds(Configuration.SEED);          /* plant seeds */

        this.seed = this.controller.getSeed(); /* get simulation initial seed */

        if (Configuration.CLOUDLET_HYPEREXP_SERVICE) {  /* if hyperexponential cloudlet service
                                                               add other system state (N1F1, N1F2, N2F1, N2F2) <- (0,0,0,0) */
            this.systemState = new HyperexpSystemState();
        } else {
            this.systemState = new SystemState();
        }

        this.reset();
        this.resetStatistics();

        for (int i = 0; i < simulationRepeatTimes; i++) {

            this.batchSize = Configuration.BATCH_SIZE; /* init simulation initial batch size */

            for (int j = 0; j <  batchRepeatTimes; j++) {

                this.controller.plantSeeds(this.seed); /* reinit random numbers generator*/

                for (int k = 0; k < this.observations; k++) {

                    this.initSimulation();

                    while (((this.controller.getCloudletEvents()[0].getEventStatus() == EventStatus.ACTIVE)
                            || !systemState.systemIsEmpty())) {
                        this.computeNext();

                        /* --- compute statistics --- */
                        if (eventCounter % (batchSize - 1) == 0) {    /* start new batch mean*/
                            if (Configuration.FINITE_HORIZON) {
                                if (k < (this.observations - 1)) {
                                    batchStatistics.resetBatch();
                                }
                                break;
                            } else {
                                batchStatistics.resetBatch();
                            }
                        } else {                                    /* update batch statistics */
                            batchStatistics.updateAggregateStatistics();
                        }
                        batchStatistics.updateStatistics(systemState, t); /* update batch base statistics
                                                                before setting t.currentTime to t.next */
                        eventCounter++;                                                         /* update event counter */

                        t.setCurrent(t.getNext());                                             /* advance the clock to next event*/

                        controller.processEvent(nextEventInfo, systemState, this.t,                     /* process next event */
                                this.arrival, this.STOP, batchStatistics);
                    }

                    this.reset();
                }

                this.statisticsUtils.printStatistics(batchStatistics, batchSize, seed); /* print statistics */

                if (Configuration.FINITE_HORIZON) {
                    batchSize *= Configuration.BATCH_SIZE_MULTIPLIER;
                }

                this.resetStatistics();
            }

            this.seed = this.controller.getSeed(); /* update simulation seed */
        }

    }

    private void computeNext() {
        nextEventInfo = this.simulatorUtils.nextEvent(                               /* compute next event index */
                this.controller.getCloudletEvents(),
                this.controller.getCloudEvents(),
                this.INFINITY                       /* infinity time*/
        );

        /* compute next event time  */
        if (nextEventInfo.getLocation() == EventLocation.CLOUDLET) {
            /* set next event time as next event of CLOUDLET */
            t.setNext(this.controller.getCloudletEvents()[nextEventInfo.getIndex()].getNextEventTime());
        } else {
            /* set next event time as next event of CLOUDLET */
            t.setNext(this.controller.getCloudEvents().get(nextEventInfo.getIndex()).getNextEventTime());
        }
    }

    private void reset() {

        this.controller.reset();

        this.systemState.reset();

        this.t = new Time();                    /* init time */

        this.t.setCurrent(START);                   /* set current time to start */

        this.arrival = new double[]{START, START}; /* init arrival time for CLASS1 <- arrival[0]
                                                                           and CLASS2 <- arrival[1]*/
        this.eventCounter = 1;
    }

    private void resetStatistics() {
        /* BATCH STATISTICS */
        this.batchStatistics = new BatchStatistics(); /* init batch statistics */
    }

    private void initSimulation() {
        this.arrival[0] += this.controller.getArrival(ClassType.CLASS1); /* get first CLASS1 arrival */
        this.arrival[1] += this.controller.getArrival(ClassType.CLASS2); /* get first CLASS2 arrival */

        this.controller.getCloudletEvents()[0].setEventStatus(EventStatus.ACTIVE); /* set first event as active */
        /* compute first arrival */
        this.controller.computeNextArrival(this.arrival, this.STOP, this.controller.getCloudletEvents());
    }
}
