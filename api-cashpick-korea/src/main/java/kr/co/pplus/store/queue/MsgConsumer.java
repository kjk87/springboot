package kr.co.pplus.store.queue;

import kr.co.pplus.store.StoreApplication;
import kr.co.pplus.store.api.jpa.model.ChatData;
import kr.co.pplus.store.api.jpa.model.EventJpa;
import kr.co.pplus.store.api.jpa.model.delivery.Delivery;
import kr.co.pplus.store.api.jpa.model.delivery.DeliveryCompanyType;
import kr.co.pplus.store.api.jpa.model.delivery.DeliveryGoods;
import kr.co.pplus.store.api.jpa.repository.PageRepository;
import kr.co.pplus.store.api.jpa.repository.delivery.DeliveryGoodsRepository;
import kr.co.pplus.store.api.jpa.repository.delivery.DeliveryRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidDeliveryException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.mvc.service.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.JoinUser;
import kr.co.pplus.store.type.dto.QueueRequest;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.SecureUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsgConsumer /* implements CcsResultHandler */ {
    private final static Logger logger = LoggerFactory.getLogger(MsgConsumer.class);
    private final static String msgIdDelimit = ":";

    @Autowired
    MsgProducer producer;

    @Autowired
    AuthService authSvc;

    @Autowired
    FanService fanSvc;

    @Autowired
    ArticleService postSvc;

    @Autowired
    UserService userSvc;

    @Autowired
    MsgService msgSvc;

    @Autowired
    CouponService couponSvc;

    @Autowired
    NoteService noteSvc;

    @Autowired
    CashBolService cashBolSvc;

    @Autowired
    QueueService queueSvc;

    @Autowired
    EventService eventService;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    DeliveryGoodsRepository deliveryGoodsRepository;


    @Autowired
    PageRepository pageRepository;

    @Autowired
    BuffService buffService;

    public static final String PPLUS = "피플러스";
    public static final String BAEMIN = "배달의민족";
    public static final String YOGIYO = "요기요";
    public static final String BAEDALTONG = "배달통";

    public MsgConsumer() {

        logger.info("MsgConsumer constructor");
    }

    @PostConstruct
    public void init() {
		/*List<App> apps = sysDao.getAppAll();
		for (App app : apps) {
			createPool(app);
		}*/
    }


    @JmsListener(destination = "${STORE.MQ_QUEUE}", containerFactory = "myFactory")
    public void onMessageReceived(final ActiveMQObjectMessage message) throws JMSException {

        logger.info("MsgConsumer : pop object");

        System.out.println("MsgConsumer : pop object");

        if (message instanceof TextMessage) {
            String text = ((TextMessage) message).getText();
        } else {
            process(message);

        }
    }


    public void process(ActiveMQObjectMessage message) {
        try {
            Object msg = message.getObject();
            logger.info("MsgConsumer : process()");
            if (msg instanceof ChatData) {
                processChatPush((ChatData) msg);
            } else if (msg instanceof EventJpa) {
                Event event = new Event();
                event.setNo(((EventJpa) msg).getSeqNo());
                eventService.copyEvent(event);
            } else if (msg instanceof JoinUser) {
                logger.debug("MsgConsumer : process() : JoinUser : ");
                authSvc.joinSuccessProc((JoinUser) msg);
            } else if (msg instanceof User) {
                logger.debug("MsgConsumer : process() : User : ");
                User user = ((User) msg);
                logger.info(user.getName());
            } else if (msg instanceof QueueRequest) {
                logger.debug("MsgConsumer : process() : QueueRequest : ");
                processQueueRequest((QueueRequest) msg);
            } else if (msg instanceof Comment) {
                logger.debug("MsgConsumer : process() : Comment : ");
                processComment((Comment) msg);
            } else if (msg instanceof Article) {
                logger.debug("MsgConsumer : process() : Article : ");
                processPosting((Article) msg);
            } else if (msg instanceof PushMsg) {
                logger.debug("MsgConsumer : process() : PushMsg : ");
                processPush((PushMsg) msg, null, msg);
            } else if (msg instanceof Coupon) {
                logger.debug("MsgConsumer : process() : LuckyCoupon : ");
                processCoupon((Coupon) msg);
            } else if (msg instanceof MsgOnly) {
                logger.debug("MsgConsumer : process() : MsgOnly : ");
                //System.out.println("MsgConsumer : process() : MsgOnly : ") ;
                processMsg((MsgOnly) msg);
            } else if (msg instanceof Offer) {
                logger.debug("MsgConsumer : process() : Offer : ");

            } else if (msg instanceof Note) {
                logger.debug("MsgConsumer : process() : Note : ");
                processNote((Note) msg);
            } else if (msg instanceof BuffMsg) {
                logger.debug("MsgConsumer : process() : BuffMsg : ");

                BuffMsg buffMsg = (BuffMsg) msg;
                List<BuffMsg> list = new ArrayList<>();
                list.add(buffMsg);
                buffService.divideList(list);

            } else if (msg instanceof HashMap) {
                processAgentMessage((HashMap<String, Object>) msg);
            } else {
                logger.error("MsgConsumer : process() : unknownObject : ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(AppUtil.excetionToString(ex));
        }
    }

    private void processChatPush(ChatData chatData) {

        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(chatData.getPageSeqNo());
        String sender = null;
        if (page.getMemberSeqNo() == chatData.getMemberSeqNo()) {
            sender = page.getName();
        } else {
            User user = userSvc.getUser(chatData.getMemberSeqNo());
            sender = user.getNickname();
        }

        User user = new User();
        user.setNo(chatData.getTargetMemberSeqNo());

        List<UserApp> appList = userSvc.getUserApp(user);

        logger.debug("MsgConsumer : processPush sending...: ");
        Map<String, String> data = new HashMap<String, String>();
        data.put("title", sender);
        data.put("contents", chatData.getMsg());
        data.put("move_type1", Const.MOVE_TYPE_INNER);
        data.put("move_type2", "chatting");
        data.put("move_target_string", chatData.getRoomName());
//        data.put("image_path", msg.getImage_path());
//        data.put("image_path1", msg.getImage_path1());

        try {
            sendChatPush(data, Const.APP_TYPE_USER, appList, null);
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    private void sendChatPush(Map<String, String> data, String appType, List<UserApp> targetList, Object handlerArg) throws Exception {

        ArrayList<UserApp> nativeTargetList = new ArrayList<UserApp>();
        for (UserApp userApp : targetList) {
            if (userApp.getDevice().getPlatform().equals("ios") || userApp.getDevice().getPlatform().equals("aos") || userApp.getDevice().getPlatform().equals("pcweb")) {
                nativeTargetList.add(userApp);
            }
        }

        if (nativeTargetList.size() > 0) {
            if (appType.equals(Const.APP_TYPE_USER)) {
                Firebase firebase = Firebase.getUserInstance();
                logger.info("Firebase.getUserInstance() : " + firebase.toString());
                firebase.sendMulticastPush(data, nativeTargetList, null, handlerArg);
            } else if (appType.equals(Const.APP_TYPE_BIZ)) {
                Firebase firebase = Firebase.getBizInstance();
                logger.info("Firebase.getBizInstance() : " + firebase.toString());
                firebase.sendMulticastPush(data, nativeTargetList, null, handlerArg);
            } else if (appType.equals(Const.APP_TYPE_LUCKYBOL)) {
                Firebase firebase = Firebase.getLuckyBolInstance();
                logger.info("Firebase.getLuckyBolInstance() : " + firebase.toString());
                firebase.sendMulticastPush(data, nativeTargetList, null, handlerArg);
            }
        }
    }

    private void processAgentMessage(HashMap<String, Object> msg) {

        try {
            Agent agent = (Agent) msg.get("agent");
            switch (agent.getChargeId()) {
                case "LnKPeopleKorea":
                    Delivery delivery = addDeliveryLnK(msg);
                    logger.debug("Insert Delivery Message to BD : " + delivery.toString());
                    break;
                default:
                    logger.error("Unknown agent : " + agent.toString());
            }
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
        }
    }


    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidDeliveryException.class)
    public Delivery addDeliveryLnK(Map<String, Object> map) throws Exception {

        try {

            String deliveryCompany = (String) map.get("deli_comp");
            DeliveryCompanyType deliveryCompanyType = null;
            switch (deliveryCompany) {
                case PPLUS:
                    deliveryCompanyType = DeliveryCompanyType.PPLUS;
                    break;
                case BAEMIN:
                    deliveryCompanyType = DeliveryCompanyType.BAEMIN;
                    break;
                case YOGIYO:
                    deliveryCompanyType = DeliveryCompanyType.YOGIYO;
                    break;
                case BAEDALTONG:
                    deliveryCompanyType = DeliveryCompanyType.BAEDALTONG;
                    break;
                default:
                    throw new UnknownException("알수 없는 배달 주문 업체 입니다.");
            }

            // 상점정보 체크 후 신규 상점이면 table 에 추가
            String shopId = (String) map.get("shop_id");
            Long pageSeqNo = 0L;
            try {
                pageSeqNo = Long.parseLong(SecureUtil.decryptMobileNumber(shopId));
            } catch (Exception e) {
                // ToDo : 테스트 코드 상용에서는 제거되어야 하는 코드
                if (StoreApplication.SERVER_NAME.equals("STAGE") || StoreApplication.SERVER_NAME.equals("DEV"))
                    pageSeqNo = 1000159L;
                else
                    throw e;
            }
            /*
            AgentShop agentShop = agentShopRepository.findByPageSeqNo(pageSeqNo);
            if (agentShop == null) {
                agentShop = new AgentShop();
                agentShop.setSeqNo(null);
                agentShop.setAgentSeqNo(agent.getNo());
                agentShop.setId(shopId);
                if (map.get("shop_name") != null) {
                    agentShop.setName((String) map.get("shop_name"));
                } else {
                    agentShop.setName(shopId);
                }
                agentShop.setPageSeqNo(pageSeqNo) ;

                agentShop = agentShopRepository.saveAndFlush(agentShop);
            }
            */

            Agent agent = (Agent) map.get("agent");
            Delivery delivery = new Delivery();
            delivery.setSeqNo(null);
            delivery.setAgentSeqNo(agent.getNo());
            delivery.setCompanySeqNo((long) deliveryCompanyType.getType());
            delivery.setPageSeqNo(pageSeqNo);
            delivery.setId((String) map.get("deli_num"));
            delivery.setClientAddress((String) map.get("deli_address"));
            delivery.setClientTel((String) map.get("deli_tel"));
            delivery.setClientMemo((String) map.get("deli_desc"));
            delivery.setTotalPrice((float) Integer.parseInt((String) map.get("total_money")));
            delivery.setPayment((String) map.get("payment"));
            delivery.setRegDatetime((String) map.get("deli_order_day"));
            delivery.setModDatetime((String) map.get("deli_order_day"));
            logger.debug("/delivery[POST] - delivery : " + delivery);
            delivery = deliveryRepository.saveAndFlush(delivery);


            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("item");
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    DeliveryGoods deliveryGoods = new DeliveryGoods();
                    deliveryGoods.setSeqNo(null);
                    deliveryGoods.setDeliverySeqNo(delivery.getSeqNo());
                    deliveryGoods.setName((String) list.get(i).get("item_title"));
                    try {
                        if (list.get(i).get("item_cnt") != null)
                            deliveryGoods.setCount(Integer.parseInt((String) list.get(i).get("item_cnt")));
                        if (list.get(i).get("item_money") != null)
                            deliveryGoods.setPrice((float) Integer.parseInt((String) list.get(i).get("item_money")));
                    } catch (Exception e) {
                        //do nothings.
                    }

                    logger.debug("/delivery[POST] deliveryGoods[" + i + "] : " + deliveryGoods);
                    deliveryGoods = deliveryGoodsRepository.saveAndFlush(deliveryGoods);

                }
            }

            MsgOnly msg = new MsgOnly();
            msg.setIncludeMe(false);
            msg.setInput("system");
            msg.setStatus("ready");
            msg.setType("push");
            msg.setMoveType1(Const.MOVE_TYPE_INNER);
            msg.setMoveType2("order");
            msg.setMoveTarget(new NoOnlyKey(delivery.getSeqNo()));
            msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
            msg.setAppType(Const.APP_TYPE_BIZ);
            /*
            if( imgPath != null ) {

                msg.setProperties(new HashMap<String, Object>());
                msg.getProperties().put("image_path", imgPath);
            }
            */

            msg.setContents("구매자 전화번호 : " + delivery.getClientTel() + "," +
                    "구매자 : " + deliveryCompany + ", 구매금액 : " + delivery.getTotalPrice());

//            msg.setMoveTargetString(buy.getTitle());
            msg.setSubject(deliveryCompany + ":" + "주문이 접수되었습니다.");

            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(delivery.getPageSeqNo());
            User user = new User();
            user.setNo(page.getMemberSeqNo());
            queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_BIZ);

            return delivery;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidDeliveryException("/delivery", "insert error");
        }
    }

    private void processNote(Note note) {

        logger.debug("MsgConsumer : processNote : " + note.getNo());

        // 노트 수신자에게 Push 보내기
        // 원본 번호가 있는 경우 답변이다. 답변은 사용자 앱으로 보낸다.
        // 원본이 없는 경우 질문이다. 질문은 점주용 앱으로 보낸다.
        List<User> receiverList = noteSvc.getReceiverAll(note);
        MsgOnly msg = new MsgOnly();
        if (note.getOriginNo() == null) {
            msg.setStatus("ready");
            msg.setType("push");
            msg.setMoveType1("inner");
            msg.setMoveType2("noteDetail");
            msg.setMoveTarget(note);

            msg.setSubject(note.getAuthor().getName() + ": " + note.getContents());
            msg.setContents(msg.getSubject());
            msg.setAppType(Const.APP_TYPE_BIZ);
            msg.setPushCase(Const.BIZ_PUSH_NOTE);
        } else {
            msg.setStatus("ready");
            msg.setType("push");
            msg.setMoveType1("inner");
            msg.setMoveType2("noteDetail");
            msg.setMoveTarget(note);

            msg.setSubject(note.getAuthor().getName() + ": " + note.getContents());
            msg.setContents(msg.getSubject());
            msg.setAppType(Const.APP_TYPE_USER);
            msg.setPushCase(Const.USER_PUSH_NOTE);
        }
        for (User receiver : receiverList) {
            try {
                queueSvc.insertMsgBox(note.getAuthor(), msg, receiver, msg.getAppType());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private void processMsg(MsgOnly msg) {
        logger.info("processMsg(msgNo:" + msg.getNo() + ");");
        if (msg.getReserved() != null && msg.getReserved()
                && msg.getReserveDate() != null
                && msg.getReserveDate().getTime() > DateUtil.getCurrentDate().getTime()) {
            //예약 전송 조건이 유효한지 검사한다.
            try {
                logger.info("msg(" + msg.getNo() + ") process canceled. reserve date=" + DateUtil.getDateString(DateUtil.PATTERN, msg.getReserveDate()));
                //System.out.println("msg(" + msg.getNo() + ") process canceled. reserve date=" + DateUtil.getDateString(DateUtil.PATTERN, msg.getReserveDate()));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (msg.getPushCase() == null || msg.getPushCase() < 1) {
            logger.info("msg(" + msg.getNo() + ") process canceled. push case unknown");
            //System.out.println("msg(" + msg.getNo() + ") process canceled. push case unknown");
            return;
        }

        logger.debug("saved : msgSvc.getMsgWithPage(msg) : " + msg.getNo());
        //System.out.println("saved : msgSvc.getMsgWithReceiver(msg) : " + msg.getNo()) ;
//        Msg saved = msgSvc.getMsgWithReceiver(msg);
        Msg saved = msgSvc.getMsgWithPage(msg);

        if (saved == null) { //Read/Write DB 간 delay 타임 고려..
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                saved = msgSvc.getMsgWithPage(msg);
                if (saved != null)
                    break;
            }
        }


        if (saved != null) {
            logger.debug("saved : " + saved.getNo() + "page.no : " + saved.getPage().getNo());
            saved.setTargetList(msgSvc.getMsgTargetList(saved)); // 보낼 전체 리스트 들어 있음.
        } else {
            logger.debug("saved is null ");
        }

        if (!saved.getType().equals("push") && !saved.getType().equals("sms") && !saved.getType().equals("lms")) {
            logger.info("msg(" + saved.getNo() + ") not send type, type=" + saved.getType());
            return;
        }

        if (!saved.getStatus().equals("ready") && !saved.getStatus().equals("reserved")) {
            logger.info("msg(" + saved.getNo() + ") already process. status=" + saved.getStatus());
            return;
        }

        //System.out.println("processMsg sending...") ;

        msg.setStatus("sending");
        msgSvc.updateMsgStatus(msg);

        if (msg.getType().equals("push")) {

            PushMsg p = new PushMsg();
            p.setMsgNo(saved.getNo());
            p.setAppType(msg.getAppType());
            p.setSender(saved.getAuthor());
            p.setMsg_id("001:" + saved.getNo().toString());
            p.setMove_type1(saved.getMoveType1());
            p.setMove_type2(saved.getMoveType2());
            p.setMove_target_string(saved.getMoveTargetString());
            p.setAos(true);
            p.setIos(true);


            if (saved.getMoveTarget() != null && saved.getMoveTarget().getNo() != null)
                p.setMove_target(saved.getMoveTarget().getNo().toString());

            if (saved.getProperties() != null && saved.getProperties().containsKey("imagePath"))
                p.setImage_path1((String) saved.getProperties().get("imagePath"));

            if (saved.getProperties() != null && saved.getProperties().containsKey("iosOnly"))
                p.setAos(false);

            if (saved.getProperties() != null && saved.getProperties().containsKey("aosOnly"))
                p.setIos(false);

            p.setSubject(saved.getSubject());
            p.setContents(saved.getContents());
            p.setPushCase(msg.getPushCase());
            if (saved.getTargetList() != null && saved.getTargetList().size() > 0) {
                p.setReceivers(new ArrayList<User>());
                for (MsgTarget t : saved.getTargetList()) {
                    PushTarget pt = (PushTarget) t;
                    User u = pt.getUser();
                    p.getReceivers().add(u);
					
					/*if (saved.getMoveType2().equals("couponDetail") 
							&& saved.getMoveTarget() != null 
							&& saved.getMoveTarget().getNo() != null) {
						//푸시 내용이 쿠폰인 경우, 쿠폰 선물로 간주 되어야 한다.
						CouponTemplate template = new CouponTemplate();
						template.setNo(saved.getMoveTarget().getNo());
						try {
							couponSvc.gift(u, template);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}*/
                }
            }

            if (saved.getMoveType2().equals("luckyCoupon") && saved.getMoveTarget() != null && saved.getMoveTarget().getNo() != null) {
                // 수신자들에게 쿠폰 선물
            }


            processPush(p, new Firebase.PushResultHandler() {
                @Override
                public void error(Object arg, User user, String cause) {
                    // TODO Auto-generated method stub
                    PushTarget target = new PushTarget();
                    target.setUser(user);
                    target.setStatus("fail");
                    msgSvc.updatePushTargetStatus((MsgOnly) arg, target);
                }

                @Override
                public void send(Object arg, User user) {
                    PushTarget target = new PushTarget();
                    target.setUser(user);
                    target.setStatus("success");
                    msgSvc.updatePushTargetStatus((MsgOnly) arg, target);
                }

                @Override
                public void notFoundPushKey(String pushKey) {
                    if (StringUtils.isNotEmpty(pushKey)) {
                        logger.error("delete push key : " + pushKey);
                        int result = userSvc.deleteInstalledAppByPushKey(pushKey);
                        logger.error("result : " + result);
                    }

                }
            }, msg);

            if (msg.getIncludeMe() == true && msg.getAuthor() != null) {
                PushMsg m = new PushMsg();
                m.setMsg_id("001:" + saved.getNo().toString());
                m.setMove_type1(saved.getMoveType1());
                m.setMove_type2(saved.getMoveType2());
                if (saved.getMoveTarget() != null && saved.getMoveTarget().getNo() != null)
                    m.setMove_target(saved.getMoveTarget().getNo().toString());

                m.setAos(true);
                m.setIos(true);

                if (saved.getProperties() != null && saved.getProperties().containsKey("iosOnly"))
                    p.setAos(false);

                if (saved.getProperties() != null && saved.getProperties().containsKey("aosOnly"))
                    p.setIos(false);

                m.setSubject(saved.getSubject());
                m.setContents(saved.getContents());
                m.setReceivers(new ArrayList<User>());
                m.getReceivers().add(msg.getAuthor());

                if (msg.getProperties() != null && msg.getProperties().get("image_path") != null) {
                    m.setImage_path((String) msg.getProperties().get("image_path"));
                }

                processPush(m, null, msg);
            }
        } else if (msg.getType().equals("lms") || msg.getType().equals("sms")) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("authKey", StoreApplication.SMS_CLIENT_KEY);

            if (saved.getProperties() != null && saved.getProperties().containsKey("senderNo"))
                params.put("sendNumber", (String) saved.getProperties().get("senderNo"));

            params.put("priority", "1");
            params.put("content", saved.getContents());

            if (saved.getTargetList() != null && saved.getTargetList().size() > 0) {
                for (MsgTarget t : saved.getTargetList()) {
                    SmsTarget st = (SmsTarget) t;


                    if (st.getCustomer() == null) {
                        logger.debug("st.getCustomer() is null");
                    }
                    if (st != null && st.getCustomer() != null && st.getCustomer().getMarketingConfig() != null) {
                        //문자 마케팅 가능 상태 확인
                        if (StoreUtil.getMask(st.getCustomer().getMarketingConfig(), Const.MKT_MSG_INDEX) == false) {
                            logger.debug("" + st.getMobile() + " is request reject receive.");
                            st.setStatus("fail");
                            msgSvc.updateSmsTargetStatus((MsgOnly) msg, st);
                            continue;
                        }
                    }

                    params.put("recvNumber", st.getMobile());
                    try {


                        msgSvc.insertSKBroadbandMsg(msgSvc.generateSKBroadbandMsg("PRNUMBER"
                                , params.get("sendNumber")
                                , st.getMobile()
                                , null, saved.getContents()
                                , null, null, null, null, null, null, null, null));

                        st.setStatus("success");

                        msgSvc.updateSmsTargetStatus((MsgOnly) msg, st);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        //재시도 몇번 해야 하는데..
                    }
                }

                if (msg.getIncludeMe() == true && msg.getAuthor() != null) {
                    User me = msg.getAuthor();
                    if (StringUtils.isEmpty(me.getMobile()))
                        me = userSvc.getUser(me.getNo());

                    params.put("recvNumber", me.getMobile());

                    try {

                        msgSvc.insertSKBroadbandMsg(msgSvc.generateSKBroadbandMsg("PRNUMBER"
                                , params.get("sendNumber")
                                , me.getMobile()
                                , null, saved.getContents()
                                , null, null, null, null, null, null, null, null));

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        msg.setStatus("complete");
        msgSvc.updateMsgStatus(msg);


    }

    private void processCoupon(Coupon coupon) {
        //쿠폰 상태가 requse 이면 점주의 점주용 앱으로 Push
        //쿠폰 상태가 used이면 쿠폰 사용자의 사용자 앱으로 Push
    }

    //bulk 바꿀꺼
    private void processPush(PushMsg msg, Firebase.PushResultHandler resultHandler, Object handlerArg) {

        logger.debug("MsgConsumer : processPush : in [ ");
        if (msg.getTargets() == null) {
            logger.debug("MsgConsumer : processPush add tagetList: ");
            msg.setTargets(new ArrayList<UserApp>());

            for (User receiver : msg.getReceivers()) {
                List<UserApp> appList = userSvc.getUserApp(receiver);

                if (resultHandler != null) {
                    boolean error = true;
                    for (UserApp app : appList) {
                        if (isSendPossible(msg.getAppType(), msg.getPushCase(), app)) {
                            if (error == true)
                                error = false;


                            if (((msg.getIos() == null || msg.getIos() == true) && (msg.getAos() == null || msg.getAos() == true))
                                    || (msg.getIos() && "ios".equals(app.getPlatform()))
                                    || (msg.getAos() && "aos".equals(app.getPlatform())))
                                msg.getTargets().add(app);
                        }
                    }

                    //사용자의 앱 정보 중에서 유효한 앱 정보가 하나도 없는 경우에는 실패 보고
                    if (error) {

                        logger.error("MsgConsumer : processPush : error : " + appList.toString());
                        resultHandler.error(handlerArg, receiver, "not found validation app");
                    }

                } else {
                    msg.getTargets().addAll(appList);
                }
            }


            if (msg.getTargets() != null && msg.getTargets().size() > 0)
                processPush(msg, resultHandler, handlerArg);


            return;
        }

        logger.debug("MsgConsumer : processPush sending...: ");
        Map<String, String> data = new HashMap<String, String>();
        if (msg.getMsgNo() != null)
            data.put("msgNo", "" + msg.getMsgNo());

        if (null != msg.getSubject())
            data.put("title", msg.getSubject());
        if (null != msg.getContents())
            data.put("contents", msg.getContents());
        if (null != msg.getMove_type1())
            data.put("move_type1", msg.getMove_type1());
        if (null != msg.getMove_type2())
            data.put("move_type2", msg.getMove_type2());
        if (null != msg.getMove_target())
            data.put("move_target", msg.getMove_target());
        if (null != msg.getMove_target_string())
            data.put("move_target_string", msg.getMove_target_string());
        if (null != msg.getMsg_id())
            data.put("msg_id", msg.getMsg_id());

        if (msg.getImage_path() != null) {
            data.put("image_path", msg.getImage_path());
        } else if (msg.getSender() != null && msg.getSender().getNo() > 3) {
            if (msg.getSender().getPage() != null && !StringUtils.isEmpty(msg.getSender().getPage().getThumbnail()))
                data.put("image_path", msg.getSender().getPage().getThumbnail());
            else if (msg.getSender().getProfileImage() != null && !StringUtils.isEmpty(msg.getSender().getProfileImage().getUrl()))
                data.put("image_path", msg.getSender().getProfileImage().getUrl());
        }

        if (msg.getImage_path1() != null)
            data.put("image_path1", msg.getImage_path1());

        try {
            resultHandler = new Firebase.PushResultHandler() {
                @Override
                public void error(Object arg, User user, String cause) {
                    // TODO Auto-generated method stub
                    PushTarget target = new PushTarget();
                    target.setUser(user);
                    target.setStatus("fail");
                    msgSvc.updatePushTargetStatus((MsgOnly) arg, target);
                }

                @Override
                public void send(Object arg, User user) {
                    PushTarget target = new PushTarget();
                    target.setUser(user);
                    target.setStatus("success");
                    msgSvc.updatePushTargetStatus((MsgOnly) arg, target);
                }

                @Override
                public void notFoundPushKey(String pushKey) {
                    if (StringUtils.isNotEmpty(pushKey)) {
                        userSvc.deleteInstalledAppByPushKey(pushKey);
                    }
                }
            };
            sendPush(data, msg.getAppType(), msg.getTargets(), resultHandler, handlerArg);
        } catch (Exception ex) {
            logger.error(AppUtil.excetionToString(ex));
        }
    }

    private void sendPush(Map<String, String> data, String appType, List<UserApp> targetList, Firebase.PushResultHandler resultHandler, Object handlerArg) throws Exception {

        ArrayList<UserApp> nativeTargetList = new ArrayList<UserApp>();
        ArrayList<UserApp> webTargetList = new ArrayList<UserApp>();
        for (UserApp userApp : targetList) {
            if (userApp.getDevice().getPlatform().equals("ios") || userApp.getDevice().getPlatform().equals("aos") || userApp.getDevice().getPlatform().equals("pcweb")) {
                nativeTargetList.add(userApp);
            } else {
                webTargetList.add(userApp);
            }
        }


        if (nativeTargetList.size() > 0) {
            if (appType.equals(Const.APP_TYPE_USER)) {
                Firebase firebase = Firebase.getUserInstance();
                logger.info("Firebase.getUserInstance() : " + firebase.toString());
                firebase.sendMulticastPush(data, nativeTargetList, resultHandler, handlerArg);
            } else if (appType.equals(Const.APP_TYPE_BIZ)) {
                Firebase firebase = Firebase.getBizInstance();
                logger.info("Firebase.getBizInstance() : " + firebase.toString());
                firebase.sendMulticastPush(data, nativeTargetList, resultHandler, handlerArg);
            } else if (appType.equals(Const.APP_TYPE_LUCKYBOL)) {
                Firebase firebase = Firebase.getLuckyBolInstance();
                logger.info("Firebase.getLuckyBolInstance() : " + firebase.toString());
                firebase.sendMulticastPush(data, nativeTargetList, resultHandler, handlerArg);
            }
        }

//        if( webTargetList.size() > 0 ) {
//            VertXClient vertXClient = VertXClient.getInstance() ;
//            logger.info("VertXClient.getInstance() : " + vertXClient.toString());
//            vertXClient.sendPush(data, webTargetList, resultHandler, handlerArg) ;
//        }
    }

    private void processQueueRequest(QueueRequest req) {
        if (req.getJobType().equals("postingPush")) {
            Article post = (Article) req.getMsg();
            processPosting(post);
        } else if (req.getJobType().equals("print")) {
            Article post = (Article) req.getMsg();
            logger.info(post.getContents());
        }
    }

    private void processComment(Comment comment) {
        Comment saved = postSvc.getComment(comment);
        Article article = postSvc.getArticle(null, saved.getPost());
        insertCommentMsgInBox(article, saved);
    }

    private void insertCommentMsgInBox(Article article, Comment comment) {
        if (article == null && !Const.ARTICLE_TYPE_REVIEW.equals(article.getType()))
            return;

        if (article.getAuthor().getNo().equals(comment.getAuthor().getNo()))
            return;

        MsgOnly msg = new MsgOnly();
        msg.setStatus("nosend");
        msg.setType("system");
        msg.setMoveType1("inner");
        msg.setMoveType2("postDetail");
        msg.setMoveTarget(article);
        msg.setPushCase(Const.PUSH_FORCED);
        msg.setSubject(article.getAuthor().getDisplayName() + " 님이 회원님의 게시물에 댓글을 남겼습니다.");
        msg.setContents(msg.getSubject());
        msg.setAppType(Const.APP_TYPE_USER);
        try {
            queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, article.getAuthor(), Const.APP_TYPE_USER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void processPosting(Article article) {
        logger.debug("MsgConsumer : processPosting : " + article.toString());
        Article saved = postSvc.getArticle(null, article);
        //MGK_CHG [
        if (saved.getType().equals("pr"))
            insertPlusMsgInBox(saved);
        //MGK_CHG ]
    }

    private void insertPlusMsgInBox(Article article) {

        logger.debug("MsgConsumer : insertPlusMsgInBox : " + article.toString());
        if (article.getAuthor().getPage() == null || article.getAuthor().getPage().getNo() == null) {
            logger.error("Page Seq No not found.");
            return;
        }


        List<User> fanList = fanSvc.getFanAll(article.getAuthor().getPage());
        if (fanList == null && fanList.size() == 0)
            return;

        for (User fan : fanList) {
            MsgOnly msg = new MsgOnly();
            msg.setStatus("nosend");
            msg.setType("system");
            msg.setMoveType1("inner");
            msg.setMoveType2("postDetail");
            msg.setMoveTarget(article);
            msg.setPushCase(Const.PUSH_FORCED);
            msg.setSubject(article.getAuthor().getPage().getName() + " 에서 새로운 게시글을 등록하였습니다.");
            msg.setContents(msg.getSubject());
            msg.setAppType(Const.APP_TYPE_USER);
            try {
                queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, fan, Const.APP_TYPE_USER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
    private CcsSenderPool createPool(App app) {
        logger.debug("MsgConsumer : createPool : " + app.toString());

        Map<String, Object> cfg = app.getServerProp();
        if (cfg == null
                || !cfg.containsKey("apiKey")
                || !cfg.containsKey("senderId")) {
            logger.warn(app.getAppKey() + " serverProp invalid. need apiKey and senderId values");
            return null;
        }

        String apiKey = (String) cfg.get("apiKey");
        long senderId = (Long) cfg.get("senderId");
        int maxCount = 10;
        if (cfg.containsKey("maxCount"))
            maxCount = (Integer) cfg.get("maxCount");

        int initCount = 1;
        if (cfg.containsKey("initCount"))
            initCount = (Integer) cfg.get("initCount");

        int maxWaitCount = 1;
        if (cfg.containsKey("maxWaitCount"))
            maxWaitCount = (Integer) cfg.get("maxWaitCount");

        logger.debug("createPool: appKey=" + app.getAppKey()
                + ", apiKey = " + apiKey
                + ", senderId = " + senderId);

        return CcsSenderPoolManager.getInstance(this, PUSH_SERVER_ADDR, PUSH_SERVER_PORT)
                .createPool(app.getAppKey(), apiKey, senderId, maxCount, initCount, maxWaitCount);

    }
    */


    private boolean isSendPossible(String appType, Integer pushCase, UserApp ua) {
        if (StringUtils.isEmpty(ua.getPushKey()))
            return false;

        if (ua.getPushActivate() != null && ua.getPushActivate() == false)
            return false;

        if (pushCase != Const.PUSH_FORCED && (ua.getPushMask() == null || ua.getPushMask().charAt(pushCase - 1) == '0'))
            return false;

        if (appType == null || appType.equals("all"))
            return true;

        logger.debug("isSendPossible : appType : " + appType + ", ua.getType : " + ua.getType());
        if (appType.equals(Const.APP_TYPE_USER)) {
            return (ua.getType().equals(Const.APP_TYPE_USER) || ua.getType().equals("pplus"));
        } else {
            return appType.equals(ua.getType());
        }


    }

    /*
    private void sendMsg(Map<String, Object> data, UserApp ua, PushResultHandler resultHandler, Object handlerArg) throws Exception {

        logger.debug("MsgConsumer : sendMsg(pcweb) data : " + data.toString());
        logger.debug("MsgConsumer : sendMsg(pcweb) UserApp : " + ua.toString());

        if(ua.getAppKey().equalsIgnoreCase("com.pplus.prbiz_web")){
            logger.debug("MsgConsumer : appkey : "+ua.getAppKey()+", pushkey : " + ua.getPushKey());

            CloseableHttpClient client = HttpClients.createDefault();
            Gson gson = new Gson() ;
            HashMap<String, Object> pushMap = new HashMap<String, Object>() ;
            pushMap.put("data", data) ;
            pushMap.put("to", ua.getPushKey()) ;

            HttpPost httpPost = new HttpPost(FCM_PUSH_SEND_URL);
            StringEntity entity = new StringEntity(gson.toJson(pushMap).toString(), "UTF-8") ;
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json; charset=utf-8");
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Authorization","key=" + FCM_PUSH_API_KEY_BIZ) ;

            CloseableHttpResponse response = client.execute(httpPost);
            logger.debug("sendMsg(pcweb) response : " + response.toString()) ;
            client.close();
            return ;
        }


        CcsSender sender = null;

        CcsSenderPool pool = CcsSenderPoolManager
                .getInstance(this, PUSH_SERVER_ADDR, PUSH_SERVER_PORT)
                .getPool(ua.getAppKey());

        if (pool == null) {
            pool = createPool(ua);
        }

        if (pool == null) {
            logger.warn("pool is null - appKey=" + ua.getAppKey());
            return;
        }

        sender = pool.getSender(10000);
        if (sender == null) {
            logger.debug("requeued. get sender failure. user=" + ua.getUser().getName() + ", appKey=" + ua.getAppKey());
            //producer.push(model);
            return;
        }

        Map<String, Object> extra = null;

        if (ua.getPlatform().equals("ios")) {
            extra = new HashMap<String, Object>();
            extra.put("ios", true);
        }

        try {
            if (resultHandler != null)
                resultHandler.send(handlerArg, ua.getUser());


            sender.send(ua.getPushKey(), generateMsgId((String) data.get("msg_id"), ua), data, extra);
        } catch (Exception ex) {
            if (resultHandler != null)
                resultHandler.error(handlerArg, ua.getUser(), ex.getMessage());
            ex.printStackTrace();
        } finally {
            CcsSenderPoolManager.getInstance(this, PUSH_SERVER_ADDR, PUSH_SERVER_PORT).freeSender(ua.getAppKey(), sender);
        }
    }
    */

    private String generateMsgId(String msgId, UserApp ua) {
        StringBuffer buf = new StringBuffer(msgId);
        if (ua != null && ua.getUser() != null && ua.getUser().getNo() != null) {
            buf.append(msgIdDelimit).append(ua.getUser().getNo());
            if (ua.getDevice() != null && ua.getDevice().getNo() != null) {
                buf.append(msgIdDelimit).append(ua.getDevice().getNo());
            }
        }
        return buf.toString();
    }


    /*
    @Override
    public void ccsSuccess(Map<String, Object> result) {
        String msgId = (String) result.get("message_id");
        if (msgId == null || msgId.length() == 0) {
            logger.error("ccsSuccess but not found msg id.");
            return;
        }

        String[] ar = msgId.split("\\:");
        if (ar.length >= 3) {
            int idx = 0;
            String type = null;
            if (ar.length >= 4)
                type = ar[idx++];

            Long msgNo = Long.parseLong(ar[idx++]);
            Long userNo = Long.parseLong(ar[idx++]);
            Long deviceNo = Long.parseLong(ar[idx++]);

            if (type.equals("001")) {
                //Push 전송 결과 DB에 업데이트
                MsgOnly msg = new MsgOnly();
                msg.setNo(msgNo);
                PushTarget target = new PushTarget();
                target.setUser(new User());
                target.getUser().setNo(userNo);
                target.setStatus("success");
                msgSvc.updatePushTargetStatus(msg, target);
            }

            logger.debug("ccsSuccess msgNo: " + msgNo + ", userNo: " + userNo + ", deviceNo:" + deviceNo);
        } else {
            logger.error("ccsSuccess but invalid msgId: {}", msgId);
        }
    }

    @Override
    public void ccsFailure(Map<String, Object> result) {
        // TODO Auto-generated method stub
        String msgId = (String) result.get("message_id");
        if (msgId == null || msgId.length() == 0) {
            logger.error("ccsFailure but not found msg id.");
            return;
        }
        String error = null, errorDesc = null;
        String[] ar = msgId.split("\\:");
        if (ar.length >= 3) {
            int idx = 0;
            String type = "0";
            if (ar.length >= 4)
                type = ar[idx++];

            Long msgNo = Long.parseLong(ar[idx++]);
            Long userNo = Long.parseLong(ar[idx++]);
            Long deviceNo = Long.parseLong(ar[idx++]);
            logger.debug("ccsFailure msgNo: " + msgNo + ", userNo: " + userNo + ", deviceNo:" + deviceNo);

            if (type.equals("001")) {
                //Push 전송 결과 DB에 업데이트
                MsgOnly msg = new MsgOnly();
                msg.setNo(msgNo);
                PushTarget target = new PushTarget();
                target.setUser(new User());
                target.getUser().setNo(userNo);
                target.setStatus("fail");
                msgSvc.updatePushTargetStatus(msg, target);
            }

            if (result.containsKey("error")) {
                error = (String) result.get("error");
                if (result.containsKey("error_description")) {
                    errorDesc = (String) result.get("error_description");
                    logger.debug("ccsFailure error: " + error + ", errorDesc: " + errorDesc);
                } else {
                    logger.debug("ccsFailure error: " + error);
                }

                if (MsgConsumer.needDeleteUserDevice(error)) {
                    logger.debug("delete userDevice: userNo=" + userNo + ", deviceNo=" + deviceNo);
                    //deleteUserDevice(userNo, deviceNo);
                }
            }
        } else {
            logger.error("ccsFailure but invalid msgId: {}", msgId);
        }
    }
    */

    private static String[] DELETEDEVICE_CODES = {
            "DEVICE_UNREGISTERED",
            "BAD_REGISTRATION"
    };

    private static boolean needDeleteUserDevice(String errCode) {
        for (String code : MsgConsumer.DELETEDEVICE_CODES) {
            if (code.equals(errCode))
                return true;
        }
        return false;
    }

}
