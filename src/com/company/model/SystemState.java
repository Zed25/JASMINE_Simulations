package com.company.model;

public class SystemState {
    private long n1Clet;    /* CLASS1 jobs' number in cloudlet */
    private long n2Clet;    /* CLASS2 jobs' number in cloudlet */
    private long n1Cloud;   /* CLASS1 jobs' number in cloud */
    private long n2Cloud;   /* CLASS2 jobs' number in cloud */

    /* First markov state (0,0,0,0) */
    public SystemState() {
        this.n1Clet = 0;
        this.n2Clet = 0;
        this.n1Cloud = 0;
        this.n2Cloud = 0;
    }

    /* check if system is empty */
    public boolean systemIsEmpty() {
        return n1Clet == 0 && n2Clet == 0 && n1Cloud == 0 && n2Cloud == 0;
    }

    /* increment n1 cloudlet state variable */
    public void incrementN1Clet() {
        this.n1Clet++;
    }

    /* decrement n1 cloudlet state variable*/
    public void decrementN1Clet() {
        this.n1Clet--;
    }

    /* increment n2 cloudlet state variable */
    public void incrementN2Clet() {
        this.n2Clet++;
    }

    /* decrement n2 cloudlet state variable*/
    public void decrementN2Clet() {
        this.n2Clet--;
    }

    /* increment n1 cloud state variable */
    public void incrementN1Cloud() {
        this.n1Cloud++;
    }

    /* decrement n1 cloud state variable*/
    public void decrementN1Cloud() {
        this.n1Cloud--;
    }

    /* increment n2 cloud state variable */
    public void incrementN2Cloud() {
        this.n2Cloud++;
    }

    /* decrement n2 cloud state variable*/
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

    public void setN1Clet(long n1Clet) {
        this.n1Clet = n1Clet;
    }

    public long getN2Clet() {
        return n2Clet;
    }

    public void setN2Clet(long n2Clet) {
        this.n2Clet = n2Clet;
    }

    public long getN1Cloud() {
        return n1Cloud;
    }

    public void setN1Cloud(long n1Cloud) {
        this.n1Cloud = n1Cloud;
    }

    public long getN2Cloud() {
        return n2Cloud;
    }

    public void setN2Cloud(long n2Cloud) {
        this.n2Cloud = n2Cloud;
    }
}
