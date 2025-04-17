package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.api.jpa.service.ReapPayService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@DisallowConcurrentExecution
public class ReappayLoginTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(ReappayLoginTask.class);

	@Autowired
	ReapPayService svc;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}
		logger.info("ReappayLoginTask execute...");
		login();
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	private void login() {
		svc.login(1L);
		svc.login(2L);
	}

}
