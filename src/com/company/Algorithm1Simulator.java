package com.company;

import com.company.model.MarkovState;
import com.company.model.Time;
import com.company.model.event.CloudEvent;
import com.company.model.event.NextEventInfo;
import com.company.model.event.enumeration.ClassType;
import com.company.model.event.CloudletEvent;
import com.company.model.event.Event;
import com.company.model.event.enumeration.EventLocation;
import com.company.model.event.enumeration.EventStatus;

import java.util.ArrayList;
import java.util.List;

public class Algorithm1Simulator {

    private final long initialSeeed = 1234567; //TODO delete this

    private final double START = 0.0;               /* initial (open the door) */
    private final double STOP = 20.0;           /* terminal (close the door) time */
    private final double INFINITY = 100*STOP;       /* infinity, much bigger than STOP */

    /* INPUT VARIABLES */
    private final int N = 20;                       /* cloudlet threshold (cloudlet servers number) */
    private final double lambda1 = 6.0;             /* CLASS1 arrival rate */
    private final double lambda2 = 6.25;            /* CLASS2 arrival rate */
    private final double mu1Cloudlet = 0.45;        /* cloudlet CLASS1 service rate */
    private final double mu2Cloudlet = 0.27;        /* cloudlet CLASS2 service rate */
    private final double mu1Cloud = 0.25;           /* cloud CLASS1 service rate */
    private final double mu2Cloud = 0.22;           /* cloud CLASS2 service rate */
    private final double hyperexpProb = 0.2;        /* Hyperexponential probability */


    private double arrival[] = {START, START};

    /* UTILITIES */
    private Rngs rngs;
    private Rvgs rvgs;
    private Time t;

    private MarkovState markovState;                /* system markov state */

    private int serverIndex;                        /* server index */
    private long jobCounter = 0;                    /* used to count processed jobs */
    double area = 0.0;                              /* time integrated number in the node */

    /** ----------------------------------------------------
     * generate the next arrival type 1 time, with rate 1/2
     * -----------------------------------------------------
     */
    public double getArrivalType1() {
        rvgs.rngs.selectStream(0);
        return rvgs.exponential(1/ lambda1);
    }

    /** ----------------------------------------------------
     * generate the next arrival type 2 time, with rate 1/2
     * -----------------------------------------------------
     */
    public double getArrivalType2() {
        rvgs.rngs.selectStream(1);
        return rvgs.exponential(1/ lambda2);
    }


    /** -------------------------------------------
     * generate the next service time with rate 2/3
     * --------------------------------------------
     */
    public double getServiceType1() {
        rvgs.rngs.selectStream(2);
        if (rvgs.rngs.random() <= hyperexpProb) {
            return rvgs.exponential(1/(2 * hyperexpProb * mu1Cloudlet));
        } else {
            return rvgs.exponential(1/(2 * (1-hyperexpProb) * mu1Cloudlet));
        }
    }

    /** -------------------------------------------
     * generate the next service time with rate 2/3
     * --------------------------------------------
     */
    public double getServiceType2() {
        rvgs.rngs.selectStream(3);
        if (rvgs.rngs.random() <= hyperexpProb) {
            return rvgs.exponential(2* hyperexpProb * mu2Cloudlet);
        } else {
            return rvgs.exponential(2 * (1-hyperexpProb) * mu2Cloudlet);
        }
    }

    public double getServiceCloudType1() {
        rvgs.rngs.selectStream(4);
        return rvgs.exponential(1/mu1Cloud);
    }

    public double getServiceCloudType2() {
        rvgs.rngs.selectStream(5);
        return rvgs.exponential(1/mu2Cloud);
    }

    public static void main(String[] args) {
	    Algorithm1Simulator algorithm1Simulator = new Algorithm1Simulator();
	    algorithm1Simulator.simulate();
    }

