package com.company;

import com.company.model.Time;
import com.company.model.event.ClassType;
import com.company.model.event.CloudJob;
import com.company.model.event.CloudletJob;
import com.company.model.event.Job;
import com.company.model.system.Node;

import java.text.DecimalFormat;

public class Algorithm1Simulator {

    private long initialSeed = 48271; //TODO delete it

    /* SIMULATION TIME INPUT VARIABLES */
    private final double START = 0.0;               /*initial time                   */
    private final double STOP = 20000.0;            /* terminal (close the door) time */
    private final double INFINITY = (100.0 * STOP); /*must be much larger than STOP  */

    /* SIMULATION SYSTEM INPUT VARIABLES */
    private final int N = 20;
    private final double hyperexpProbability = 0.2;
    private final double lambda1 = 6.0;
    private final double lambda2 = 6.25;
    private final double mu1Cloudlet = 0.45;
    private final double mu2Cloudlet = 0.27;
    private final double mu1Cloud = 0.25;
    private final double mu2Cloud = 0.22;

    private Rngs rngs;
    private Rvgs rvgs;

    private Time t;
    private Node node;

    public Algorithm1Simulator() {
        rngs = new Rngs();
        rvgs = new Rvgs(rngs);
        this.t = new Time();
        this.node = new Node(N);
    }

    /** -----------------------------
     * return the smaller of a, b, c
     * ------------------------------
     */
    public double min(double a, double b, double c, double d) {
        double min = a;
        if (min > b) {
            min = b;
        }
        if (min > c) {
            min = c;
        }
        if (min > d) {
            min = d;
        }
        return min;
    }


    /** ----------------------------------------------------
     * generate the next arrival type 1 time, with rate 1/2
     * -----------------------------------------------------
     */
    public double getArrivalType1(double from) {
        rvgs.rngs.selectStream(0);
        from += rvgs.exponential(1/ lambda1);
        return from;
    }

    /** ----------------------------------------------------
     * generate the next arrival type 2 time, with rate 1/2
     * -----------------------------------------------------
     */
    public double getArrivalType2(double from) {
        rvgs.rngs.selectStream(1);
        from += rvgs.exponential(1/ lambda2);
        return from;
    }


    /** -------------------------------------------
     * generate the next service time with rate 2/3
     * --------------------------------------------
     */
    public double getServiceType1() {
        rvgs.rngs.selectStream(2);
        if (rvgs.rngs.random() <= hyperexpProbability) {
            return rvgs.exponential(1/(2 * hyperexpProbability * mu1Cloudlet));
        } else {
            return rvgs.exponential(1/(2 * (1-hyperexpProbability) * mu1Cloudlet));
        }
    }

