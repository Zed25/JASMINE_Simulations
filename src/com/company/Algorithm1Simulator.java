package com.company;

import com.company.model.Time;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Algorithm1Simulator {

    private long intialSeed = 48271; //TODO delete it

    private final double START = 0.0;               /*initial time                   */
    private final double STOP = 20000.0;            /* terminal (close the door) time */
    private final double INFINITY = (100.0 * STOP); /*must be much larger than STOP  */
    private Rngs rngs;
    private Rvgs rvgs;
    private double arrival[] = {START, START};

    private double hyperexpProbability = 0.2;
    private double lambda1 = 6.0;
    private double lambda2 = 6.25;
    private double mu1Cloudlet = 0.45;
    private double mu2Cloudlet = 0.27;
    private double mu1Cloud = 0.25;
    private double mu2Cloud = 0.22;

    private long toCloud = 0;
    private long cloudDeparted = 0;
    private long cloudletDeparted = 0;
    private long n1 = 0;
    private long n2 = 0;
    private long N = 20;
    private long number = 0;
    private List<Integer> cloudQueue = new ArrayList<>();
    private int lastScheduled;

    Time t = new Time();

    public Algorithm1Simulator() {
        rngs = new Rngs();
        rvgs = new Rvgs(rngs);
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
    public double getArrivalType1() {
        rvgs.rngs.selectStream(0);
        arrival[0] += rvgs.exponential(1/ lambda1);
        return arrival[0];
    }

    /** ----------------------------------------------------
     * generate the next arrival type 2 time, with rate 1/2
     * -----------------------------------------------------
     */
    public double getArrivalType2() {
        rvgs.rngs.selectStream(1);
        arrival[1] += rvgs.exponential(1/ lambda2);
        return arrival[1];
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

        rvgs.rngs.plantSeeds(intialSeed);
        t.setCurrent(START);         /* set the clock                         */
        t.setArrival(new double[]{getArrivalType1(), getArrivalType2()}); /* schedule the first arrival            */
        t.setLast(new double[]{INFINITY, INFINITY});
        t.setDeparture(INFINITY); /* the first event can't be a cloudlet completion */
        t.setCloudDeparture(INFINITY); /* the first event can't be a cloud completion */
        while (t.getArrival()[0] < STOP || t.getArrival()[1] < STOP || number > 0 || cloudQueue.size() > 0) {
            t.setNext(min(t.getArrival()[0], t.getArrival()[1], t.getDeparture(), t.getCloudDeparture())); /*next event time */
            //TODO update statistics
            t.setCurrent(t.getNext());                   /* advance the clock */

            if (t.getCurrent() == t.getArrival()[0])  {              // process a type 1 arrival
                processType1Arrival();
            } else if (t.getCurrent() == t.getArrival()[1]) {        // process a type 2 arrival
                processType2Arrival();
            } else if (t.getCurrent() == t.getDeparture()) {         // process departure from cloudlet
                processCloudletDeparture();
            } else {
                processCloudDeparture();
            }
        }

        DecimalFormat f = new DecimalFormat("###0.00");

        System.out.println("toCloud = " + toCloud +"\n" +
                "cloudDeparted = " + cloudDeparted + "\n" +
                "cloudletDeparted = " + cloudletDeparted + "\n" +
                "n1 = " + n1 + "\n" +
                "n2 = " + n2 + "\n" +
                "N = " + N + "\n" +
                "number = " + number + "\n");

    }

    private void processType1Arrival() {
        if (n1 + n2 == N) {
            cloudArrival(1);
        } else {
            number++;
            n1++;
            if (lastScheduled == 0) {
                t.setDeparture(t.getCurrent() + getServiceType1());
                lastScheduled = 1;
            }
        }
        t.setType1Arrival(getArrivalType1());
        if (t.getArrival()[0] > STOP)  {
            t.setType1Last(t.getCurrent());
            t.setType1Arrival(INFINITY);
        }
    }

    private void processType2Arrival() {
        if (n1 + n2 == N) {
            cloudArrival(2);
        } else {
            number++;
            n2++;
            if (lastScheduled == 0) {
                t.setDeparture(t.getCurrent() + getServiceType2());
                lastScheduled = 2;
            }
        }
        t.setType2Arrival(getArrivalType2());
        if (t.getArrival()[1] > STOP)  {
            t.setType2Last(t.getCurrent());
            t.setType2Arrival(INFINITY);
        }
    }

    private void processCloudletDeparture() {
        cloudletDeparted++;
        number--;
        if (lastScheduled == 1) {
            n1--;
        } else {
            n2--;
        }

        processNextCloudletDeparture();
    }

    private void processNextCloudletDeparture() {
        if (n1 > 0) {
            t.setDeparture(t.getCurrent() + getServiceType1());
            lastScheduled = 1;
        } else if (n2 > 0) {
            t.setDeparture(t.getCurrent() + getServiceType2());
            lastScheduled = 2;
        } else {
            t.setDeparture(INFINITY);
            lastScheduled = 0;
        }
    }

    private void processCloudDeparture() {
        cloudDeparted++;
        cloudQueue.remove(0);

        if (cloudQueue.size() > 0) {
            if (cloudQueue.get(0) == 1) {
                t.setCloudDeparture(t.getCurrent() + getServiceCloudType1());
            } else {
                t.setCloudDeparture(t.getCurrent() + getServiceCloudType2());
            }
        } else {
            t.setCloudDeparture(INFINITY);
        }
    }

    private void cloudArrival(int jobType) {
        cloudQueue.add(jobType);

        if (cloudQueue.size() == 1) {
            if (cloudQueue.get(0) == 1) {
                t.setCloudDeparture(t.getCurrent() + getServiceCloudType1());
            } else {
                t.setCloudDeparture(t.getCurrent() + getServiceCloudType2());
            }
        }
    }
}
