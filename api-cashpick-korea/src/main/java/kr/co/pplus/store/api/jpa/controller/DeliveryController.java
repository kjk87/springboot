//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.annotation.AgentSessionUser;
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.delivery.*;
//import kr.co.pplus.store.api.jpa.repository.PageRepository;
//import kr.co.pplus.store.api.jpa.repository.delivery.*;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.*;
//import kr.co.pplus.store.mvc.service.AgentService;
//import kr.co.pplus.store.mvc.service.QueueService;
//import kr.co.pplus.store.queue.MsgProducer;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.*;
//import kr.co.pplus.store.util.SecureUtil;
//import kr.co.pplus.store.util.StoreUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Isolation;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@RestController
//public class DeliveryController extends RootController {
//    private Logger logger = LoggerFactory.getLogger(DeliveryController.class);
//
//    @Autowired
//    DeliveryGoodsRepository deliveryGoodsRepository;
//
//    @Autowired
//    DeliveryRepository deliveryRepository;
//
//    @Autowired
//    AgentService agentService;
//
//    @Autowired
//    DeliveryDetailRepository deliveryDetailRepository;
//
//    @Autowired
//    PageRepository pageRepository;
//
//    @Autowired
//    QueueService queueSvc ;
//
//
//    @Autowired
//    MsgProducer producer;
//
//    @PersistenceContext(unitName = "store")
//    private EntityManager entityManager;
//
//    @Value("${STORE.TYPE}")
//    String storeType = "LOCAL" ;
//
//
//    public static final String PPLUS = "피플러스";
//    public static final String BAEMIN = "배달의민족";
//    public static final String YOGIYO = "요기요";
//    public static final String BAEDALTONG = "배달통";
//
//
//    //-- LnK 등 POS Agent 사용자를 위한 API
//    @AgentSessionUser
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery/lnk")
//     public Map<String,Object> addDeliveryLnKToQueue(Agent agent, @RequestBody HashMap<String, Object> map) throws ResultCodeException {
//
//        try {
//            map.put("agent", agent) ;
//            producer.push(map);
//            return result(Const.E_SUCCESS, "row", "success");
//        } catch(Exception e) {
//            logger.error(AppUtil.excetionToString(e)) ;
//            return result(Const.E_UNKNOWN, "row", e.getMessage());
//        }
//     }
//
//    @AgentSessionUser
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery/lnk/direct")
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidDeliveryException.class)
//    public Map<String,Object>  addDeliveryLnK(Agent agent, @RequestBody Map<String, Object> map) throws Exception{
//
//        try {
//
//            String deliveryCompany = (String) map.get("deli_comp");
//            DeliveryCompanyType deliveryCompanyType = null;
//            switch (deliveryCompany) {
//                case PPLUS:
//                    deliveryCompanyType = DeliveryCompanyType.PPLUS;
//                    break;
//                case BAEMIN:
//                    deliveryCompanyType = DeliveryCompanyType.BAEMIN;
//                    break;
//                case YOGIYO:
//                    deliveryCompanyType = DeliveryCompanyType.YOGIYO;
//                    break;
//                case BAEDALTONG:
//                    deliveryCompanyType = DeliveryCompanyType.BAEDALTONG;
//                    break;
//                default:
//                    throw new UnknownException("알수 없는 배달 주문 업체 입니다.");
//            }
//
//            // 상점정보 체크 후 신규 상점이면 table 에 추가
//            String shopId = (String) map.get("shop_id");
//            Long pageSeqNo = 0L ;
//            try {
//                pageSeqNo = Long.parseLong(SecureUtil.decryptMobileNumber(shopId)) ;
//            } catch(Exception e){
//                // ToDo : 테스트 코드 상용에서는 제거되어야 하는 코드
//                if( storeType.equals("STAGE") || storeType.equals("DEV"))
//                    pageSeqNo = 1000159L ;
//                else
//                    throw e ;
//            }
//            /*
//            AgentShop agentShop = agentShopRepository.findByPageSeqNo(pageSeqNo);
//            if (agentShop == null) {
//                agentShop = new AgentShop();
//                agentShop.setSeqNo(null);
//                agentShop.setAgentSeqNo(agent.getNo());
//                agentShop.setId(shopId);
//                if (map.get("shop_name") != null) {
//                    agentShop.setName((String) map.get("shop_name"));
//                } else {
//                    agentShop.setName(shopId);
//                }
//                agentShop.setPageSeqNo(pageSeqNo) ;
//
//                agentShop = agentShopRepository.saveAndFlush(agentShop);
//            }
//            */
//
//            // Agent agent = (Agent)map.get("agent") ;
//            Delivery delivery = new Delivery();
//            delivery.setSeqNo(null);
//            delivery.setAgentSeqNo(agent.getNo());
//            delivery.setCompanySeqNo((long) deliveryCompanyType.getType());
//            delivery.setPageSeqNo(pageSeqNo);
//            delivery.setId((String) map.get("deli_num"));
//            delivery.setClientAddress((String) map.get("deli_address"));
//            delivery.setClientTel((String) map.get("deli_tel"));
//            delivery.setClientMemo((String) map.get("deli_desc"));
//            delivery.setTotalPrice((float) Integer.parseInt((String) map.get("total_money")));
//            delivery.setPayment((String) map.get("payment"));
//            delivery.setRegDatetime((String)map.get("deli_order_day"));
//            delivery.setModDatetime((String)map.get("deli_order_day"));
//            logger.debug("/delivery[POST] - delivery : " + delivery);
//            delivery = deliveryRepository.saveAndFlush(delivery);
//
//
//            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("item");
//            if (list != null) {
//                for (int i = 0; i < list.size(); i++) {
//                    DeliveryGoods deliveryGoods = new DeliveryGoods();
//                    deliveryGoods.setSeqNo(null);
//                    deliveryGoods.setDeliverySeqNo(delivery.getSeqNo());
//                    deliveryGoods.setName((String) list.get(i).get("item_title"));
//                    try {
//                        if (list.get(i).get("item_cnt") != null)
//                            deliveryGoods.setCount(Integer.parseInt((String) list.get(i).get("item_cnt")));
//                        if (list.get(i).get("item_money") != null)
//                            deliveryGoods.setPrice((float) Integer.parseInt((String) list.get(i).get("item_money")));
//                    } catch(Exception e){
//                        //do nothings.
//                    }
//
//                    logger.debug("/delivery[POST] deliveryGoods[" + i + "] : " + deliveryGoods);
//                    deliveryGoods = deliveryGoodsRepository.saveAndFlush(deliveryGoods);
//
//                }
//            }
//
//            MsgOnly msg = new MsgOnly() ;
//            msg.setIncludeMe(false);
//            msg.setInput(Const.MSG_INPUT_SYSTEM);
//            msg.setStatus(Const.MSG_STATUS_READY) ;
//            msg.setType(Const.MSG_TYPE_PUSH);
//            msg.setMoveType1(Const.MOVE_TYPE_INNER);
//            msg.setMoveType2(Const.MOVE_TYPE_ORDER) ;
//            msg.setMoveTarget(new NoOnlyKey(delivery.getSeqNo()));
//            msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//            msg.setAppType(Const.APP_TYPE_BIZ);
//            /*
//            if( imgPath != null ) {
//
//                msg.setProperties(new HashMap<String, Object>());
//                msg.getProperties().put("image_path", imgPath);
//            }
//            */
//
//            msg.setContents("구매자 전화번호 : "  + delivery.getClientTel() + "," +
//                    "구매자 : "  + deliveryCompany + ", 구매금액 : "  + delivery.getTotalPrice());
//
////            msg.setMoveTargetString(buy.getTitle());
//            msg.setSubject(deliveryCompany + ":" + "주문이 접수되었습니다.") ;
//
//            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(delivery.getPageSeqNo()) ;
//            User user = new User() ;
//            user.setNo(page.getMemberSeqNo()) ;
//            queueSvc.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_BIZ);
//
//            return result(Const.E_SUCCESS, "row", delivery);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidDeliveryException("/delivery", "insert error");
//        }
//    }
//
//    @AgentSessionUser
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery/lnk")
//    public Map<String,Object> selectDeliveryLnk(Session session, Pageable pageable,
//                                       @RequestParam(value = "companySeqNo", required = true) Long companySeqNo,
//                                       @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
//                                       @RequestParam(value = "startDuration", required = true) String startDuration,
//                                       @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Page<Delivery> page = null ;
//        try {
//
//            if( pageSeqNo == null || startDuration == null ) {
//                throw new InvalidArgumentException("selectDeliveryTotalPrice()") ;
//            }
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//            ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//            Date startTime = Date.from(zdt.toInstant());
//            Date endTime = null ;
//            if( endDuration != null && !endDuration.trim().isEmpty() ) {
//                zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//
//            page = deliveryRepository.findAllBy(companySeqNo, pageSeqNo, startTime, endTime, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidArgumentException("/delivery[GET]", "select error");
//        }
//
//    }
//
//
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery")
//    public Map<String,Object> selectDelivery(Session session, Pageable pageable,
//                                    @RequestParam(value = "companySeqNo", required = true) Long companySeqNo,
//                                    @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
//                                    @RequestParam(value = "startDuration", required = true) String startDuration,
//                                    @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Page<Delivery> page = null ;
//        try {
//
//            if( pageSeqNo == null || startDuration == null ) {
//                throw new InvalidArgumentException("selectDelivery()") ;
//            }
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//            ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//            Date startTime = Date.from(zdt.toInstant());
//            Date endTime = null ;
//            if( endDuration != null && !endDuration.trim().isEmpty() ) {
//                zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//
//            page = deliveryRepository.findAllBy(companySeqNo, pageSeqNo, startTime, endTime, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidArgumentException("/delivery[GET]", e);
//        }
//
//    }
//
//
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery/detail")
//    public Map<String,Object> selectDeliveryDetail(Session session, Pageable pageable,
//                                    @RequestParam(value = "companySeqNo", required = true) Long companySeqNo,
//                                    @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
//                                    @RequestParam(value = "startDuration", required = true) String startDuration,
//                                    @RequestParam(value = "endDuration", required = false) String endDuration) throws ResultCodeException {
//
//        Page<DeliveryDetail> page = null ;
//
//        try {
//
//            if( pageSeqNo == null || startDuration == null ) {
//                throw new InvalidArgumentException("selectDeliveryDetail()") ;
//            }
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//            ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//            Date startTime = Date.from(zdt.toInstant());
//            Date endTime = null ;
//            if( endDuration != null && !endDuration.trim().isEmpty() ) {
//                zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//
//            page = deliveryDetailRepository.findAllBy(companySeqNo, pageSeqNo, startTime, endTime, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidArgumentException("/delivery/detail[GET]", e);
//        }
//
//    }
//
//    //==== PPLUS 회원 가입된 회원을 위한 API ===========================================================================
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery/total")
//    public Map<String,Object> selectDeliveryTotal(Session session,
//                                              @RequestParam(value="pageSeqNo", required=true) Long pageSeqNo,
//                                              @RequestParam(value="startDuration", required=true) String startDuration,
//                                              @RequestParam(value="endDuration", required=false) String endDuration) throws ResultCodeException {
//
//        try {
//            if( pageSeqNo == null || startDuration == null ) {
//                throw new InvalidArgumentException("selectDeliveryTotal()") ;
//            }
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//            ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//            Date startTime = Date.from(zdt.toInstant());
//            Date endTime = null ;
//            if( endDuration != null && !endDuration.trim().isEmpty() ) {
//                zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//
//
//            List<Map<String,Object>> deliveryTotals = deliveryRepository.selectDeliveryTotal(pageSeqNo, startTime, endTime);
//            return result(Const.E_SUCCESS, "rows", deliveryTotals);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            if( e instanceof  ResultCodeException ) {
//                throw e ;
//            } else {
//                throw new InvalidArgumentException("/delivery[GET]", e);
//            }
//        }
//    }
//
//    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/delivery/company/total")
//    public Map<String,Object> selectDeliveryCompanyTotal(Session session,
//                                              @RequestParam(value="pageSeqNo", required=true) Long pageSeqNo,
//                                              @RequestParam(value="companySeqNo", required=false) Long companySeqNo,
//                                              @RequestParam(value="startDuration", required=true) String startDuration,
//                                              @RequestParam(value="endDuration", required=false) String endDuration) throws ResultCodeException {
//
//        try {
//            if( pageSeqNo == null || startDuration == null ) {
//                throw new InvalidArgumentException("selectDeliveryCompanyTotal()") ;
//            }
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//            ZonedDateTime zdt = ZonedDateTime.parse(startDuration, formatter);
//            Date startTime = Date.from(zdt.toInstant());
//            Date endTime = null ;
//            if( endDuration != null && !endDuration.trim().isEmpty() ) {
//                zdt = ZonedDateTime.parse(endDuration, formatter);
//                endTime = Date.from(zdt.toInstant());
//            }
//
//            List<Map<String,Object>> deliveryCompanyTotals = deliveryRepository.selectDeliveryCompanyTotal(pageSeqNo, companySeqNo, startTime, endTime);
//            return result(Const.E_SUCCESS, "rows", deliveryCompanyTotals);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            if( e instanceof  ResultCodeException ) {
//                throw e ;
//            } else {
//                throw new InvalidArgumentException("/delivery[GET]", e);
//            }
//        }
//    }
//
//
//}
