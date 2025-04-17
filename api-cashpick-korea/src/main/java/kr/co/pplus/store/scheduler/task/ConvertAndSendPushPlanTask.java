package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.PushPlanService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.model.MsgOnly;
import kr.co.pplus.store.type.model.PushPlan;
import kr.co.pplus.store.type.model.SearchOpt;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DisallowConcurrentExecution
public class ConvertAndSendPushPlanTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(ConvertAndSendPushPlanTask.class);
	
	@Autowired
	PushPlanService svc;
	
	@Autowired
    MsgProducer producer;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;
	
	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("ConvertAndSendPushPlanTask execute...");

		process();
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}
	
	private void process() {
		//아직 처리 안 된 PushPlan 목록을 얻어온다.
		//하나의 PushPlan을 하나이 Msg로 변환한다.
		//하나의 Msg를 발송한다.
		SearchOpt opt = new SearchOpt();
		opt.setFilter(new ArrayList<String>());
		opt.getFilter().add("ready");
		
		List<PushPlan> planList = svc.getAllByStatus(opt);
		for (PushPlan plan : planList) {
			try {
				MsgOnly msg = svc.convert(plan);
				if (msg != null) {
					producer.push(msg);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}
