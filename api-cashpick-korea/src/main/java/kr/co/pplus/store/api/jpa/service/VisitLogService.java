package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.PageRepository;
import kr.co.pplus.store.api.jpa.repository.VisitLogDetailRepository;
import kr.co.pplus.store.api.jpa.repository.VisitLogRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.AlreadyJoinException;
import kr.co.pplus.store.exception.AlreadyLimitException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class VisitLogService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(VisitLogService.class);

    @Autowired
    private VisitLogRepository visitLogRepository;

    @Autowired
    private VisitLogDetailRepository visitLogDetailRepository;

    @Autowired
    PageJpaService pageJpaService;

    @Autowired
    UserService userService;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    QueueService queueService;

    private String adminStageUrl = "https://stg-www.plusmember.co.kr/";
    private String adminProdUrl = "https://www.plusmember.co.kr/";

    @Value("${STORE.TYPE}")
    private String storeType = "STAGE";

    public VisitLog getVisitLog(Long pageSeqNo, Long memberSeqNo) {
        return visitLogRepository.findFirstByPageSeqNoAndMemberSeqNoAndStatus(pageSeqNo, memberSeqNo, "completed");
    }

    public Page<VisitLogDetail> getVisitLogListByPageSeqNo(Long pageSeqNo, String status, Pageable pageable) {
        List<String> statusList = new ArrayList<>();
        if (AppUtil.isEmpty(status)) {
            statusList.add("completed");
            statusList.add("request");
        } else {
            statusList.add(status);
        }

        return visitLogDetailRepository.findAllByPageSeqNoAndStatusInOrderBySeqNoDesc(pageSeqNo, statusList, pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int receive(Long pageSeqNo, Long memberSeqNo) throws ResultCodeException {

        PageWithAvgEval page = pageJpaService.getPageBySeqNo(null, pageSeqNo, null, null);

        VisitLog visitLog = getVisitLog(pageSeqNo, memberSeqNo);
        if (visitLog != null) {
            throw new AlreadyLimitException();
        }

        List<VisitLog> list = visitLogRepository.findAllByPageSeqNoAndMemberSeqNoAndStatus(pageSeqNo, memberSeqNo, "request");

        if (list.size() > 0) {
            throw new AlreadyJoinException();
        }

        visitLog = new VisitLog();
        visitLog.setPageSeqNo(pageSeqNo);
        visitLog.setMemberSeqNo(memberSeqNo);
        visitLog.setNote(page.getVisitBenefit());
        visitLog.setStatus("request");
        visitLog.setRegDatetime(AppUtil.localDatetimeNowString());
        visitLog.setType(page.getVisitBenefitType());
        visitLog = visitLogRepository.saveAndFlush(visitLog);

        try {
            sendVisitBenefitUsePush(visitLog);
        } catch (Exception e) {

        }

        VisitMessage message = new VisitMessage();
        message.setPageSeqNo(pageSeqNo.toString());
        message.setSeqNo(visitLog.getSeqNo().toString());
        message.setType("request");
        adminResult(message);


        return Const.E_SUCCESS;
    }

    private void sendVisitBenefitUsePush(VisitLog visitLog) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "오리마켓[첫방문혜택 사용]");
                    data.put("contents", "첫방문혜택 사용요청 있어요");
                    data.put("move_type1", "inner");
                    data.put("move_type2", "visitBenefit");
                    data.put("move_target", String.valueOf(visitLog.getSeqNo()));

                    kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(visitLog.getPageSeqNo());

                    queueService.sendPush(page.getMemberSeqNo(), data, Const.APP_TYPE_ORDER);

                } catch (Exception e) {
                    logger.debug("sendPush : " + e.toString());
                }
            }
        });
        thread.start();

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int receiveResult(Long visitLogSeqNo, Long pageSeqNo, String status) throws ResultCodeException {

        VisitLog visitLog = visitLogRepository.findBySeqNo(visitLogSeqNo);

        if (!pageSeqNo.equals(visitLog.getPageSeqNo()) || !visitLog.getStatus().equals("request")) {
            throw new NotPermissionException();
        }

        visitLog.setStatus(status);
        visitLog.setStatusDatetime(AppUtil.localDatetimeNowString());
        visitLog = visitLogRepository.saveAndFlush(visitLog);

        try {
            sendPush(visitLog, status);
        } catch (Exception e) {
            logger.error("sendPush : " + e.toString());
        }

        VisitMessage message = new VisitMessage();
        message.setPageSeqNo(pageSeqNo.toString());
        message.setSeqNo(visitLog.getSeqNo().toString());
        message.setType("result");
        adminResult(message);


        return Const.E_SUCCESS;
    }

    public void adminResult(VisitMessage message) {
        try {
            String url = "";
            if (storeType.equals("PROD")) {
                url = adminProdUrl + "cs/requestVisit";
            } else {
                url = adminStageUrl + "cs/requestVisit";
            }

            if (StringUtils.isNotEmpty(url)) {
                logger.debug(url);

                CloseableHttpClient client = HttpClients.createDefault();

                HttpPost post = AppUtil.getPostFormData(url, message);
                HttpResponse res = client.execute(post);
//				client.execute(post) ;

                logger.debug("adminResult : " + res.getEntity().toString());
                client.close();
            }
        } catch (Exception e) {
            logger.debug("adminResult : " + e.toString());
        }

    }

    private void sendPush(VisitLog visitLog, String type) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (visitLog.getMemberSeqNo() != null) {

                        Map<String, String> data = new HashMap<String, String>();
                        data.put("move_type1", "inner");
                        data.put("move_target", String.valueOf(visitLog.getSeqNo()));

                        NotificationBox notificationBox = new NotificationBox();
                        notificationBox.setMemberSeqNo(visitLog.getMemberSeqNo());
                        switch (type) {
                            case "completed":
                                notificationBox.setSubject("오리마켓[첫방문혜택]");
                                notificationBox.setContents("첫방문혜택을 받으셨습니다.");
                                notificationBox.setMoveType2("visitBenefit");

                                data.put("title", "오리마켓[첫방문혜택]");
                                data.put("contents", "첫방문혜택을 받으셨습니다.");
                                data.put("move_type2", "visitBenefit");
                                break;
                            case "reject":
                                notificationBox.setSubject("오리마켓[첫방문혜택 거절]");
                                notificationBox.setContents("첫방문혜택이 거절되었습니다.");
                                notificationBox.setMoveType2("reject");

                                data.put("title", "오리마켓[첫방문혜택 거절]");
                                data.put("contents", "첫방문혜택이 거절되었습니다.");
                                data.put("move_type2", "reject");
                                break;
                        }

                        notificationBox.setMoveSeqNo(visitLog.getSeqNo());
                        notificationBox.setMoveType1("inner");

//			notificationBoxService.save(notificationBox);

                        queueService.sendPush(visitLog.getMemberSeqNo(), data, Const.APP_TYPE_USER);
                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        });
        thread.start();

    }
}
