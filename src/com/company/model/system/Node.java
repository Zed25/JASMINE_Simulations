package com.company.model.system;

import com.company.model.event.pool.CloudJobQueue;
import com.company.model.event.pool.CloudletJobQueue;

public class Node {
    private CloudletJobQueue cloudletJobQueue;
    private CloudJobQueue cloudJobQueue;
    private double class1CloudletQueue;
    private double class2CloudletQueue;
    private double class1CloudQueue;
    private double class2CloudQueue;
    private double cloudletDeparture;
    private double cloudDeparture;

    public Node(int N) {
        this.class1CloudletQueue = 0.0;
        this.class2CloudletQueue = 0.0;
        this.class1CloudQueue = 0.0;
        this.class2CloudQueue = 0.0;
        this.cloudletDeparture = 0.0;
        this.cloudDeparture = 0.0;

        this.cloudletJobQueue = new CloudletJobQueue(N);
        this.cloudJobQueue = new CloudJobQueue();
    }

    public void incrementClass1CloudletQueue() {
        this.class1CloudletQueue++;
    }
    public void incrementClass2CloudletQueue() {
        this.class2CloudletQueue++;
    }
    public void incrementClass1CloudQueue() {
        this.class1CloudQueue++;
    }
    public void incrementClass2CloudQueue() {
        this.class2CloudQueue++;
    }

    public void decrementClass1CloudletQueue() {
        this.class1CloudletQueue--;
    }
    public void decrementClass2CloudletQueue() {
        this.class2CloudletQueue--;
    }
    public void decrementClass1CloudQueue() {
        this.class1CloudQueue--;
    }
    public void decrementClass2CloudQueue() {
        this.class2CloudQueue--;
    }

    public void incrementCloudletDeparted() {
        this.cloudletDeparture++;
    }
    public void incrementCloudDeparted() {
        this.cloudDeparture++;
    }
    public void decrementCloudletDeparted() {
        this.cloudletDeparture--;
    }
    public void decrementCloudDeparted() {
        this.cloudDeparture--;
    }

    public double getCloudletJobsNumber() {
        return this.class1CloudletQueue + this.class2CloudletQueue;
    }

    public double getCloudJodbNumber() {
        return this.class1CloudQueue + this.class2CloudQueue;
    }

    public double getClass1CloudletQueue() {
        return class1CloudletQueue;
    }

    public void setClass1CloudletQueue(double class1CloudletQueue) {
        this.class1CloudletQueue = class1CloudletQueue;
    }

    public double getClass2CloudletQueue() {
        return class2CloudletQueue;
    }

    public void setClass2CloudletQueue(double class2CloudletQueue) {
        this.class2CloudletQueue = class2CloudletQueue;
    }

    public double getClass1CloudQueue() {
        return class1CloudQueue;
    }

    public void setClass1CloudQueue(double class1CloudQueue) {
        this.class1CloudQueue = class1CloudQueue;
    }

    public double getClass2CloudQueue() {
        return class2CloudQueue;
    }

    public void setClass2CloudQueue(double class2CloudQueue) {
        this.class2CloudQueue = class2CloudQueue;
    }

    public CloudletJobQueue getCloudletJobQueue() {
        return cloudletJobQueue;
    }

    public void setCloudletJobQueue(CloudletJobQueue cloudletJobQueue) {
        this.cloudletJobQueue = cloudletJobQueue;
    }

    public CloudJobQueue getCloudJobQueue() {
        return cloudJobQueue;
    }

    public void setCloudJobQueue(CloudJobQueue cloudJobQueue) {
        this.cloudJobQueue = cloudJobQueue;
    }

    public double getCloudletDeparture() {
        return cloudletDeparture;
    }

    public void setCloudletDeparture(double cloudletDeparture) {
        this.cloudletDeparture = cloudletDeparture;
    }

    public double getCloudDeparture() {
        return cloudDeparture;
    }

    public void setCloudDeparture(double cloudDeparture) {
        this.cloudDeparture = cloudDeparture;
    }
}
