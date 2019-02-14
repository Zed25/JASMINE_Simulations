package com.company.model.system;

import com.company.configuration.Configuration;
import com.company.model.SystemState;
import com.company.model.Time;
import com.company.model.event.CloudEvent;
import com.company.model.event.CloudletEvent;
import com.company.model.event.NextEventInfo;
import com.company.model.event.enumeration.ClassType;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;
import com.company.model.statistics.BaseStatistics;
import com.company.model.statistics.BatchStatistics;
import com.company.model.statistics.StationaryStatistics;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final double lambda1 = Configuration.LAMBDA_1;              /* CLASS1 arrival rate */
    private final double lambda2 = Configuration.LAMBDA_2;              /* CLASS2 arrival rate */

    private final double mu1Cloudlet = Configuration.MU_1_CLET;         /* cloudlet CLASS1 service rate */
    private final double mu2Cloudlet = Configuration.MU_2_CLET;         /* cloudlet CLASS2 service rate */
    private final double hyperexpProb = Configuration.HYPEREXP_PROB;    /* Hyperexponential probability */

    private final double mu1Cloud = Configuration.MU_1_CLOUD;           /* cloud CLASS1 service rate */
    private final double mu2Cloud = Configuration.MU_2_CLOUD;           /* cloud CLASS2 service rate */

    private final int N = Configuration.N;                              /* cloudlet threshold (cloudlet servers number) */

    Rngs rngs;
    Rvgs rvgs;

    CloudletEvent[] cloudletEvents; /* cloudlet events */
    List<CloudEvent> cloudEvents; /* cloud events */

    public Controller() {
        this.rngs = new Rngs();                 /* init Lehmer random number generators with streams */
        this.rvgs = new Rvgs(rngs);             /* init random variate generators */

        this.cloudletEvents = new CloudletEvent[N + 1]; /* init cloudlet event list, cloudEvents[0]
                                                        <- new arrival, other entries ar node servers */

        for (int s = 0; s < N + 1; s++)
            cloudletEvents[s] = new CloudletEvent(); /* fill server with a fake event, to set server as
                                                        idle and init arrival as empty event */

        this.cloudEvents = new ArrayList<>();   /* init cloud event list, every entry is a server */
    }

    public void plantSeeds(long seed) {
        rngs.plantSeeds(seed);          /* plan seeds */
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  ---------------------------------------------- PUBLIC METHODS  -------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */

    public void processEvent(NextEventInfo nextEventInfo, SystemState systemState,
                             Time t, double[] arrival, double stopTime, BatchStatistics batchStatistics, StationaryStatistics stationaryStatistics) {
        if (nextEventInfo.getIndex() == 0 &&
                nextEventInfo.getLocation() == EventLocation.CLOUDLET) {  /* process cloudlet arrival*/
            if (Configuration.EXECUTE_ALGORITHM_1) {
                this.execAlgorithm1(cloudletEvents, cloudEvents, systemState, t, arrival, stopTime);  /* exec algorithm 1 */
            } else {
                //this.execAlgorithm2(cloudletEvents, cloudEvents, systemState, t);  /* exec algorithm 1 */
            }
        }
        else if (nextEventInfo.getIndex() != 0
                && nextEventInfo.getLocation() == EventLocation.CLOUDLET) { /* process cloudlet departure */
            this.processCloudletDeparture(nextEventInfo.getIndex(), cloudletEvents, systemState, batchStatistics.getLastBatchStatistics(), stationaryStatistics.getBaseStatistics());
        } else { /* process cloud departure */
            this.processCloudDeparture(nextEventInfo.getIndex(), cloudEvents, systemState, batchStatistics.getLastBatchStatistics(), stationaryStatistics.getBaseStatistics());
        }
    }

    /** --------------------
     * compute next arrival
     * ---------------------
     */
    public void computeNextArrival(double[] arrival, double stopTime, CloudletEvent[] cloudletEvents) {
        if (cloudletEvents[0].getEventStatus() == EventStatus.ACTIVE) {
            if (arrival[0] < arrival[1]) {                            /* next arrival will be a CLASS1 job */
                cloudletEvents[0].setNextEventTime(arrival[0]);
                cloudletEvents[0].setClassType(ClassType.CLASS1);
            } else {                                                            /* next arrival will be a CLASS2 job */
                cloudletEvents[0].setNextEventTime(arrival[1]);
                cloudletEvents[0].setClassType(ClassType.CLASS2);
            }
            if (cloudletEvents[0].getNextEventTime() > stopTime) {                  /* if arrival time out
                                                                                   of simulation range */
                cloudletEvents[0].setEventStatus(EventStatus.NOT_ACTIVE);       /* disable arrival event */
            }
        }
    }

    /** ----------------------------------------------------------------------------------------------------------------
     *  ----------------------------------------------   ALGORITHM 1   -------------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */
    private void execAlgorithm1(CloudletEvent[] cloudletEvents,
                                List<CloudEvent> cloudEvents, SystemState systemState, Time time, double[] arrival, double stopTime) {
        if ((systemState.getN1Clet() + systemState.getN2Clet()) == N) {     /* check (n1 + n2 = N)*/
            this.acceptJobToCloud(cloudletEvents[0], cloudEvents, systemState, time, arrival);    /* accept job on cloud */
        } else {
            this.acceptJobToCloudlet(cloudletEvents, systemState, time, arrival);    /* accept job on cloudlet */
        }
        computeNextArrival(arrival, stopTime, cloudletEvents);                                 /* compute next job arrival */
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
    private void acceptJobToCloudlet(CloudletEvent[] cloudletEvents,
                                     SystemState systemState, Time time, double[] arrival) {
        if (cloudletEvents[0].getClassType() == ClassType.CLASS1) {            /* process CLASS1 arrival */
            this.processClass1Arrival(cloudletEvents, systemState, time, arrival);
        } else {                                                               /* process CLASS2 arrival */
            this.processClass2Arrival(cloudletEvents, systemState, time, arrival);
        }
    }

    /** ----------------------
     * process CLASS1 arrival
     * -----------------------
     */
    private void processClass1Arrival(CloudletEvent[] cloudletEvents,
                                      SystemState systemState, Time time, double[] arrival) {
        systemState.incrementN1Clet();                                      /* increment n1 cloudlet state variable */
        arrival[0] += getArrival(ClassType.CLASS1);                    /* compute next arrival of CLASS1 */
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
    private void processClass2Arrival(CloudletEvent[] cloudletEvents,
                                      SystemState systemState, Time time, double[] arrival) {
        systemState.incrementN2Clet();                                      /* increment n2 cloudlet state variable */
        arrival[1] += getArrival(ClassType.CLASS2);                    /* compute next arrival of CLASS2 */
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
    private void processCloudletDeparture(int eventIndex, CloudletEvent[] cloudletEvents, SystemState systemState,
                                          BaseStatistics baseStatistics, BaseStatistics stationaryStatistics) {
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
                                  List<CloudEvent> cloudEvents, SystemState systemState, Time time, double[] arrival) {
        int s = findCloudIdleServer(cloudEvents);                           /* find the longest idle server on cloud */
        ClassType arrivalType = arrivalEvent.getClassType();                /* get arrival class type */

        double service = -1.0;
        switch (arrivalType) {
            case CLASS1:
                arrival[0] += getArrival(ClassType.CLASS1);            /* compute next CLASS1 arrival */
                systemState.incrementN1Cloud();                             /* increment cloud CLASS1 state */
                service = getServiceCloud(ClassType.CLASS1);                /* compute cloud CLASS1 service time */
                cloudEvents.get(s).setClassType(ClassType.CLASS1);          /* set job of CLASS1 */
                break;
            case CLASS2:
                arrival[1] += getArrival(ClassType.CLASS2);            /* compute next CLASS2 arrival */
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
     *  ----------------------------------------- ARRIVAL AND SERVICES   -----------------------------------------------
     *  ----------------------------------------------------------------------------------------------------------------
     *  */

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

    public CloudletEvent[] getCloudletEvents() {
        return cloudletEvents;
    }

    public List<CloudEvent> getCloudEvents() {
        return cloudEvents;
    }
}
