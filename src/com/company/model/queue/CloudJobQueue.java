package com.company.model.queue;

import com.company.model.job.CloudJob;
import com.company.model.job.Job;

import java.util.ArrayList;
import java.util.List;

public class CloudJobQueue implements JobQueue {
    private List<CloudJob> cloudJobs;

    public CloudJobQueue() {
        this.cloudJobs = new ArrayList<>();
    }

    @Override
    public void putJob(Job job) {
       if (job instanceof CloudJob) {
           this.cloudJobs.add((CloudJob) job);
       }
    }

    @Override
    public Job popJob() {
        if (this.cloudJobs.size() > 0) {
            return this.cloudJobs.remove(0);
        } else {
            return null;
        }
    }

    public List<CloudJob> getCloudJobs() {
        return cloudJobs;
    }

    public void setCloudJobs(List<CloudJob> cloudJobs) {
        this.cloudJobs = cloudJobs;
    }
}

