package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.MsgService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Msg;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * 예약 상태이고, 예약 일시가 경과한 Msg를 읽어와서 메시지 전송을 위해 Queue에 넣는다.
 * @author sykim
 *
 */
@DisallowConcurrentExecution
public class SendReservedMsgTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(SendReservedMsgTask.class);
	
	@Autowired
	MsgService msgSvc;
	
	@Autowired
    MsgProducer producer;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;
	

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("SendReservedMsgTask execute...");

		onQueuingSendReservedMsg();
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	public void onQueuingSendReservedMsg() {
		List<Msg> msgList = msgSvc.getReservedMsgAllForSend();
		for (Msg msg : msgList) {
			msg.setPushCase(Const.USER_PUSH_PAGE);
			msg.setAppType(Const.APP_TYPE_USER);
			producer.push(msg);
		}
	}
}
