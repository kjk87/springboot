package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PrepaymentService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(PrepaymentService.class);

    @Autowired
    PrepaymentRepository prepaymentRepository;

    @Autowired
    PrepaymentPublishRepository prepaymentPublishRepository;

    @Autowired
    PrepaymentDetailRepository prepaymentDetailRepository;

    @Autowired
    PrepaymentPublishDetailRepository prepaymentPublishDetailRepository;

    @Autowired
    PrepaymentLogRepository prepaymentLogRepository;

    @Autowired
    PrepaymentLogDetailRepository prepaymentLogDetailRepository;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    NotificationBoxService notificationBoxService;

    @Autowired
    UserService userService;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    BolService bolService;

    @Autowired
    CashLogRepository cashLogRepository;

    @Autowired
    QueueService queueSvc;

    private String adminStageUrl = "https://stg-www.plusmember.co.kr/";
    private String adminProdUrl = "https://www.plusmember.co.kr/";

    @Value("${STORE.TYPE}")
    private String storeType = "STAGE";

    public Integer getPrepaymentRetentionCount(Long memberSeqNo) {
        return prepaymentPublishRepository.countByMemberSeqNoAndStatus(memberSeqNo, "normal");
    }

    public PrepaymentDetail getPrepayment(Long seqNo) {
        return prepaymentDetailRepository.findBySeqNo(seqNo);
    }

    public Map<String, Object> getPrepaymentCountData(Long prepaymentSeqNo) {

        Integer publishCount = prepaymentPublishRepository.countByPrepaymentSeqNo(prepaymentSeqNo);
        Integer logCount = prepaymentLogRepository.countByPrepaymentSeqNo(prepaymentSeqNo);
        Map<String, Object> map = new HashMap<>();

        map.put("publishCount", publishCount);
        map.put("logCount", logCount);

        return map;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateStatus(Long prepaymentSeqNo, String status) throws ResultCodeException {
        String strDate = AppUtil.localDatetimeNowString();
        Prepayment prepayment = prepaymentRepository.findBySeqNo(prepaymentSeqNo);
        prepayment.setStatus(status);
        prepayment.setStatusDatetime(strDate);
        prepayment.setModiDatetime(strDate);
        prepaymentRepository.save(prepayment);

        return Const.E_SUCCESS;
    }

    public Page<PrepaymentDetail> getPrepaymentListByPageSeqNo(Long pageSeqNo, Pageable pageable) {

        return prepaymentDetailRepository.findAllByPageSeqNoOrderBySeqNoDesc(pageSeqNo, pageable);
    }


    public PrepaymentPublishDetail getPrepaymentPublish(Long seqNo) {
        return prepaymentPublishDetailRepository.findBySeqNo(seqNo);
    }

    public Page<PrepaymentPublishDetail> getPrepaymentPublishByPageSeqNo(Long pageSeqNo, String status, Pageable pageable) {

        List<String> statusList = new ArrayList<>();
        if (AppUtil.isEmpty(status)) {
            statusList.add("normal");
            statusList.add("request");
        } else {
            statusList.add(status);
        }
        return prepaymentPublishDetailRepository.findAllByPageSeqNoAndStatusInOrderBySeqNoDesc(pageSeqNo, statusList, pageable);
    }

    public Page<PrepaymentLogDetail> getPrepaymentLogByPageSeqNo(Long pageSeqNo, String status, Pageable pageable) {

        List<String> statusList = new ArrayList<>();
        if (AppUtil.isEmpty(status)) {
            statusList.add("completed");
            statusList.add("request");
        } else {
            statusList.add(status);
        }
        return prepaymentLogDetailRepository.findAllByPageSeqNoAndStatusInOrderBySeqNoDesc(pageSeqNo, statusList, pageable);
    }


    public List<PrepaymentLog> getPrepaymentLogList(Long prepaymentPublishSeqNo) {
        return prepaymentLogRepository.findAllByPrepaymentPublishSeqNoAndStatus(prepaymentPublishSeqNo, "completed");
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int publish(Long memberSeqNo, Long prepaymentSeqNo) throws ResultCodeException {
        PrepaymentDetail prepayment = prepaymentDetailRepository.findBySeqNo(prepaymentSeqNo);

        PrepaymentPublish prepaymentPublish = new PrepaymentPublish();
        prepaymentPublish.setPrepaymentSeqNo(prepayment.getSeqNo());
        prepaymentPublish.setPageSeqNo(prepayment.getPageSeqNo());
        prepaymentPublish.setMemberSeqNo(memberSeqNo);
        prepaymentPublish.setAgentSeqNo(prepayment.getPage().getAgentSeqNo());
        prepaymentPublish.setStatus("request");

        String dateStr = AppUtil.localDatetimeNowString();
        LocalDateTime nowDate = LocalDateTime.now(ZoneId.systemDefault());
        String expireDate = nowDate.plusYears(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        prepaymentPublish.setExpireDate(expireDate);

        prepaymentPublish.setPrice(prepayment.getPrice());
        prepaymentPublish.setAddPrice(prepayment.getAddPrice());
        prepaymentPublish.setTotalPrice(prepayment.getPrice() + prepayment.getAddPrice());
        prepaymentPublish.setNotice(prepayment.getNotice());
        prepaymentPublish.setRegDatetime(dateStr);
        prepaymentPublish.setHavePrice(prepaymentPublish.getTotalPrice());
        prepaymentPublish.setUsePrice(0f);

        prepaymentPublish.setPageCommissionRatio(prepayment.getPage().getTicketCommission());
        if (prepaymentPublish.getPageCommissionRatio() != null) {
            prepaymentPublish.setPageCommission(prepaymentPublish.getPrice() * (prepaymentPublish.getPageCommissionRatio() / 100));
        }

        Agent agent = prepayment.getPage().getAgent();

        if (agent.getType() == 1) {//총판

            prepaymentPublish.setWholesaleCode(agent.getCode());
            prepaymentPublish.setWholesaleCommissionRatio(agent.getPrepaymentCommission());

            if (prepaymentPublish.getWholesaleCommissionRatio() != null) {
                prepaymentPublish.setWholesaleCommission(prepaymentPublish.getPrice() * (prepaymentPublish.getWholesaleCommissionRatio() / 100));
            }


        } else if (agent.getType() == 2) {//대행사

            Agent wholesaleAgent = agentRepository.findByCode(agent.getParents());

            prepaymentPublish.setDistributorCode(agent.getCode());
            prepaymentPublish.setWholesaleCode(agent.getParents());

            prepaymentPublish.setWholesaleCommissionRatio(wholesaleAgent.getPrepaymentCommission());
            prepaymentPublish.setDistributorCommissionRatio(agent.getPrepaymentCommission());

            if (prepaymentPublish.getWholesaleCommissionRatio() != null) {
                prepaymentPublish.setWholesaleCommission(prepaymentPublish.getPrice() * (prepaymentPublish.getWholesaleCommissionRatio() / 100));

                if (prepaymentPublish.getDistributorCommissionRatio() != null) {
                    prepaymentPublish.setDistributorCommission(prepaymentPublish.getWholesaleCommission() * (prepaymentPublish.getDistributorCommissionRatio() / 100));
                }
            }
        } else {
            String parentsCode = agent.getParents();//에이전트코드

            Agent distributorAgent = getDistAgentCode(parentsCode);
            Agent wholesaleAgent = agentRepository.findByCode(distributorAgent.getParents());

            prepaymentPublish.setDealerCode(agent.getCode());
            prepaymentPublish.setDistributorCode(distributorAgent.getCode());
            prepaymentPublish.setWholesaleCode(distributorAgent.getParents());

            prepaymentPublish.setWholesaleCommissionRatio(wholesaleAgent.getTicketCommission());
            prepaymentPublish.setDistributorCommissionRatio(distributorAgent.getTicketCommission());

            if (prepaymentPublish.getWholesaleCommissionRatio() != null) {
                prepaymentPublish.setWholesaleCommission(prepaymentPublish.getPrice() * (prepaymentPublish.getWholesaleCommissionRatio() / 100));

                if (prepaymentPublish.getDistributorCommissionRatio() != null) {
                    prepaymentPublish.setDistributorCommission(prepaymentPublish.getWholesaleCommission() * (prepaymentPublish.getDistributorCommissionRatio() / 100));

                }
            }

        }

        prepaymentPublish = prepaymentPublishRepository.save(prepaymentPublish);


        try {
            sendPrepaymentPush("publish", prepaymentPublish, null);
        } catch (Exception e) {
            logger.error(e.toString());
        }


        PrepaymentMessage message = new PrepaymentMessage();
        message.setPageSeqNo(prepaymentPublish.getPageSeqNo().toString());
        message.setSeqNo(prepaymentPublish.getSeqNo().toString());
        message.setType("publish");
        adminResult(message);

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int use(Long memberSeqNo, Long prepaymentPublishSeqNo, Integer usePrice) throws ResultCodeException {
        PrepaymentPublish prepaymentPublish = prepaymentPublishRepository.findBySeqNo(prepaymentPublishSeqNo);
        if (usePrice.floatValue() > prepaymentPublish.getHavePrice()) {
            throw new AlreadyLimitException();
        }

        logger.debug("memberSeqNo : " + memberSeqNo + ", " + prepaymentPublish.getMemberSeqNo());
        if (!memberSeqNo.equals(prepaymentPublish.getMemberSeqNo())) {
            throw new NotPermissionException();
        }

        List<PrepaymentLog> prepaymentLogList = prepaymentLogRepository.findAllByPrepaymentPublishSeqNoAndStatus(prepaymentPublishSeqNo, "request");

        if (prepaymentLogList.size() > 0) {
            throw new AlreadyJoinException();
        }

        PrepaymentLog prepaymentLog = new PrepaymentLog();
        prepaymentLog.setMemberSeqNo(memberSeqNo);
        prepaymentLog.setPageSeqNo(prepaymentPublish.getPageSeqNo());
        prepaymentLog.setPrepaymentPublishSeqNo(prepaymentPublishSeqNo);
        prepaymentLog.setPrepaymentSeqNo(prepaymentPublish.getPrepaymentSeqNo());
        prepaymentLog.setStatus("request");
        prepaymentLog.setRegDatetime(AppUtil.localDatetimeNowString());
        prepaymentLog.setUsePrice(usePrice.floatValue());
        prepaymentLog = prepaymentLogRepository.save(prepaymentLog);

        try {
            sendPrepaymentPush("log", prepaymentPublish, prepaymentLog.getSeqNo());
        } catch (Exception e) {
            logger.error(e.toString());
        }

        PrepaymentMessage message = new PrepaymentMessage();
        message.setPageSeqNo(prepaymentLog.getPageSeqNo().toString());
        message.setSeqNo(prepaymentLog.getSeqNo().toString());
        message.setType("log");
        adminResult(message);

        return Const.E_SUCCESS;
    }

    public Agent getDistAgentCode(String parentsCode) {

        Agent agent = agentRepository.findByCode(parentsCode);
        parentsCode = agent.getParents();

        Agent distAgent = null;
        if (agent.getType() == 2) {
            distAgent = agent;
        } else {
            distAgent = agentRepository.findByCode(parentsCode);
        }

        return distAgent;
    }

    private void sendPrepaymentPush(String type, PrepaymentPublish prepaymentPublish, Long logSeqNo) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> data = new HashMap<String, String>();

                if (type.equals("log")) {
                    data.put("title", "오리마켓[금액권사용]");
                    data.put("contents", "금액권 사용요청 있어요");
                    data.put("move_type2", "prepaymentLog");
                    data.put("move_target", String.valueOf(logSeqNo));
                } else {
                    data.put("title", "오리마켓[금액권발행]");
                    data.put("contents", "금액권 발행요청 있어요");
                    data.put("move_type2", "prepaymentPublish");
                    data.put("move_target", String.valueOf(prepaymentPublish.getSeqNo()));
                }

                data.put("move_type1", "inner");
                kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(prepaymentPublish.getPageSeqNo());

                queueSvc.sendPush(page.getMemberSeqNo(), data, Const.APP_TYPE_ORDER);
            }
        });
        thread.start();

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int publishResult(Long pageSeqNo, Long prepaymentPublishSeqNo, String status) throws ResultCodeException {
        PrepaymentPublish prepaymentPublish = prepaymentPublishRepository.findBySeqNo(prepaymentPublishSeqNo);

        if (!pageSeqNo.equals(prepaymentPublish.getPageSeqNo()) || !prepaymentPublish.getStatus().equals("request")) {
            throw new NotPermissionException();
        }

        if (status.equals("normal")) {
            recommenderPoint(prepaymentPublish);
        }

        prepaymentPublish.setStatus(status);
        prepaymentPublish.setCompletedDatetime(AppUtil.localDatetimeNowString());
        prepaymentPublish = prepaymentPublishRepository.save(prepaymentPublish);

        try {
            String type = null;
            if (status.equals("normal")) {
                type = "publish";
            } else {
                type = "publishReject";
            }
            sendPush(prepaymentPublish, type);
        } catch (Exception e) {
            logger.error("sendPush : " + e.toString());
        }

        PrepaymentMessage message = new PrepaymentMessage();
        message.setPageSeqNo(prepaymentPublish.getPageSeqNo().toString());
        message.setSeqNo(prepaymentPublish.getSeqNo().toString());
        message.setType("result");
        adminResult(message);

        return Const.E_SUCCESS;
    }

    private void recommenderPoint(PrepaymentPublish prepaymentPublish) {
        try {
            Member member = memberService.getMemberBySeqNo(prepaymentPublish.getMemberSeqNo());
            if (!AppUtil.isEmpty(member.getRecommendationCode())) {
                Member recommendMember = memberService.getMemberByRecommendKey(member.getRecommendationCode());

                if (recommendMember != null) {
                    Long point = (long)(prepaymentPublish.getPrice() * 0.0005f);

                    prepaymentPublish.setRecommendedMemberType(recommendMember.getAppType());
                    prepaymentPublish.setRecommendedMemberSeqNo(recommendMember.getSeqNo());
                    prepaymentPublish.setRecommendedMemberPoint(point.floatValue());

                    if (recommendMember.getAppType().equals(Const.APP_TYPE_BIZ)) {
                        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findFirstByMemberSeqNo(recommendMember.getSeqNo());

                        CashLog cashLog = new CashLog();
                        cashLog.setMemberSeqNo(recommendMember.getSeqNo());
                        cashLog.setPageSeqNo(page.getSeqNo());
                        cashLog.setCash(point.intValue());
                        cashLog.setType("charge");
                        cashLog.setNote("선불금액권발행 추천인 수익금");
                        cashLog.setGaveMemberSeqNo(prepaymentPublish.getMemberSeqNo());
                        cashLog.setGaveType("prepayment");
                        cashLog.setGaveSeqNo(prepaymentPublish.getSeqNo());


                        cashLogRepository.save(cashLog);
                    }else{
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(point.floatValue());
                        bolHistory.setMemberSeqNo(recommendMember.getSeqNo());
                        bolHistory.setSubject("선불금액권발행 추천인 수익금");

                        bolHistory.setPrimaryType("increase");
                        bolHistory.setSecondaryType("prepayment");
                        bolHistory.setTargetType("member");
                        bolHistory.setTargetSeqNo(recommendMember.getSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");
                        bolHistory.getHistoryProp().put("적립유형", "내가 추천한회원이 금액권 발행");

                        bolService.increaseBol(recommendMember.getSeqNo(), bolHistory);
                    }

                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int useResult(Long pageSeqNo, Long prepaymentLogSeqNo, String status) throws ResultCodeException {
        PrepaymentLog prepaymentLog = prepaymentLogRepository.findBySeqNo(prepaymentLogSeqNo);


        if (!pageSeqNo.equals(prepaymentLog.getPageSeqNo()) || !prepaymentLog.getStatus().equals("request")) {
            throw new NotPermissionException();
        }

        PrepaymentPublish prepaymentPublish = prepaymentPublishRepository.findBySeqNo(prepaymentLog.getPrepaymentPublishSeqNo());

        if (prepaymentPublish.getStatus().equals("expired")) {
            throw new ExpiredException();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expireDate = null;
        try {
            expireDate = sdf.parse(prepaymentPublish.getExpireDate());
        } catch (ParseException e) {
            logger.error(e.toString());
        }

        if (System.currentTimeMillis() > expireDate.getTime()) {
            throw new ExpiredException();
        }


        if (status.equals("completed")) {

            if (prepaymentPublish.getHavePrice() < prepaymentLog.getUsePrice()) {
                throw new AlreadyLimitException();
            }

            prepaymentPublish.setUsePrice(prepaymentPublish.getUsePrice() + prepaymentLog.getUsePrice());
            prepaymentPublish.setHavePrice(prepaymentPublish.getHavePrice() - prepaymentLog.getUsePrice());
            if (prepaymentPublish.getHavePrice() == 0L) {
                prepaymentPublish.setStatus("completed");
            }

            prepaymentPublish = prepaymentPublishRepository.saveAndFlush(prepaymentPublish);
        }

        prepaymentLog.setStatus(status);
        prepaymentLog.setStatusDatetime(AppUtil.localDatetimeNowString());
        prepaymentLog = prepaymentLogRepository.save(prepaymentLog);

        try {
            String type = null;
            if (status.equals("completed")) {
                type = "use";
            } else {
                type = "useReject";
            }
            sendPush(prepaymentPublish, type);
        } catch (Exception e) {
            logger.error("sendPush : " + e.toString());
        }

        PrepaymentMessage message = new PrepaymentMessage();
        message.setPageSeqNo(prepaymentLog.getPageSeqNo().toString());
        message.setSeqNo(prepaymentLog.getSeqNo().toString());
        message.setType("result");
        adminResult(message);

        return Const.E_SUCCESS;
    }

    private void sendPush(PrepaymentPublish prepaymentPublish, String type) {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (prepaymentPublish.getMemberSeqNo() != null) {

                        Map<String, String> data = new HashMap<String, String>();
                        data.put("move_type1", "inner");
                        data.put("move_target", String.valueOf(prepaymentPublish.getSeqNo()));

                        NotificationBox notificationBox = new NotificationBox();
                        notificationBox.setMemberSeqNo(prepaymentPublish.getMemberSeqNo());
                        switch (type) {
                            case "publish":
                                notificationBox.setSubject("오리마켓[금액권발급]");
                                notificationBox.setContents("금액권이 발급되었습니다.");
                                notificationBox.setMoveType2("prepaymentPublish");

                                data.put("title", "오리마켓[금액권발급]");
                                data.put("contents", "금액권이 발급되었습니다.");
                                data.put("move_type2", "prepaymentPublish");
                                break;
                            case "use":
                                notificationBox.setSubject("오리마켓[금액권사용]");
                                notificationBox.setContents("금액권을 사용하였습니다.");
                                notificationBox.setMoveType2("prepaymentPublish");

                                data.put("title", "오리마켓[금액권사용]");
                                data.put("contents", "금액권을 사용하였습니다.");
                                data.put("move_type2", "prepaymentPublish");
                                break;
                            case "publishReject":
                                notificationBox.setSubject("오리마켓[발급거절]");
                                notificationBox.setContents("금액권발급이 거절되었습니다.");
                                notificationBox.setMoveType2("reject");

                                data.put("title", "오리마켓[발급거절]");
                                data.put("contents", "금액권발급이 거절되었습니다.");
                                data.put("move_type2", "reject");

                                break;
                            case "useReject":
                                notificationBox.setSubject("오리마켓[사용거절]");
                                notificationBox.setContents("금액권사용이 거절되었습니다.");
                                notificationBox.setMoveType2("reject");

                                data.put("title", "오리마켓[사용거절]");
                                data.put("contents", "금액권사용이 거절되었습니다.");
                                data.put("move_type2", "reject");
                                break;
                        }

                        notificationBox.setMoveSeqNo(prepaymentPublish.getSeqNo());
                        notificationBox.setMoveType1("inner");

//			notificationBoxService.save(notificationBox);
                        queueSvc.sendPush(prepaymentPublish.getMemberSeqNo(), data, Const.APP_TYPE_USER);

                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                }

            }
        });
        thread.start();
        ;


    }

    public void adminResult(PrepaymentMessage message) {
        try {
            String url = "";
            if (storeType.equals("PROD")) {
                url = adminProdUrl + "cs/requestPrepayment";
            } else {
                url = adminStageUrl + "cs/requestPrepayment";
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

}
