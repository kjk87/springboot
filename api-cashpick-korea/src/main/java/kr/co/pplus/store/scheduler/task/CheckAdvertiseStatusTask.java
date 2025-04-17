//package kr.co.pplus.store.scheduler.task;
//
//import kr.co.pplus.store.mvc.service.AdvertiseService;
//import kr.co.pplus.store.type.model.Advertise;
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
//public class CheckAdvertiseStatusTask extends AbstractTask {
//	private final static Logger logger = LoggerFactory.getLogger(CheckAdvertiseStatusTask.class);
//
//	@Autowired
//	private AdvertiseService advertiseSvc;
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
//		logger.info("CheckAdvertiseStatusTask execute...");
//
//		startAdvertiseAll();
//
//		finishAdvertiseAll();
//	}
//
//	@Override
//	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
//		execute(paramMap);
//	}
//
//	private void startAdvertiseAll() {
//		advertiseSvc.startAdvertiseAll();
//	}
//
//	private void finishAdvertiseAll() {
//		List<Advertise> adList = advertiseSvc.getNeedCompleteAdvertiseAll();
//		for (Advertise ad : adList) {
//			finishAdvertise(ad);
//		}
//	}
//
//	private void finishAdvertise(Advertise advertise) {
//		// 환불 처리후 상태를 complete으로 변경한다.
//		try {
//			advertiseSvc.complete(advertise);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//}
