package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.CashBolService;
import kr.co.pplus.store.mvc.service.EventService;
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
public class CheckEventStatusTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(CheckEventStatusTask.class);

	@Autowired
	EventService svc;

	@Autowired
	CashBolService cashBolSvc;
	
	@Autowired
	QueueService queueSvc;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;
	

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("CheckEventStatusTask execute...");

		cancelAll();
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	private void cancelAll() {
		svc.clearCancelAll();
	}
}