    private void simulate() {

        int s;                                  /* servers iterator index */
        NextEventInfo nextEventInfo;            /* next event info :
                                                   nextEventInfo[0] <- index,
                                                   nextEventInfo <- Location (CLOUDLET, CLOUD) */

        this.markovState = new MarkovState();   /* init markov state (0,0,0,0) */
        this.jobCounter = 0;                    /* init job counter */
        this.area = 0.0;                        /* init area */

        this.rngs = new Rngs();                 /* init random number generators */
        this.rvgs = new Rvgs(rngs);             /* init random variable generators */
        this.t = new Time();                    /* init time */

        rngs.plantSeeds(initialSeeed);

        /* init cloudlet event list, cloudEvents[0] <- new arrival, other entries ar node servers */
        CloudletEvent[] cloudletEvents = new CloudletEvent[N + 1];

        for (s = 0; s < N + 1; s++) {
            cloudletEvents[s] = new CloudletEvent(); /* fill server with a fake event, to set server as idle */
        }

        /* init cloud event list, every entry is a server */
        List<CloudEvent> cloudEvents = new ArrayList<>();

        this.t.setCurrent(START);  /* set current time to start */

        this.arrival[0] += this.getArrivalType1(); /* get first CLASS1 arrival */
        this.arrival[1] += this.getArrivalType2(); /* get first CLASS2 arrival */

        computeNextArrival(cloudletEvents);                   /* compute first arrival */
        cloudletEvents[0].setEventStatus(EventStatus.ACTIVE); /* set first event as active */

        while ((cloudletEvents[0].getEventStatus() == EventStatus.ACTIVE) || !markovState.systemIsEmpty()) {
            nextEventInfo = this.nextEvent(cloudletEvents, cloudEvents);     /* compute next event index */

            /* compute next event time  */
            if (nextEventInfo.getLocation() == EventLocation.CLOUDLET) {
                t.setNext(cloudletEvents[nextEventInfo.getIndex()].getNextEventTime());
            } else {
                t.setNext(cloudEvents.get(nextEventInfo.getIndex()).getNextEventTime());
            }
            //TODO compute statistics

            t.setCurrent(t.getNext());                      /* advance the clock to next event*/

            if (nextEventInfo.getIndex() == 0 && nextEventInfo.getLocation() == EventLocation.CLOUDLET) {  /* process cloudlet arrival*/
                this.execAlgorithm1(cloudletEvents, cloudEvents, markovState, t);  /* exec algorithm 1 */
            }
            else if (nextEventInfo.getIndex() != 0 && nextEventInfo.getLocation() == EventLocation.CLOUDLET) { /* process cloudlet departure */
                this.processCloudletDeparture(nextEventInfo.getIndex(), cloudletEvents, markovState);
            } else { /* process cloud departure */
                this.processCloudDeparture(nextEventInfo.getIndex(), cloudEvents, markovState);
            }
        }

        /*DecimalFormat f = new DecimalFormat("###0.00");
        DecimalFormat g = new DecimalFormat("###0.000");

        System.out.println("\nfor " + index + " jobs the service node statistics are:\n");
        System.out.println("  avg interarrivals .. =   " + f.format(event[0].t / index));
        System.out.println("  avg wait ........... =   " + f.format(area / index));
        System.out.println("  avg # in node ...... =   " + f.format(area / t.current));

        for (s = 1; s <= SERVERS; s++)          /* adjust area to calculate */
            //area -= sum[s].service;              /* averages for the queue   */

        /*System.out.println("  avg delay .......... =   " + f.format(area / index));
        System.out.println("  avg # in queue ..... =   " + f.format(area / t.current));
        System.out.println("\nthe server statistics are:\n");
        System.out.println("    server     utilization     avg service      share");
        for (s = 1; s <= SERVERS; s++) {
            System.out.print("       " + s + "          " + g.format(sum[s].service / t.current) + "            ");
            System.out.println(f.format(sum[s].service / sum[s].served) + "         " + g.format(sum[s].served / (double)index));
        }

        System.out.println("");*/
    }

