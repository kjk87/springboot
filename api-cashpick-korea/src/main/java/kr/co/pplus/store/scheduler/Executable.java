package kr.co.pplus.store.scheduler;

import java.io.Serializable;
import java.util.Map;

import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public interface Executable extends Serializable {
	public void execute(Map<String, Object> paramMap) throws JobExecutionException;
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException;
}
