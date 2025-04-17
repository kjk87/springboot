package kr.co.pplus.store.scheduler;

import java.util.Properties;

import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory


	implements ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(AutowiringSpringBeanJobFactory.class);
	private transient AutowireCapableBeanFactory beanFactory;
	private ApplicationContext context;
	private Properties config;
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
		this.beanFactory = context.getAutowireCapableBeanFactory();
	}
	
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle)
			throws Exception {
		Object task = bundle.getJobDetail().getJobDataMap().get("task");
		logger.info(task.getClass().getSimpleName() + " execute");
		final Object job = super.createJobInstance(bundle);
		beanFactory.autowireBean(job);
		return job;
	}
}