    /* ALGORITHM 1 */
    private void execAlgorithm1(CloudletEvent[] cloudletEvents, List<CloudEvent> cloudEvents, MarkovState markovState, Time time) {
        if ((markovState.getN1Clet() + markovState.getN2Clet()) == N) {     /* check (n1 + n2 = N)*/
            this.acceptJobToCloud(cloudletEvents[0], cloudEvents, markovState, time);    /* accept job on cloud */
        } else {
            this.acceptJobToCloudlet(cloudletEvents, markovState, time);    /* accept job on cloudlet */
        }
        computeNextArrival(cloudletEvents);                                 /* compute next job arrival */
    }

    /* CLOUDLET */

    /* accept job to cloudlet */
    private void acceptJobToCloudlet(CloudletEvent[] cloudletEvents, MarkovState markovState, Time time) {
        if (cloudletEvents[0].getClassType() == ClassType.CLASS1) {            /* process CLASS1 arrival */
            this.processClass1Arrival(cloudletEvents, markovState, time);
        } else {                                                                        /* process CLASS2 arrival */
            this.processClass2Arrival(cloudletEvents, markovState, time);
        }
    }

    private void processClass1Arrival(CloudletEvent[] cloudletEvents, MarkovState markovState, Time time) {
        markovState.incrementN1Clet();                                      /* increment n1 cloudlet state variable */
        this.arrival[0] += getArrivalType1();                               /* compute next arrival of CLASS1 */
        int s = findCloudletIdleServer(cloudletEvents);                     /* find longest idle server */
        double service = getServiceType1();                                 /* compute service time */
        cloudletEvents[s].setNextEventTime(time.getCurrent() + service);    /* set job departure time */
        cloudletEvents[s].setEventStatus(EventStatus.ACTIVE);               /* set event active */
        cloudletEvents[s].setClassType(ClassType.CLASS1);                   /* set event class 1 */
    }

    private void processClass2Arrival(CloudletEvent[] cloudletEvents, MarkovState markovState, Time time) {
        markovState.incrementN2Clet();                                      /* increment n2 cloudlet state variable */
        this.arrival[1] += getArrivalType2();                               /* compute next arrival of CLASS2 */
        int s = findCloudletIdleServer(cloudletEvents);                     /* find longest idle server */
        double service = getServiceType2();                                 /* compute service time */
        cloudletEvents[s].setNextEventTime(time.getCurrent() + service);    /* set job departure time */
        cloudletEvents[s].setEventStatus(EventStatus.ACTIVE);               /* set event active */
        cloudletEvents[s].setClassType(ClassType.CLASS2);                   /* set event class 2 */
    }

    private void processCloudletDeparture(int eventIndex, CloudletEvent[] cloudletEvents, MarkovState markovState) {
        CloudletEvent event = cloudletEvents[eventIndex];       /* get event to process */
        if (event.getClassType() == ClassType.CLASS1) {         /* process class 1 departure */
        markovState.decrementN1Clet();                          /* decrement class 1 state */
        } else if (event.getClassType() == ClassType.CLASS2) {  /* process class 2 departure */
            markovState.decrementN2Clet();                      /* decrement class 2 state */
        }

        /* set server idle */
        cloudletEvents[eventIndex].setClassType(ClassType.NONE);
        cloudletEvents[eventIndex].setEventStatus(EventStatus.NOT_ACTIVE);
    }

