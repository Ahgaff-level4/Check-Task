package com.ahgaff_projects.mygoals;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

public class JobScheduler extends JobService {
    private final Context context;
    public JobScheduler(Context context) {
        this.context = context;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent intent = new Intent(context, NotificationService.class);
        startService(intent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}