package com.company.model.event;

public class CloudJob extends Job {

    public CloudJob(ClassType classType, double arrivalTime, double scheduledTime) {
        this.classType = classType;
        this.arrivalTime = arrivalTime;
        this.scheduledTime = scheduledTime;
    }

    public CloudJob() {
        this.classType = ClassType.NONE;
        this.arrivalTime = -1.0;
        this.scheduledTime = -1.0;
    }
}