    /* -----------------------------------------------------
     * return the index of the available server idle longest
     * -----------------------------------------------------
     */
    int findCloudletIdleServer(CloudletEvent[] cloudletEvents) {
        int s;
        int i = 1;

        while (cloudletEvents[i].getEventStatus() == EventStatus.ACTIVE) {    /* find the index of the first available */
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

    /* END CLOUDLET */

    /* CLOUD */

    private void acceptJobToCloud(CloudletEvent arrivalEvent, List<CloudEvent> cloudEvents, MarkovState markovState, Time time) {
        int s = findCloudIdleServer(cloudEvents);                           /* find the longest idle server on cloud */
        ClassType arrivalType = arrivalEvent.getClassType();                /* get arrival class type */

        double service = -1.0;
        switch (arrivalType) {
            case CLASS1:
                this.arrival[0] += getArrivalType1();                       /* compute next CLASS1 arrival */
                markovState.incrementN1Cloud();                             /* increment cloud CLASS1 state */
                service = getServiceCloudType1();                           /* compute cloud CLASS1 service time */
                cloudEvents.get(s).setClassType(ClassType.CLASS1);          /* set job of CLASS1 */
                break;
            case CLASS2:
                this.arrival[1] += getArrivalType2();                       /* compute next CLASS1 arrival */
                markovState.incrementN2Cloud();                             /* increment cloud CLASS1 state */
                service = getServiceCloudType2();                           /* compute cloud CLASS1 service time */
                cloudEvents.get(s).setClassType(ClassType.CLASS2);          /* set job of CLASS1 */
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

    private void processCloudDeparture(int eventIndex, List<CloudEvent> cloudEvents, MarkovState markovState) {
        if (cloudEvents.get(eventIndex).getClassType() == ClassType.CLASS1) {           /* process cloud class1 departure */
            markovState.decrementN1Cloud();                                             /* decrement cloud class1 state */
        } else if (cloudEvents.get(eventIndex).getClassType() == ClassType.CLASS2) {    /* process cloud class2 departure */
            markovState.decrementN2Cloud();                                             /* decrement cloud class2 state */
        }

        /* set server idle */
        cloudEvents.get(eventIndex).setClassType(ClassType.NONE);
        cloudEvents.get(eventIndex).setEventStatus(EventStatus.NOT_ACTIVE);
    }
    /* END CLOUD*/

    /* UTILS */

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
        while (cloudletEvents[cloudletScan].getEventStatus() == EventStatus.NOT_ACTIVE && cloudletScan < N) {   /* find the index of the first 'active' */
            cloudletScan++;                                                                            /* element in the event list */
        }
        cloudletIndex = cloudletScan;
        while (cloudletScan < N) {         /* now, check the others to find which event type is most imminent */
            cloudletScan++;
            if ((cloudletEvents[cloudletScan].getEventStatus() == EventStatus.ACTIVE) &&
                    (cloudletEvents[cloudletScan].getNextEventTime() < cloudletEvents[cloudletIndex].getNextEventTime()))
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
            while (cloudEvents.get(cloudScan).getEventStatus() == EventStatus.NOT_ACTIVE && cloudScan < cloudEvents.size() - 1) {
                cloudScan++;
            }
            cloudIndex = cloudScan;
            while (cloudScan < cloudEvents.size() - 1) {
                cloudScan++;
                if (cloudEvents.get(cloudScan).getEventStatus() == EventStatus.ACTIVE &&
                        cloudEvents.get(cloudScan).getNextEventTime() < cloudEvents.get(cloudIndex).getNextEventTime()) {
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

    private void computeNextArrival(CloudletEvent[] cloudletEvents) {
        if (cloudletEvents[0].getEventStatus() == EventStatus.ACTIVE) {
            if (this.arrival[0] < this.arrival[1]) {                            /* next arrival will be a CLASS1 job */
                cloudletEvents[0].setNextEventTime(this.arrival[0]);
                cloudletEvents[0].setClassType(ClassType.CLASS1);
            } else {                                                            /* next arrival will be a CLASS2 job */
                cloudletEvents[0].setNextEventTime(this.arrival[1]);
                cloudletEvents[0].setClassType(ClassType.CLASS2);
            }
            if (cloudletEvents[0].getNextEventTime() > STOP) {                  /* if arrival time out of simulation range */
                cloudletEvents[0].setEventStatus(EventStatus.NOT_ACTIVE);       /* disable arrival event */
            }
        }
    }
}
