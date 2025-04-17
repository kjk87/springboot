package kr.co.pplus.store.mvc.service;

import com.google.gson.JsonObject;
import kr.co.pplus.store.api.jpa.model.GiftStatus;
import kr.co.pplus.store.api.jpa.model.Giftishow;
import kr.co.pplus.store.api.jpa.model.Lotto;
import kr.co.pplus.store.api.jpa.service.CategoryService;
import kr.co.pplus.store.api.jpa.service.GiftishowService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.Filtering;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EventService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(EventService.class);

//	@Autowired
//	EventDao dao;

    @Autowired
    AuthService authSvc;

    @Autowired
    PageService pageSvc;

    @Autowired
    CashBolService cashBolSvc;

    @Autowired
    CommonService commonSvc;

    @Autowired
    UserService userSvc;

    @Autowired
    ArticleService articleSvc;

    @Autowired
    AttachmentService attachSvc;

    @Autowired
    MsgService msgSvc;

    @Autowired
    QueueService queueSvc;

    @Autowired
    PlusService plusSvc;

    @Autowired
    CategoryService categoryService;

    @Autowired
    GiftishowService giftishowService;

    @Autowired
    BuffService buffService;

    @Autowired
    MsgProducer producer;

    @Value("${STORE.TYPE}")
    String storeType;

    public List<String> getRandomVirtualNumber() {
        return sqlSession.selectList("Event.getRandomVirtualNumber");
    }

    public Event setEventValues(Event event, String primaryType) throws Exception {
        //기본값 세팅
        event.setPrimaryType(primaryType);
        event.setMoveMethod("none");


        if (event.getWinSelectType() == null) {
            event.setWinSelectType("none");
        }


        event.setWinPushType("none");

        event.setSecondaryType("duration");
        event.setWinAnnounceType("special");
        event.setJoinType("event");
        event.setJoinLimitCount(1);
        event.setReward(0f);
        event.setGift(true);
        event.setTargetType("all");
        event.setMoveType("external");
        event.setMoveMethod("none");
        event.setWinSelectType("none");
        event.setRewardType("none");

        event.setMan(true);
        event.setWoman(true);
        event.setAge10(true);
        event.setAge20(true);
        event.setAge30(true);
        event.setAge40(true);
        event.setAge50(true);
        event.setAge60(true);
        event.setMarried(true);
        event.setNotMarried(true);
        event.setHasChild(true);
        event.setNoChild(true);
        event.setAllAddress(true);


        event.setPriority(1);

        //이동형일때 존재하지 않음

        event.setMoveType("none");


        if (StringUtils.isEmpty(event.getRewardType())) {
            event.setRewardType("none");
        }

        if (event.getReward() == null) {
            event.setReward(0f);
        }

        if (event.getMinJoinCount() == null) {
            event.setMinJoinCount(0);
        }

        if (event.getMaxJoinCount() == null) {
            event.setMaxJoinCount(0);
        }


        //이벤트 상세 내용
        String regexp = "<p><br></p>";
        if (StringUtils.isEmpty(event.getContents()) == false && regexp.equals(event.getContents().trim())) {
            event.setContents(null);
        } else if ("".equals(event.getContents())) {
            event.setContents(null);
        }

        if (StringUtils.isEmpty(event.getWinnerDesc()) == false && regexp.equals(event.getWinnerDesc().trim())) {
            event.setWinnerDesc(null);
        } else if ("".equals(event.getWinnerDesc())) {
            event.setWinnerDesc(null);
        }

        //내용이 없으면
        if (event.getContents() == null) {
//			event.setContentsType(null);
        }

        return event;
    }

    public String getMaxCode() {

        String newCode = "";
        String code = sqlSession.selectOne("Event.getMaxCode");
        if (code == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("1");

            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sb.append(sdf.format(dt).toString());

            sb.append("050001");

            newCode = sb.toString();
        } else {

            String lastCode = code.substring(11);
            Integer lastNum = Integer.parseInt(lastCode);

            if (lastNum < 9999) {
                Integer newLastNum = lastNum + 1;
                String newLastCode = String.format("%04d", newLastNum);
                newCode = code.substring(0, 11) + newLastCode;
            } else if (lastNum == 9999) {
                String firstCode = code.substring(0, 1);
                Integer firstNum = Integer.parseInt(firstCode) + 1;

                newCode = String.valueOf(firstNum) + code.substring(1, 11) + "0001";

            }

        }
        return newCode;

    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Map<String, Object> serializableInstall(User user, Event event, EventJoin join, long seq_no) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        Event saved = get(event);
        ParamMap map = new ParamMap();
        map.put("result", false);

        ParamMap requestParam = new ParamMap();
        user = userSvc.getUser(seq_no);
        requestParam.put("user", user);
        requestParam.put("event", event);
        List<EventJoin> joinList = sqlSession.selectList("Event.getEventJoinAllByUser", requestParam);

        if (!"lotto".equals(event.getPrimaryType()) && !"lottoPlaybol".equals(event.getPrimaryType())) {
            if (joinList.size() < 1) {
                throw new Exception("Not Join!!!");
            }
            result.put("install", installEvent(user, saved, join));
            map.put("result", true);
        } else {
            throw new Exception("Not Event Type!!!");
        }

        return result;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventJoin installEvent(User user, Event event, EventJoin join) throws ResultCodeException {
        join.setEvent(event);
        join.setUser(user);
        sqlSession.insert("Event.insertInstall", join);
        return join;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Map<String, Object> serializableJoin(User user, Event event, EventJoin join) throws Exception {
        return join(user, event, join);
    }

    @Transactional(transactionManager = "transactionManager")
    public boolean checkJoinEnable(User user, Event event) throws Exception {
        Event saved = get(event);
        return checkJoinPossible(user, saved);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Map<String, Object> cpaJoin(Long memberSeqNo, Long eventSeqNo) throws Exception {
        User user = userSvc.getUser(memberSeqNo);
        Event event = new Event();
        event.setNo(eventSeqNo);
        Event saved = get(event);

        checkStatus(saved);

        checkDisplayDuration(saved);

        checkTimeDuration(saved);

        //true 응모할수없는 경우 예외처리로 빠진다. 이벤트 티켓하고 상관없이 진행될경우
        //false 이미 이벤트에 참여는 했고, 이벤트 티켓이 있는경우
        boolean bJoinPossible = checkJoinPossible(user, saved);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("join", joinEvent(user, saved, null, bJoinPossible));

        if (saved.getIsPlus() && saved.getPageSeqNo() != null) {
            Plus plus = new Plus();
            plus.setPageNo(saved.getPageSeqNo());
            plus.setBlock(false);

            if (saved.getAgreement2() == 1 || saved.getAgreement2() == 2) {
                plus.setAgreement(true);
            } else {
                plus.setAgreement(false);
            }


            Plus savedPlus = plusSvc.getOnlyPlus(user, plus);

            if (savedPlus == null) {
                plusSvc.insert(user, plus);
            } else {
                if (!savedPlus.getAgreement() && plus.getAgreement()) {
                    savedPlus.setAgreement(true);
                    plusSvc.updateAgreement(savedPlus);
                }
            }
        }
        return result;
    }

    public Integer getEventJoinCount(User user, Long eventSeqNo) throws Exception {

        Event event = new Event();
        event.setNo(eventSeqNo);

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);

        Integer joinCount = sqlSession.selectOne("Event.getEventJoinCountByUser", map);
        logger.debug("joinCount : " + joinCount);
        return joinCount;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, Object> join(User user, Event event, EventJoin join) throws Exception {

        Event saved = get(event);
        if (saved.getIsDb() != null && saved.getIsDb() && join.getProperties() == null) {
            throw new InvalidArgumentException();
        }

        if (saved.getPrimaryType().equals("lotto")) {

            Lotto lotto = sqlSession.selectOne("Event.getLotto");
            if (user.getTotalBol() < lotto.getJoinLuckybol()) {
                throw new LackCostException("You don't have any lotto ticket or enoguh luckybols !!!");
            }
        }

//        if (saved.getPrimaryType().equals("lottoPlaybol")) {
//            if (user.getLottoTicketCount() < 1 && user.getTotalBol() < (-saved.getReward())) {
//                throw new LackCostException("You don't have any lotto ticket or enoguh luckybols !!!");
//            }
//        }

        if (saved.getPrimaryType().equals("number")) {
            if (join.getWinCode() == null || join.getWinCode().replaceAll("[0-9].*", "").length() > 0)
                throw new Exception("당첨번호가 입력되지 않았거나 형식이 틀립니다.");
        } else if (saved.getPrimaryType().equals("lotto") || saved.getPrimaryType().equals("lottoPlaybol")) {
//            if (join.getWinCode() == null || join.getWinCode().replaceAll("([0-9]{1,2},){5}[0-9]{1,2}", "").length() > 0)
            if (join.getWinCode() == null)
                throw new Exception("로또 당첨번호가 입력되지 않았거나 형식이 틀립니다.");
        }

        checkStatus(saved);

        checkDisplayDuration(saved);

        checkTimeDuration(saved);

        //true 응모할수없는 경우 예외처리로 빠진다. 이벤트 티켓하고 상관없이 진행될경우
        //false 이미 이벤트에 참여는 했고, 이벤트 티켓이 있는경우
        boolean bJoinPossible = checkJoinPossible(user, saved);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("join", joinEvent(user, saved, join, bJoinPossible));

        if (saved.getMaxJoinCount() != null && saved.getMaxJoinCount() > 0 && saved.getMaxJoinCount() <= saved.getJoinCount() && "limit".equals(saved.getWinAnnounceType())) {
            sqlSession.update("Event.setEndDateNowAndWinAnnounceDateNow", saved);

        } else if ("immediately".equals(saved.getWinAnnounceType())) {
            EventWin win = lot(user, saved);
            if (win != null) {
                result.put("win", win);
            }
        }

        if (saved.getIsPlus() && saved.getPageSeqNo() != null) {
            Plus plus = new Plus();
            plus.setPageNo(saved.getPageSeqNo());
            plus.setBlock(false);

            if (saved.getAgreement2() == 1 || saved.getAgreement2() == 2) {
                plus.setAgreement(true);
            } else {
                plus.setAgreement(false);
            }


            Plus savedPlus = plusSvc.getOnlyPlus(user, plus);

            if (savedPlus == null) {
                plusSvc.insert(user, plus);
            } else {
                if (plus.getAgreement() && (savedPlus.getAgreement() == null || !savedPlus.getAgreement())) {
                    savedPlus.setAgreement(true);
                    plusSvc.updateAgreement(savedPlus);
                }
            }
        }
        return result;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, Object> increaseBannerPageUserView(User user, Event event, EventBanner banner, Map result) throws ResultCodeException {
        if ("move".equals(event.getPrimaryType())
                && banner != null
                && banner.getBannerNo() != null) {
            //이동현인 경우에는 배너 클릭 시에 참여가 된다.
            if (result == null)
                result = new HashMap<String, Object>();
            banner.setEvent(event);
            result.put("pageView", increaseBannerPageView(user, banner));
            result.put("userView", increaseBannerUserView(user, banner, banner.getGiveReward()));
        }
        return result;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventBannerPageView increaseBannerPageView(User user, EventBanner banner) throws ResultCodeException {
        EventBannerPageView view = new EventBannerPageView();
        view.setBanner(banner);
        view.setUser(user);
        sqlSession.insert("Event.insertBannerPageView", view);
        sqlSession.update("Event.increaseBannerPageView", banner);
        sqlSession.update("Event.increasePageView", banner.getEvent());
        return view;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventBannerUserView increaseBannerUserView(User user, EventBanner banner, Boolean giveReward) throws ResultCodeException {
        ParamMap map = new ParamMap();
        int exists = sqlSession.selectOne("Event.existsBannerUserView", map);
        if (exists == 0) {
            EventBannerUserView view = new EventBannerUserView();
            view.setBanner(banner);
            view.setUser(user);
            view.setGiveReward(giveReward);
            sqlSession.insert("Event.insertBannerUserView", view);
            sqlSession.update("Event.increaseBannerUserView", banner);
            sqlSession.update("Event.increaseUserView", banner.getEvent());
            return view;
        }
        return null;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventJoin joinEvent(User user, Event event, EventJoin join, boolean bJoinPossible) throws ResultCodeException {
        Float bol = event.getReward();
        Float earnedPoint = event.getEarnedPoint();
        join.setEvent(event);
        join.setUser(user);
        join.setJoinDate(DateUtil.getCurrentDate());
//        Integer joinNo = sqlSession.selectOne("Event.getEventJoinMaxSeqNo", event);
        join.setJoinNo(1);

        logger.debug("join >>>>>>>>>>>>>>>>>>>>>>>: " + join.toString());

        sqlSession.insert("Event.insertJoin", join);
        sqlSession.update("Event.increaseJoinCount", event);
        join.setJoinDate(null);
        logger.error(user.toString());
        boolean useEventTicket = false;
        if (user.getEventTicketCount() > 0 && bJoinPossible == false) {
            useEventTicket = true;
            sqlSession.update("Event.decreaseEventTicketCount", user);
            BolHistory bh = new BolHistory();
            bh.setUser(user);
            bh.setPrimaryType("decrease");
            bh.setTarget(event);
            bh.setTargetType("event");
            bh.setSubject("이벤트 참여");
            bh.setProperties(new HashMap<String, Object>());
            bh.setSecondaryType("joinReduceEvent");
            bh.setAmount(1f);
            bh.setIsEventTicket(true);
            bh.getProperties().put("소진 유형[이벤트 티켓]", event.getTitle() + " : " + event.getPrimaryType() + " 참여");
            sqlSession.insert("CashBol.insertBolHistory", bh);
        }

		/*if (event.getMinJoinCount() > 0 && event.getMinJoinCount() > event.getJoinCount() && event.getReward() < 0) {
			dao.decreaseMinJoinCount(event);
		}*/

        if (bol != 0) {
            BolHistory bh = new BolHistory();
            bh.setTarget(event);
            bh.setTargetType("event");
            bh.setSubject("이벤트 참여");
            bh.setProperties(new HashMap<String, Object>());
            if (bol > 0) {
                bh.setSecondaryType("joinEvent");
                bh.setAmount(bol);
                bh.getProperties().put("적립 유형", event.getTitle() + " 참여");
                if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                    bh.getProperties().put("지급처", "캐시픽 운영팀");
                } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                    bh.getProperties().put("지급처", "럭키픽 운영팀");
                } else {
                    bh.getProperties().put("지급처", "오리마켓 운영팀");
                }

                cashBolSvc.increaseBol(user, bh);
            } else {
                if (useEventTicket == false) {
                    bh.setSecondaryType("joinReduceEvent");
                    bh.setAmount(-1 * bol);
                    bh.getProperties().put("소진 유형", event.getTitle() + " 참여");
                    cashBolSvc.decreaseBol(user, bh);
                }
            }
            user.setTotalBol(user.getTotalBol() + bol);
        }

        if (event.getCampaignSeqNo() != null) {

            ParamMap map = new ParamMap();
            map.put("user", user);
            map.put("event", event);
            EventCampaignJoin eventCampaignJoin = sqlSession.selectOne("Event.getEventCampaignJoin", map);
            if (eventCampaignJoin != null) {
                sqlSession.update("Event.increaseEventCampaignJoinCount", eventCampaignJoin);
            } else {
                EventCampaignJoin params = new EventCampaignJoin();
                params.setEventCampaignSeqNo(event.getCampaignSeqNo());
                params.setJoinCount(1);
                params.setMemberSeqNo(user.getNo());
                sqlSession.insert("Event.insertEventCampaignJoin", params);
            }


        }

        if (earnedPoint != null && earnedPoint > 0) {

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(user.getNo());
            pointHistory.setType("charge");
            pointHistory.setPoint(earnedPoint);
            pointHistory.setSubject("이벤트 참여");
            pointHistory.setHistoryProp(new HashMap<String, Object>());
            pointHistory.getHistoryProp().put("적립 유형", event.getTitle() + " 참여");
            if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
            } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                pointHistory.getHistoryProp().put("지급처", "럭키픽 운영팀");
            } else {
                pointHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");
            }
            cashBolSvc.increasePointHistory(pointHistory);
        }

        joinLotto(user.getNo(), 1, "eventJoin");


        Integer joinCount = sqlSession.selectOne("Event.getJoinCountFromEvent", event);

        event.setJoinCount(joinCount);
        return join;
    }

    public void joinLotto(Long memberSeqNo, int count, String joinType) {

        Long lotterySeqNo = sqlSession.selectOne("Event.getLotterySeqNo");

        List<ParamMap> lottoJoinList = new ArrayList<>();
        ParamMap lottoJoinMap;

        List<Integer> range = IntStream.range(1, 46).boxed().collect(Collectors.toList());//1~45
        List<Integer> noList;

        for (int i = 0; i < count; i++) {
            Collections.shuffle(range);
            noList = range.subList(0, 6);
            Collections.sort(noList);
            lottoJoinMap = new ParamMap();
            lottoJoinMap.put("seqNo", StoreUtil.getLottoID("") + i);
            lottoJoinMap.put("memberSeqNo", memberSeqNo);
            lottoJoinMap.put("lotterySeqNo", lotterySeqNo);
            lottoJoinMap.put("joinType", joinType);
            lottoJoinMap.put("no1", noList.get(0));
            lottoJoinMap.put("no2", noList.get(1));
            lottoJoinMap.put("no3", noList.get(2));
            lottoJoinMap.put("no4", noList.get(3));
            lottoJoinMap.put("no5", noList.get(4));
            lottoJoinMap.put("no6", noList.get(5));
            lottoJoinList.add(lottoJoinMap);
        }

        ParamMap lottoMap = new ParamMap();
        lottoMap.put("lottoJoinList", lottoJoinList);

        Random random = new Random();
        int no = random.nextInt(28) + 1;

        lottoMap.put("tableName", "lottery_join" + no);
        sqlSession.insert("Event.joinLottery", lottoMap);

    }

    private List<ParamMap> generateLottJoinList(Long memberSeqNo, int count, String joinType, int lottoIdx) {


        List<ParamMap> lottoJoinList = new ArrayList<>();
        ParamMap lottoJoinMap;

        List<Integer> range = IntStream.range(1, 46).boxed().collect(Collectors.toList());//1~45
        List<Integer> noList;

        for (int i = 0; i < count; i++) {
            Collections.shuffle(range);
            noList = range.subList(0, 6);
            Collections.sort(noList);
            lottoJoinMap = new ParamMap();
            lottoJoinMap.put("seqNo", StoreUtil.getLottoID("") + lottoIdx);
            lottoJoinMap.put("memberSeqNo", memberSeqNo);
            lottoJoinMap.put("joinType", joinType);
            lottoJoinMap.put("no1", noList.get(0));
            lottoJoinMap.put("no2", noList.get(1));
            lottoJoinMap.put("no3", noList.get(2));
            lottoJoinMap.put("no4", noList.get(3));
            lottoJoinMap.put("no5", noList.get(4));
            lottoJoinMap.put("no6", noList.get(5));
            lottoJoinList.add(lottoJoinMap);
            lottoIdx++;
        }

        return lottoJoinList;
    }


    private void checkStatus(Event event) throws ResultCodeException {
        if ("active".equals(event.getStatus())) {
            return;
        }
        throw new NotPossibleValueException("status", "not possible join. status=" + event.getStatus());
    }

    private void checkDisplayDuration(Event event) throws ResultCodeException {
        Date now = DateUtil.getCurrentDate();
        if (event.getDisplayDuration().getStartDate().getTime() > now.getTime() || event.getDisplayDuration().getEndDate().getTime() < now.getTime())
            throw new NotPossibleTimeException("display", "not possible time.");

        return;
    }

    public boolean checkJoinPossible(User user, Event event) throws ResultCodeException {

        if (!"none".equals(event.getRewardType()) && event.getReward() < 0 && user.getTotalBol() < (-1 * event.getReward()))
            throw new NotEnoughBolException("reward", event.getReward());

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);


        if (event.getCampaignSeqNo() != null) {

            EventCampaign eventCampaign = sqlSession.selectOne("Event.getEventCampaign", map);
            if (eventCampaign.getJoinLimit().equals("campaign")) {
                EventCampaignJoin eventCampaignJoin = sqlSession.selectOne("Event.getEventCampaignJoin", map);
                if (eventCampaignJoin != null && eventCampaignJoin.getJoinCount() >= eventCampaign.getJoinCount()) {
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("joinType", eventCampaign.getJoinLimit());
                    result.put("joinLimitCount", event.getJoinLimitCount());
                    throw new AlreadyExistsException("row", result);
                }
            }
        }


        if (event.getMaxJoinCount() != null && event.getMaxJoinCount() > 0 && event.getMaxJoinCount() <= event.getJoinCount())
            throw new AlreadyLimitException("max-join-count", "limited");

        Date now = DateUtil.getCurrentDate();

        if (event.getPrimaryType().equals("goodluck") && event.getReward() >= 0) {

            Date sameEventJoinDate = sqlSession.selectOne("Event.getLastJoinSameEvent", map);

            if (ObjectUtils.isNotEmpty(sameEventJoinDate)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sameEventJoinDate);

                if (storeType.equals("PROD")) {
                    cal.add(Calendar.SECOND, 600);
                } else {
                    cal.add(Calendar.SECOND, 10);
                }


                Date joinableDate = cal.getTime();

                if (joinableDate.getTime() > now.getTime()) {
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("joinDate", sameEventJoinDate);

                    if (storeType.equals("PROD")) {
                        result.put("joinTerm", 600);
                    } else {
                        result.put("joinTerm", 10);
                    }

                    Long remainSecond = (joinableDate.getTime() - now.getTime()) / 1000;
                    result.put("remainSecond", remainSecond);
                    throw new NotPossibleTimeException("row", result);
                }
            }

            Date joinDate = sqlSession.selectOne("Event.getEventLastJoin", map);

            if (ObjectUtils.isNotEmpty(joinDate)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(joinDate);
                cal.add(Calendar.SECOND, 30);

                Date joinableDate = cal.getTime();

                if (joinableDate.getTime() > now.getTime()) {
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("joinDate", joinDate);
                    result.put("joinTerm", 30);
                    Long remainSecond = (joinableDate.getTime() - now.getTime()) / 1000;
                    result.put("remainSecond", remainSecond);
                    throw new NotPossibleTimeException("row", result);
                }
            }
        }

        if ("always".equals(event.getJoinType())) {
            return true;
        }

        List<EventJoin> joinList = sqlSession.selectList("Event.getEventJoinAllByUser", map);

			/*if (event.getJoinLimitCount() > 0 && joinList.size() >= event.getJoinLimitCount())
				throw new NotPossibleValueException("limit", "already limited.");*/

        if ("event".equals(event.getJoinType())) {
            if (!"lotto".equals(event.getPrimaryType()) && !"lottoPlaybol".equals(event.getPrimaryType()) &&
                    (joinList.size() >= event.getJoinLimitCount() && user.getEventTicketCount() < 1)) {
                throw new AlreadyExistsException("join", "lastDate=" + DateUtil.getDate(DateUtil.DEFAULT_FORMAT, joinList.get(0).getJoinDate()));
            }
            if (joinList.size() > 0 && user.getEventTicketCount() > 0) {
                return false;
            }
            return true;
        }

        if ("minute".equals(event.getJoinType())) {

            if (joinList == null || joinList.isEmpty()) {
                return true;
            } else {
                if (event.getJoinTerm() != null && event.getJoinTerm() > 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(joinList.get(0).getJoinDate());
                    cal.add(Calendar.MINUTE, event.getJoinTerm());

                    Date joinableDate = cal.getTime();

                    if (joinableDate.getTime() > now.getTime()) {
                        throw new AlreadyExistsException("join", "already.");
                    }

                } else {
                    return true;
                }
            }
        } else {
            Date prevDate = null;
            if ("daily".equals(event.getJoinType())) {
                prevDate = DateUtil.getDateAdd(now, DateUtil.DATE, -1);
            } else if ("weekly".equals(event.getJoinType())) {
                prevDate = DateUtil.getDateAdd(now, DateUtil.DATE, -7);
            } else if ("monthly".equals(event.getJoinType())) {
                prevDate = DateUtil.getDateAdd(now, DateUtil.MONTH, -1);
            }

            if (prevDate == null)
                throw new CommonException(501, "join type", "join type (" + event.getJoinType() + ") not defined.");

            Calendar cal = Calendar.getInstance();
            cal.setTime(prevDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            prevDate = cal.getTime();

            int joinCount = 0;
            for (EventJoin join : joinList) {
                if (join.getJoinDate().getTime() > prevDate.getTime())
                    joinCount++;
            }

            logger.debug("joinCount ==>" + joinCount);
            //MGK_ADD_LOTTO : limit 체크 안함
            if (!event.getPrimaryType().equals("lotto") && !event.getPrimaryType().equals("lottoPlaybol")) {
                if (joinCount >= event.getJoinLimitCount() && user.getEventTicketCount() < 1) {
                    Map<String, Object> result = new HashMap<String, Object>();
                    result.put("joinType", event.getJoinType());
                    result.put("joinLimitCount", event.getJoinLimitCount());
                    throw new AlreadyExistsException("row", result);
                }


                if (joinCount > 0 && user.getEventTicketCount() > 0) {
                    return false;
                }
            }

            if (event.getPrimaryType().equals("lotto") && (event.getReward() == null || event.getReward() >= 0)) {
                if (joinList == null || joinList.isEmpty()) {
                    return true;
                } else {
                    if (event.getJoinTerm() != null && event.getJoinTerm() > 0) {
                        cal = Calendar.getInstance();
                        cal.setTime(joinList.get(0).getJoinDate());
                        cal.add(Calendar.SECOND, event.getJoinTerm());

                        Date joinableDate = cal.getTime();

                        if (joinableDate.getTime() > now.getTime()) {
                            Map<String, Object> result = new HashMap<String, Object>();
                            result.put("joinDate", joinList.get(0).getJoinDate());
                            result.put("joinTerm", event.getJoinTerm());
                            Long remainSecond = (joinableDate.getTime() - now.getTime()) / 1000;
                            result.put("remainSecond", remainSecond);
                            throw new NotPossibleTimeException("row", result);
                        }

                    } else {
                        return true;
                    }
                }
            }

        }
        return true;
    }

    public boolean checkLottoJoinPossible(User user, Event event) throws ResultCodeException {

        if (!"none".equals(event.getRewardType()) && event.getReward() < 0 && user.getTotalBol() < (-1 * event.getReward()))
            throw new NotEnoughBolException("reward", event.getReward());

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);


        if (event.getMaxJoinCount() != null && event.getMaxJoinCount() > 0 && event.getMaxJoinCount() <= event.getJoinCount())
            throw new AlreadyLimitException("max-join-count", "limited");


        if ("always".equals(event.getJoinType())) {
            return true;
        }

        List<EventJoin> joinList = sqlSession.selectList("Event.getEventJoinAllByUser", map);

        Date now = DateUtil.getCurrentDate();

        if ("event".equals(event.getJoinType()) && joinList.size() >= event.getJoinLimitCount()) {
            throw new AlreadyExistsException("join", "lastDate=" + DateUtil.getDate(DateUtil.DEFAULT_FORMAT, joinList.get(0).getJoinDate()));
        }

        Date prevDate = null;
        if ("daily".equals(event.getJoinType())) {
            prevDate = DateUtil.getDateAdd(now, DateUtil.DATE, -1);
        } else if ("weekly".equals(event.getJoinType())) {
            prevDate = DateUtil.getDateAdd(now, DateUtil.DATE, -7);
        } else if ("monthly".equals(event.getJoinType())) {
            prevDate = DateUtil.getDateAdd(now, DateUtil.MONTH, -1);
        }

        if (prevDate == null)
            throw new CommonException(501, "join type", "join type (" + event.getJoinType() + ") not defined.");

        Calendar cal = Calendar.getInstance();
        cal.setTime(prevDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        prevDate = cal.getTime();

        int joinCount = 0;
        for (EventJoin join : joinList) {
            if (join.getJoinDate().getTime() > prevDate.getTime())
                joinCount++;
        }

        if (joinCount >= event.getJoinLimitCount()) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("joinType", event.getJoinType());
            result.put("joinLimitCount", event.getJoinLimitCount());
            throw new AlreadyExistsException("row", result);
        }


//        if (joinList.size() >= event.getJoinLimitCount()) {
//            throw new AlreadyExistsException("join", "lastDate=" + DateUtil.getDate(DateUtil.DEFAULT_FORMAT, joinList.get(0).getJoinDate()));
//        }

        return true;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin lot(User user, Event event) throws ResultCodeException {
        EventWin win = null;
        if (event.getGift()) {
            List<EventGift> giftList = sqlSession.selectList("Event.getEventGiftAll", event);

            boolean remain = false;
            boolean allzero = true;
            boolean noorder = true;
            int remainCount = 0;
            for (EventGift gift : giftList) {
                remainCount += gift.getRemainCount();
                if (allzero == true && gift.getLotPercent() > 0)
                    allzero = false;

                if (noorder == true && !StringUtils.isEmpty(gift.getWinOrder()))
                    noorder = false;

                if (remain == false && gift.getRemainCount() > 0)
                    remain = true;


            }

            if (allzero == true && noorder == true) {
                return win;
            }
//                throw new NotPossibleValueException("not possible lot", "0 percent");


            if ("immediately".equals(event.getWinAnnounceType()) && noorder == false && remain == true) {
                //순번에 의해 당첨되어야 하는지 일단 검사한다.
                for (EventGift gift : giftList) {
                    boolean winner = false;
                    if (!StringUtils.isEmpty(gift.getWinOrder())) {
                        String[] arr = gift.getWinOrder().split("\\s*\\,\\s*");
                        if (arr != null && arr.length > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                String orderStr = arr[i];
                                if (!StringUtils.isEmpty(orderStr)) {
                                    int seq = Integer.parseInt(orderStr);
                                    if (seq == event.getJoinCount()) {
                                        winner = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }


                    if (winner) {
                        win = win(user, event, gift);
                        if (win != null) {
                            if (--remainCount == 0)
                                remain = false;

                            break;
                        }
                    }
                }
            }

            if (win == null && allzero == false && remain == true) {
                for (EventGift gift : giftList) {
                    if (lot(gift)) {
                        win = win(user, event, gift);
                        if (win != null) {
                            break;
                        }
                    }
                }
            }

            for (EventGift gift : giftList) {
                if (gift.getRemainCount() > 0) {
                    remain = true;
                    break;
                }
            }

            if (remain == false) {
                if ("immediately".equals(event.getWinAnnounceType())) {
                    event.setStatus("announce");
                    event.setPriority(-1);
                    sqlSession.update("Event.setWinAnnounceDateNow", event);
                    sqlSession.update("Event.updateStatus", event);
                    sqlSession.update("Event.updatePriority", event);
                    try {
                        if (event.getAutoRegist() != null && event.getAutoRegist()) {
                            copyEvent(event);
                        }

                    } catch (Exception e) {

                    }

                } else if (!event.getPrimaryType().startsWith("lotto")) {
                    event.setStatus("pending");
                    event.setPriority(-1);
                    sqlSession.update("Event.updateStatus", event);
                    sqlSession.update("Event.updatePriority", event);
                }
            }
        }
        return win;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin win(User user, Event event, EventGift gift) {
        boolean last = gift.getTotalCount() == gift.getRemainCount() + 1;
        EventWin win = new EventWin();
        win.setUser(user);
        win.setEvent(event);
        win.setGift(gift);
        gift.setEvent(event);
        if (sqlSession.update("Event.decreaseRemainCount", gift) > 0) {
            sqlSession.insert("Event.insertWin", win);
            sqlSession.update("Event.increaseWinnerCount", event);
            event.setWinnerCount(event.getWinnerCount() + 1);
            gift.setRemainCount(gift.getRemainCount() - 1);
        }
        return win;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void concludeLottoAll() {
        List<Event> eventList = getLottoForConclude();
        for (Event event : eventList) {
            event.setStatus("conclude");
            updateStatus(event);
        }
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void lotExpiredEventAll() {
        // 추후발표 혹은 참여제한 이벤트 이면서 활성화 상태인 이벤트 목록을 조회한다.
        // 이벤트의 모든 경품수보다 참여자가 적은 경우에는 모든 참여자가 당첨될 때까지 계속 돌린다.
        List<Event> eventList = getAllForLot();
        for (Event event : eventList) {
            try {
                lot(event);
//                Map<String, Object> resultMap = lot(event);
//                event.setStatus("pending");

//                ParamMap map = new ParamMap();
//                map.put("openStatus", true);
//                map.put("eventSeqNo", event.getNo());
//                sqlSession.update("Event.updateEventWinOpenStatus", map);

            } catch (DeadlockLoserDataAccessException de) {
                logger.error("eventService DeadlockLoserDataAccessException : " + de.toString());
            } catch (Exception ex) {
                logger.error("eventService Error : " + AppUtil.excetionToString(ex));
//                event.setStatus("lotfail");
            }
        }
//        if(eventList.size() > 0){
//            ParamMap map = new ParamMap();
//            map.put("list", eventList);
//            sqlSession.update("Event.updateStatusList", map);
//        }

    }

//    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public void lotExpiredEventAllWhen(String code) {
//        // 추후발표 혹은 참여제한 이벤트 이면서 활성화 상태인 이벤트 목록을 조회한다.
//        // 이벤트의 모든 경품수보다 참여자가 적은 경우에는 모든 참여자가 당첨될 때까지 계속 돌린다.
//        List<Event> eventList = getAllForLotWhen(code);
//        for (Event event : eventList) {
//            try {
//                Map<String, Object> resultMap = lot(event);
//                event.setStatus("pending");
//            } catch (Exception ex) {
//                logger.error("eventService Error : " + AppUtil.excetionToString(ex));
//
//                event.setStatus("lotfail");
//            } finally {
//                updateStatus(event);
//            }
//        }
//    }


    public Long getLottoCount(String primaryType, Boolean active, String status) {
        ParamMap map = new ParamMap();
        map.put("primaryType", primaryType);
        map.put("active", active);
        map.put("status", status);
        return sqlSession.selectOne("Event.getLottoCount", map);
    }

    public List<Event> getLottoList(String primaryType, Boolean active, String status, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("primaryType", primaryType);
        map.put("active", active);
        map.put("status", status);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getLottoList", map);
    }

    public int getLottoMatchCount(EventJoin join, Event event) {
        String lottoCodes[] = event.getWinCode().split(",");
        String joinCodes[] = join.getWinCode().split(",");

        int count = 0;
        for (int i = 0; i < lottoCodes.length; i++) {
            for (int j = 0; j < joinCodes.length; j++) {
                if (lottoCodes[i].trim().equals(joinCodes[j].trim())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public Event copyEvent(Event copyEvent) throws Exception {

        Event event = get(copyEvent);
        List<EventGift> eGiftList = this.getEventGiftAll(event);

        event.setNo(null);
        logger.debug("Event.copyedEvent : " + event);

//		event.setVirtualNumber(null);
        //코드 120171218010001
//		String code = getMaxCode();
//		event.setCode(code);

//		setEventValues(event, event.getPrimaryType()) ;

        if (event.getPrimaryType().equals("lottoPlaybol")) {
            event.setWinCode(null);
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            LocalDate ld = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            ld = ld.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
            String satdayStr = ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 20:00:00";
            Date satDay = sdf.parse(satdayStr);


            String satdayWinStr = ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 21:30:00";
            Date satWinDay = sdf.parse(satdayWinStr);

            Duration duration = new Duration();
            duration.setStart(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", new Date()));
            duration.setEnd(satdayStr);
            event.setLottoTimes(event.getLottoTimes() + 1);
            event.setDuration(duration);
            event.setDisplayDuration(duration);
            event.setWinAnnounceDate(satWinDay);
            event.setWinAnnounceType("special");
        }

        event.setStatus("active");
        event.setJoinCount(0);
        event.setWinnerCount(0);

        Integer priority = 0;
        if (event.getPrimaryType().equals("goodluck") && (event.getRewardPlay() == null || event.getRewardPlay() == 0)) {
            if (event.getReward() >= 0) {
                priority = sqlSession.selectOne("Event.getPriorityFree");
            } else if (event.getReward() < 0) {
                priority = sqlSession.selectOne("Event.getPriorityReward");
            }
        } else {
            priority = sqlSession.selectOne("Event.getPriority");
        }

        if (event.getPrimaryType().equals("goodluck") && event.getReward() >= 0) {
            event.setPriority(9999);
        } else {
            event.setPriority(priority);
        }


        Duration duration = new Duration();

        Calendar calendar = Calendar.getInstance();
        event.setRegDate(calendar.getTime());
        calendar.add(Calendar.MINUTE, 5);
        duration.setStart(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", calendar.getTime()));
        calendar.add(Calendar.YEAR, 1);
        duration.setEnd(DateUtil.getDateString("yyyy-MM-dd HH:mm:ss", calendar.getTime()));
        event.setDuration(duration);
        event.setDisplayDuration(duration);
        event.setWinAnnounceDate(calendar.getTime());

        Integer ret = insert(event);

        if (ret <= 0 || event.getNo() == null) {
            logger.debug("insertEvent Error");
            throw new Exception("insertEvent Error : " + event.toString());
        }

        logger.debug("event seqNo : " + event.getNo());


        for (int i = 0; eGiftList != null && i < eGiftList.size(); i++) {
            EventGift gift = eGiftList.get(i);
            gift.setRemainCount(gift.getTotalCount());
            gift.setEvent(event);
            eGiftList.set(i, gift);
            insertEventGift(gift);
        }

        return event;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, Object> lot(Event event) throws ResultCodeException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Object> resultMap = new HashMap<String, Object>();

        List<EventWin> winList = null;
        boolean c = false;
        EventGift sum = getTotalGiftCount(event);
        int winCount = 0;

        if (event.getPrimaryType().equals("number") || event.getPrimaryType().startsWith("lotto")) {
            sum.setTotalCount(1);
            sum.setRemainCount(1);
        }

        if (sum.getTotalCount() > 0 && sum.getRemainCount() > 0) {
            winList = new ArrayList<EventWin>();
            List<EventJoin> joinList = getEventJoinAll(event);
            List<EventJoin> copyList = new ArrayList<EventJoin>();
            copyList.addAll(joinList);

            List<BolHistory> bolHistoryList = new ArrayList<>();
            List<PointHistory> pointHistoryList = new ArrayList<>();
            List<CashHistory> cashHistoryList = new ArrayList<>();
            List<ParamMap> paramMapList = new ArrayList<>();
            List<ParamMap> lottoMapList = new ArrayList<>();
            int lottoIdx = 0;

            logger.info("EventService.lot() : joinList.size() : " + joinList.size());

            if (event.getPrimaryType().equals("number")) {

                List<EventGift> giftList = sqlSession.selectList("Event.getEventGiftAll", event);

                if (giftList == null || giftList.size() == 0) {
                    return null;
                }

                EventGift gift = giftList.get(0);
                for (int i = 0; i < joinList.size(); i++) {
                    EventJoin join = joinList.get(i);

                    if (join.getWinCode() != null && event.getWinCode().equals(join.getWinCode())) {
                        EventWin win = new EventWin();
                        win.setUser(join.getUser());
                        win.setEvent(event);
                        win.setGift(gift);
                        win.setGiftStatus(0);
                        winList.add(win);
                    }
                }

                int num = winList.size();

                logger.info("EventWin count : " + num);
                lottoIdx = 0;
                for (EventWin win : winList) {

                    if ("bol".equals(gift.getType()) && gift.getPrice() != null && gift.getPrice() > 0) {

                        Float amount = gift.getPrice().floatValue() / num;
                        win.setAmount(amount);

                        if (amount > 0) {
                            BolHistory history = new BolHistory();
                            history.setAmount(amount);
                            history.setUser(win.getUser());
                            history.setSubject("당첨번호 맞추기 당첨");
                            history.setPrimaryType("increase");
                            history.setSecondaryType("winEvent");
                            history.setTargetType("member");
                            history.setTarget(win.getUser());
                            history.setProperties(new HashMap<String, Object>());
                            history.getProperties().put("적립 유형", event.getTitle() + " 당첨");
                            if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                                history.getProperties().put("지급처", "캐시픽 운영팀");
                            } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                                history.getProperties().put("지급처", "럭키픽 운영팀");
                            } else {
                                history.getProperties().put("지급처", "오리마켓 운영팀");
                            }
                            history.setSaveBuff(gift.getSaveBuff());
                            bolHistoryList.add(history);
                        }
                    } else if ("point".equals(gift.getType()) && gift.getPrice() != null && gift.getPrice() > 0) {

                        Float amount = gift.getPrice().floatValue() / num;
                        win.setAmount(amount);

                        PointHistory history = new PointHistory();
                        history.setMemberSeqNo(win.getUser().getNo());
                        history.setType("charge");
                        history.setPoint(amount);
                        history.setSubject("당첨번호 맞추기 당첨");
                        history.setHistoryProp(new HashMap<String, Object>());
                        history.getHistoryProp().put("적립 유형", event.getTitle() + " 당첨");
                        if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                            history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                            history.getHistoryProp().put("지급처", "럭키픽 운영팀");
                        } else {
                            history.getHistoryProp().put("지급처", "오리마켓 운영팀");
                        }
                        history.setSaveBuff(gift.getSaveBuff());
                        pointHistoryList.add(history);

                    } else if ("cash".equals(gift.getType()) && gift.getPrice() != null && gift.getPrice() > 0) {
                        Float amount = gift.getPrice().floatValue() / num;
                        win.setAmount(amount);
                        CashHistory history = new CashHistory();
                        history.setMemberSeqNo(win.getUser().getNo());
                        history.setType("charge");
                        history.setSecondaryType("member");
                        history.setCash(amount);
                        history.setSubject("당첨번호 맞추기 당첨");
                        history.setHistoryProp(new HashMap<String, Object>());
                        history.getHistoryProp().put("적립 유형", event.getTitle() + " 당첨");
                        history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        cashHistoryList.add(history);
                    } else if ("lotto".equals(gift.getType()) && gift.getPrice() != null && gift.getPrice() > 0) {

                        lottoMapList.addAll(generateLottJoinList(win.getUser().getNo(), gift.getPrice().intValue(), "eventWin", lottoIdx));
                    }

                }


                if (winList.size() > 0) {

                    ParamMap map = new ParamMap();
                    map.put("winList", winList);
                    paramMapList.add(map);

                }

                resultMap.put("joinList", copyList);
                resultMap.put("winList", winList);

            } else {

                List<EventGift> giftList = sqlSession.selectList("Event.getEventGiftAll", event);
                if (giftList.size() > 1) {
                    logger.error("multi gift");

                    for (EventGift gift : giftList) {
                        if (gift.getRemainCount() <= 0) {
                            continue;
                        }

                        Collections.shuffle(joinList);

                        winList = new ArrayList<EventWin>();
                        List<Long> winSeqNo = new ArrayList<>();
                        List<EventCampaignWin> eventCampaignWinList = new ArrayList<>();
                        lottoIdx = 0;
                        do {
                            for (int i = 0; i < joinList.size(); i++) {
                                EventJoin join = joinList.get(i);
                                int existCampaign = existsEventCampaignWin(join.getUser(), event);
                                if (existCampaign > 0) {
                                    joinList.remove(i);
                                } else {
                                    if (winSeqNo.contains(join.getUser().getNo())) {
                                        joinList.remove(i);
                                    } else {
                                        EventWin win = new EventWin();
                                        win.setUser(join.getUser());
                                        win.setEvent(event);
                                        win.setGift(gift);
                                        win.setGiftStatus(0);

                                        if (event.getCampaignSeqNo() != null) {
                                            EventCampaignWin eventCampaignWin = new EventCampaignWin();
                                            eventCampaignWin.setEventCampaignSeqNo(event.getCampaignSeqNo());
                                            eventCampaignWin.setMemberSeqNo(join.getUser().getNo());
                                            eventCampaignWin.setWinCount(1);
                                            eventCampaignWinList.add(eventCampaignWin);
                                        }


                                        winList.add(win);
                                        winSeqNo.add(join.getUser().getNo());
                                        gift.setRemainCount(gift.getRemainCount() - 1);
                                        joinList.remove(i);

                                        if ("bol".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {
                                            BolHistory history = new BolHistory();
                                            history.setAmount(win.getGift().getPrice().floatValue());
                                            history.setUser(win.getUser());
                                            history.setTarget(win.getUser());
                                            history.setPrimaryType("increase");
                                            history.setSecondaryType("winEvent");
                                            history.setTargetType("member");
                                            history.setSubject("이벤트 당첨");
                                            history.setProperties(new HashMap<String, Object>());
                                            history.getProperties().put("적립 유형", event.getTitle() + " 당첨");
                                            if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                                                history.getProperties().put("지급처", "캐시픽 운영팀");
                                            } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                                                history.getProperties().put("지급처", "럭키픽 운영팀");
                                            } else {
                                                history.getProperties().put("지급처", "오리마켓 운영팀");
                                            }
                                            history.setSaveBuff(gift.getSaveBuff());
                                            bolHistoryList.add(history);

                                        } else if ("point".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {

                                            PointHistory history = new PointHistory();
                                            history.setMemberSeqNo(win.getUser().getNo());
                                            history.setType("charge");
                                            history.setPoint(win.getGift().getPrice().floatValue());
                                            history.setSubject("이벤트 당첨");
                                            history.setHistoryProp(new HashMap<String, Object>());
                                            history.getHistoryProp().put("적립 유형", event.getTitle() + " 당첨");
                                            if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                                                history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                                            } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                                                history.getHistoryProp().put("지급처", "럭키픽 운영팀");
                                            } else {
                                                history.getHistoryProp().put("지급처", "오리마켓 운영팀");
                                            }
                                            history.setSaveBuff(gift.getSaveBuff());
                                            pointHistoryList.add(history);

                                        } else if ("cash".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {
                                            CashHistory history = new CashHistory();
                                            history.setMemberSeqNo(win.getUser().getNo());
                                            history.setType("charge");
                                            history.setSecondaryType("member");
                                            history.setCash(win.getGift().getPrice().floatValue());
                                            history.setSubject("이벤트 당첨");
                                            history.setHistoryProp(new HashMap<String, Object>());
                                            history.getHistoryProp().put("적립 유형", event.getTitle() + " 당첨");
                                            history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                                            cashHistoryList.add(history);
                                        } else if ("lotto".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {
                                            lottoMapList.addAll(generateLottJoinList(win.getUser().getNo(), win.getGift().getPrice().intValue(), "eventWin", lottoIdx));
                                        }
                                    }
                                }


                                if (gift.getRemainCount() == 0)
                                    break;
                            }

                        } while (!(gift.getRemainCount() == 0 || joinList.size() == 0));

                        if (winList.size() > 0) {
                            ParamMap map = new ParamMap();
                            map.put("count", winList.size());
                            map.put("gift", gift);
                            map.put("winList", winList);
                            if (eventCampaignWinList.size() > 0) {
                                map.put("campaignList", eventCampaignWinList);
                            }

                            paramMapList.add(map);
                        }
                    }

                } else {
                    logger.error("single gift");


                    if (sum.getRemainCount() > 0) {
                        Collections.shuffle(joinList);

                        List<Long> winSeqNo = new ArrayList<>();
                        List<EventCampaignWin> eventCampaignWinList = new ArrayList<>();
                        lottoIdx = 0;
                        do {
                            for (int i = 0; i < joinList.size(); i++) {
                                EventJoin join = joinList.get(i);
                                int existCampaign = existsEventCampaignWin(join.getUser(), event);
                                if (existCampaign > 0) {
                                    joinList.remove(i);
                                } else {
                                    if (winSeqNo.contains(join.getUser().getNo())) {
                                        joinList.remove(i);
                                    } else {
                                        EventWin win = new EventWin();
                                        win.setUser(join.getUser());
                                        win.setEvent(event);
                                        win.setGift(giftList.get(0));
                                        win.setGiftStatus(0);

                                        if (event.getCampaignSeqNo() != null) {
                                            EventCampaignWin eventCampaignWin = new EventCampaignWin();
                                            eventCampaignWin.setEventCampaignSeqNo(event.getCampaignSeqNo());
                                            eventCampaignWin.setMemberSeqNo(join.getUser().getNo());
                                            eventCampaignWin.setWinCount(1);
                                            eventCampaignWinList.add(eventCampaignWin);
                                        }


                                        winList.add(win);
                                        winSeqNo.add(join.getUser().getNo());
                                        sum.setRemainCount(sum.getRemainCount() - 1);
                                        joinList.remove(i);

                                        if ("bol".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {
                                            BolHistory history = new BolHistory();
                                            history.setAmount(win.getGift().getPrice().floatValue());
                                            history.setUser(win.getUser());
                                            history.setTarget(win.getUser());
                                            history.setPrimaryType("increase");
                                            history.setSecondaryType("winEvent");
                                            history.setTargetType("member");
                                            history.setSubject("이벤트 당첨");
                                            history.setProperties(new HashMap<String, Object>());
                                            history.getProperties().put("적립 유형", event.getTitle() + " 당첨");
                                            if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                                                history.getProperties().put("지급처", "캐시픽 운영팀");
                                            } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                                                history.getProperties().put("지급처", "럭키픽 운영팀");
                                            } else {
                                                history.getProperties().put("지급처", "오리마켓 운영팀");
                                            }
                                            history.setSaveBuff(win.getGift().getSaveBuff());
                                            bolHistoryList.add(history);
                                        } else if ("point".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {

                                            PointHistory history = new PointHistory();
                                            history.setMemberSeqNo(win.getUser().getNo());
                                            history.setType("charge");
                                            history.setPoint(win.getGift().getPrice().floatValue());
                                            history.setSubject("이벤트 당첨");
                                            history.setHistoryProp(new HashMap<String, Object>());
                                            history.getHistoryProp().put("적립 유형", event.getTitle() + " 당첨");
                                            if (event.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                                                history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                                            } else if (event.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                                                history.getHistoryProp().put("지급처", "럭키픽 운영팀");
                                            } else {
                                                history.getHistoryProp().put("지급처", "오리마켓 운영팀");
                                            }
                                            history.setSaveBuff(win.getGift().getSaveBuff());
                                            pointHistoryList.add(history);

                                        } else if ("cash".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {
                                            CashHistory history = new CashHistory();
                                            history.setMemberSeqNo(win.getUser().getNo());
                                            history.setType("charge");
                                            history.setSecondaryType("member");
                                            history.setCash(win.getGift().getPrice().floatValue());
                                            history.setSubject("이벤트 당첨");
                                            history.setHistoryProp(new HashMap<String, Object>());
                                            history.getHistoryProp().put("적립 유형", event.getTitle() + " 당첨");
                                            if (event.getAppType().equals("luckyball")) {
                                                history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                                            } else {
                                                history.getHistoryProp().put("지급처", "오리마켓 운영팀");
                                            }
                                            cashHistoryList.add(history);

                                        } else if ("lotto".equals(win.getGift().getType()) && win.getGift().getPrice() != null && win.getGift().getPrice() > 0) {
                                            lottoMapList.addAll(generateLottJoinList(win.getUser().getNo(), win.getGift().getPrice().intValue(), "eventWin", lottoIdx));
                                        }
                                    }
                                }


                                if (sum.getRemainCount() == 0)
                                    break;
                            }

                        } while (!(sum.getRemainCount() == 0 || joinList.size() == 0));

                        if (winList.size() > 0) {
                            ParamMap map = new ParamMap();
                            map.put("count", winList.size());
                            map.put("gift", giftList.get(0));
                            map.put("winList", winList);
                            if (eventCampaignWinList.size() > 0) {
                                map.put("campaignList", eventCampaignWinList);
                            }

                            paramMapList.add(map);

                        }

                    }

                }

                resultMap.put("joinList", copyList);
                resultMap.put("winList", winList);

//                sqlSession.update("Event.updateEventLot", event);

            }

            ParamMap map = new ParamMap();
            map.put("event", event);
            if (paramMapList.size() > 0) {
                map.put("paramList", paramMapList);
            }

            if (bolHistoryList.size() > 0) {
                map.put("bolHistoryList", bolHistoryList);
            }

            if (pointHistoryList.size() > 0) {
                map.put("pointHistoryList", pointHistoryList);
            }

            if (cashHistoryList.size() > 0) {
                map.put("cashHistoryList", cashHistoryList);
            }

            if (lottoMapList.size() > 0) {
                Long lotterySeqNo = sqlSession.selectOne("Event.getLotterySeqNo");
                Random random = new Random();
                int no = random.nextInt(28) + 1;

                map.put("lotterySeqNo", lotterySeqNo);
                map.put("tableName", "lottery_join" + no);
                map.put("lottoMapList", lottoMapList);

            }

            sqlSession.insert("Event.eventLot", map);

        }

        stopWatch.stop();

        logger.error("runningTime ====> " + (int) stopWatch.getTotalTimeSeconds() / 60 + "분" + (int) stopWatch.getTotalTimeSeconds() % 60 + "초");

        return resultMap;
    }

    public void insertEventGift(EventGift eventGift) {
        sqlSession.insert("Event.insertEventGift", eventGift);
    }

    public void announce(Event event) {
        if ("win".equals(event.getWinPushType())) {

            try {
                List<EventWin> winList = getEventWinAll(event);

                if (winList.size() > 0) {
                    MsgOnly msg = new MsgOnly();
                    msg.setInput("system");
                    msg.setStatus("ready");
                    msg.setType("push");
                    msg.setMoveType1("inner");
                    msg.setMoveType2("eventWin");
                    msg.setMoveTarget(event);
                    msg.setPushCase(Const.USER_PUSH_EVENT);
                    if (event.getPrimaryType().equals("page")) {
                        msg.setMoveType2("eventWinPage");
                        msg.setSubject("이벤트 당첨");
                        msg.setContents(event.getTitle());
                    } else {
                        msg.setSubject(event.getWinPushTitle());
                        msg.setContents(event.getWinPushBody());
                    }

                    if (event.getAppType().equals("pplus") || event.getAppType().equals("biz")) {
                        msg.setAppType(Const.APP_TYPE_USER);
                    } else {
                        msg.setAppType(Const.APP_TYPE_LUCKYBOL);
                    }

                    msgSvc.initMsgOnly(msg);
                    msg.setTargetCount(winList.size());
                    msg.setAuthor(StoreUtil.getCommonAdmin());

                    int effected = sqlSession.insert("Msg.insertMsg", msg);
                    if (effected > 0) {

                        List<User> targetList = new ArrayList<>();

                        for (EventWin win : winList) {
                            targetList.add(win.getUser());
                        }

                        PushTarget target = new PushTarget();
                        target.setStatus("ready");
                        ParamMap map = new ParamMap();
                        map.put("msg", msg);
                        map.put("target", target);
                        map.put("list", targetList);
                        sqlSession.insert("Msg.insertPushTargetUserList", map); //MGK msg, target);

                        map.clear();
                        map.put("list", targetList);
                        map.put("msg", msg);
                        map.put("type", msg.getAppType());
                        sqlSession.insert("Msg.insertMsgBoxUserList", map);
                    }

                    if (msg != null && msg.getNo() != null) {
                        producer.push(msg);
                    }
                }

            } catch (Exception e) {
                logger.error("announce win error : " + e.toString());
            }


        } else if ("join".equals(event.getWinPushType())) {
            try {
                List<User> joinList = getEventJoinUserAll(event);
                if (joinList.size() > 0) {
                    MsgOnly msg = new MsgOnly();
                    msg.setInput("system");
                    msg.setStatus("ready");
                    msg.setType("push");
                    msg.setMoveType1("inner");
                    msg.setMoveType2("eventWin");
                    msg.setMoveTarget(event);
                    msg.setPushCase(Const.USER_PUSH_EVENT);
                    msg.setSubject(event.getWinPushTitle());
                    msg.setContents(event.getWinPushBody());
                    msg.setAppType(Const.APP_TYPE_LUCKYBOL);
                    msgSvc.initMsgOnly(msg);

                    msg.setTargetCount(joinList.size());
                    msg.setAuthor(StoreUtil.getCommonAdmin());

                    int effected = sqlSession.insert("Msg.insertMsg", msg);
                    if (effected > 0) {

                        PushTarget target = new PushTarget();
                        target.setStatus("ready");
                        ParamMap map = new ParamMap();
                        map.put("msg", msg);
                        map.put("target", target);
                        map.put("list", joinList);
                        sqlSession.insert("Msg.insertPushTargetUserList", map); //MGK msg, target);

                        map.clear();
                        map.put("list", joinList);
                        map.put("msg", msg);
                        map.put("type", msg.getAppType());
                        sqlSession.insert("Msg.insertMsgBoxUserList", map);
                    }

                    if (msg != null && msg.getNo() != null) {
                        producer.push(msg);
                    }
                }

            } catch (Exception e) {
                logger.error("announce join error : " + e.toString());
            }

        }
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin winNumberMatching(EventJoin join, Event event) {
        EventGift gift = getRandomRemainGift(event);
        if (join.getWinCode() != null &&
                event.getWinCode().equals(join.getWinCode())) {

            EventWin win = new EventWin();
            win.setUser(join.getUser());
            win.setEvent(event);
            win.setGift(gift);
            sqlSession.insert("Event.insertWin", win);
            sqlSession.update("Event.increaseWinnerCount", event);
            event.setWinnerCount(event.getWinnerCount() + 1);
            return win;
        } else {
            return null;
        }
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin winRandomGift(User user, Event event) {
        EventGift gift = getRandomRemainGift(event);
        if (gift != null
                && gift.getRemainCount() != null
                && gift.getRemainCount() > 0) {

            if (sqlSession.update("Event.decreaseRemainCount", gift) > 0) {
                EventWin win = new EventWin();
                win.setUser(user);
                win.setEvent(event);
                win.setGift(gift);
                win.setGiftStatus(0);
                gift.setEvent(event);
                sqlSession.insert("Event.insertWin", win);
//                sqlSession.update("Event.increaseWinnerCount", event);
//                event.setWinnerCount(event.getWinnerCount() + 1);
                gift.setRemainCount(gift.getRemainCount() - 1);

                if (event.getCampaignSeqNo() != null) {
                    EventCampaignWin eventCampaignWin = new EventCampaignWin();
                    eventCampaignWin.setEventCampaignSeqNo(event.getCampaignSeqNo());
                    eventCampaignWin.setMemberSeqNo(user.getNo());
                    eventCampaignWin.setWinCount(1);
                    sqlSession.insert("Event.insertEventCampaignWin", eventCampaignWin);
                }


                return win;
            }

        }
        return null;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin winRandomGiftBeta(User user, Event event) {
        EventGift gift = getRandomRemainGift(event);
        if (gift != null
                && gift.getRemainCount() != null
                && gift.getRemainCount() > 0) {

            logger.debug("winRandomGiftBeta : gift : " + gift);

//			BetaPost betaPost = betaPostRepository.findByRandom(gift.getBetaCode()) ;
//			if( betaPost == null ) {
//				logger.error("해당 경품에 대한 베타 당첨 소감 내역 리스트가 없습니다 : " + gift.getBetaCode()) ;
//				return null ;
//			}
//			betaPost.setActive(false);
//			betaPostRepository.save(betaPost) ;

            if (sqlSession.update("Event.decreaseRemainCount", gift) > 0) {
                EventWin win = new EventWin();
                win.setUser(user);
                win.setEvent(event);
                win.setGift(gift);
                //win.setImpression(betaPost.getReview());
                win.setImpression("");
                gift.setEvent(event);
                sqlSession.insert("Event.insertWin", win);
                sqlSession.update("Event.increaseWinnerCount", event);
                event.setWinnerCount(event.getWinnerCount() + 1);
                gift.setRemainCount(gift.getRemainCount() - 1);
                logger.debug("winRandomGiftBeta : win : " + gift);
                return win;
            }

        }
        return null;
    }


    private EventGift getRandomRemainGift(Event event) {
        List<EventGift> giftList = sqlSession.selectList("Event.getEventGiftAll", event);
        if (giftList.size() > 0) {
            if (giftList.size() > 1)
                Collections.shuffle(giftList);

            EventGift selected = null;
            for (EventGift gift : giftList) {
                if (gift.getRemainCount() == 0)
                    continue;
                else {
                    selected = gift;
                    selected.setEvent(event);
                    break;
                }
            }

            return selected;
        }
        return null;
    }

    private boolean lot(EventGift gift) {
        if (gift.getRemainCount() > 0) {
            return StoreUtil.lots(gift.getLotPercent());
        }
        return false;
    }

    private void checkTimeDuration(Event event) throws Exception {
        Date now = DateUtil.getCurrentDate();
        if (!"time".equals(event.getSecondaryType())) {
            if (event.getDuration().getStartDate().getTime() > now.getTime()
                    || event.getDuration().getEndDate().getTime() < now.getTime())
                throw new NotPossibleTimeException("key", "time");

            return;
        }


        List<TimeDuration> displayTimeList = sqlSession.selectList("Event.getEventTimeList", event);

        if (displayTimeList == null || displayTimeList.size() == 0)
            throw new NotPossibleTimeException("key", "time");

        boolean include = false;
        String strNowTime = DateUtil.getDate(DateUtil.BASE_TIME_FORMAT, now);
        int nowTime = Integer.valueOf(strNowTime.substring(0, 2)) * 60 * 60 + Integer.valueOf(strNowTime.substring(2, 4)) * 60 + Integer.valueOf(strNowTime.substring(4, 6));

        logger.debug("size : " + displayTimeList.size());
        for (TimeDuration d : displayTimeList) {
            logger.debug("start : " + d.getStart() + " end : " + d.getEnd());
            int startTime = Integer.valueOf(d.getStart().substring(0, 2)) * 60 * 60 + Integer.valueOf(d.getStart().substring(2, 4)) * 60;
            int endTime = Integer.valueOf(d.getEnd().substring(0, 2)) * 60 * 60 + Integer.valueOf(d.getEnd().substring(2, 4)) * 60 + 59;
//			Date startDate = DateUtil.getDate(DateUtil.BASE_TIME_FORMAT,d.getStart() + "00");
//			Date endDate = DateUtil.getDate(DateUtil.BASE_TIME_FORMAT, d.getEnd() + "59");
//			Date nowTime = DateUtil.getDate(DateUtil.BASE_TIME_FORMAT, DateUtil.getDate(DateUtil.BASE_TIME_FORMAT, now));
            logger.debug("start : " + startTime);
            logger.debug("now : " + nowTime);
            logger.debug("end : " + endTime);
            if (startTime <= nowTime && endTime >= nowTime) {
                include = true;
                break;
            }
        }

        if (include == false)
            throw new NotPossibleTimeException("key", "time");

        return;

    }

    private Boolean checkExistVirtualNumber(String virtualNumber) {
        //이벤트번호 체크, 없으면 먼저 insert
        Boolean exist = false;
        int cnt = sqlSession.selectOne("Event.checkExistVirtualNumber", virtualNumber);
        if (cnt > 0) {
            exist = true;
        }
        return exist;
    }

    public boolean checkeVirtualUsed(String virtualNumber) {

        int cnt = sqlSession.selectOne("Event.checkeVirtualUsed", virtualNumber);
        if (cnt > 0) {
            return true;
        }
        return false;
    }

    private void insertVirtualNumber(String number) {
        VirtualNumber virtualNumber = new VirtualNumber();
        virtualNumber.setNumber(number);
        virtualNumber.setType("gold");
        virtualNumber.setReserved(false);
        virtualNumber.setActSrc("pms");//실제 로그인 아이디 필요
        virtualNumber.setActor(StoreUtil.getCommonAdmin());

        sqlSession.insert("Event.insertVirtualNumber", virtualNumber);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer insert(Event event) throws ResultCodeException {

        //이벤트번호 체크, 없으면 먼저 insert
        if (StringUtils.isEmpty(event.getVirtualNumber()) == false) {


            if (checkeVirtualUsed(event.getVirtualNumber())) {
                return 0;
            }

            if (checkExistVirtualNumber(event.getVirtualNumber()) == false) {
                insertVirtualNumber(event.getVirtualNumber());
            }
        }

        if (event.getAos() == null) {
            event.setAos(false);
        }
        if (event.getIos() == null) {
            event.setIos(false);
        }
        if (event.getPcweb() == null) {
            event.setPcweb(false);
        }
        if (event.getMobileweb() == null) {
            event.setMobileweb(false);
        }
        if (event.getCms() == null) {
            event.setCms(false);
        }
        if (event.getElectron() == null) {
            event.setElectron(false);
        }
        if (event.getIsBatch() == null) {
            event.setIsBatch(false);
        }

        //코드 120171218010001
        String code = getMaxCode();
        event.setCode(code);

        logger.info("event : " + event);
        return sqlSession.insert("Event.insert", event);


    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateEventWinAddress(User user, EventWin win) throws ResultCodeException {
        EventWin saved = null;

        saved = getWinById(win.getId());

        if (saved == null)
            throw new NotFoundTargetException("win", "not found.");

        if (!user.getNo().equals(saved.getUser().getNo()))
            throw new NotMatchedValueException("user", "not matched.");

        win.setId(saved.getId());
        int effected = sqlSession.update("Event.updateDeliveryAddress", win);

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateImpression(User user, EventWin win) throws ResultCodeException {

        EventWin saved = null;

        if (win.getId() != null) {
            saved = getWinById(win.getId());
        } else {
            saved = getWin(win.getEvent().getNo(), win.getWinNo(), user.getNo());
        }

        if (saved == null)
            throw new NotFoundTargetException("win", "not found.");

        if (!user.getNo().equals(saved.getUser().getNo()))
            throw new NotMatchedValueException("user", "not matched.");

        boolean insert = StringUtils.isEmpty(saved.getImpression());

        EventGift gift = saved.getGift();
        if ("bol".equals(gift.getType()) && gift.getPrice() != null && gift.getPrice() > 0) {
            if (saved.getEvent().getPrimaryType().equals("number")) {
                win.setImpression("럭키볼이 당첨되었습니다.");
            } else {
                int bolRatio = commonSvc.getBolRatio(user.getCountry());
                win.setImpression((gift.getPrice() / bolRatio) + "볼 당첨되었습니다.");
            }

        }


        win.setImpression(Filtering.filter(win.getImpression()));
        win.setId(saved.getId());
        int effected = sqlSession.update("Event.updateImpression", win);

        if (effected == 0)
            throw new CommonException(501, "cause", "unknown");
        else {

            if (insert && !user.getIsVirtual()) {
                if (gift.getType().equals("mobileGift") && gift.getAutoSend() && gift.getGiftishowSeqNo() != null) {

                    Giftishow giftishow = giftishowService.getGiftishow(gift.getGiftishowSeqNo());

                    String trId = giftishowService.getTrId();
                    String mobile = user.getMobile().replace("luckyball##", "").replace("biz##", "");
                    JsonObject resultObject = null;
                    if (saved.getEvent().getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                        resultObject = giftishowService.send(giftishow.getGoodsCode(), trId, "캐시픽 이벤트 당첨", "캐시픽 이벤트 당첨을 축하드립니다. \n경품에 당첨되신 기념으로  구글플레이스토어에\n리뷰를 남겨주시면 캐시픽에 큰 힘이됩니다.\n\nhttps://play.google.com/store/apps/details?id=com.pplus.luckybol", mobile);
                    } else if (saved.getEvent().getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                        resultObject = giftishowService.send(giftishow.getGoodsCode(), trId, "럭키픽 이벤트 당첨", "럭키픽 이벤트 당첨을 축하드립니다. \n경품에 당첨되신 기념으로  구글플레이스토어에\n리뷰를 남겨주시면 럭키픽에 큰 힘이됩니다.\n\nhttps://play.google.com/store/apps/details?id=com.pplus.luckybol", mobile);
                    } else {
                        resultObject = giftishowService.send(giftishow.getGoodsCode(), trId, "플러스멤버 이벤트 당첨", saved.getEvent().getTitle() + " 당첨을 축하드립니다. \n경품에 당첨되신 기념으로  구글플레이스토어에\n리뷰를 남겨주시면 플러스멤버에 큰 힘이됩니다.\n\nhttps://play.google.com/store/apps/details?id=com.pplus.prnumberuser", mobile);
                    }

                    if (resultObject != null) {
                        String orderNo = resultObject.get("orderNo").getAsString();
                        win.setGiftTrId(trId);
                        win.setGiftOrderNo(orderNo);
                        win.setGiftMobileNumber(mobile);
                        sqlSession.update("Event.updateGiftTrId", win);
                    } else {
                        throw new GiftishowException();
                    }
                }
            }

        }
        return Const.E_SUCCESS;

    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateStatus(Event event) {

        if (event.getPrimaryType().equals("lotto") || event.getPrimaryType().equals("lottoPlaybol")) {
            int effected = sqlSession.update("Event.updateStatusWithWinCode", event);
        } else {
            int effected = sqlSession.update("Event.updateStatus", event);
        }

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePriority(Event event) {
        int effected = sqlSession.update("Event.updatePriority", event);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer setWinAnnounceDateNow(Event event) {
        int effected = sqlSession.update("Event.setWinAnnounceDateNow", event);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateLotto(Lotto lotto) {
        int effected = sqlSession.update("Event.updateLotto", lotto);
        return effected;
    }


    public Event get(Event event) {
        ParamMap map = new ParamMap();
        map.put("event", event);
        return sqlSession.selectOne("Event.get", map);
    }

    public Event getByCode(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.getByCode", map);
    }

    public EventBanner getBanner(long eventNo, int bannerNo) {
        ParamMap map = new ParamMap();
        map.put("eventNo", eventNo);
        map.put("bannerNo", bannerNo);
        return sqlSession.selectOne("Event.getBanner", map);
    }

    public EventWin getWin(long eventNo, int winNo, long memberSeqNo) {
        ParamMap map = new ParamMap();
        map.put("eventNo", eventNo);
        map.put("winNo", winNo);
        map.put("memberSeqNo", memberSeqNo);
        return sqlSession.selectOne("Event.getWin", map);
    }

    public EventWin getWinById(long id) {
        ParamMap map = new ParamMap();
        map.put("id", id);
        return sqlSession.selectOne("Event.getWinById", map);
    }

    public Event getValidByNumber(User user, Device device, SearchOpt opt) throws ResultCodeException {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("opt", opt);
        map.put("addr", addr);
        Event saved = sqlSession.selectOne("Event.getValidByNumber", map);
        if (saved == null)
            throw new NotFoundTargetException("event", "not found.");
        return saved;
    }

    public int getCountByBatch(User user, SearchOpt opt, String winAnnounceDate) {
        Page page = pageSvc.getPageByUser(user);
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("opt", opt);
        map.put("winAnnounceDate", winAnnounceDate);
        return sqlSession.selectOne("Event.getCountByBatch", map);
    }

    public List<Event> getListByBatch(User user, SearchOpt opt, String winAnnounceDate) {
        Page page = pageSvc.getPageByUser(user);
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("opt", opt);
        map.put("winAnnounceDate", winAnnounceDate);
        return sqlSession.selectList("Event.getListByBatch", map);
    }

    public int getWinCountByBatch(User user, SearchOpt opt, String winAnnounceDate) {
        Page page = pageSvc.getPageByUser(user);
        ParamMap map = new ParamMap();
        map.put("page", page);
        map.put("opt", opt);
        map.put("winAnnounceDate", winAnnounceDate);
        return sqlSession.selectOne("Event.getCountByBatch", map);
    }

    public List<String> getSearchAddress(User user) {
        List<String> addr = new ArrayList<String>();
        if (user != null && !StringUtils.isEmpty(user.getBaseAddr())) {
            String[] arr = user.getBaseAddr().split("\\s+");
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                if (i == 0)
                    buf.append(arr[i]);
                else
                    buf.append(" ").append(arr[i]);

                addr.add(buf.toString());
            }
        }
        return addr;
    }

    public Event getActiveEventByPageSeqNo(User user, Device device, SearchOpt opt, String appType, Long pageSeqNo) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("opt", opt);
            map.put("address", addr);
        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("opt", opt);
            map.put("address", null);
        }
        map.put("appType", appType);
        map.put("pageSeqNo", pageSeqNo);
        return sqlSession.selectOne("Event.getActiveEventByPageSeqNo", map);
    }

    public int getCountByPageSeqNo(User user, Device device, SearchOpt opt, String appType, Long pageSeqNo) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("opt", opt);
        map.put("address", addr);
        map.put("appType", appType);
        map.put("pageSeqNo", pageSeqNo);
        return sqlSession.selectOne("Event.getCountByPageSeqNo", map);
    }

    public List<Event> getEventListByPageSeqNo(User user, Device device, SearchOpt opt, String appType, Long pageSeqNo) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("opt", opt);
            map.put("address", addr);
        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("opt", opt);
            map.put("address", null);
        }
        map.put("appType", appType);
        map.put("pageSeqNo", pageSeqNo);
        return sqlSession.selectList("Event.getEventListByPageSeqNo", map);
    }

    public int getCount(User user, Device device, SearchOpt opt, String appType, Long groupSeqNo, Boolean isToday, Boolean isLotto) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("opt", opt);
        map.put("address", addr);
        map.put("appType", appType);
        map.put("groupSeqNo", groupSeqNo);
        map.put("isToday", isToday);
        map.put("isLotto", isLotto);
        return sqlSession.selectOne("Event.getCount", map);
    }

    public List<Event> getEventList(User user, Device device, SearchOpt opt, String appType, Long groupSeqNo, Boolean isToday, Boolean isLotto) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("address", addr);
        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("address", null);
        }

        map.put("opt", opt);
        if (opt.getFilter() != null && opt.getFilter().size() > 0) {
            map.put("primaryType", opt.getFilter().get(0));
        }

        map.put("appType", appType);
        map.put("groupSeqNo", groupSeqNo);
        map.put("isToday", isToday);
        map.put("isLotto", isLotto);
        return sqlSession.selectList("Event.getEventList", map);
    }

    public List<Event> getList(User user, Device device, SearchOpt opt, String appType, Long groupSeqNo) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("opt", opt);
            map.put("address", addr);
        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("opt", opt);
            map.put("address", null);
        }
        map.put("appType", appType);
        map.put("groupSeqNo", groupSeqNo);
        return sqlSession.selectList("Event.getList", map);
    }

    public List<EventDetail> getEventDetailList(Long eventSeqNo) {
        ParamMap map = new ParamMap();
        map.put("eventSeqNo", eventSeqNo);
        return sqlSession.selectList("Event.getEventDetailList", map);
    }

    public List<EventDetailImage> getEventDetailImageList(Long eventSeqNo) {
        ParamMap map = new ParamMap();
        map.put("eventSeqNo", eventSeqNo);
        return sqlSession.selectList("Event.getEventDetailImageList", map);
    }

    public List<EventDetail> getEventDetailItemList(Long eventSeqNo, Long eventDetailSeqNo) {
        ParamMap map = new ParamMap();
        map.put("eventSeqNo", eventSeqNo);
        map.put("eventDetailSeqNo", eventDetailSeqNo);
        return sqlSession.selectList("Event.getEventDetailItemList", map);
    }

    public int getWinAnnouncedCount(User user, Device device, SearchOpt opt) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("opt", opt);
        map.put("addr", addr);
        return sqlSession.selectOne("Event.getWinAnnouncedCount", map);
    }

    public List<Event> getWinAnnouncedList(User user, Device device, SearchOpt opt) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("opt", opt);
        map.put("addr", addr);
        return sqlSession.selectList("Event.getWinAnnouncedList", map);
    }

    public List<EventGroup> getGroupAll() {
        return sqlSession.selectList("Event.getGroupAll");
    }

    public int getCountByGroup(User user, Device device, EventGroup group, SearchOpt opt, String appType) {
        ParamMap map = new ParamMap();

        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("group", group);
            map.put("opt", opt);
        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("group", group);
            map.put("opt", opt);
        }
        map.put("appType", appType);
        return sqlSession.selectOne("Event.getCountByGroup", map);
    }

    public List<Event> getEventListByGroup(User user, Device device, EventGroup group, SearchOpt opt, String appType) {
        ParamMap map = new ParamMap();

        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("group", group);
            map.put("opt", opt);

        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("group", group);
            map.put("opt", opt);
        }
        map.put("appType", appType);
        return sqlSession.selectList("Event.getEventListByGroup", map);
    }

    public List<Event> getListByGroup(User user, Device device, EventGroup group, SearchOpt opt, String appType) {
        ParamMap map = new ParamMap();

        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("group", group);
            map.put("opt", opt);

        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("group", group);
            map.put("opt", opt);
        }
        map.put("appType", appType);
        return sqlSession.selectList("Event.getListByGroup", map);
    }

    public int getAnnounceCountByGroup(User user, Device device, EventGroup group, SearchOpt opt) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("opt", opt);
        map.put("group", group);
        map.put("address", addr);
        return sqlSession.selectOne("Event.getAnnounceCountByGroup", map);
    }

    public List<Event> getAnnounceListByGroup(User user, Device device, EventGroup group, SearchOpt opt) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("device", device);
        map.put("group", group);
        map.put("opt", opt);
        map.put("address", addr);
        return sqlSession.selectList("Event.getAnnounceListByGroup", map);
    }

    public List<EventBanner> getEventBannerAll(Event event) {
        return sqlSession.selectList("Event.getEventBannerAll", event);
    }

    public List<EventGift> getEventGiftAll(Event event) {
        return sqlSession.selectList("Event.getEventGiftAll", event);
    }

    public EventGift getEventGiftSeqNo(Long giftSeqNo) {
        ParamMap map = new ParamMap();
        map.put("giftSeqNo", giftSeqNo);
        return sqlSession.selectOne("Event.getEventGiftBySeqNo", map);
    }

    public int existsEventJoin(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.existsEventJoin", map);
    }

    public int getEventJoinCount(Event event) {
        return sqlSession.selectOne("Event.getEventJoinCount", event);
    }

    public int getMyBuyJoinCount(User user, Event event) {

        event = get(event);

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.getMyBuyJoinCount", map);
    }

    public int getMyBuyJoinCountAndBuyType(User user, Event event) {

        event = get(event);

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);

        if (!AppUtil.isEmpty(event.getBuyType())) {
            Date now = DateUtil.getCurrentDate();
            Date startDate = null;
            Date endDate = null;
            if (event.getBuyType().equals("daily")) {
                startDate = DateUtil.getDateAdd(now, DateUtil.DATE, -1);
            } else if (event.getBuyType().equals("weekly")) {
                startDate = DateUtil.getDateAdd(now, DateUtil.DATE, -7);
            } else if (event.getBuyType().equals("monthly")) {
                startDate = DateUtil.getDateAdd(now, DateUtil.MONTH, -1);
            }

            if (startDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startDate = cal.getTime();

                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                endDate = cal.getTime();

                map.put("start", startDate);
                map.put("end", endDate);
            }
        }


        return sqlSession.selectOne("Event.getMyBuyJoinCountAndBuyType", map);
    }

    public List<EventJoin> getEventJoinList(Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getEventJoinList", map);
    }

    public List<EventJoin> getEventJoinAll(Event event) {
        return sqlSession.selectList("Event.getEventJoinAll", event);
    }

    public List<User> getEventJoinUserAll(Event event) {
        return sqlSession.selectList("Event.getEventJoinUserAll", event);
    }

    public int existsEventWin(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.existsEventWin", map);
    }

    public int existsEventCampaignWin(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.existsEventCampaignWin", map);
    }

    public int getEventWinCount(Event event) {
        return sqlSession.selectOne("Event.getEventWinCount", event);
    }

    public List<EventWin> getEventWinList(User user, Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getEventWinList", map);
    }

    public int getEventWinCountByGiftSeqNo(Long giftSeqNo) {
        ParamMap map = new ParamMap();
        map.put("giftSeqNo", giftSeqNo);
        return sqlSession.selectOne("Event.getEventWinCountByGiftSeqNo", map);
    }

    public List<EventWin> getEventWinListByGiftSeqNo(Long giftSeqNo, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("giftSeqNo", giftSeqNo);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getEventWinListByGiftSeqNo", map);
    }

    public EventWin getEventWinBySeqNo(User user, Long eventSeqNo, Integer seqNo) {
        ParamMap map = new ParamMap();
        map.put("eventSeqNo", eventSeqNo);
        map.put("seqNo", seqNo);
        map.put("user", user);
        return sqlSession.selectOne("Event.getEventWinBySeqNo", map);
    }

    public void expiredEventWin() {
        ParamMap map = new ParamMap();
        map.put("status", GiftStatus.EXPIRED.getStatus());
        sqlSession.update("Event.updateExpiredEventWin", map);
    }

    public int updateUseGift(Long eventSeqNo, Integer seqNo) {
        ParamMap map = new ParamMap();
        map.put("eventSeqNo", eventSeqNo);
        map.put("seqNo", seqNo);

        int effected = sqlSession.update("Event.updateUseGift", map);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public int updateGiftStatus(Long eventSeqNo, Integer seqNo, Integer status) {
        ParamMap map = new ParamMap();
        map.put("eventSeqNo", eventSeqNo);
        map.put("seqNo", seqNo);
        map.put("status", status);

        int effected = sqlSession.update("Event.updateGiftStatus", map);
        return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
    }

    public int getWinCount(SearchOpt opt) {
        return sqlSession.selectOne("Event.getWinCount", opt);
    }

    public List<EventWin> getWinList(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getWinList", map);
    }

    public List<EventWin> getWinListOnly(SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("opt", opt);
        return sqlSession.selectList("Event.getWinListOnly", map);
    }

    public int getMyWinCountOnlyPresent(User user) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        return sqlSession.selectOne("Event.getMyWinCountOnlyPresent", map);
    }

    public List<EventWin> getMyWinListOnlyPresent(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);

        List<EventWin> list = sqlSession.selectList("Event.getMyWinListOnlyPresent", map);
        for (EventWin eventWin : list) {
            ParamMap countMap = new ParamMap();
            countMap.put("eventSeqNo", eventWin.getEvent().getNo());
            countMap.put("eventWinId", eventWin.getId());
            Integer replyCount = sqlSession.selectOne("Event.getEventReplyCount", countMap);
            eventWin.setReplyCount(replyCount);
        }

        return list;
//        return sqlSession.selectList("Event.getMyWinListOnlyPresent", map);
    }

    public int getWinCountOnlyPresentByMemberSeqNo(Long memberSeqNo) {
        return sqlSession.selectOne("Event.getWinCountOnlyPresentByMemberSeqNo", memberSeqNo);
    }

    public int getWinCountOnlyPresentToday() {
        return sqlSession.selectOne("Event.getWinCountOnlyPresentToday");
    }

    public int getWinCountOnlyPresent() {
        return sqlSession.selectOne("Event.getWinCountOnlyPresent");
    }

    public List<EventWin> getWinListOnlyPresent(User user, SearchOpt opt) {
        opt.setSz(10);
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);

        List<EventWin> list = sqlSession.selectList("Event.getWinListOnlyPresent", map);
        for (EventWin eventWin : list) {
            ParamMap countMap = new ParamMap();
            countMap.put("eventSeqNo", eventWin.getEvent().getNo());
            countMap.put("eventWinId", eventWin.getId());
            Integer replyCount = sqlSession.selectOne("Event.getEventReplyCount", countMap);
            eventWin.setReplyCount(replyCount);
        }

        return list;
//        return sqlSession.selectList("Event.getWinListOnlyPresent", map);
    }

    public List<EventWin> getEventWinAll(Event event) {
        return sqlSession.selectList("Event.getEventWinAll", event);
    }

    public int getEventImpressionCount(Event event) {
        return sqlSession.selectOne("Event.getEventImpressionCount", event);
    }

    public List<EventWin> getEventImpressionList(User user, Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getEventImpressionList", map);
    }

    public int getImpressionCount(SearchOpt opt) {
        return sqlSession.selectOne("Event.getImpressionCount", opt);
    }

    public List<EventWin> getImpressionList(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getImpressionList", map);
    }

    public List<EventJoin> getJoinAllByUser(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectList("Event.getEventJoinAllByUser", map);
    }

    public List<EventWin> getEventWinAllByUser(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectList("Event.getEventWinAllByUser", map);
    }

    public int getWinCountByUser(User user) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        return sqlSession.selectOne("Event.getWinCountByUser", map);
    }

    public int getNewWinCountByUser(User user) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        return sqlSession.selectOne("Event.getNewWinCountByUser", map);
    }

    public List<EventWin> getWinListByUser(User user, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getWinListByUser", map);
    }

    public List<Event> getAllForLotto(Integer lottoTimes) {
        return sqlSession.selectList("Event.getAllForLotto", lottoTimes);
    }

    public List<Event> getAllForLot() {
        return sqlSession.selectList("Event.getAllForLot");
    }

    public List<Event> getLottoForConclude() {
        return sqlSession.selectList("Event.getLottoForConclude");
    }

    public List<Event> getAllForLotWhen(String code) {
        return sqlSession.selectList("Event.getAllForLotWhen", code);
    }

    public EventGift getTotalGiftCount(Event event) {
        return sqlSession.selectOne("Event.getTotalGiftCount", event);
    }

    public List<Event> getAllForCancel() {
        return sqlSession.selectList("Event.getAllForCancel");
    }

    public List<Event> getAllForResultPush() {
        return sqlSession.selectList("Event.getAllForResultPush");
    }

    public int getArticleAdvertiseCount(Event event) {
        return sqlSession.selectOne("Event.getArticleAdvertiseCount", event);
    }

    public List<ArticleAdvertise> getArticleAdvertiseList(User user, Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getArticleAdvertiseList", map);
    }

    public int getArticleAdvertiseCountForEvent(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.getArticleAdvertiseCountForEvent", map);
    }

    public List<ArticleAdvertise> getArticleAdvertiseListForEvent(User user, Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getArticleAdvertiseListForEvent", map);
    }

    public int getCouponTemplateAdvertiseCount(Event event) {
        return sqlSession.selectOne("Event.getCouponTemplateAdvertiseCount", event);
    }

    public List<CouponTemplateAdvertise> getCouponTemplateAdvertiseList(User user, Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectOne("Event.getCouponTemplateAdvertiseList", map);
    }

    public int getCouponTemplateAdvertiseCountForEvent(User user, Event event) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        return sqlSession.selectOne("Event.getCouponTemplateAdvertiseCountForEvent", map);
    }

    public List<CouponTemplateAdvertise> getCouponTemplateAdvertiseListForEvent(User user, Event event, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("event", event);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getCouponTemplateAdvertiseListForEvent", map);
    }

    public Long getLottoWinnerCount(Integer lottoTimes, String primaryType) {
        ParamMap map = new ParamMap();
        map.put("lottoTimes", lottoTimes);
        map.put("primaryType", primaryType);
        return sqlSession.selectOne("Event.getLottoWinnerCount", map);
    }


    public Long getLottoUserJoinCount(User user, Integer lottoTimes, String primaryType) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("lottoTimes", lottoTimes);
        map.put("primaryType", primaryType);
        return sqlSession.selectOne("Event.getLottoUserJoinCount", map);
    }

    public List<Event> getLottoUserJoinList(User user, Integer lottoTimes, String primaryType) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("lottoTimes", lottoTimes);
        map.put("primaryType", primaryType);
        return sqlSession.selectList("Event.getLottoUserJoinList", map);
    }

    public List<EventWin> getLottoWinnerUser(User user, Integer lottoTimes, String primaryType) {
        ParamMap map = new ParamMap();
        logger.info("EventService.getLottoWinnerUser() : user.no : " + user.getNo());
        map.put("user", user);
        map.put("lottoTimes", lottoTimes);
        map.put("primaryType", primaryType);
        return sqlSession.selectList("Event.getLottoWinnerUser", map);
    }

    public List<EventWin> getLottoWinner(Integer lottoTimes, String primaryType, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("lottoTimes", lottoTimes);
        map.put("primaryType", primaryType);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getLottoWinner", map);
    }


    public Long getLottoTicketHistoryCount(User user, SearchOpt opt) {
        if (opt.getAlign() == null || "new".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        } else if ("old".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("ASC");
        } else if ("amount".equals(opt.getAlign())) {
            opt.setOrderColumn("amount");
            opt.setOrderAsc("DESC");
        } else {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        }
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("Event.getLottoTicketHistoryCount", map);
    }

    public List<BolHistory> getLottoTicketHistory(User user, SearchOpt opt) {
        if (opt.getAlign() == null || "new".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        } else if ("old".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("ASC");
        } else if ("amount".equals(opt.getAlign())) {
            opt.setOrderColumn("amount");
            opt.setOrderAsc("DESC");
        } else {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        }
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getLottoTicketHistory", map);
    }

    public Integer getBatchWinCountByDate(User user, EventWin win, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("win", win);
        map.put("opt", opt);
        return sqlSession.selectOne("Event.getBatchWinCountByDate", map);
    }

    public List<EventWin> getBatchWinListByDate(User user, EventWin win, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("win", win);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getBatchWinListByDate", map);
    }

    public List<EventWin> getBatchGiftListByDate(User user, EventWin win, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("win", win);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getBatchGiftListByDate", map);
    }


    public Integer getBatchAnnounceDateCount() {
        return sqlSession.selectOne("Event.getBatchAnnounceDateCount");
    }

    public List<String> getBatchAnnounceDateList(SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("opt", opt);
        return sqlSession.selectList("Event.getBatchAnnounceDateList", map);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void clearCancelAll() {
        // 이벤트 기간이 종료된 것의 상태를 기간 만료로 변경한다.
        // 기존 상태는 활성화/비활성화/당첨자발표/당첨완료 인 것들을 대상으로 한다.

        try {
            List<Event> eventList = this.getAllForCancel();
            for (Event event : eventList) {
                if (event.getPrimaryType().equals("lotto") || event.getPrimaryType().equals("lottoPlaybol")) {
                    continue;
                }
                List<EventJoin> joinList = this.getEventJoinAll(event);
                Map<User, Float> userMap = new HashMap<User, Float>();

                for (EventJoin join : joinList) {
                    if (!userMap.containsKey(join.getUser()))
                        userMap.put(join.getUser(), 0f);

                    Float amount = userMap.get(join.getUser());
                    amount += (event.getReward() * -1);
                    userMap.put(join.getUser(), amount);
                }


                for (Map.Entry<User, Float> entry : userMap.entrySet()) {
                    //참여로 소진된 금액 환불
                    User user = entry.getKey();
                    Float amount = entry.getValue();
                    if (amount != null && amount > 0) {
                        BolHistory bh = new BolHistory();
                        bh.setTarget(event);
                        bh.setTargetType("event");
                        bh.setSubject("이벤트 환불");
                        bh.setProperties(new HashMap<String, Object>());
                        bh.setSecondaryType("refundJoinEvent");
                        bh.setAmount(amount);
                        bh.getProperties().put("환불 유형", event.getTitle() + " 환불");
                        if (event.getAppType().equals("luckyball")) {
                            bh.getProperties().put("지급처", "캐시픽 운영팀");
                        } else {
                            bh.getProperties().put("지급처", "오리마켓 운영팀");
                        }
                        cashBolSvc.increaseBol(user, bh);

                        try {
                            MsgOnly msg = new MsgOnly();
                            msg.setInput("system");
                            msg.setStatus("ready");
                            msg.setType("push");
                            msg.setMoveType1("inner");
                            msg.setMoveType2("bolDetail");
                            msg.setMoveTarget(bh);
                            msg.setPushCase(Const.USER_PUSH_EVENT);
                            msg.setSubject("플레이 이벤트 취소");
                            msg.setContents(event.getTitle() + " 이 취소 되었습니다.");
                            msg.setAppType(Const.APP_TYPE_LUCKYBOL);
                            queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_LUCKYBOL);
                        } catch (ResultCodeException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                event.setStatus("cancel");
                this.updateStatus(event);
            }
        } catch (Exception e) {
            logger.error("clearCancelAll : " + AppUtil.excetionToString(e));
        }
    }

    public Long getEventTicketHistoryCount(User user, SearchOpt opt) {
        if (opt.getAlign() == null || "new".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        } else if ("old".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("ASC");
        } else if ("amount".equals(opt.getAlign())) {
            opt.setOrderColumn("amount");
            opt.setOrderAsc("DESC");
        } else {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        }
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("Event.getEventTicketHistoryCount", map);
    }

    public List<BolHistory> getEventTicketHistory(User user, SearchOpt opt) {
        if (opt.getAlign() == null || "new".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        } else if ("old".equals(opt.getAlign())) {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("ASC");
        } else if ("amount".equals(opt.getAlign())) {
            opt.setOrderColumn("amount");
            opt.setOrderAsc("DESC");
        } else {
            opt.setOrderColumn("reg_datetime");
            opt.setOrderAsc("DESC");
        }
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Event.getEventTicketHistory", map);
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void payPoint(User user, Long eventSeqNo) throws ResultCodeException {

        try {
            Event event = new Event();
            event.setNo(eventSeqNo);
            event = get(event);
            if (event != null) {
                BolHistory bolHistory = cashBolSvc.getBolHistoryByTargetAndMemberSeqNo(user.getNo(), event.getNo(), "cpa");
                if (bolHistory == null) {
                    BolHistory bh = new BolHistory();
                    bh.setTarget(event);
                    bh.setTargetType("cpa");
                    bh.setSubject("이벤트 참여");
                    bh.setProperties(new HashMap<String, Object>());
                    bh.setSecondaryType("joinEvent");
                    bh.setAmount(300f);
                    bh.getProperties().put("적립 유형", event.getTitle() + " 참여");
                    if (event.getAppType().equals("luckyball")) {
                        bh.getProperties().put("지급처", "캐시픽 운영팀");
                    } else {
                        bh.getProperties().put("지급처", "오리마켓 운영팀");
                    }
                    cashBolSvc.increaseBol(user, bh);

                    MsgOnly msg = new MsgOnly();
                    msg.setInput("system");
                    msg.setStatus("ready");
                    msg.setType("push");
                    msg.setMoveType1("inner");
                    msg.setMoveType2("bolDetail");
                    msg.setMoveTarget(bh);
                    msg.setPushCase(Const.USER_PUSH_EVENT);
                    msg.setSubject("이벤트 참여 적립");
                    msg.setContents("300포인트가 적립되었습니다.");
                    msg.setAppType(Const.APP_TYPE_LUCKYBOL);
                    queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_LUCKYBOL);
                }

            }


        } catch (ResultCodeException ex) {
            ex.printStackTrace();
            throw new UnknownException();
        }

    }

    public List<Event> getMainBannerLottoList(User user, Device device, String appType) {
        List<String> addr = getSearchAddress(user);
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("user", user);
            map.put("device", device);
            map.put("address", addr);
        } else {
            map.put("user", user);
            map.put("device", null);
            map.put("address", null);
        }
        map.put("appType", appType);
        return sqlSession.selectList("Event.getMainBannerLottoList", map);
    }

    public Integer getLottoHistoryCount(Device device, String appType) {
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("device", device);
        } else {
            map.put("device", null);
        }
        map.put("appType", appType);
        return sqlSession.selectOne("Event.getLottoHistoryCount", map);
    }

    public List<Event> getLottoHistoryList(User user, Device device, SearchOpt opt, String appType) {
        ParamMap map = new ParamMap();
        if (device != null) {
            map.put("user", user);
            map.put("device", device);
        } else {
            map.put("user", user);
            map.put("device", null);
        }
        map.put("opt", opt);
        map.put("appType", appType);
        return sqlSession.selectList("Event.getLottoHistoryList", map);
    }
}
