package com.company.model;

public class SystemState {
    private long n1Clet;    /* CLASS1 jobs' number in cloudlet */
    private long n2Clet;    /* CLASS2 jobs' number in cloudlet */
    private long n1Cloud;   /* CLASS1 jobs' number in cloud */
    private long n2Cloud;   /* CLASS2 jobs' number in cloud */

    /* First markov state (0,0,0,0) */
    public SystemState() {
        this.reset();
    }

    public void reset() {
        this.n1Clet = 0;
        this.n2Clet = 0;
        this.n1Cloud = 0;
        this.n2Cloud = 0;
    }

    /* check if system is empty */
    public boolean systemIsEmpty() {
        return n1Clet == 0 && n2Clet == 0 && n1Cloud == 0 && n2Cloud == 0;
    }

    public void incrementN1Clet() {
        this.n1Clet++;
    }

    public void decrementN1Clet() {
        this.n1Clet--;
    }

    public void incrementN2Clet() {
        this.n2Clet++;
    }

    public void decrementN2Clet() {
        this.n2Clet--;
    }

    public void incrementN1Cloud() {
        this.n1Cloud++;
    }

    public void decrementN1Cloud() {
        this.n1Cloud--;
    }

    public void incrementN2Cloud() {
        this.n2Cloud++;
    }

    public void decrementN2Cloud() {
        this.n2Cloud--;
    }

    /* get cloudlet aggregate state */
    public long getCloudletJobsNumber() {
        return this.n1Clet + this.n2Clet;
    }

    /* get cloud aggregate state*/
    public long getCloudJobsNumber() {
        return this.n1Cloud + this.n2Cloud;
    }

    public long getN1Clet() {
        return n1Clet;
    }

    public long getN2Clet() {
        return n2Clet;
    }

    public long getN1Cloud() {
        return n1Cloud;
    }

    public long getN2Cloud() {
        return n2Cloud;
    }

}
