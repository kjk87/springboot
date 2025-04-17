//package kr.co.pplus.store.scheduler.task;
//
//import kr.co.pplus.store.mvc.service.AgentService;
//import kr.co.pplus.store.type.model.Agent;
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//
//import java.util.List;
//import java.util.Map;
//
//@DisallowConcurrentExecution
//public class ChangeAgentContractStatusTask extends AbstractTask {
//	private final static Logger logger = LoggerFactory.getLogger(ChangeAgentContractStatusTask.class);
//
//	@Autowired
//	AgentService svc;
//
//	@Value("${STORE.SCHEDULER_ACTIVATE}")
//	Boolean SCHEDULER_ACTIVATE;
//
//	@Override
//	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
//		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
//			logger.debug("Scheduler Not Activated.");
//			return;
//		}
//
//		logger.info("ChangeAgentContractStatusTask execute...");
//
//		try {
//			active();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		try {
//			expire();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	@Override
//	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
//		execute(paramMap);
//	}
//
//
//	private void active() {
//		List<Agent> list = svc.getPendingAgentAll();
//		for (Agent agent : list) {
//			agent.setStatus("active");
//			svc.updateStatus(agent);
//		}
//
//	}
//
//	private void expire() {
//		List<Agent> list = svc.getNeedExpiredAgentAll();
//		for (Agent agent : list) {
//			agent.setStatus("expired");
//			svc.updateStatus(agent);
//		}
//	}
//}
