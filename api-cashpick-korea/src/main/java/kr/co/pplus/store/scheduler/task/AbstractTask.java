package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.StoreApplication;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import kr.co.pplus.store.scheduler.Executable;

@DisallowConcurrentExecution
public abstract class AbstractTask extends QuartzJobBean implements Executable {


	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		execute(context.getMergedJobDataMap());
	}

}
