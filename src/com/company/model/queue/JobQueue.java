package com.company.model.queue;

import com.company.model.job.Job;

public interface JobQueue {
    void putJob(Job job);
    Job popJob();
}