    /** -------------------------------------------
     * generate the next service time with rate 2/3
     * --------------------------------------------
     */
    public double getServiceType2() {
        rvgs.rngs.selectStream(3);
        if (rvgs.rngs.random() <= hyperexpProbability) {
            return rvgs.exponential(2*hyperexpProbability*mu2Cloudlet);
        } else {
            return rvgs.exponential(2*(1-hyperexpProbability)*mu2Cloudlet);
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

        rvgs.rngs.plantSeeds(initialSeed);
        t.setCurrent(START);         /* set the clock                         */
        t.setArrival(new double[]{getArrivalType1(START), getArrivalType2(START)}); /* schedule the first arrival            */
        t.setLast(new double[]{INFINITY, INFINITY});
        t.setCloudletDeparture(INFINITY); /* the first event can't be a cloudlet completion */
        t.setCloudDeparture(INFINITY); /* the first event can't be a cloud completion */

        while (t.getArrival()[0] < STOP || t.getArrival()[1] < STOP || node.getCloudletJobsNumber() > 0 || node.getCloudJodbNumber() > 0) {
            t.setNext(min(
                        t.getArrival()[0],
                        t.getArrival()[1],
                        t.getCloudletDeparture(),
                        t.getCloudDeparture()
                    )
            ); /*next event time */
            //TODO update statistics
            t.setCurrent(t.getNext());                   /* advance the clock */

            if (t.getCurrent() == t.getArrival()[0])  {              // process a type 1 arrival
                processType1Arrival();
            } else if (t.getCurrent() == t.getArrival()[1]) {        // process a type 2 arrival
                processType2Arrival();
            } else if (t.getCurrent() == t.getCloudletDeparture()) {         // process departure from cloudlet
                processCloudletDeparture();
            } else {
                processCloudDeparture();
            }
        }

        DecimalFormat f = new DecimalFormat("###0.00");

        System.out.println("toCloud = " + 0 +"\n" +
                "cloudDeparted = " + node.getCloudletDeparture() + "\n" +
                "cloudletDeparted = " + node.getCloudDeparture() + "\n" +
                "n1 = " + node.getClass1CloudletQueue() + "\n" +
                "n2 = " + node.getClass2CloudletQueue() + "\n" +
                "N = " + N + "\n" +
                "number = " + node.getCloudletJobsNumber() + "\n");

    }

    private void processType1Arrival() {
        if (node.getClass1CloudletQueue() + node.getClass2CloudletQueue() == N) {
            cloudArrival(1);
        } else {
            node.incrementClass1CloudletQueue();
            double scheduleTime;
            if (node.getCloudletJobsNumber() == 1) {
                t.setCloudletDeparture(t.getCurrent() + getServiceType1());
                scheduleTime = t.getCloudletDeparture();
            } else {
                scheduleTime = -1.0;
            }
            node.getCloudletJobQueue().putJob(
                    new CloudletJob(
                            ClassType.CLASS1,
                            t.getArrival()[0],
                            scheduleTime
                            )
            );
        }
        t.setType1Arrival(getArrivalType1(t.getArrival()[0]));
        if (t.getArrival()[0] > STOP)  {
            t.setType1Last(t.getCurrent());
            t.setType1Arrival(INFINITY);
        }
    }

    private void processType2Arrival() {
        if (node.getClass1CloudletQueue() + node.getClass2CloudletQueue() == N) {
            cloudArrival(2);
        } else {
            node.incrementClass2CloudletQueue();
            double scheduleTime;
            if (node.getCloudletJobsNumber() == 1) {
                t.setCloudletDeparture(t.getCurrent() + getServiceType2());
                scheduleTime = t.getCloudletDeparture();
            } else {
                scheduleTime = -1.0;
            }
            node.getCloudletJobQueue().putJob(
                    new CloudletJob(
                            ClassType.CLASS2,
                            t.getArrival()[1],
                            scheduleTime
                    )
            );
        }
        t.setType2Arrival(getArrivalType2(t.getArrival()[1]));
        if (t.getArrival()[1] > STOP)  {
            t.setType2Last(t.getCurrent());
            t.setType2Arrival(INFINITY);
        }
    }

    private void processCloudletDeparture() {
        node.incrementCloudletDeparted();

        if (node.getCloudletJobQueue().getCloudletJobs()[0].getClassType() == ClassType.CLASS1) {
            node.decrementClass1CloudletQueue();
        } else {
            node.decrementClass2CloudletQueue();
        }

        node.getCloudletJobQueue().popJob();
        processNextCloudletDeparture();
    }

    private void processNextCloudletDeparture() {
        if (node.getCloudletJobQueue().getLastJobIndex() > -1) {
            CloudletJob job = node.getCloudletJobQueue().getCloudletJobs()[0]; //get first job in queue
            if (job.getClassType() == ClassType.CLASS1) {
                t.setCloudletDeparture(t.getCurrent() + getServiceType1());
                job.setScheduledTime(t.getCloudletDeparture());
            } else {
                t.setCloudletDeparture(t.getCurrent() + getServiceType2());
                job.setScheduledTime(t.getCloudletDeparture());
            }
        } else { // no jobs in queue
            t.setCloudletDeparture(INFINITY);
        }
    }

    private void processCloudDeparture() {
        node.incrementCloudDeparted();
        node.getCloudJobQueue().popJob();

        if (node.getCloudJobQueue().getCloudJobs().size() > 0) {
            CloudJob job = node.getCloudJobQueue().getCloudJobs().get(0); //get first job in queue
            shceduleCloudJob(job);
        } else { //no job in queue
            t.setCloudDeparture(INFINITY);
        }
    }

    private void cloudArrival(int jobType) {
        CloudJob job = new CloudJob(
                jobType == 1 ? ClassType.CLASS1 : ClassType.CLASS2,
                jobType==  1 ? t.getArrival()[0] : t.getArrival()[1],
                -1.0);
        node.getCloudJobQueue().putJob(job);

        if (node.getCloudJobQueue().getCloudJobs().size() == 1) {
            shceduleCloudJob(job);
        }
    }

    private void shceduleCloudJob(Job job) {
        if (job.getClassType() == ClassType.CLASS1) {
            t.setCloudDeparture(t.getCurrent() + getServiceCloudType1());
            job.setScheduledTime(t.getCloudDeparture());
        } else {
            t.setCloudDeparture(t.getCurrent() + getServiceCloudType2());
            job.setScheduledTime(t.getCloudDeparture());
        }
    }
}
