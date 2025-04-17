package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.TBL_SUBMIT_QUEUE;
import kr.co.pplus.store.api.jpa.repository.TBL_SUBMIT_QUEUERepository;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class TBL_SUBMIT_QUEUEService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(TBL_SUBMIT_QUEUEService.class);

    @Value("${STORE.SKBB_DBAGENTID}")
    public String SKBB_DBAGENTID = "5567";

    @Value("${STORE.SMS_CERT_SENDER_NUM}")
    public String SMS_CERT_SENDER_NUM = "02-6315-1234";

    @Autowired
    TBL_SUBMIT_QUEUERepository tbl_submit_queueRepository;

    public void insert(TBL_SUBMIT_QUEUE tbl_submit_queue){
        tbl_submit_queueRepository.saveAndFlush(tbl_submit_queue);
    }

    public TBL_SUBMIT_QUEUE generateTBL_SUBMIT_QUEUE(
            String groupKey, String sendNumber, String recvNumber
            , String title, String content, String callbackUrl, String reservedDate
            , String etc1, String etc2, String etc3, String etc4, Integer etc5, Integer etc6
    ) throws UnsupportedEncodingException {
            TBL_SUBMIT_QUEUE queue = new TBL_SUBMIT_QUEUE();
        queue.setCMP_MSG_GROUP_ID(groupKey);
        queue.setETC_CHAR_1(etc1);
        queue.setETC_CHAR_2(etc2);
        queue.setETC_CHAR_3(etc3);
        queue.setETC_CHAR_4(etc4);
        queue.setETC_INT_5(etc5);
        queue.setETC_INT_6(etc6);
        queue.setUSR_ID(SKBB_DBAGENTID);
        if (StringUtils.isEmpty(sendNumber))
            queue.setSND_PHN_ID(SMS_CERT_SENDER_NUM.replaceAll("\\-", ""));
        else
            queue.setSND_PHN_ID(sendNumber);
        queue.setRCV_PHN_ID(recvNumber);

        if (!StringUtils.isEmpty(content)) {
            if (content.trim().getBytes("UTF-8").length > 90)
                queue.setUSED_CD("10");
            else
                queue.setUSED_CD("00");
            queue.setSND_MSG(content.trim());
        }

        if (!StringUtils.isEmpty(callbackUrl)) {
            queue.setCALLBACK_URL(callbackUrl);
            if (queue.getUSED_CD().startsWith("0")) {
                String totalMsg = queue.getSND_MSG() + queue.getCALLBACK_URL();
                if (totalMsg.trim().getBytes("UTF-8").length > 90)
                    queue.setUSED_CD("11");
                else
                    queue.setUSED_CD("01");
            } else if (queue.getUSED_CD().startsWith("1")) {
                queue.setUSED_CD("11");
            }
        }

        if ("10".equals(queue.getUSED_CD()) || "11".equals(queue.getUSED_CD())) {
            queue.setCONTENT_CNT(1);
            queue.setCONTENT_MIME_TYPE("text/plain");
        } else {
            queue.setCONTENT_CNT(0);
        }

        if (StringUtils.isEmpty(title))
            queue.setMSG_TITLE(StringUtils.left(queue.getSND_MSG(), 50));
        else
            queue.setMSG_TITLE(StringUtils.left(title.trim(), 50));

        if (StringUtils.isEmpty(reservedDate)) {
            queue.setRESERVED_FG("I");
            queue.setRESERVED_DTTM(DateUtil.getDateString(DateUtil.PATTERN, DateUtil.getCurrentDate()));
        } else {
            queue.setRESERVED_FG("L");
            queue.setRESERVED_DTTM(reservedDate);
        }
        return queue;
    }
}
