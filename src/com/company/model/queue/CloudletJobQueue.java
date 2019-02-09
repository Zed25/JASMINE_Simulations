package com.company.model.queue;

import com.company.model.job.ClassType;
import com.company.model.job.CloudletJob;
import com.company.model.job.Job;

public class CloudletJobQueue implements JobQueue {
    private CloudletJob[] cloudletJobs;
    private int lastJobIndex;

    public CloudletJobQueue(int queueSize) {
        this.cloudletJobs = new CloudletJob[queueSize];
        this.lastJobIndex = -1;
        for (int i = 0; i < this.cloudletJobs.length; i++) {
            this.cloudletJobs[i] = new CloudletJob();
        }
    }

    @Override
    public void putJob(Job job) {
        if (job instanceof CloudletJob) {
            if (job.getClassType() == ClassType.CLASS1) {
                if (this.cloudletJobs[0].getClassType() == ClassType.NONE) {
                    this.cloudletJobs[0] = (CloudletJob) job;
                } else {
                    for (int i = this.cloudletJobs.length - 1; i >= 0; i--) {
                        if (this.cloudletJobs[i].getClassType() == ClassType.NONE) {
                            continue;
                        } else if (this.cloudletJobs[i].getClassType() == ClassType.CLASS1) {
                            this.cloudletJobs[i + 1] = (CloudletJob) job;
                            break;
                        } else {
                            if (this.cloudletJobs[i].getScheduledTime() > 0.0) { //class 2 job already in service
                                this.cloudletJobs[i + 1] = (CloudletJob) job;
                            } else {
                                this.cloudletJobs[i + 1] = this.cloudletJobs[i];
                            }
                        }
                        /*switch (this.cloudletJobs[i].getClassType()) {
                            case NONE:
                                continue;
                            case CLASS1:
                                this.cloudletJobs[i + 1] = (CloudletJob) job;
                                break;
                            case CLASS2:
                                if (this.cloudletJobs[i].getScheduledTime() > 0.0) { //class 2 job already in service
                                    this.cloudletJobs[i + 1] = (CloudletJob) job;
                                } else {
                                    this.cloudletJobs[i + 1] = this.cloudletJobs[i];
                                }
                        }*/
                    }
                }
                this.lastJobIndex++;
            } else {
                this.cloudletJobs[this.lastJobIndex + 1] = (CloudletJob) job;
                this.lastJobIndex++;
            }
        }
    }

    @Override
    public Job popJob() {
        if (this.lastJobIndex > -1) {
            CloudletJob job = this.cloudletJobs[0];
            for (int i = 0; i < this.lastJobIndex; i++) {
                this.cloudletJobs[i] = this.cloudletJobs[i + 1];
            }
            for (int j = this.lastJobIndex; j < this.cloudletJobs.length; j++) {
                this.cloudletJobs[j] = new CloudletJob();
            }
            this.lastJobIndex--;
            return job;
        } else {
            return  null;
        }
    }

    public CloudletJob[] getCloudletJobs() {
        return cloudletJobs;
    }

    public void setCloudletJobs(CloudletJob[] cloudletJobs) {
        this.cloudletJobs = cloudletJobs;
    }

    public int getLastJobIndex() {
        return lastJobIndex;
    }

    public void setLastJobIndex(int lastJobIndex) {
        this.lastJobIndex = lastJobIndex;
    }
}
