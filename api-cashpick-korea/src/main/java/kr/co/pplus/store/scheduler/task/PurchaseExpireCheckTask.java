package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.api.jpa.service.LuckyBoxService;
import kr.co.pplus.store.api.jpa.service.ProductService;
import kr.co.pplus.store.api.jpa.service.PurchaseService;
import kr.co.pplus.store.api.util.AppUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@DisallowConcurrentExecution
public class PurchaseExpireCheckTask extends AbstractTask {
    private final static Logger logger = LoggerFactory.getLogger(EventLotTask.class);


    @Autowired
    PurchaseService purchaseService;

    @Autowired
    ProductService productService;

    @Autowired
    LuckyBoxService luckyBoxService;

    @Value("${STORE.SCHEDULER_ACTIVATE}")
    Boolean SCHEDULER_ACTIVATE;


    @Override
    public void execute(Map<String, Object> paramMap) throws JobExecutionException {
        if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
            return;
        }

        logger.info("PurchaseExpireCheckTask execute...");
        expiredLuckyBox();

        try {
            purchaseService.updatePurchaseProductExpired();
            productService.setExpire();
        } catch (Exception e) {
            logger.error(e.toString());
        }


    }

    @Override
    public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
        execute(paramMap);
    }

    private void expiredLuckyBox() {
        try {
            luckyBoxService.expiredLuckyBoxPurchaseItem();
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
        }
    }
}
