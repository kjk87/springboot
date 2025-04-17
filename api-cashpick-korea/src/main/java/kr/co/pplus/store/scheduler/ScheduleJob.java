package kr.co.pplus.store.scheduler;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import kr.co.pplus.store.scheduler.task.AbstractTask;

public class ScheduleJob extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(ScheduleJob.class);
	
	@Autowired
	ApplicationContext context;
	
	private AbstractTask task;
	public ApplicationContext getContext() {
		return context;
	}

	public AbstractTask getTask() {
		return task;
	}

	public void setTask(AbstractTask task) {
		this.task = task;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		try {
			if (task != null && task.getClass().isAnnotationPresent(DisallowConcurrentExecution.class) && isNotRunning(ctx)) {
				task.execute(context, ctx.getMergedJobDataMap());
			}
		} catch (Exception ex) {
			
		}
	}

	private boolean isNotRunning(JobExecutionContext ctx) throws SchedulerException {
		List<JobExecutionContext> jobs = ctx.getScheduler().getCurrentlyExecutingJobs();
		for (JobExecutionContext job : jobs) {
			if (job.getTrigger().equals(ctx.getTrigger()) && !job.getJobInstance().equals(this)) {
				return false;
			}
		}
		return true;
	}
}
