package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.api.jpa.service.BuffWalletService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;
/**
 * 메시지 발송 종료 처리를 위한 스케쥴러
 * 종료되지 않은 메시지를 읽어와서 대상 결과가 모두 성공 혹은 실패이면 실패한 갯수만큼을 환불 시키고 해당 메시지를 종료 처리한다.
 * @author sykim
 *
 */
@DisallowConcurrentExecution
public class BuffCoinTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(BuffCoinTask.class);
	
	@Autowired
	BuffWalletService buffWalletService;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("BuffCoinTask execute...");

		buffWalletService.getCoinValue();
	}


	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

}
