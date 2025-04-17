package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.RankingService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@DisallowConcurrentExecution
public class RankingJobTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(RankingJobTask.class);

	@Autowired
	RankingService svc;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("RankingJobTask execute...");
		
		try {
			weeklyJob();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			allJob();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	private void weeklyJob() {
		svc.updateWeekRanking();
	}
	
	private void allJob() {
		svc.updateAllRanking();
	}


}
