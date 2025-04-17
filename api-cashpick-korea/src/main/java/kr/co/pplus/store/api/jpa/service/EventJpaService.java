package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Cancel;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Token;
import kr.co.pplus.store.api.jpa.model.bootpay.response.BootPayCancelResponse;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResToken;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayRequest;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayResponse;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.FTLinkPayApi;
import kr.co.pplus.store.util.Filtering;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class EventJpaService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(EventJpaService.class);

    @Autowired
    EventReviewRepository eventReviewRepository;

    @Autowired
    EventReviewImageRepository eventReviewImageRepository;


    @Autowired
    EventReviewDetailRepository eventReviewDetailRepository;

    @Autowired
    EventReplyRepository eventReplyRepository;

    @Autowired
    EventReplyOnlyRepository eventReplyOnlyRepository;

    @Autowired
    EventGiftRepository eventGiftRepository;

    @Autowired
    EventJoinRepository eventJoinRepository;

    @Autowired
    EventWinRepository eventWinRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BolService bolService;

    @Autowired
    EventBuyRepository eventBuyRepository;

    @Autowired
    EventPolicyRepository eventPolicyRepository;

    @Autowired
    PointService pointService;

    @Autowired
    LpngCallbackRepository lpngCallbackRepository;

    @Autowired
    LpngCallbackResultRepository lpngCallbackResultRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EventJoinWithLottoNumberRepository eventJoinWithLottoNumberRepository;

    @Autowired
    LottoWinNumberRepository lottoWinNumberRepository;

    @Autowired
    LottoMemberCountRepository lottoMemberCountRepository;

    @Autowired
    EventWinWithJoinRepository eventWinWithJoinRepository;

    @Autowired
    MsgProducer producer;

    @Value("${STORE.BOOTPAY.CASH_APP_ID}")
    String CASH_APP_ID = "";

    @Value("${STORE.BOOTPAY.CASH_PRIVATE_KEY}")
    String CASH_PRIVATE_KEY = "";

    @Value("${STORE.TYPE}")
    String storeType = "STAGE";

    @Value("${STORE.DANAL.CPID}")
    String CPID = "9810030929";

    private final String BASE_URL = "https://api.bootpay.co.kr/";
    private final String URL_ACCESS_TOKEN = BASE_URL + "request/token";
    private final String URL_VERIFY = BASE_URL + "receipt";
    private final String URL_CANCEL = BASE_URL + "v2/cancel";


    public List<EventPolicy> getEventPolicyList(Long pageSeqNo, Long eventSeqNo){
        return eventPolicyRepository.findAllByPageSeqNoAndEventSeqNo(pageSeqNo, eventSeqNo);
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertEventReview(EventReview eventReview) throws ResultCodeException {

        try {

            eventReview.setReview(Filtering.filter(eventReview.getReview()));

            List<EventReviewImage> eventReviewImageList = eventReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            eventReview.setSeqNo(null);
            eventReview.setRegDatetime(dateStr);
            eventReview.setModDatetime(dateStr);
            eventReview.setStatus(1);
            eventReview = eventReviewRepository.saveAndFlush(eventReview);

            if (eventReviewImageList != null && eventReviewImageList.size() > 0) {

                for (EventReviewImage eventReviewImage : eventReviewImageList) {
                    eventReviewImage.setEventReviewSeqNo(eventReview.getSeqNo());
                    eventReviewImage.setType("thumbnail");
                    eventReviewImageRepository.save(eventReviewImage);
                }
            }

            BolHistory bolHistory = new BolHistory();
            bolHistory.setAmount(10f);
            bolHistory.setMemberSeqNo(eventReview.getMemberSeqNo());
            bolHistory.setSubject("이벤트 당첨후기 작성");
            bolHistory.setPrimaryType("increase");
            bolHistory.setSecondaryType("buy");
            bolHistory.setTargetType("member");
            bolHistory.setTargetSeqNo(eventReview.getMemberSeqNo());
            bolHistory.setHistoryProp(new HashMap<String, Object>());
            bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
            bolHistory.getHistoryProp().put("적립유형", "이벤트 당첨후기 작성");

            bolService.increaseBol(eventReview.getMemberSeqNo(), bolHistory);

            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateEventReview(EventReview eventReview) throws ResultCodeException {

        try {

            if (eventReview.getSeqNo() == null) {
                throw new InvalidEventRevewException("/eventReview[PUT]", "eventReview.seq_no cannot be null");
            }

            eventReview.setReview(Filtering.filter(eventReview.getReview()));

            List<EventReviewImage> eventReviewImageList = eventReview.getImageList();
            String dateStr = AppUtil.localDatetimeNowString();
            eventReview.setModDatetime(dateStr);
            eventReview.setStatus(1);
            eventReview = eventReviewRepository.saveAndFlush(eventReview);

            eventReviewImageRepository.deleteAllByEventReviewSeqNo(eventReview.getSeqNo());

            if (eventReviewImageList != null && eventReviewImageList.size() > 0) {

                for (EventReviewImage eventReviewImage : eventReviewImageList) {
                    eventReviewImage.setEventReviewSeqNo(eventReview.getSeqNo());
                    eventReviewImage.setType("thumbnail");
                    eventReviewImageRepository.save(eventReviewImage);
                }
            }
            return Const.E_SUCCESS;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    public Page<EventReviewDetail> getEventReviewDetailList(User user, Pageable pageable){
        if(user == null){
            return eventReviewDetailRepository.findAllBy(null, pageable);
        }
        return eventReviewDetailRepository.findAllBy(user.getNo(), pageable);
    }

    public Page<EventReviewDetail> getMyEventReviewDetailList(User user, Pageable pageable){
        return eventReviewDetailRepository.findAllMy(user.getNo(), pageable);
    }

    public EventReviewDetail getEventReviewDetail(User user, Long seqNo){
        if(user == null){
            return eventReviewDetailRepository.findBySeqNo(seqNo, null);
        }
        return eventReviewDetailRepository.findBySeqNo(seqNo, user.getNo());
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertEventReply(EventReplyOnly eventReply) throws ResultCodeException {

        eventReply.setReply(Filtering.filter(eventReply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        eventReply.setSeqNo(null);
        eventReply.setRegDatetime(dateStr);
        eventReply.setModDatetime(dateStr);
        eventReply.setStatus(1);
        eventReply = eventReplyOnlyRepository.saveAndFlush(eventReply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateEventReply(EventReplyOnly eventReply) throws ResultCodeException {

        eventReply.setReply(Filtering.filter(eventReply.getReply()));

        String dateStr = AppUtil.localDatetimeNowString();
        eventReply.setModDatetime(dateStr);
        eventReply.setStatus(1);
        eventReply = eventReplyOnlyRepository.saveAndFlush(eventReply);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteEventReply(Long seqNo) throws ResultCodeException {
        EventReply eventReply = eventReplyRepository.findBySeqNo(seqNo);
        eventReply.setStatus(-999);
        eventReplyRepository.save(eventReply);
        return Const.E_SUCCESS;
    }

    public Page<EventReply> getEventReplyListByEventReviewSeqNo(Long eventReviewSeqNo, Pageable pageable){
        return eventReplyRepository.findAllByEventReviewSeqNoAndStatusOrderBySeqNoAsc(eventReviewSeqNo, 1, pageable);
    }

    public Page<EventReply> getEventReplyListByEventSeqNoAndEventWinSeqNo(Long eventSeqNo, Integer eventWinSeqNo, Pageable pageable){
        return eventReplyRepository.findAllByEventSeqNoAndEventWinSeqNoAndStatusOrderBySeqNoAsc(eventSeqNo, eventWinSeqNo, 1, pageable);
    }

    public Page<EventReply> getEventReplyListByEventWinId(Long eventWinId, Pageable pageable){
        return eventReplyRepository.findAllByEventWinIdAndStatusOrderBySeqNoAsc(eventWinId, 1, pageable);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateEventWinAnnounceDateTime(Long eventSeqNo){
        eventRepository.updateWinAnnounceDateTime(eventSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateJoinTypeAndJoinTermByCode(String code, String joinType, Integer joinTerm){
        eventRepository.updateJoinTypeAndJoinTermByCode(code, joinType, joinTerm);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin lot(User user, EventJpa event) throws ResultCodeException {

        logger.debug("join count : " + event.getJoinCount());
        EventWin eventWin = null;

        if (event.getGift()) {
            List<EventGiftJpa> giftList = eventGiftRepository.findAllByEventSeqNo(event.getSeqNo());

            boolean remain = false;
            boolean allzero = true;
            boolean noorder = true;
            int remainCount = 0;
            for (EventGiftJpa gift : giftList) {
                remainCount += gift.getRemainCount();
                if (allzero == true && gift.getLotPercent() > 0)
                    allzero = false;

                if (noorder == true && !AppUtil.isEmpty(gift.getWinOrder()))
                    noorder = false;

                if (remain == false && gift.getRemainCount() > 0)
                    remain = true;

            }

            if (allzero == true && noorder == true){
                return eventWin;
            }
//                throw new NotPossibleValueException("not possible lot", "0 percent");

            if ("immediately".equals(event.getWinAnnounceType()) && noorder == false && remain == true) {
                //순번에 의해 당첨되어야 하는지 일단 검사한다.
                for (EventGiftJpa gift : giftList) {
                    boolean winner = false;
                    if (!AppUtil.isEmpty(gift.getWinOrder())) {
                        String[] arr = gift.getWinOrder().split("\\s*\\,\\s*");
                        if (arr != null && arr.length > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                String orderStr = arr[i];
                                if (!AppUtil.isEmpty(orderStr)) {
                                    long seq = Long.parseLong(orderStr);
                                    if (seq == event.getJoinCount() && gift.getRemainCount() > 0) {
                                        winner = true;

                                        StringBuilder sb = new StringBuilder();
                                        for (int j = 0; j < arr.length; j++) {

                                            if(orderStr.equals(arr[j])){
                                                continue;
                                            }

                                            sb.append(arr[j]);
                                            if(j < arr.length-1){
                                                sb.append(",");
                                            }
                                        }
                                        gift.setWinOrder(sb.toString());
                                        gift = eventGiftRepository.saveAndFlush(gift);
                                        break;
                                    }
                                }
                            }
                        }
                    }


                    if (winner) {
                        eventWin = win(user, event, gift);
                        if (eventWin != null) {
                            if (--remainCount == 0)
                                remain = false;

                            break;
                        }
                    }
                }
            }

            if (eventWin == null && allzero == false && remain == true) {
                for (EventGiftJpa gift : giftList) {
                    if (lot(gift)) {
                        eventWin = win(user, event, gift);
                        if (eventWin != null) {
                            break;
                        }
                    }
                }
            }

            for (EventGiftJpa gift : giftList) {
                if (gift.getRemainCount() > 0) {
                    remain = true;
                    break;
                }
            }

            if (remain == false) {
                if ("immediately".equals(event.getWinAnnounceType())) {
                    event.setStatus("announce");
                    event.setPriority(-1);
                    updateEventWinAnnounceDateTime(event.getSeqNo());
                    eventRepository.updateStatus(event.getSeqNo(), event.getStatus());
                    eventRepository.updatePriority(event.getSeqNo(), event.getPriority());
                    if(event.getAutoRegist()){
                        producer.push(event);
                    }
                }
            }
        }

        return eventWin;

    }

    private boolean lot(EventGiftJpa gift) {
        if (gift.getRemainCount() > 0) {
            return StoreUtil.lots(gift.getLotPercent());
        }
        return false;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin win(User user, EventJpa event, EventGiftJpa gift) {
        String dateStr = AppUtil.localDatetimeNowString();

        Integer maxSeqNo = eventWinRepository.findMaxSeqNo(event.getSeqNo());
        if(maxSeqNo == null){
            maxSeqNo = 0;
        }
        EventWin win = new EventWin();
        win.setSeqNo(maxSeqNo+1);
        win.setMemberSeqNo(user.getNo());
        win.setEventSeqNo(event.getSeqNo());
        win.setGiftSeqNo(gift.getSeqNo());
        win.setStatus("pending");
        win.setOpenStatus(true);
        win.setGiftStatus(0);
        win.setWinDatetime(dateStr);
        win.setIsLotto(false);
        win = eventWinRepository.saveAndFlush(win);

        gift.setEventSeqNo(event.getSeqNo());
        gift.setRemainCount(gift.getRemainCount()-1);
        gift = eventGiftRepository.saveAndFlush(gift);

        event.setWinnerCount(event.getWinnerCount()+1);
        event = eventRepository.saveAndFlush(event);

        win.setEventGift(gift);
        return win;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, Object> lot(EventJpa event) throws ResultCodeException {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        List<EventWin> winList = null;
        boolean c = false;
        Integer totalCount = eventGiftRepository.sumTotalCount(event.getSeqNo());
        Integer remainCount = eventGiftRepository.sumRemainCount(event.getSeqNo());
        int winCount = 0;

        if (event.getPrimaryType().equals("number") || event.getPrimaryType().startsWith("lotto")) {
            totalCount = 1;
            remainCount = 1;
        }

        if (totalCount > 0 && remainCount > 0) {
            winList = new ArrayList<EventWin>();
            List<EventJoinJpa> joinList = eventJoinRepository.findAllByEventSeqNo(event.getSeqNo());
            List<EventJoinJpa> copyList = new ArrayList<>();
            copyList.addAll(joinList);

            do {
                Collections.shuffle(joinList);

                for (int i = 0; i < joinList.size(); i++) {
                    EventJoinJpa join = joinList.get(i);

                    Boolean exists = eventWinRepository.existsByEventSeqNoAndMemberSeqNo(event.getSeqNo(), join.getMemberSeqNo());
                    if (exists) {
                        joinList.remove(i);
                        logger.info("EventJpaService.lot() : joinList.size()" + joinList.size());
                    } else {

                        EventWin win = winRandomGift(join.getMemberSeqNo(), event);
                        logger.info("EventService.lot() : EventWin.win " + win);
                        if (win != null) {
                            win.setAmount(null);
                            winList.add(win);
                            joinList.remove(i);
                            remainCount--;
                            logger.info("EventJpaService.lot() : remainCount : " + remainCount);

                        }
                    }

                    if (remainCount == 0)
                        break;
                }

                //모든 사람이 당첨이 되었던지, 전체 당첨자 수를 채운 경우에는 loop를 벗어난다.
                c = (joinList.size() == 0 || remainCount == 0) ? false : true;
            } while (c);

            resultMap.put("joinList", copyList);
            resultMap.put("winList", winList);
        }

        return resultMap;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin winRandomGift(Long memberSeqNo, EventJpa event) {
        String dateStr = AppUtil.localDatetimeNowString();
        EventGiftJpa gift = getRandomRemainGift(event);
        if (gift != null
                && gift.getRemainCount() != null
                && gift.getRemainCount() > 0) {


            eventGiftRepository.updateDecreaseRemainCount(event.getSeqNo(), gift.getSeqNo());

            Integer maxSeqNo = eventWinRepository.findMaxSeqNo(event.getSeqNo());
            if(maxSeqNo == null){
                maxSeqNo = 0;
            }

            EventWin win = new EventWin();
            win.setSeqNo(maxSeqNo + 1);
            win.setMemberSeqNo(memberSeqNo);
            win.setEventSeqNo(event.getSeqNo());
            win.setGiftSeqNo(gift.getSeqNo());
            win.setStatus("pending");
            win.setOpenStatus(true);
            win.setGiftStatus(0);
            win.setWinDatetime(dateStr);
            win.setIsLotto(false);
            gift.setEventSeqNo(event.getSeqNo());
            win = eventWinRepository.saveAndFlush(win);
            eventRepository.updateIncreaseWinnerCount(event.getSeqNo());
            event.setWinnerCount(event.getWinnerCount() + 1);
            gift.setRemainCount(gift.getRemainCount() - 1);
            win.setEventGift(gift);
            return win;

        }
        return null;
    }

    private EventGiftJpa getRandomRemainGift(EventJpa event) {
        List<EventGiftJpa> giftList = eventGiftRepository.findAllByEventSeqNo(event.getSeqNo());
        if (giftList.size() > 0) {
            if (giftList.size() > 1)
                Collections.shuffle(giftList);

            EventGiftJpa selected = null;
            for (EventGiftJpa gift : giftList) {
                if (gift.getRemainCount() == 0)
                    continue;
                else {
                    selected = gift;
                    selected.setEventSeqNo(event.getSeqNo());
                    break;
                }
            }

            return selected;
        }
        return null;
    }

    public boolean checkBuyJoinPossible(Long memberSeqNo, EventJpa event) throws ResultCodeException {

        if (event.getMaxJoinCount() != 0 && event.getMaxJoinCount() <= event.getJoinCount())
            return false;

        if ("always".equals(event.getJoinType()))
            return true;

        List<EventJoinJpa> joinList = eventJoinRepository.findAllByEventSeqNoAndMemberSeqNoAndIsBuyOrderByJoinDatetimeDesc(event.getSeqNo(), memberSeqNo, true);

        if(event.getBuyType().equals("always") || event.getBuyLimitCount() == null ||  event.getBuyLimitCount() == 0){
            return true;
        }

        Date now = DateUtil.getCurrentDate();
        Date prevDate = null;
        if ("daily".equals(event.getJoinType()))
            prevDate = DateUtil.getDateAdd(now, DateUtil.DATE, -1);
        else if ("weekly".equals(event.getJoinType()))
            prevDate = DateUtil.getDateAdd(now, DateUtil.DATE, -7);
        else if ("monthly".equals(event.getJoinType()))
            prevDate = DateUtil.getDateAdd(now, DateUtil.MONTH, -1);

        if (prevDate == null)
            throw new CommonException(501, "join type", "join type (" + event.getJoinType() + ") not defined.");

        Calendar cal = Calendar.getInstance();
        cal.setTime(prevDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        prevDate = cal.getTime();

        int joinCount = 0;
        for (EventJoinJpa join : joinList) {
            if (join.getJoinDatetime().getTime() > prevDate.getTime())
                joinCount++;
        }
        logger.debug("joinCount ==>" + joinCount);
        //MGK_ADD_LOTTO : limit 체크 안함
        if (joinCount >= event.getBuyLimitCount()){
            return false;
        }
        return true;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public EventWin eventBuy(User user, EventBuy eventBuy) throws ResultCodeException {

        try {

            EventJpa eventJpa = eventRepository.findBySeqNo(eventBuy.getEventSeqNo());
            Integer count = eventBuy.getCount();

            if(!checkBuyJoinPossible(user.getNo(), eventJpa)){
                if (eventBuy.getPayMethod().equals("card")) {
                    Cancel cancel = new Cancel();
                    cancel.receipt_id =  eventBuy.getReceiptId();
                    cancel.cancel_username = "피플러스" ;
                    cancel.cancel_message = "수량 초과" ;
                    cancel.cancel_price = eventBuy.getPgPrice() ;
                    bootPayCancel(cancel);
                }

                throw new AlreadyLimitException("max-join-count", "limited");
            }

            Member member = memberRepository.findBySeqNo(user.getNo());
            if(eventBuy.getBolPrice() > member.getBol()){
                if (eventBuy.getPayMethod().equals("card")) {
                    Cancel cancel = new Cancel();
                    cancel.receipt_id =  eventBuy.getReceiptId();
                    cancel.cancel_username = "피플러스" ;
                    cancel.cancel_message = "럭키볼부족" ;
                    cancel.cancel_price = eventBuy.getPgPrice() ;
                    bootPayCancel(cancel);
                }

                throw new LackCostException("럭키볼이 부족합니다.");
            }

            Integer bolPrice = eventBuy.getBolPrice();

            if(bolPrice != null && bolPrice > 0){
                BolHistory bolHistory = new BolHistory();
                bolHistory.setAmount(bolPrice.floatValue());
                bolHistory.setMemberSeqNo(user.getNo());
                if(eventJpa.getPrimaryType().equals("randomluck")){
                    bolHistory.setSubject("랜덤뽑기");
                }else{
                    bolHistory.setSubject("일상혜택");
                }

                bolHistory.setPrimaryType("decrease");
                bolHistory.setSecondaryType("joinReduceEvent");
                bolHistory.setTargetType("event");
                bolHistory.setTargetSeqNo(eventJpa.getSeqNo());
                bolHistory.setHistoryProp(new HashMap<String, Object>());
                bolHistory.getHistoryProp().put("소진 유형", eventJpa.getTitle() + " 응모");
                bolService.decreaseBol(user.getNo(), bolHistory);
            }

            String dateStr = AppUtil.localDatetimeNowString();
            Date date = AppUtil.localDatetimeNowDate();

            eventBuy.setSeqNo(null);
            eventBuy.setMemberSeqNo(user.getNo());
            eventBuy.setStatus(0);

            if(eventBuy.getPayMethod().equals("point") && eventBuy.getBolPrice() >= eventBuy.getTotalPrice()){

                eventBuy.setStatus(1);
                eventBuy.setPgPrice(0);
                eventBuy.setRegDatetime(dateStr);
                eventBuy.setModDatetime(dateStr);
                eventBuy = eventBuyRepository.saveAndFlush(eventBuy);

            }else if (eventBuy.getPayMethod().equals("card")) {

                String token = getAccessToken();

                if (token == null || token.isEmpty()) {
                    throw new InvalidCashException();
                }

                HttpResponse res = verify(eventBuy.getReceiptId(), token);
                String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");

                if (!data.get("status").getAsString().equals("1")) {
                    throw new Exception(" pay rejected !!!");
                }

                JsonObject paymentData = data.get("payment_data").getAsJsonObject();
                String tid = paymentData.get("tid").getAsString();
                String orderNo = data.get("order_id").getAsString();

                eventBuy.setStatus(data.get("status").getAsInt());
                eventBuy.setPgPrice(data.get("price").getAsInt());
                eventBuy.setCardName(paymentData.get("card_name").getAsString());
                eventBuy.setCardQuota(paymentData.get("card_quota").getAsString());
                eventBuy.setCardAuthNo(paymentData.get("card_auth_no").getAsString());

                String settDate = paymentData.get("p_at").getAsString(); // 2020-11-23 10:35:44

                String email = "";

                // purchaseProduct.set
                // 2020-11-23 10:35:44
                settDate = settDate.replaceAll("-", "");
                settDate = settDate.replaceAll(":", "");
                settDate = settDate.replaceAll(" ", "");

                String setDay = settDate.substring(0,8);
                String setTime = settDate.substring(8);

                LpngCallbackResult lpngCallbackResult = new LpngCallbackResult();
                lpngCallbackResult.setOrderNo(orderNo);
                lpngCallbackResult.setShopCode("200002659");
                lpngCallbackResult.setOrderStatus("12");
                if(eventJpa.getPrimaryType().equals("randomluck")){
                    lpngCallbackResult.setOrderGoodsname("랜덤뽑기");
                }else{
                    lpngCallbackResult.setOrderGoodsname("일상혜택");
                }
                lpngCallbackResult.setOrderReqAmt(eventBuy.getPgPrice().toString());
                lpngCallbackResult.setOrderName(user.getName());
                lpngCallbackResult.setOrderHp(user.getMobile().replace(user.getAppType() + "##", ""));
                lpngCallbackResult.setApprNo(eventBuy.getCardAuthNo());
                lpngCallbackResult.setApprTranNo(tid);
                lpngCallbackResult.setApprDate(setDay);
                lpngCallbackResult.setApprTime(setTime);
                lpngCallbackResult.setCardtxt(eventBuy.getCardName());
                lpngCallbackResult.setReqCardNo(eventBuy.getCardNo());
                lpngCallbackResult.setApprShopCode("");
                lpngCallbackResult.setReqInstallment(eventBuy.getCardQuota());

                lpngCallbackResult = lpngCallbackResultRepository.saveAndFlush(lpngCallbackResult);

                LpngCallback lpngCallback = new LpngCallback();
                lpngCallback.setMemberSeqNo(user.getNo());
                lpngCallback.setOrderId(orderNo);
                lpngCallback.setPgTranId(tid);
                lpngCallback.setName(user.getName());
                lpngCallback.setPrice(eventBuy.getPgPrice());
                lpngCallback.setStatus(true);
                lpngCallback.setApprDate(setDay);
                lpngCallback.setApprTime(setTime);
                lpngCallback.setRegDatetime(dateStr);
                lpngCallback.setProcess(1); // 결제완료
                lpngCallback.setResultSeqNo(lpngCallbackResult.getSeqNo());


                if(eventBuy.getPg().equals("danal")){
                    danalNoti(user, jsonObject, eventJpa.getPrimaryType());
                }

                eventBuy.setRegDatetime(dateStr);
                eventBuy.setModDatetime(dateStr);
                eventBuy = eventBuyRepository.saveAndFlush(eventBuy);
                lpngCallback.setEventBuySeqNo(eventBuy.getSeqNo());
                lpngCallbackRepository.save(lpngCallback);

            }else if (eventBuy.getPayMethod().equals("ftlink")) {
                FTLinkPayRequest ftLinkPayRequest = new FTLinkPayRequest();
                ftLinkPayRequest.setShopcode("200002659");//피플러스 샵코드
                ftLinkPayRequest.setOrder_req_amt(eventBuy.getPgPrice().toString());
                ftLinkPayRequest.setOrder_hp(user.getMobile().replace(user.getAppType() + "##", ""));
                ftLinkPayRequest.setOrder_name(user.getName());
                ftLinkPayRequest.setComp_memno(user.getName());
                if(eventJpa.getPrimaryType().equals("randomluck")){
                    ftLinkPayRequest.setOrder_goodsname("랜덤뽑기");
                }else{
                    ftLinkPayRequest.setOrder_goodsname("일상혜택");
                }
                ftLinkPayRequest.setReq_installment(eventBuy.getInstallment());
                ftLinkPayRequest.setComp_orderno(eventBuy.getOrderId());
                ftLinkPayRequest.setAutokey(eventBuy.getAutoKey());
                ftLinkPayRequest.setReq_cardcode(eventBuy.getCardCode());
                ftLinkPayRequest.setManual_used("N");
                ftLinkPayRequest.setLoginId("test08");//피플러스 id
                ftLinkPayRequest.setServerType("");
                ftLinkPayRequest.setRoomId("");
                ftLinkPayRequest.setReqdephold("N");
                if (storeType.equals("PROD")) {
                    ftLinkPayRequest.setISTEST("USE");
                } else {
                    ftLinkPayRequest.setISTEST("TEST");
                }
                FTLinkPayResponse res = FTLinkPayApi.payRequest(ftLinkPayRequest);


                LpngCallbackResult callbackResult = new LpngCallbackResult();
                callbackResult.setErrorMsg(res.getErrMessage());
                callbackResult.setShopCode(res.getShopcode());
                callbackResult.setOrderNo(res.getOrderno());
                callbackResult.setErrorCode(res.getErrCode());
                callbackResult.setCompOrderNo(res.getComp_orderno());
                callbackResult.setCompMemNo(res.getComp_memno());
                callbackResult.setOrderGoodsname(res.getOrder_goodsname());
                callbackResult.setOrderReqAmt(res.getOrder_req_amt());
                callbackResult.setOrderName(res.getOrder_name());
                callbackResult.setOrderHp(res.getOrder_hp());
                callbackResult.setOrderEmail(res.getOrder_email());
                callbackResult.setCompTemp1(res.getComp_temp1());
                callbackResult.setCompTemp2(res.getComp_temp2());
                callbackResult.setCompTemp3(res.getComp_temp3());
                callbackResult.setCompTemp4(res.getComp_temp4());
                callbackResult.setCompTemp5(res.getComp_temp5());
                callbackResult.setReqInstallment(res.getReq_installment());
                callbackResult.setApprNo(res.getAppr_no());
                callbackResult.setApprTranNo(res.getAppr_tranNo());
                callbackResult.setApprShopCode(res.getAppr_shopCode());
                callbackResult.setApprDate(res.getAppr_date());
                callbackResult.setApprTime(res.getAppr_time());
                callbackResult.setCardtxt(res.getCardtxt());


                LpngCallback callback = new LpngCallback();
                callback.setSeqNo(null);
                callback.setMemberSeqNo(user.getNo());
                callback.setPgTranId(res.getAppr_tranNo());
                callback.setApprDate(res.getAppr_date());
                callback.setApprTime(res.getAppr_time());
                callback.setOrderId(ftLinkPayRequest.getComp_orderno());
                callback.setName(res.getOrder_name());
                callback.setPrice(Integer.parseInt(res.getOrder_req_amt()));
                callback.setPaymentData(AppUtil.ConverObjectToMap(res));
                callback.setRegDatetime(dateStr);
                callback.setLpngOrderNo(res.getOrderno());

                if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {

                    eventBuy.setStatus(1);
                    eventBuy.setRegDatetime(dateStr);
                    eventBuy.setModDatetime(dateStr);
                    eventBuy = eventBuyRepository.saveAndFlush(eventBuy);
                    callback.setEventBuySeqNo(eventBuy.getSeqNo());
                    callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);

                    callback.setResultSeqNo(callbackResult.getSeqNo());

                    callback.setStatus(true);
                    callback.setProcess(LpngProcess.PAY.getType());
                    callback = lpngCallbackRepository.saveAndFlush(callback);
                }else{
                    throw new InvalidCashException();
                }
            }


            if (eventBuy.getStatus() == 1) {

//                if(eventBuy.getPgPrice() > 0){
////                    PointHistory pointHistory = new PointHistory();
////                    pointHistory.setMemberSeqNo(user.getNo());
////                    pointHistory.setType("charge");
////                    pointHistory.setPoint(eventBuy.getPgPrice());
////                    pointHistory.setSubject("플레이 구매적립");
////                    pointService.updatePoint(user.getNo(), pointHistory);
//
//                    BolHistory bolHistory = new BolHistory();
//                    bolHistory.setAmount(eventBuy.getPgPrice().longValue());
//                    bolHistory.setMemberSeqNo(user.getNo());
//                    bolHistory.setSubject("경품 응모적립");
//                    bolHistory.setPrimaryType("increase");
//                    bolHistory.setSecondaryType("eventBuy");
//                    bolHistory.setTargetType("event");
//                    bolHistory.setTargetSeqNo(eventJpa.getSeqNo());
//                    bolHistory.setHistoryProp(new HashMap<String, Object>());
//                    bolHistory.getHistoryProp().put("적립 유형", "경품 응모적립");
//                    bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
//                    bolService.increaseBol(user.getNo(), bolHistory);
//                }

                for (int i = 0; i < count; i++) {

                    EventJoinJpa eventJoinJpa = new EventJoinJpa();
                    eventJoinJpa.setEventSeqNo(eventJpa.getSeqNo());
                    eventJoinJpa.setMemberSeqNo(user.getNo());
                    eventJoinJpa.setJoinDatetime(date);
                    eventJoinJpa.setIsBuy(true);
                    eventJoinJpa.setEventBuySeqNo(eventBuy.getSeqNo());
                    Integer maxSeqNo = eventJoinRepository.findMaxSeqNo(eventJpa.getSeqNo());
                    if(maxSeqNo == null){
                        maxSeqNo = 0;
                    }
                    eventJoinJpa.setSeqNo(maxSeqNo + 1);
                    eventJoinRepository.save(eventJoinJpa);
                    eventJpa = eventRepository.findBySeqNo(eventJpa.getSeqNo());
                    eventJpa.setJoinCount(eventJpa.getJoinCount()+1);
                    eventJpa = eventRepository.saveAndFlush(eventJpa);
                }

                if(eventJpa.getWinAnnounceType().equals("immediately")){
                    EventWin win =  lot(user, eventJpa);
                    if(win == null){
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(eventBuy.getPgPrice().floatValue());
                        bolHistory.setMemberSeqNo(user.getNo());
                        bolHistory.setSubject("경품 응모적립");
                        bolHistory.setPrimaryType("increase");
                        bolHistory.setSecondaryType("eventBuy");
                        bolHistory.setTargetType("event");
                        bolHistory.setTargetSeqNo(eventJpa.getSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("적립 유형", "경품 응모적립");
                        bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        bolService.increaseBol(user.getNo(), bolHistory);
                    }
                    return win;
                }else{
                    eventJpa = eventRepository.findBySeqNo(eventJpa.getSeqNo());
                    if(eventJpa.getJoinCount() >= eventJpa.getMaxJoinCount()){
                        eventJpa.setEndDatetime(dateStr);
                        String prevStatus = eventJpa.getStatus();
                        eventJpa = eventRepository.saveAndFlush(eventJpa);
                        if(!eventJpa.getPrimaryType().equals("randomluck")|| eventJpa.getWinAnnounceRandomDatetime() == null){
                            lot(eventJpa);
                            eventJpa.setStatus("pending");
                            updateEventWinAnnounceDateTime(eventJpa.getSeqNo());
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            try {
                if (eventBuy.getPayMethod().equals("card")) {
                    Cancel cancel = new Cancel();
                    cancel.receipt_id =  eventBuy.getReceiptId();
                    cancel.cancel_username = "피플러스" ;
                    cancel.cancel_message = "수량 초과" ;
                    cancel.cancel_price = eventBuy.getPgPrice() ;
                    bootPayCancel(cancel);
                }
            }catch (Exception cancelE){
                logger.error(cancelE.toString());
            }

            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<Map<String, Object>> eventBuyList(User user, EventBuy eventBuy) throws ResultCodeException {

        try {

            EventJpa eventJpa = eventRepository.findBySeqNo(eventBuy.getEventSeqNo());
            Integer count = eventBuy.getCount();

            if(!checkBuyJoinPossible(user.getNo(), eventJpa)){
                if (eventBuy.getPayMethod().equals("card")) {
                    Cancel cancel = new Cancel();
                    cancel.receipt_id =  eventBuy.getReceiptId();
                    cancel.cancel_username = "피플러스" ;
                    cancel.cancel_message = "수량 초과" ;
                    cancel.cancel_price = eventBuy.getPgPrice() ;
                    bootPayCancel(cancel);
                }

                throw new AlreadyLimitException("max-join-count", "limited");
            }

//            Member member = memberRepository.findBySeqNo(user.getNo());
//            if(eventBuy.getBolPrice() > member.getBol()){
//                if (eventBuy.getPayMethod().equals("card")) {
//                    Cancel cancel = new Cancel();
//                    cancel.receipt_id =  eventBuy.getReceiptId();
//                    cancel.cancel_username = "피플러스" ;
//                    cancel.cancel_message = "럭키볼부족" ;
//                    cancel.cancel_price = eventBuy.getPgPrice() ;
//                    bootPayCancel(cancel);
//                }
//
//                throw new LackCostException("럭키볼이 부족합니다.");
//            }

//            Integer bolPrice = eventBuy.getBolPrice();
//
//            if(bolPrice != null && bolPrice > 0){
//                BolHistory bolHistory = new BolHistory();
//                bolHistory.setAmount(bolPrice.longValue());
//                bolHistory.setMemberSeqNo(user.getNo());
//                bolHistory.setSubject("플레이 이벤트 응모");
//                bolHistory.setPrimaryType("decrease");
//                bolHistory.setSecondaryType("joinReduceEvent");
//                bolHistory.setTargetType("event");
//                bolHistory.setTargetSeqNo(eventJpa.getSeqNo());
//                bolHistory.setHistoryProp(new HashMap<String, Object>());
//                bolHistory.getHistoryProp().put("소진 유형", eventJpa.getTitle() + " 응모");
//                bolService.decreaseBol(user.getNo(), bolHistory);
//            }

            String dateStr = AppUtil.localDatetimeNowString();
            Date date = AppUtil.localDatetimeNowDate();

            eventBuy.setSeqNo(null);
            eventBuy.setMemberSeqNo(user.getNo());
            eventBuy.setStatus(0);

            if(eventBuy.getPayMethod().equals("point") && eventBuy.getBolPrice() >= eventBuy.getTotalPrice()){

                eventBuy.setStatus(1);
                eventBuy.setPgPrice(0);
                eventBuy.setRegDatetime(dateStr);
                eventBuy.setModDatetime(dateStr);
                eventBuy = eventBuyRepository.saveAndFlush(eventBuy);

            }else if (eventBuy.getPayMethod().equals("card")) {

                String token = getAccessToken();

                if (token == null || token.isEmpty()) {
                    throw new InvalidCashException();
                }

                HttpResponse res = verify(eventBuy.getReceiptId(), token);
                String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");

                if (!data.get("status").getAsString().equals("1")) {
                    throw new Exception(" pay rejected !!!");
                }

                JsonObject paymentData = data.get("payment_data").getAsJsonObject();
                String tid = paymentData.get("tid").getAsString();
                String orderNo = data.get("order_id").getAsString();

                eventBuy.setStatus(data.get("status").getAsInt());
                eventBuy.setPgPrice(data.get("price").getAsInt());
                eventBuy.setCardName(paymentData.get("card_name").getAsString());
                eventBuy.setCardQuota(paymentData.get("card_quota").getAsString());
                eventBuy.setCardAuthNo(paymentData.get("card_auth_no").getAsString());

                String settDate = paymentData.get("p_at").getAsString(); // 2020-11-23 10:35:44

                String email = "";

                // purchaseProduct.set
                // 2020-11-23 10:35:44
                settDate = settDate.replaceAll("-", "");
                settDate = settDate.replaceAll(":", "");
                settDate = settDate.replaceAll(" ", "");

                String setDay = settDate.substring(0,8);
                String setTime = settDate.substring(8);

                LpngCallbackResult lpngCallbackResult = new LpngCallbackResult();
                lpngCallbackResult.setOrderNo(orderNo);
                lpngCallbackResult.setShopCode("200002659");
                lpngCallbackResult.setOrderStatus("12");
                if(eventJpa.getPrimaryType().equals("randomluck")){
                    lpngCallbackResult.setOrderGoodsname("랜덤뽑기");
                }else{
                    lpngCallbackResult.setOrderGoodsname("일상혜택");
                }
                lpngCallbackResult.setOrderReqAmt(eventBuy.getPgPrice().toString());
                lpngCallbackResult.setOrderName(user.getName());
                lpngCallbackResult.setOrderHp(user.getMobile().replace(user.getAppType() + "##", ""));
                lpngCallbackResult.setApprNo(eventBuy.getCardAuthNo());
                lpngCallbackResult.setApprTranNo(tid);
                lpngCallbackResult.setApprDate(setDay);
                lpngCallbackResult.setApprTime(setTime);
                lpngCallbackResult.setCardtxt(eventBuy.getCardName());
                lpngCallbackResult.setReqCardNo(eventBuy.getCardNo());
                lpngCallbackResult.setApprShopCode("");
                lpngCallbackResult.setReqInstallment(eventBuy.getCardQuota());

                lpngCallbackResult = lpngCallbackResultRepository.saveAndFlush(lpngCallbackResult);

                LpngCallback lpngCallback = new LpngCallback();
                lpngCallback.setMemberSeqNo(user.getNo());
                lpngCallback.setEventBuySeqNo(eventBuy.getSeqNo());
                lpngCallback.setOrderId(orderNo);
                lpngCallback.setPgTranId(tid);
                lpngCallback.setName(user.getName());
                lpngCallback.setPrice(eventBuy.getPgPrice());
                lpngCallback.setStatus(true);
                lpngCallback.setApprDate(setDay);
                lpngCallback.setApprTime(setTime);
                lpngCallback.setRegDatetime(dateStr);
                lpngCallback.setProcess(1); // 결제완료
                lpngCallback.setResultSeqNo(lpngCallbackResult.getSeqNo());
                lpngCallbackRepository.save(lpngCallback);

                if(eventBuy.getPg().equals("danal")){
                    danalNoti(user, jsonObject, eventJpa.getPrimaryType());
                }

                eventBuy.setRegDatetime(dateStr);
                eventBuy.setModDatetime(dateStr);
                eventBuy = eventBuyRepository.saveAndFlush(eventBuy);

            }else if (eventBuy.getPayMethod().equals("ftlink")) {
                FTLinkPayRequest ftLinkPayRequest = new FTLinkPayRequest();
                ftLinkPayRequest.setShopcode("200002659");//피플러스 샵코드
                ftLinkPayRequest.setOrder_req_amt(eventBuy.getPgPrice().toString());
                ftLinkPayRequest.setOrder_hp(user.getMobile().replace(user.getAppType() + "##", ""));
                ftLinkPayRequest.setOrder_name(user.getName());
                ftLinkPayRequest.setComp_memno(user.getName());
                if(eventJpa.getPrimaryType().equals("randomluck")){
                    ftLinkPayRequest.setOrder_goodsname("랜덤뽑기");
                }else{
                    ftLinkPayRequest.setOrder_goodsname("일상혜택");
                }
                ftLinkPayRequest.setReq_installment(eventBuy.getInstallment());
                ftLinkPayRequest.setComp_orderno(eventBuy.getOrderId());
                ftLinkPayRequest.setAutokey(eventBuy.getAutoKey());
                ftLinkPayRequest.setReq_cardcode(eventBuy.getCardCode());
                ftLinkPayRequest.setManual_used("N");
                ftLinkPayRequest.setLoginId("test08");//피플러스 id
                ftLinkPayRequest.setServerType("");
                ftLinkPayRequest.setRoomId("");
                ftLinkPayRequest.setReqdephold("N");
                if (storeType.equals("PROD")) {
                    ftLinkPayRequest.setISTEST("USE");
                } else {
                    ftLinkPayRequest.setISTEST("TEST");
                }
                FTLinkPayResponse res = FTLinkPayApi.payRequest(ftLinkPayRequest);


                LpngCallbackResult callbackResult = new LpngCallbackResult();
                callbackResult.setErrorMsg(res.getErrMessage());
                callbackResult.setShopCode(res.getShopcode());
                callbackResult.setOrderNo(res.getOrderno());
                callbackResult.setErrorCode(res.getErrCode());
                callbackResult.setCompOrderNo(res.getComp_orderno());
                callbackResult.setCompMemNo(res.getComp_memno());
                callbackResult.setOrderGoodsname(res.getOrder_goodsname());
                callbackResult.setOrderReqAmt(res.getOrder_req_amt());
                callbackResult.setOrderName(res.getOrder_name());
                callbackResult.setOrderHp(res.getOrder_hp());
                callbackResult.setOrderEmail(res.getOrder_email());
                callbackResult.setCompTemp1(res.getComp_temp1());
                callbackResult.setCompTemp2(res.getComp_temp2());
                callbackResult.setCompTemp3(res.getComp_temp3());
                callbackResult.setCompTemp4(res.getComp_temp4());
                callbackResult.setCompTemp5(res.getComp_temp5());
                callbackResult.setReqInstallment(res.getReq_installment());
                callbackResult.setApprNo(res.getAppr_no());
                callbackResult.setApprTranNo(res.getAppr_tranNo());
                callbackResult.setApprShopCode(res.getAppr_shopCode());
                callbackResult.setApprDate(res.getAppr_date());
                callbackResult.setApprTime(res.getAppr_time());
                callbackResult.setCardtxt(res.getCardtxt());


                LpngCallback callback = new LpngCallback();
                callback.setSeqNo(null);
                callback.setMemberSeqNo(user.getNo());
                callback.setPgTranId(res.getAppr_tranNo());
                callback.setApprDate(res.getAppr_date());
                callback.setApprTime(res.getAppr_time());
                callback.setOrderId(ftLinkPayRequest.getComp_orderno());
                callback.setName(res.getOrder_name());
                callback.setPrice(Integer.parseInt(res.getOrder_req_amt()));
                callback.setPaymentData(AppUtil.ConverObjectToMap(res));
                callback.setRegDatetime(dateStr);
                callback.setLpngOrderNo(res.getOrderno());

                if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {

                    eventBuy.setStatus(1);
                    eventBuy.setRegDatetime(dateStr);
                    eventBuy.setModDatetime(dateStr);
                    eventBuy = eventBuyRepository.saveAndFlush(eventBuy);
                    callback.setEventBuySeqNo(eventBuy.getSeqNo());
                    callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);

                    callback.setResultSeqNo(callbackResult.getSeqNo());

                    callback.setStatus(true);
                    callback.setProcess(LpngProcess.PAY.getType());
                    callback = lpngCallbackRepository.saveAndFlush(callback);
                }else{
                    throw new InvalidCashException();
                }
            }


            if (eventBuy.getStatus() == 1) {

                List<Map<String, Object>> resutList = new ArrayList<>();
                for (int i = 0; i < count; i++) {

                    Map<String, Object> result = new HashMap<String, Object>();

                    EventJoinJpa eventJoinJpa = new EventJoinJpa();
                    eventJoinJpa.setEventSeqNo(eventJpa.getSeqNo());
                    eventJoinJpa.setMemberSeqNo(user.getNo());
                    eventJoinJpa.setJoinDatetime(date);
                    eventJoinJpa.setIsBuy(true);
                    eventJoinJpa.setEventBuySeqNo(eventBuy.getSeqNo());
                    Integer maxSeqNo = eventJoinRepository.findMaxSeqNo(eventJpa.getSeqNo());
                    if(maxSeqNo == null){
                        maxSeqNo = 0;
                    }
                    eventJoinJpa.setSeqNo(maxSeqNo + 1);
                    eventJoinJpa = eventJoinRepository.saveAndFlush(eventJoinJpa);
                    result.put("join", eventJoinJpa);

                    eventJpa = eventRepository.findBySeqNo(eventJpa.getSeqNo());
                    eventJpa.setJoinCount(eventJpa.getJoinCount()+1);
                    eventJpa = eventRepository.saveAndFlush(eventJpa);

                    if(eventJpa.getWinAnnounceType().equals("immediately")){
                        EventWin eventWin = lot(user, eventJpa);

                        if(eventWin == null){
                            if(eventJpa.getEarnedPoint() != null && eventJpa.getEarnedPoint() > 0){
                                PointHistory pointHistory = new PointHistory();
                                pointHistory.setMemberSeqNo(user.getNo());
                                pointHistory.setType("charge");
                                pointHistory.setPoint(eventJpa.getEarnedPoint());
                                pointHistory.setSubject("랜덤뽑기 리워드");
                                pointHistory.setHistoryProp(new HashMap<String, Object>());
                                pointHistory.getHistoryProp().put("적립 유형", eventJpa.getTitle() + " 응모");
                                pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                                pointService.updatePoint(user.getNo(), pointHistory);
                            }
                        }
                        result.put("win", eventWin);
                    }

                    resutList.add(result);
                }

                if(eventJpa.getWinAnnounceType().equals("immediately")){
                    return resutList;
                }else if(eventJpa.getJoinCount() >= eventJpa.getMaxJoinCount()){
                    eventJpa.setEndDatetime(dateStr);
                    String prevStatus = eventJpa.getStatus();
                    eventJpa = eventRepository.saveAndFlush(eventJpa);
                    if(!eventJpa.getPrimaryType().equals("randomluck")|| eventJpa.getWinAnnounceRandomDatetime() == null){
                        lot(eventJpa);
                        eventJpa.setStatus("pending");
                        updateEventWinAnnounceDateTime(eventJpa.getSeqNo());
                    }
                }
            }

            return null;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidCashException();
        }
    }

    public boolean bootPayCancel(Cancel cancel){
        try{

            String token = getAccessToken();
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = getPost(URL_CANCEL, new StringEntity(new Gson().toJson(cancel), "UTF-8"));
            post.setHeader("Authorization", token) ;
            HttpResponse res = client.execute(post);
            Gson gson = new Gson();
            BootPayCancelResponse bootPayCancelResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), BootPayCancelResponse.class);

            if( res.getStatusLine().getStatusCode() == 20) {
                return true;
            } else {
                logger.error(convertStreamToString(res.getEntity().getContent())) ;
                throw new Exception("bootPayCancel Error") ;
            }
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            return false ;
        }
    }

    public String getAccessToken() throws Exception {

        kr.co.pplus.store.api.jpa.model.bootpay.request.Token token = new Token();
        token.application_id = CASH_APP_ID;
        token.private_key = CASH_PRIVATE_KEY;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = getPost(URL_ACCESS_TOKEN, new StringEntity(new Gson().toJson(token), "UTF-8"));

        HttpResponse res = client.execute(post);
        String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        ResToken resToken = new Gson().fromJson(str, ResToken.class);

        logger.debug("bootPay.getAccessToken() response : " + str);
        if (resToken.status == 200) {
            return resToken.data.token;
        } else {
            return null;
        }
    }

    public HttpResponse verify(String receipt_id, String token) throws Exception {
        if (token == null || token.isEmpty()) throw new Exception("token 값이 비어있습니다.");

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = getGet(URL_VERIFY + "/" + receipt_id);
        get.setHeader("Authorization", token);
        return client.execute(get);
    }

    private HttpPost getPost(String url, StringEntity entity) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept-Charset", "utf-8");
        post.setEntity(entity);
        return post;
    }

    private HttpGet getGet(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        URI uri = new URIBuilder(get.getURI()).build();
        get.setURI(uri);
        return get;
    }

    public void danalNoti(User user, JsonObject jsonObject, String primaryType) {

        try {

	        /*
	         {"status":200,"code":0,"message":"",
	         "data":{"receipt_id":"5fbb11d50627a8002bbcec21","order_id":"O20112335179329","name":"단일형 상품","item_name":"단일형 상품","price":30000,"tax_free":0,"remain_price":30000,"remain_tax_free":0,"cancelled_price":0,"cancelled_tax_free":0,"receipt_url":"https://www.danalpay.com/receipt/creditcard/view.aspx?datatype=receipt&cpid=9810030929&data=xYSaEE7psDG%2FV7o%2FyzZzfHg50lhXOyFpIqGB3vTRk85q0AbKj%2B6IYjqSN7tb%0An2zo%0A",
	         "unit":"krw","pg":"danal","method":"card","pg_name":"다날","method_name":"ISP / 앱카드 결제",
	         "payment_data":{"card_name":"KB국민","card_no":"5598690000005020","card_quota":"00","card_code":"0300","card_auth_no":"30043797","receipt_id":"5fbb11d50627a8002bbcec21","n":"단일형 상품","p":30000,"tid":"202011231035196083903400","pg":"다날","pm":"ISP / 앱카드 결제","pg_a":"danal","pm_a":"card","o_id":"O20112335179329","p_at":"2020-11-23 10:35:44","s":1,"g":2},
	         "requested_at":"2020-11-23 10:35:17","purchased_at":"2020-11-23 10:35:44","status":1,"status_en":"complete","status_ko":"결제완료"}}
	         */


	        /*
	        0 - 결제 대기 상태입니다. 승인이 나기 전의 상태입니다.
	        1 - 결제 완료된 상태입니다.
	        2 - 결제승인 전 상태입니다. transactionConfirm() 함수를 호출하셔서 결제를 승인해야합니다.
	        3 - 결제승인 중 상태입니다. PG사에서 transaction 처리중입니다.
	        20 - 결제가 취소된 상태입니다.
	        -20 - 결제취소가 실패한 상태입니다.
	        -30 - 결제취소가 진행중인 상태입니다.
	        -1 - 오류로 인해 결제가 실패한 상태입니다.
	        -2 - 결제승인이 실패하였습니다.
	        */

            Integer status = jsonObject.get("status").getAsInt();
            Integer code = jsonObject.get("code").getAsInt();


            if(status.equals(200) && code.equals(0)) {

                JsonObject data = jsonObject.getAsJsonObject("data");

                String orderNo = data.get("order_id").getAsString();
                Integer process = data.get("status").getAsInt();
                String pg = data.get("pg").getAsString().toUpperCase();
                Integer paymentPrice = data.get("price").getAsInt();

                JsonObject payment = data.get("payment_data").getAsJsonObject();

                String cardNo = payment.get("card_no").getAsString();
                String authNo = payment.get("card_auth_no").getAsString();
                String cardName = payment.get("card_name").getAsString();
                String cardQuota = payment.get("card_quota").getAsString();
                String cardCode = payment.get("card_code").getAsString();
                String tid = payment.get("tid").getAsString();



                String payMethod = "CARD";

                String cpid = CPID;

                String daouTrx = tid;

                String settDate = payment.get("p_at").getAsString(); // 2020-11-23 10:35:44



                String email = "";


                // 2020-11-23 10:35:44
                settDate = settDate.replaceAll("-", "");
                settDate = settDate.replaceAll(":", "");
                settDate = settDate.replaceAll(" ", "");

                String setDay = settDate.substring(0,8);
                String setTime = settDate.substring(8);

                String reqdephold = "N";

                String type = "TEST";
                if (storeType.equals("PROD")) {
                    type = "USE";
                }

                String[] mobiles = user.getMobile().split("##");
                String mobile = null;
                if(mobiles.length == 2){
                    mobile = mobiles[1];
                }else{
                    mobile = mobiles[0];
                }

                URIBuilder uriBuilder = new URIBuilder("http://pay.ftlink.co.kr/payalert/pplus/noti_cert.asp");
                uriBuilder
                        .addParameter("PAYMETHOD", URLEncoder.encode(payMethod,"EUC-KR"))
                        .addParameter("CPID", URLEncoder.encode(cpid,"EUC-KR"))
                        .addParameter("DAOUTRX", URLEncoder.encode(daouTrx,"EUC-KR"))
                        .addParameter("ORDERNO", URLEncoder.encode(orderNo,"EUC-KR"))
                        .addParameter("AMOUNT", URLEncoder.encode(paymentPrice+"","EUC-KR"))
                        .addParameter("SETDATE", URLEncoder.encode(settDate,"EUC-KR"))
                        .addParameter("AUTHNO", URLEncoder.encode(authNo,"EUC-KR"))
                        .addParameter("CARDCODE", URLEncoder.encode(cardCode,"EUC-KR"))
                        .addParameter("CARDNAME", URLEncoder.encode(cardName,"EUC-KR"))
                        .addParameter("CARDNO", URLEncoder.encode(cardNo,"EUC-KR"))
                        .addParameter("EMAIL", URLEncoder.encode(email,"EUC-KR"))
                        .addParameter("USERID", URLEncoder.encode(mobile,"EUC-KR"))
                        .addParameter("USERNAME", URLEncoder.encode(user.getName(),"EUC-KR"))
                        .addParameter("RESERVEDINDEX1", URLEncoder.encode("200002659","EUC-KR"))
                        .addParameter("RESERVEDINDEX2", URLEncoder.encode("","EUC-KR"))
                        .addParameter("RESERVEDINDEX3", URLEncoder.encode("","EUC-KR"))
                        .addParameter("RESERVEDSTRING", URLEncoder.encode("","EUC-KR"))
                        .addParameter("ISTEST", URLEncoder.encode(type,"EUC-KR"))
                        .addParameter("reqdephold", URLEncoder.encode(reqdephold,"EUC-KR"))
                        .addParameter("MANUAL_USED", "N")
                        .addParameter("PGCODE", "20"); // 다우-30, 다날-20

                if(primaryType.equals("randomluck")){
                    uriBuilder.addParameter("PRODUCTNAME", URLEncoder.encode("랜덤뽑기","EUC-KR"));
                }else{
                    uriBuilder.addParameter("PRODUCTNAME", URLEncoder.encode("일상혜택","EUC-KR"));
                }

                URI uri = uriBuilder.build();

                HttpGet getMethod = new HttpGet(uri);

                getMethod.addHeader(new BasicHeader("Accept", "application/json"));
                getMethod.addHeader(new BasicHeader("Accept-Charset", "EUC-KR"));

                CloseableHttpClient httpclient = HttpClients.createDefault();

                logger.info("params ==> " + getMethod.toString());

                try {

                    CloseableHttpResponse response = httpclient.execute(getMethod);

                    String resultData2 = EntityUtils.toString(response.getEntity(), "UTF-8");


                    logger.info("finteck result ==> " + resultData2);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<EventJoinWithLottoNumber> getLottoJoinList(User user, Long eventSeqNo){
        return eventJoinWithLottoNumberRepository.findAllByEventSeqNoAndMemberSeqNo(eventSeqNo, user.getNo());
    }

    public List<LottoWinNumber> getLottoWinNumberList(Long eventSeqNo){
        return lottoWinNumberRepository.findAllByEventSeqNoOrderByLottoNumberAsc(eventSeqNo);
    }

    public Integer getLottoWinnerCount(Long eventSeqNo){
        return lottoMemberCountRepository.countByEventSeqNoAndIsWinner(eventSeqNo, true);
    }

    public Page<EventWinWithJoin> getLottoWinnerList(Long eventSeqNo, Pageable pageable){
        return eventWinWithJoinRepository.findAllByEventSeqNo(eventSeqNo, pageable);
    }

}
