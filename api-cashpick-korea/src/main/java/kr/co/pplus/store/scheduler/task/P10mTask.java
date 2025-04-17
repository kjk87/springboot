package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.QueueService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@DisallowConcurrentExecution
public class P10mTask extends AbstractTask {

	@Autowired
	QueueService queueSvc;

	@Autowired
	ApplicationContext context;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;

	private static Logger logger = LoggerFactory.getLogger(P10mTask.class);
	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("P10mTask execute...");
		process();
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	private void process() {
		try {
			logger.info("P10mTask Start");
			Thread.sleep(10000L);

			logger.info("P10mTask End");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
