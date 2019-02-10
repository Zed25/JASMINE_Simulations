package com.company.model.statistics;

import com.company.model.SystemState;
import com.company.model.Time;

public class AreaStatistics implements Statistics {
    private double systemArea;               /* mean job number in system */
    private double cloudletArea;            /* mean job number in cloudlet */
    private double cloudArea;           /* mean job number in cloud */
    private double n1CletArea;          /* mean n1 job number in cloulet */
    private double n2CletArea;          /* mean n2 job number in cloudlet */
    private double n1CloudArea;         /* mean n1 job number in cloud */
    private double n2CloudArea;         /* mean n2 job number in cloud */

    public AreaStatistics() {
        this.systemArea = 0.0;
        this.cloudletArea = 0.0;
        this.cloudArea = 0.0;
        this.n1CletArea = 0.0;
        this.n2CletArea = 0.0;
        this.n1CloudArea = 0.0;
        this.n2CloudArea = 0.0;
    }

    @Override
    public void updateStatistics(SystemState systemState, Time time) {
        if (time.getNext() > time.getCurrent()) {
            double deltaT = time.getNext() - time.getCurrent();
            systemArea += deltaT * (systemState.getCloudletJobsNumber() + systemState.getCloudJobsNumber());
            cloudletArea += deltaT * systemState.getCloudletJobsNumber();
            cloudArea += deltaT * systemState.getCloudJobsNumber();
            n1CletArea += deltaT * systemState.getN1Clet();
            n2CletArea += deltaT * systemState.getN2Clet();
            n1CloudArea += deltaT * systemState.getN1Cloud();
            n2CloudArea += deltaT * systemState.getN2Cloud();
        }
    }

    public double getSystemArea() {
        return systemArea;
    }

    public double getCloudletArea() {
        return cloudletArea;
    }

    public double getCloudArea() {
        return cloudArea;
    }

    public double getN1CletArea() {
        return n1CletArea;
    }

    public double getN2CletArea() {
        return n2CletArea;
    }

    public double getN1CloudArea() {
        return n1CloudArea;
    }

    public double getN2CloudArea() {
        return n2CloudArea;
    }
}
