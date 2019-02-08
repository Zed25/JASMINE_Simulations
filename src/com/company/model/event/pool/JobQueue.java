package com.company.model.event.pool;

import com.company.model.event.Job;

public interface JobQueue {
    void putJob(Job job);
    Job popJob();
}
