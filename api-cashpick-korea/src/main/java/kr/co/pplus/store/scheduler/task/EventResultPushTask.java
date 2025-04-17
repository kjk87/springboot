package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.EventService;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.type.model.Event;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

@DisallowConcurrentExecution
public class EventResultPushTask extends AbstractTask {
private final static Logger logger = LoggerFactory.getLogger(EventResultPushTask.class);

	@Autowired
	EventService svc;
	
	@Autowired
	QueueService queueSvc;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("EventResultPushTask execute...");

		announce();
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	public void announce() {
		List<Event> eventList = svc.getAllForResultPush();
		for (Event event : eventList) {
			event.setStatus("announce");

			logger.debug("updateStatus eventSeqNo : " + event.getNo());
			svc.updateStatus(event);

			if(event.getAutoRegist() != null && event.getAutoRegist()){
				try {
					logger.debug("copyEvent eventSeqNo : " + event.getNo());
					svc.copyEvent(event);
				}catch (Exception e){
					logger.error("auto regist error : " + e.toString());
				}
			}

			logger.debug("updatePriority eventSeqNo : " + event.getNo());
			event.setPriority(-1);
			svc.updatePriority(event);

			logger.debug("announce eventSeqNo : " + event.getNo());
			svc.announce(event);
		}		
	}
}
