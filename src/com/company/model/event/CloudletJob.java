package com.company.model.event;

public class CloudletJob extends Job {


    public CloudletJob(ClassType classType, double arrivalTime, double scheduledTime) {
        this.classType = classType;
        this.arrivalTime = arrivalTime;
        this.scheduledTime = scheduledTime;
    }

    public CloudletJob() {
        this.classType = ClassType.NONE;
        this.arrivalTime = -1.0;
        this.scheduledTime = -1.0;
    }
}
