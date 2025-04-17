package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.controller.BootPayApi;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Cancel;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Token;
import kr.co.pplus.store.api.jpa.model.bootpay.response.BootPayCancelResponse;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResToken;
import kr.co.pplus.store.api.jpa.model.ftlink.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.mvc.service.SmsService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.SmsMsg;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.FTLinkPayApi;
import kr.co.pplus.store.util.SetID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class OrderPurchaseService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(OrderPurchaseService.class);

    @Autowired
    OrderPurchaseRepository orderPurchaseRepository;

    @Autowired
    OrderPurchaseMenuRepository orderPurchaseMenuRepository;

    @Autowired
    OrderPurchaseMenuOptionRepository orderPurchaseMenuOptionRepository;

    @Autowired
    OrderPurchaseMenuWithCountRepository orderPurchaseMenuWithCountRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MenuService menuService;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    PageWithAvgEvalRepository pageWithAvgEvalRepository;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    LpngCallbackRepository lpngCallbackRepository;

    @Autowired
    LpngCallbackResultRepository lpngCallbackResultRepository;

    @Autowired
    CartService cartService;

    @Autowired
    OrderCallUserInfoRepository orderCallUserInfoRepository;

    @Autowired
    OrderPurchaseDetailRepository orderPurchaseDetailRepository;

    @Autowired
    OrderPurchaseNativeRepository orderPurchaseNativeRepository;

    @Autowired
    UserService userService;

    @Autowired
    SmsService smsService;

    @Autowired
    NotificationBoxService notificationBoxService;

    @Autowired
    BolService bolService;

    @Autowired
    MemberService memberService;

    @Autowired
    CashLogRepository cashLogRepository;

    @Autowired
    QueueService queueService;


    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";

    @Value("${STORE.TYPE}")
    private String storeType = "STAGE";

    @Value("${STORE.BOOTPAY.CASH_APP_ID}")
    String CASH_APP_ID = "";

    @Value("${STORE.BOOTPAY.CASH_PRIVATE_KEY}")
    String CASH_PRIVATE_KEY = "";

    @Value("${STORE.DANAL.CPID}")
    String CPID = "9810030929";

    @Value("${STORE.DAOU.NOMEMBER.CPID}")
    String NOMEMBER_CPID = "CTS16541";

    @Value("${STORE.DAOU.NOMEMBER.AUTHORIZATION}")
    String NOMEMBER_AUTHORIZATION = "2e48f6bac5a7b243bed4b647b9387e485ffc148473124aabf6f08953ea3370ac";

    @Value("${STORE.DAOU.NOMEMBER.READY_URL}")
    String NOMEMBER_READY_URL = "https://apitest.payjoa.co.kr/pay/ready";

    private String adminStageUrl = "https://stg-www.plusmember.co.kr/";
    private String adminProdUrl = "https://www.plusmember.co.kr/";

    private final String BASE_URL = "https://api.bootpay.co.kr/";
    private final String URL_ACCESS_TOKEN = BASE_URL + "request/token";
    private final String URL_VERIFY = BASE_URL + "receipt";
    private final String URL_CANCEL = BASE_URL + "cancel";


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

    public OrderPurchaseDetail getOrderPurchase(Long seqNo) {
        return orderPurchaseDetailRepository.findBySeqNo(seqNo);
    }

    public org.springframework.data.domain.Page<OrderPurchaseDetail> getOrderPurchaseListByMemberSeqNo(Long memberSeqNo, Pageable pageable) {
        List<Long> salesTypeList = new ArrayList<>();
        salesTypeList.add(1L);
        salesTypeList.add(2L);
        salesTypeList.add(5L);
        return orderPurchaseDetailRepository.findAllByMemberSeqNoAndStatusGreaterThanEqualAndSalesTypeIn(memberSeqNo, OrderPurchaseStatus.AFTER_PAY.getStatus(), salesTypeList, pageable);
    }

    public org.springframework.data.domain.Page<OrderPurchaseDetail> getTicketPurchaseListByMemberSeqNo(Long memberSeqNo, Pageable pageable) {
        List<Long> salesTypeList = new ArrayList<>();
        salesTypeList.add(6L);
        return orderPurchaseDetailRepository.findAllByMemberSeqNoAndStatusGreaterThanEqualAndSalesTypeIn(memberSeqNo, OrderPurchaseStatus.AFTER_PAY.getStatus(), salesTypeList, pageable);
    }

    public Map<String, Object> getOrderPurchaseTotalData(Long pageSeqNo, String startDateTime, String endDateTime) {

        Float totalPrice = orderPurchaseDetailRepository.sumPrice(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), startDateTime, endDateTime);
        Long totalCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), startDateTime, endDateTime);

        Map<String, Object> map = new HashMap<>();

        map.put("totalPrice", totalPrice.intValue());
        map.put("totalCount", totalCount);

        return map;
    }

    public Map<String, Object> getOrderPurchaseStatistics(Long pageSeqNo, String startDateTime, String endDateTime) {

        Long totalCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), startDateTime, endDateTime);
        Long appCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndAppTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), "pplus", startDateTime, endDateTime);
        Long csCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndAppTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), "cs", startDateTime, endDateTime);
        Long visitCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndSalesTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), 1l, startDateTime, endDateTime);
        Long packageCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndSalesTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), 5l, startDateTime, endDateTime);
        Long deliveryCount = orderPurchaseDetailRepository.countByPageSeqNoAndStatusAndSalesTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), 2l, startDateTime, endDateTime);

        Float totalPrice = orderPurchaseDetailRepository.sumPrice(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), startDateTime, endDateTime);
        Float avgPrice = orderPurchaseDetailRepository.avgPrice(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), startDateTime, endDateTime);

        Map<String, Object> map = new HashMap<>();

        map.put("totalCount", totalCount);
        map.put("appCount", appCount);
        map.put("csCount", csCount);
        map.put("visitCount", visitCount);
        map.put("packageCount", packageCount);
        map.put("deliveryCount", deliveryCount);
        map.put("totalPrice", totalPrice.intValue());
        map.put("avgPrice", avgPrice.intValue());

        return map;
    }

    public List<OrderPurchaseMenuWithCount> getPopularOrderPurchaseMenuList(Long pageSeqNo, String startDateTime, String endDateTime) {
        return orderPurchaseMenuWithCountRepository.findAllOrderPurchaseGroupByMenu(pageSeqNo, OrderPurchaseStatus.COMPLETE.getStatus(), startDateTime, endDateTime);

    }

    public org.springframework.data.domain.Page<OrderPurchaseDetail> getOrderPurchaseListByPageSeqNo(Long pageSeqNo, String status, Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        String[] statuss = status.split(",");
        for (String item : statuss) {
            statusList.add(Integer.valueOf(item));
        }

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String endDateTime = dateFormat.format(calendar.getTime()) + " 23:59:59";
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String startDateTime = dateFormat.format(calendar.getTime()) + " 00:00:01";

        return orderPurchaseDetailRepository.findAllByPageSeqNoAndStatusInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, statusList, startDateTime, endDateTime, pageable);
    }

    public org.springframework.data.domain.Page<OrderPurchaseDetail> getOrderPurchaseListByPageSeqNoAndDate(Long pageSeqNo, String startDateTime, String endDateTime, Pageable pageable) {
        return orderPurchaseDetailRepository.findAllByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, startDateTime, endDateTime, pageable);
    }

    public org.springframework.data.domain.Page<OrderPurchaseNative> getTicketPurchaseListByPageSeqNo(HttpServletRequest request, Long pageSeqNo, Integer status, String nickName, String phone, String startDateTime, String endDateTime, Pageable pageable) {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);

        return orderPurchaseNativeRepository.findAllTicketByPageSeqNo(pageSeqNo, status, nickName, phone, startDateTime, endDateTime, pageable);
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public OrderPurchase purchase(User user, OrderPurchase orderPurchase) throws ResultCodeException {

        if (!orderPurchase.isValidOrderId(REDIS_PREFIX)) {
            throw new InvalidArgumentException("The orderId is not valid ");
        }

        PageWithAvgEval page = pageWithAvgEvalRepository.findBySeqNo(orderPurchase.getPageSeqNo());

        if (page.getBusinessCategory().equals("restaurant")) {
            if ((page.getBusinessHoursType() != 4 && !page.getIsBusinessHour()) || page.getIsTimeOff() || page.getIsDayOff() || !page.getOrderable()) {
                throw new NotPossibleTimeException();
            }
        }

        String currentDateTime = AppUtil.localDatetimeNowString();
        orderPurchase.setSeqNo(null);

        if (orderPurchase.getPayMethod().equals("card")) {
            if (orderPurchase.getPg() == null)
                orderPurchase.setPg("FTLINK");
        }

        orderPurchase.setRegDatetime(currentDateTime);
        orderPurchase.setMemberSeqNo(user.getNo());

//        Member member = memberRepository.findBySeqNo(user.getNo());

        List<OrderPurchaseMenu> orderPurchaseMenuList = orderPurchase.getOrderPurchaseMenuList();

        Integer amount = 0;

        String code = null;
        if (orderPurchaseMenuList != null) {

            Float totalMenuPrice = 0f;
            Float totalOptionPrice = 0f;
            StringBuilder title = new StringBuilder();

            for (int i = 0; i < orderPurchaseMenuList.size(); i++) {

                OrderPurchaseMenu orderPurchaseMenu = orderPurchaseMenuList.get(i);
                List<OrderPurchaseMenuOption> orderPurchaseMenuOptionList = orderPurchaseMenu.getOrderPurchaseMenuOptionList();

                OrderMenuWithOption orderMenu = menuService.getMenu(orderPurchaseMenu.getOrderMenuSeqNo());

                if (i != 0) {
                    title.append(",");
                }

                title.append(orderMenu.getTitle());
                title.append("X");
                title.append(orderPurchaseMenu.getAmount());

                Float optionPrice = 0f;
                if (orderPurchaseMenuOptionList != null) {
                    for (OrderPurchaseMenuOption orderPurchaseMenuOption : orderPurchaseMenuOptionList) {
                        MenuOptionDetail menuOptionDetail = menuService.getMenuOptionDetail(orderPurchaseMenuOption.getMenuOptionDetailSeqNo());
                        optionPrice += menuOptionDetail.getPrice();
                    }
                }


                amount += orderPurchaseMenu.getAmount();
                orderPurchaseMenu.setOptionPrice(optionPrice);
                orderPurchaseMenu.setPrice(orderMenu.getPrice());
                orderPurchaseMenu.setTitle(orderMenu.getTitle());

                if (orderPurchase.getSalesType() == 6) {
                    if (orderMenu.getExpireType().equals("number")) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, orderMenu.getRemainDate());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        orderPurchase.setExpireDatetime(calendar.getTime());
                        orderPurchaseMenu.setExpireDatetime(calendar.getTime());

                    } else if (orderMenu.getExpireType().equals("date")) {
                        orderPurchase.setExpireDatetime(orderMenu.getExpireDate());
                        orderPurchaseMenu.setExpireDatetime(orderMenu.getExpireDate());
                    }
                }

                totalMenuPrice += orderPurchaseMenu.getPrice() * orderPurchaseMenu.getAmount();
                totalOptionPrice += orderPurchaseMenu.getOptionPrice() * orderPurchaseMenu.getAmount();

            }

            Float price = totalMenuPrice + totalOptionPrice;
            orderPurchase.setMenuPrice(price);
            orderPurchase.setOptionPrice(totalOptionPrice);
            orderPurchase.setAmount(amount);
            orderPurchase.setSavedPoint(price * 0.05f);

            if (orderPurchase.getSalesType() == 2) {
                Float riderFee = page.getRiderFee().floatValue();

                if (page.getRiderFreePrice() != null && page.getRiderFreePrice() > 0 && price >= page.getRiderFreePrice()) {
                    riderFee = 0f;
                }

                if (orderPurchase.getAddRiderFee() == null) {
                    orderPurchase.setAddRiderFee(0f);
                }

                orderPurchase.setRiderFee(riderFee + orderPurchase.getAddRiderFee());

                orderPurchase.setPrice(price + orderPurchase.getRiderFee());
            } else {
                orderPurchase.setPrice(price);
            }

            if (orderPurchase.getPayMethod().equals("card") || orderPurchase.getPayMethod().equals("ftlink")) {
                orderPurchase.setStatus(OrderPurchaseStatus.PAY_REQ.getStatus());
            } else {
                orderPurchase.setStatus(OrderPurchaseStatus.AFTER_PAY.getStatus());
            }


            orderPurchase.setIsStatusCompleted(false);
            orderPurchase.setTitle(title.toString());
            orderPurchase.setAppType("pplus");

            if (orderPurchase.getSalesType() == 1) {
                orderPurchase.setStatusShop(0);
            } else if (orderPurchase.getSalesType() == 2) {
                orderPurchase.setStatusRider(0);
            } else if (orderPurchase.getSalesType() == 5) {
                orderPurchase.setStatusPack(0);
            } else if (orderPurchase.getSalesType() == 6) {
                orderPurchase.setStatusTicket(0);
            }

            Float paymentFee = orderPurchase.getPrice() * 0.035f;
            orderPurchase.setPaymentFee(paymentFee);

            Float platformFee = 0f;
            if (orderPurchase.getSalesType() == 2) {
//                platformFee = orderPurchase.getMenuPrice() * 0.1f;
                platformFee = orderPurchase.getMenuPrice() * (page.getRiderCommission() / 100f);
                orderPurchase.setPlatformFee(platformFee);

            } else {
                if (orderPurchase.getSalesType() == 1) {
                    platformFee = orderPurchase.getPrice() * (page.getShopCommission() / 100f);
                } else if (orderPurchase.getSalesType() == 5) {
                    platformFee = orderPurchase.getPrice() * (page.getPackCommission() / 100f);
                }

                orderPurchase.setPlatformFee(platformFee);

            }
            orderPurchase.setReturnPaymentPrice(orderPurchase.getPlatformFee() + orderPurchase.getPrice() * 0.004f);


            orderPurchase.setAgentSeqNo(page.getAgentSeqNo());

            String str = "OP";
            code = SetID.getID(str);
            orderPurchase.setCode(code);

            if (orderPurchase.getSalesType() == 1) {
                orderPurchase.setPageCommissionRatio(page.getShopCommission());
            } else if (orderPurchase.getSalesType() == 2) {
                orderPurchase.setPageCommissionRatio(page.getRiderCommission());
            } else if (orderPurchase.getSalesType() == 5) {
                orderPurchase.setPageCommissionRatio(page.getPackCommission());
            } else if (orderPurchase.getSalesType() == 6) {
                orderPurchase.setPageCommissionRatio(page.getTicketCommission());
            }

            if (orderPurchase.getPageCommissionRatio() != null) {
                orderPurchase.setPageCommission(orderPurchase.getMenuPrice() * (orderPurchase.getPageCommissionRatio() / 100));
            }


            if (page.getAgent().getType() == 1) {//총판

                orderPurchase.setWholesaleCode(page.getAgent().getCode());

                if (orderPurchase.getSalesType() == 1) {
                    orderPurchase.setWholesaleCommissionRatio(page.getAgent().getShopCommission());
                } else if (orderPurchase.getSalesType() == 2) {
                    orderPurchase.setWholesaleCommissionRatio(page.getAgent().getRiderCommission());
                } else if (orderPurchase.getSalesType() == 5) {
                    orderPurchase.setWholesaleCommissionRatio(page.getAgent().getPackCommission());
                } else if (orderPurchase.getSalesType() == 6) {
                    orderPurchase.setWholesaleCommissionRatio(page.getAgent().getTicketCommission());
                }

                if (orderPurchase.getWholesaleCommissionRatio() != null) {
                    orderPurchase.setWholesaleCommission(orderPurchase.getMenuPrice() * (orderPurchase.getWholesaleCommissionRatio() / 100));
                }


            } else if (page.getAgent().getType() == 2) {//대행사

                Agent wholesaleAgent = agentRepository.findByCode(page.getAgent().getParents());

                orderPurchase.setDistributorCode(page.getAgent().getCode());
                orderPurchase.setWholesaleCode(page.getAgent().getParents());

                if (orderPurchase.getSalesType() == 1) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getShopCommission());
                    orderPurchase.setDistributorCommissionRatio(page.getAgent().getShopCommission());
                } else if (orderPurchase.getSalesType() == 2) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getRiderCommission());
                    orderPurchase.setDistributorCommissionRatio(page.getAgent().getRiderCommission());
                } else if (orderPurchase.getSalesType() == 5) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getPackCommission());
                    orderPurchase.setDistributorCommissionRatio(page.getAgent().getPackCommission());
                } else if (orderPurchase.getSalesType() == 6) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getTicketCommission());
                    orderPurchase.setDistributorCommissionRatio(page.getAgent().getTicketCommission());
                }

                if (orderPurchase.getWholesaleCommissionRatio() != null) {
                    orderPurchase.setWholesaleCommission(orderPurchase.getMenuPrice() * (orderPurchase.getWholesaleCommissionRatio() / 100));

                    if (orderPurchase.getDistributorCommissionRatio() != null) {
                        orderPurchase.setDistributorCommission(orderPurchase.getWholesaleCommission() * (orderPurchase.getDistributorCommissionRatio() / 100));
                    }
                }

            } else {
                String parentsCode = page.getAgent().getParents();//에이전트코드

                Agent distributorAgent = getDistAgentCode(parentsCode);
                Agent wholesaleAgent = agentRepository.findByCode(distributorAgent.getParents());

                orderPurchase.setDealerCode(page.getAgent().getCode());
                orderPurchase.setDistributorCode(distributorAgent.getCode());
                orderPurchase.setWholesaleCode(distributorAgent.getParents());

                if (orderPurchase.getSalesType() == 1) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getShopCommission());
                    orderPurchase.setDistributorCommissionRatio(distributorAgent.getShopCommission());
                    orderPurchase.setDealerCommissionRatio(page.getAgent().getShopCommission());
                } else if (orderPurchase.getSalesType() == 2) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getRiderCommission());
                    orderPurchase.setDistributorCommissionRatio(distributorAgent.getRiderCommission());
                    orderPurchase.setDealerCommissionRatio(page.getAgent().getRiderCommission());
                } else if (orderPurchase.getSalesType() == 5) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getPackCommission());
                    orderPurchase.setDistributorCommissionRatio(distributorAgent.getPackCommission());
                    orderPurchase.setDealerCommissionRatio(page.getAgent().getPackCommission());
                } else if (orderPurchase.getSalesType() == 6) {
                    orderPurchase.setWholesaleCommissionRatio(wholesaleAgent.getTicketCommission());
                    orderPurchase.setDistributorCommissionRatio(distributorAgent.getTicketCommission());
                    orderPurchase.setDealerCommissionRatio(page.getAgent().getTicketCommission());
                }

                if (orderPurchase.getWholesaleCommissionRatio() != null) {
                    orderPurchase.setWholesaleCommission(orderPurchase.getPrice() * (orderPurchase.getWholesaleCommissionRatio() / 100));

                    if (orderPurchase.getDistributorCommissionRatio() != null) {
                        orderPurchase.setDistributorCommission(orderPurchase.getWholesaleCommission() * (orderPurchase.getDistributorCommissionRatio() / 100));

                        if (orderPurchase.getDealerCommissionRatio() != null) {
                            orderPurchase.setDealerCommission(orderPurchase.getDistributorCommission() * (orderPurchase.getDealerCommissionRatio() / 100));
                        }

                    }

                }

            }

            orderPurchase = orderPurchaseRepository.saveAndFlush(orderPurchase);


            for (int i = 0; i < orderPurchaseMenuList.size(); i++) {
                OrderPurchaseMenu orderPurchaseMenu = orderPurchaseMenuList.get(i);
                orderPurchaseMenu.setOrderPurchaseSeqNo(orderPurchase.getSeqNo());

                List<OrderPurchaseMenuOption> orderPurchaseMenuOptionList = orderPurchaseMenu.getOrderPurchaseMenuOptionList();

                orderPurchaseMenu = orderPurchaseMenuRepository.saveAndFlush(orderPurchaseMenu);

                if (orderPurchaseMenuOptionList != null) {
                    for (OrderPurchaseMenuOption orderPurchaseMenuOption : orderPurchaseMenuOptionList) {
                        MenuOptionDetail menuOptionDetail = menuService.getMenuOptionDetail(orderPurchaseMenuOption.getMenuOptionDetailSeqNo());
                        orderPurchaseMenuOption.setOrderPurchaseMenuSeqNo(orderPurchaseMenu.getSeqNo());
                        orderPurchaseMenuOption.setPrice(menuOptionDetail.getPrice());
                        orderPurchaseMenuOption.setTitle(menuOptionDetail.getTitle());
                        orderPurchaseMenuOption = orderPurchaseMenuOptionRepository.saveAndFlush(orderPurchaseMenuOption);
                    }
                }
            }

        }

//        if (orderPurchase.getSalesType() == 2) {
//            OrderCallUserInfo orderCallUserInfo = orderCallUserInfoRepository.findByPageSeqNoAndPhone(orderPurchase.getPageSeqNo(), orderPurchase.getPhone());
//
//            if(orderCallUserInfo == null){
//                orderCallUserInfo = new OrderCallUserInfo();
//                orderCallUserInfo.setPageSeqNo(orderPurchase.getPageSeqNo());
//                orderCallUserInfo.setPhone(orderPurchase.getPhone());
//            }
//
//            orderCallUserInfo.setName(user.getNickname());
//            orderCallUserInfo.setAddress(orderPurchase.getAddress());
//            orderCallUserInfo.setAddressDetail(orderPurchase.getAddressDetail());
//
//            orderCallUserInfoRepository.save(orderCallUserInfo);
//        }

        if (orderPurchase.getPayMethod().equals("outsideCard") || orderPurchase.getPayMethod().equals("outsideCash")) {
            cartService.clearCart(orderPurchase.getMemberSeqNo(), orderPurchase.getSalesType().intValue());

            //데이터 전송
            OrderMessage message = new OrderMessage();
            message.setPageSeqNo(orderPurchase.getPageSeqNo().toString());
            message.setSeqNo(orderPurchase.getSeqNo().toString());
            message.setAddress(orderPurchase.getAddress());
            message.setAddressDetail(orderPurchase.getAddressDetail());
            message.setAmount(orderPurchase.getAmount().toString());
            message.setPayMethod(orderPurchase.getPayMethod());
            message.setPrice(orderPurchase.getPrice().toString());
            message.setRegDatetime(orderPurchase.getRegDatetime());
            message.setSalesType(orderPurchase.getSalesType().toString());
            message.setStatus(orderPurchase.getStatus().toString());
            message.setTitle(orderPurchase.getTitle());

            if (orderPurchase.getVisitNumber() != null) {
                message.setVisitNumber(orderPurchase.getVisitNumber().toString());
            }

            if (orderPurchase.getVisitTime() != null) {
                message.setVisitTime(orderPurchase.getVisitTime());
            }
            if (orderPurchase.getSalesType() != 6L) {
                adminResult(message);
            }
        }


        return orderPurchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public FTLinkPayResponse ftlinkPay(FTLinkPayRequest data) throws ResultCodeException {
        OrderPurchase orderPurchase = orderPurchaseRepository.findByOrderId(data.getComp_orderno());
        try {
            if (StringUtils.isEmpty(data.getShopcode())) {
                FTLinkPayResponse res = new FTLinkPayResponse();
                res.setErrCode("99");
                res.setErrMessage("결제 가능한 스토어가 아닙니다.");
                return res;
            }

            String serverType = data.getServerType();
            String roomId = data.getRoomId();
            logger.debug("price : " + orderPurchase.getPrice());

            if (Integer.valueOf(data.getOrder_req_amt()) != orderPurchase.getPrice().intValue()) {
                throw new InvalidBuyException("ftlinkPay", "결제금액이 일치하지 않습니다.");
            }

            data.setManual_used("Y");
            data.setManual_amt(String.valueOf(orderPurchase.getReturnPaymentPrice().intValue()));

            Page page = pageRepository.findBySeqNo(orderPurchase.getPageSeqNo());
            Member pageMember = memberRepository.findBySeqNo(page.getMemberSeqNo());
            data.setLoginId(pageMember.getLoginId().replace(pageMember.getAppType() + "##", ""));
            data.setServerType("");
            data.setRoomId("");
            data.setReqdephold("Y");
            if (storeType.equals("PROD")) {
                data.setISTEST("USE");
            } else {
                data.setISTEST("TEST");
            }

            FTLinkPayResponse res = FTLinkPayApi.payRequest(data);
            String dateStr = AppUtil.localDatetimeNowString();


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

            callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);

            LpngCallback callback = new LpngCallback();
            callback.setSeqNo(null);
            callback.setOrderPurchaseSeqNo(orderPurchase.getSeqNo());
            callback.setMemberSeqNo(orderPurchase.getMemberSeqNo());
            callback.setPgTranId(res.getAppr_tranNo());
            callback.setApprDate(res.getAppr_date());
            callback.setApprTime(res.getAppr_time());
            callback.setOrderId(data.getComp_orderno());
            callback.setName(res.getOrder_name());
            callback.setPrice(Integer.parseInt(res.getOrder_req_amt()));
            callback.setPaymentData(AppUtil.ConverObjectToMap(res));
            callback.setRegDatetime(dateStr);
            callback.setLpngOrderNo(res.getOrderno());
            callback.setResultSeqNo(callbackResult.getSeqNo());


            String currentDateTime = AppUtil.localDatetimeNowString();
            if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {

                callback.setStatus(true);
                callback.setProcess(LpngProcess.PAY.getType());
                lpngCallbackRepository.save(callback);

                orderPurchase.setStatus(OrderPurchaseStatus.PAY.getStatus());
                orderPurchase.setPgTranId(res.getAppr_tranNo());
                orderPurchase.setApprNo(res.getAppr_no());

                orderPurchase.setChangeStatusDatetime(currentDateTime);
                orderPurchase.setPayDatetime(currentDateTime);
                if (orderPurchase.getSalesType() == 6) {
                    orderPurchase.setStatusTicket(1);
                }
                orderPurchase = orderPurchaseRepository.saveAndFlush(orderPurchase);
                cartService.clearCart(orderPurchase.getMemberSeqNo(), orderPurchase.getSalesType().intValue());

                //데이터 전송
                OrderMessage message = new OrderMessage();
                message.setPageSeqNo(orderPurchase.getPageSeqNo().toString());
                message.setSeqNo(orderPurchase.getSeqNo().toString());
                message.setAddress(orderPurchase.getAddress());
                message.setAddressDetail(orderPurchase.getAddressDetail());
                message.setAmount(orderPurchase.getAmount().toString());
                message.setPayMethod(orderPurchase.getPayMethod());
                message.setPrice(orderPurchase.getPrice().toString());
                message.setRegDatetime(orderPurchase.getRegDatetime());
                message.setSalesType(orderPurchase.getSalesType().toString());
                message.setStatus(orderPurchase.getStatus().toString());
                message.setTitle(orderPurchase.getTitle());
                if (orderPurchase.getVisitNumber() != null) {
                    message.setVisitNumber(orderPurchase.getVisitNumber().toString());
                }

                if (orderPurchase.getVisitTime() != null) {
                    message.setVisitTime(orderPurchase.getVisitTime());
                }

                if (orderPurchase.getSalesType() != 6L) {
                    adminResult(message);
                }

            } else {

                callback.setStatus(false);
                callback.setProcess(LpngProcess.CANCEL.getType());
                callback.setMemo("결제 실패");
                if (!AppUtil.isEmpty(callback.getPgTranId())) {
                    lpngCallbackRepository.save(callback);
                }
                orderPurchase.setStatus(OrderPurchaseStatus.FAIL.getStatus());
                orderPurchase.setChangeStatusDatetime(currentDateTime);
                orderPurchaseRepository.save(orderPurchase);


                res.setReq_installment(null);
            }


            return res;
        } catch (Exception e) {
            logger.error(e.toString());
            throw new InvalidBuyException(e);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int verifyBootPay(User user, String orderId, String receiptId) throws ResultCodeException {
        OrderPurchase orderPurchase = orderPurchaseRepository.findByOrderId(orderId);

        if (orderPurchase == null) {
            throw new NotFoundException();
        }

        try {

//            String[] mobiles = user.getMobile().split("##");
            String mobile = orderPurchase.getPhone();
//            if (mobiles.length == 2) {
//                mobile = mobiles[1];
//            } else {
//                mobile = mobiles[0];
//            }

            String dateStr = AppUtil.localDatetimeNowString();
            String token = getAccessToken();

            if (token == null || token.isEmpty()) {
                throw new InvalidCashException();
            }

            HttpResponse res = verify(receiptId, token);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            logger.debug("verify result :" + str);
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            JsonObject data = jsonObject.getAsJsonObject("data");


//            0 - 결제 대기 상태입니다. 승인이 나기 전의 상태입니다.
//            1 - 결제 완료된 상태입니다.
//            2 - 결제승인 전 상태입니다. transactionConfirm() 함수를 호출하셔서 결제를 승인해야합니다.
//            3 - 결제승인 중 상태입니다. PG사에서 transaction 처리중입니다.
//            20 - 결제가 취소된 상태입니다.
//            -20 - 결제취소가 실패한 상태입니다.
//            -30 - 결제취소가 진행중인 상태입니다.
//            -1 - 오류로 인해 결제가 실패한 상태입니다.
//            -2 - 결제승인이 실패하였습니다.

            if (!data.get("status").getAsString().equals("1")) {
                throw new Exception(" pay rejected !!!");
            }

            Page page = pageRepository.findBySeqNo(orderPurchase.getPageSeqNo());

            String orderNo = data.get("order_id").getAsString();
            Integer process = data.get("status").getAsInt();
            String pg = data.get("pg").getAsString().toUpperCase();
            Integer paymentPrice = data.get("price").getAsInt();

            JsonObject paymentData = data.getAsJsonObject("payment_data");

            String cardNo = paymentData.get("card_no").getAsString();
            String authNo = paymentData.get("card_auth_no").getAsString();
            String cardName = paymentData.get("card_name").getAsString();
            String cardQuota = paymentData.get("card_quota").getAsString();
            String cardCode = paymentData.get("card_code").getAsString();
            String tid = paymentData.get("tid").getAsString();


            String payMethod = "CARD";

            String cpid = CPID;

            String daouTrx = tid;

            String settDate = paymentData.get("p_at").getAsString(); // 2020-11-23 10:35:44

            String email = "";

            // purchaseProduct.set
            // 2020-11-23 10:35:44
            settDate = settDate.replaceAll("-", "");
            settDate = settDate.replaceAll(":", "");
            settDate = settDate.replaceAll(" ", "");

            String setDay = settDate.substring(0, 8);
            String setTime = settDate.substring(8);

            LpngCallbackResult lpngCallbackResult = new LpngCallbackResult();
            lpngCallbackResult.setOrderNo(orderNo);
            lpngCallbackResult.setShopCode(page.getShopCode());
            lpngCallbackResult.setOrderStatus("12");
            lpngCallbackResult.setOrderGoodsname(orderPurchase.getTitle());
            lpngCallbackResult.setOrderReqAmt(paymentPrice + "");
            lpngCallbackResult.setOrderName(user.getNickname());
            lpngCallbackResult.setOrderHp(mobile);
            lpngCallbackResult.setApprNo(authNo);
            lpngCallbackResult.setApprTranNo(daouTrx);
            lpngCallbackResult.setApprDate(setDay);
            lpngCallbackResult.setApprTime(setTime);
            lpngCallbackResult.setCardtxt(cardName);
            lpngCallbackResult.setReqCardNo(cardNo);
            lpngCallbackResult.setApprShopCode("");
            lpngCallbackResult.setReqInstallment(cardQuota);

            lpngCallbackResult = lpngCallbackResultRepository.save(lpngCallbackResult);

            LpngCallback lpngCallback = new LpngCallback();
            lpngCallback.setMemberSeqNo(user.getNo());
            lpngCallback.setOrderPurchaseSeqNo(orderPurchase.getSeqNo());
            lpngCallback.setOrderId(orderNo);
            lpngCallback.setPgTranId(daouTrx);
            lpngCallback.setName(user.getNickname());
            lpngCallback.setPrice(paymentPrice);
            lpngCallback.setStatus(true);
            lpngCallback.setApprDate(setDay);
            lpngCallback.setApprTime(setTime);
            lpngCallback.setRegDatetime(dateStr);
            lpngCallback.setProcess(1); // 결제완료
            lpngCallback.setResultSeqNo(lpngCallbackResult.getSeqNo());
            lpngCallbackRepository.save(lpngCallback);


            String currentDateTime = AppUtil.localDatetimeNowString();

            orderPurchase.setReceiptId(receiptId);
            orderPurchase.setPgTranId(daouTrx);
            orderPurchase.setApprNo(authNo);
            //purchase.setApprTranNo("");
            orderPurchase.setStatus(OrderPurchaseStatus.PAY.getStatus()); // 결제완료
            orderPurchase.setPayDatetime(currentDateTime);
            orderPurchase.setChangeStatusDatetime(currentDateTime);

            if (orderPurchase.getSalesType() == 6) {
                orderPurchase.setStatusTicket(1);
            }

            String reqdephold = "Y";

            String type = "TEST";
            if (storeType.equals("PROD")) {
                type = "USE";
            }

            URIBuilder uriBuilder = new URIBuilder("http://pay.ftlink.co.kr/payalert/pplus/noti_cert.asp");
            uriBuilder
                    .addParameter("PAYMETHOD", URLEncoder.encode(payMethod, "EUC-KR"))
                    .addParameter("CPID", URLEncoder.encode(cpid, "EUC-KR"))
                    .addParameter("DAOUTRX", URLEncoder.encode(daouTrx, "EUC-KR"))
                    .addParameter("ORDERNO", URLEncoder.encode(orderNo, "EUC-KR"))
                    .addParameter("AMOUNT", URLEncoder.encode(paymentPrice + "", "EUC-KR"))
                    .addParameter("PRODUCTNAME", URLEncoder.encode(orderPurchase.getTitle(), "EUC-KR"))
                    .addParameter("SETDATE", URLEncoder.encode(settDate, "EUC-KR"))
                    .addParameter("AUTHNO", URLEncoder.encode(authNo, "EUC-KR"))
                    .addParameter("CARDCODE", URLEncoder.encode(cardCode, "EUC-KR"))
                    .addParameter("CARDNAME", URLEncoder.encode(cardName, "EUC-KR"))
                    .addParameter("CARDNO", URLEncoder.encode(cardNo, "EUC-KR"))
                    .addParameter("EMAIL", URLEncoder.encode(email, "EUC-KR"))
                    .addParameter("USERID", URLEncoder.encode(mobile, "EUC-KR"))
                    .addParameter("USERNAME", URLEncoder.encode(user.getNickname(), "EUC-KR"))
                    .addParameter("PRODUCTCODE", URLEncoder.encode(orderPurchase.getCode() + "", "EUC-KR"))
                    .addParameter("RESERVEDINDEX1", URLEncoder.encode(page.getShopCode(), "EUC-KR"))
                    .addParameter("RESERVEDINDEX2", URLEncoder.encode("", "EUC-KR"))
                    .addParameter("RESERVEDINDEX3", URLEncoder.encode("", "EUC-KR"))
                    .addParameter("RESERVEDSTRING", URLEncoder.encode("", "EUC-KR"))
                    .addParameter("ISTEST", URLEncoder.encode(type, "EUC-KR"))
                    .addParameter("reqdephold", URLEncoder.encode(reqdephold, "EUC-KR"))
                    .addParameter("MANUAL_USED", "Y")
                    .addParameter("MANUAL_AMT", URLEncoder.encode(orderPurchase.getReturnPaymentPrice().intValue() + "", "EUC-KR"))
                    .addParameter("PGCODE", "20"); // 다우-30, 다날-20

            URI uri = uriBuilder.build();

            HttpGet getMethod = new HttpGet(uri);

            getMethod.addHeader(new BasicHeader("Accept", "application/json"));
            getMethod.addHeader(new BasicHeader("Accept-Charset", "EUC-KR"));

            CloseableHttpClient httpclient = HttpClients.createDefault();

            logger.info("params ==> " + getMethod.toString());

            CloseableHttpResponse response = httpclient.execute(getMethod);

            String resultData2 = EntityUtils.toString(response.getEntity(), "UTF-8");


            logger.info("finteck result ==> " + resultData2);


            orderPurchase = orderPurchaseRepository.save(orderPurchase);

            cartService.clearCart(orderPurchase.getMemberSeqNo(), orderPurchase.getSalesType().intValue());

            //데이터 전송
            OrderMessage message = new OrderMessage();
            message.setPageSeqNo(orderPurchase.getPageSeqNo().toString());
            message.setSeqNo(orderPurchase.getSeqNo().toString());
            message.setAddress(orderPurchase.getAddress());
            message.setAddressDetail(orderPurchase.getAddressDetail());
            message.setAmount(orderPurchase.getAmount().toString());
            message.setPayMethod(orderPurchase.getPayMethod());
            message.setPrice(orderPurchase.getPrice().toString());
            message.setRegDatetime(orderPurchase.getRegDatetime());
            message.setSalesType(orderPurchase.getSalesType().toString());
            message.setStatus(orderPurchase.getStatus().toString());
            message.setTitle(orderPurchase.getTitle());

            if (orderPurchase.getVisitNumber() != null) {
                message.setVisitNumber(orderPurchase.getVisitNumber().toString());
            }

            if (orderPurchase.getVisitTime() != null) {
                message.setVisitTime(orderPurchase.getVisitTime());
            }

            if (orderPurchase.getSalesType() != 6L) {
                adminResult(message);
            }


        } catch (Exception e) {
            throw new InvalidBuyException("verifyBootPay", e);
        }
        return Const.E_SUCCESS;
    }

    public void adminResult(OrderMessage message) {
        try {
            String url = "";
            if (storeType.equals("PROD")) {
                url = adminProdUrl + "cs/receiveOrderData";
            } else {
                url = adminStageUrl + "cs/receiveOrderData";
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

//    public static void main(String argv[]) {
//
//
//        OrderMessage message = new OrderMessage();
//        message.setPageSeqNo("1001579");
//        message.setAddress("서울특별시 서초구 반포대로28길 20(서초동)");
//        message.setAddressDetail("두원빌딩 6층");
//        message.setAmount("1");
//        message.setPayMethod("card");
//        message.setPrice("25000");
//        message.setRegDatetime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        message.setSalesType("2");
//        message.setStatus("2");
//        message.setTitle("간장치킨X1");
//        message.setVisitNumber(null);
//        message.setVisitTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        adminResult(message);
//    }

    public String getAccessToken() throws Exception {

        Token token = new Token();
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

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int useTicket(User user, Long orderPurchaseSeqNo) throws ResultCodeException {
        OrderPurchase orderPurchase = orderPurchaseRepository.findBySeqNo(orderPurchaseSeqNo);

        if (orderPurchase == null) {
            throw new NotFoundException();
        }

        if (!orderPurchase.getMemberSeqNo().equals(user.getNo())) {
            throw new NotPermissionException("owner", "not matched.");
        }

        if (orderPurchase.getStatus() == OrderPurchaseStatus.COMPLETE.getStatus() || orderPurchase.getStatus() == OrderPurchaseStatus.CANCEL_COMPLETE.getStatus()) {//사용완료
            throw new AlreadyLimitException();
        }

        if (orderPurchase.getStatusTicket() == 4) {//사용요청
            throw new AlreadyLimitException();
        }

        orderPurchase.setStatusTicket(4);
        orderPurchase.setChangeStatusDatetime(AppUtil.localDatetimeNowString());
        orderPurchase = orderPurchaseRepository.save(orderPurchase);

        try {

            sendTicketUsePush(orderPurchase);
        }catch (Exception e){
            logger.error(e.toString());
        }

        OrderMessage message = new OrderMessage();
        message.setSeqNo(orderPurchase.getSeqNo().toString());
        message.setPageSeqNo(orderPurchase.getPageSeqNo().toString());
        message.setType("request");
        useTicketAdmin(message);

        return Const.E_SUCCESS;
    }

    private void sendTicketUsePush(OrderPurchase orderPurchase) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "오리마켓[티켓사용]");
                    data.put("contents", "티켓 사용요청 있어요");
                    data.put("move_type1", "inner");
                    data.put("move_type2", "ticket");
                    data.put("move_target", String.valueOf(orderPurchase.getSeqNo()));

                    Page page = pageRepository.findBySeqNo(orderPurchase.getPageSeqNo());

                    queueService.sendPush(page.getMemberSeqNo(), data, Const.APP_TYPE_ORDER);

                } catch (Exception e) {
                    logger.debug("sendPush : " + e.toString());
                }
            }
        });
        thread.start();

    }

    private void useTicketAdmin(OrderMessage message) {
        try {
            String url = "";
            if (storeType.equals("PROD")) {
                url = adminProdUrl + "cs/requestTicket";
            } else {
                url = adminStageUrl + "cs/requestTicket";
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

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int cancelOrderPurchaseUser(User user, Long orderPurchaseSeqNo, String memo) throws ResultCodeException {

        if (StringUtils.isEmpty(memo)) {
            memo = "결제 취소";
        }

        String currentDateTime = AppUtil.localDatetimeNowString();

        OrderPurchase orderPurchase = orderPurchaseRepository.findBySeqNo(orderPurchaseSeqNo);

        if (orderPurchase == null) {
            throw new NotFoundException();
        }

        if (!orderPurchase.getMemberSeqNo().equals(user.getNo())) {
            throw new NotPermissionException("owner", "not matched.");
        }

        if (orderPurchase.getStatus() != OrderPurchaseStatus.PAY.getStatus() && orderPurchase.getStatus() != OrderPurchaseStatus.AFTER_PAY.getStatus()) {
            throw new NotPermissionException();
        }


        Boolean success = false;

        //outsideCard, outsideCash
        if (orderPurchase.getPayMethod().equals("outsideCard") || orderPurchase.getPayMethod().equals("outsideCash")) {
            success = true;
        } else {
            success = ftlinkCancel(orderPurchase, memo);
        }

        if (!success) {
            throw new LpngCancelPeriodException("lpngCancel Error", "결제 취소 중 오류가 발생하였습니다.");

        } else {

            orderPurchase.setChangeStatusDatetime(currentDateTime);
            orderPurchase.setCancelDatetime(currentDateTime);
            if (orderPurchase.getSalesType() == 1) {
                orderPurchase.setStatusShop(2);
            } else if (orderPurchase.getSalesType() == 2) {
                orderPurchase.setStatusRider(2);
            } else if (orderPurchase.getSalesType() == 5) {
                orderPurchase.setStatusPack(2);
            }else if (orderPurchase.getSalesType() == 6) {
                orderPurchase.setStatusTicket(2);
            }
            orderPurchase.setStatus(OrderPurchaseStatus.CANCEL_COMPLETE.getStatus());
            orderPurchase.setCancelMemo("직접취소");
            orderPurchase = orderPurchaseRepository.save(orderPurchase);

            if (orderPurchase.getSalesType() == 6L) {
                OrderMessage message = new OrderMessage();
                message.setSeqNo(orderPurchase.getSeqNo().toString());
                message.setPageSeqNo(orderPurchase.getPageSeqNo().toString());
                message.setType("cancel");
                useTicketAdmin(message);
            }

        }

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int cancelOrderPurchase(Long pageSeqNo, Long orderPurchaseSeqNo, String memo) throws ResultCodeException {

        if (StringUtils.isEmpty(memo)) {
            memo = "가게 사정으로 인해 주문이 쥐소되었습니다.";
        }

        String currentDateTime = AppUtil.localDatetimeNowString();

        OrderPurchase orderPurchase = orderPurchaseRepository.findBySeqNo(orderPurchaseSeqNo);

        if (orderPurchase == null) {
            throw new NotFoundException();
        }

        if (!orderPurchase.getPageSeqNo().equals(pageSeqNo)) {
            throw new NotPermissionException("owner", "not matched.");
        }

        if (orderPurchase.getStatus() != OrderPurchaseStatus.PAY.getStatus() && orderPurchase.getStatus() != OrderPurchaseStatus.AFTER_PAY.getStatus() && orderPurchase.getStatus() != OrderPurchaseStatus.CONFIRM.getStatus()) {
            throw new NotPermissionException();
        }

        if (orderPurchase.getSalesType() == 2L) {
            if (orderPurchase.getStatusRider() != RiderStatus.WAIT.getStatus() && orderPurchase.getStatusRider() != RiderStatus.CONFIRM.getStatus() && orderPurchase.getStatusRider() != RiderStatus.NOT_CALL.getStatus()) {
                throw new NotPermissionException();
            }
        }

        Boolean success = false;

        //outsideCard, outsideCash
        if (orderPurchase.getPayMethod().equals("outsideCard") || orderPurchase.getPayMethod().equals("outsideCash")) {
            success = true;
        } else {
            success = ftlinkCancel(orderPurchase, memo);
        }

        if (!success) {
            throw new LpngCancelPeriodException("lpngCancel Error", "결제 취소 중 오류가 발생하였습니다.");
        } else {
            orderPurchase.setChangeStatusDatetime(currentDateTime);
            orderPurchase.setCancelDatetime(currentDateTime);
            if (orderPurchase.getSalesType() == 1) {
                orderPurchase.setStatusShop(2);
            } else if (orderPurchase.getSalesType() == 2) {
                orderPurchase.setStatusRider(2);
            } else if (orderPurchase.getSalesType() == 5) {
                orderPurchase.setStatusPack(2);
            }else if (orderPurchase.getSalesType() == 6) {
                orderPurchase.setStatusTicket(2);
            }
            orderPurchase.setStatus(OrderPurchaseStatus.CANCEL_COMPLETE.getStatus());
            orderPurchase.setCancelMemo(memo);
            orderPurchase = orderPurchaseRepository.save(orderPurchase);
            try {
                sendCancelPush(orderPurchase);
                if (storeType.equals("PROD") && orderPurchase.getAppType().equals("cs")) {
                    SmsMsg msg = new SmsMsg();
                    msg.setSender("0263151234");
                    msg.setReceiver(orderPurchase.getPhone());
                    msg.setMsg("'" + memo + "'로 오리마켓 주문이 취소되었습니다.");
                    smsService.send(msg);
                }

            } catch (Exception e) {
                logger.error("push error : " + e.toString());
            }

        }

        return Const.E_SUCCESS;
    }

    private void sendCancelPush(OrderPurchase orderPurchase){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (orderPurchase.getMemberSeqNo() != null) {

                        NotificationBox notificationBox = new NotificationBox();
                        notificationBox.setMemberSeqNo(orderPurchase.getMemberSeqNo());
                        notificationBox.setSubject("오리마켓[취소완료]");
                        notificationBox.setContents(orderPurchase.getCancelMemo());
                        notificationBox.setMoveSeqNo(orderPurchase.getSeqNo());
                        notificationBox.setMoveType1("inner");
                        if (orderPurchase.getSalesType() == 6L) {
                            notificationBox.setMoveType2("ticketDetail");
                        } else {
                            notificationBox.setMoveType2("orderDetail");
                        }

                        notificationBoxService.save(notificationBox);

                        Map<String, String> data = new HashMap<String, String>();
                        data.put("title", "오리마켓[취소완료]");
                        data.put("contents", orderPurchase.getCancelMemo());
                        data.put("move_type1", "inner");
                        if (orderPurchase.getSalesType() == 6L) {
                            data.put("move_type2", "ticketDetail");
                        } else {
                            data.put("move_type2", "orderDetail");
                        }

                        data.put("move_target", String.valueOf(orderPurchase.getSeqNo()));

                        queueService.sendPush(orderPurchase.getMemberSeqNo(), data, Const.APP_TYPE_USER);
                    }
                }catch (Exception e){
                    logger.error(e.toString());
                }
            }
        });
        thread.start();


    }


    public Boolean ftlinkCancel(OrderPurchase orderPurchase, String message) {
        try {

            LpngCallback callback = lpngCallbackRepository.findByOrderId(orderPurchase.getOrderId());
//            String apprDate = callback.getApprDate();
//            String today = AppUtil.localTodayYYYYMMDD();

            Page page = pageRepository.findBySeqNo(orderPurchase.getPageSeqNo());
            Member member = memberRepository.findBySeqNo(page.getMemberSeqNo());
            LpngCallbackResult callbackResult = callback.getResult();

            if (orderPurchase.getPg().equals("FTLINK")) {
                FTLinkCancelRequest ftLinkCancelRequest = new FTLinkCancelRequest();
                ftLinkCancelRequest.setShopcode(callbackResult.getShopCode());
                ftLinkCancelRequest.setLoginId(member.getLoginId().replace("biz##", ""));
                ftLinkCancelRequest.setCancelAmt(callback.getPrice().toString());
                ftLinkCancelRequest.setOrderNo(callback.getLpngOrderNo());
                ftLinkCancelRequest.setTranNo(callback.getPgTranId());
                ftLinkCancelRequest.setCancelAmt(callback.getPrice().toString());

                FTLinkCancelResponse res = FTLinkPayApi.cancelRequest(ftLinkCancelRequest);

                if (res.getErrCode().equals("00") || res.getErrCode().equals("0000")) {

                    callbackResult.setErrorCode(res.getErrCode());
                    callbackResult.setErrorMsg(res.getErrMessage());
                    lpngCallbackResultRepository.save(callbackResult);

                    callback.setStatus(false);
                    callback.setProcess(LpngProcess.CANCEL.getType());
                    callback = lpngCallbackRepository.save(callback);

                    return true;
                } else {
                    logger.error("ftlinkCancel Error " + res.getErrMessage());
                    return false;
                }
            } else {

                if (orderPurchase.getPg().toUpperCase().equals("DANAL")) {
                    BootPayApi api = new BootPayApi();
                    api.getAccessTokenV2();

                    Cancel cancel = new Cancel();
                    cancel.receipt_id = orderPurchase.getReceiptId();
                    cancel.cancel_username = callback.getName();
                    cancel.cancel_message = message;
                    cancel.cancel_price = orderPurchase.getPrice().intValue();

                    HttpResponse res = api.cancel(cancel);
                    Gson gson = new Gson();
                    BootPayCancelResponse bootPayCancelResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), BootPayCancelResponse.class);
//                    String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
                    logger.error("cancel status : " + bootPayCancelResponse.getStatus());
                    if (bootPayCancelResponse.getStatus() == 20) {
                        FTLinkCancelNotiRequest ftLinkCancelNotiRequest = new FTLinkCancelNotiRequest();

//                        ftLinkCancelNotiRequest.setTOKEN(payJoaCancelResponse.getTOKEN());
                        ftLinkCancelNotiRequest.setRESULTCODE("0000");
                        ftLinkCancelNotiRequest.setERRORMESSAGE("");
                        ftLinkCancelNotiRequest.setDAOUTRX(bootPayCancelResponse.getCard_data().getTid());
                        ftLinkCancelNotiRequest.setAMOUNT(callback.getPrice().toString());
                        ftLinkCancelNotiRequest.setCANCELDATE(bootPayCancelResponse.getCancelled_at());
                        ftLinkCancelNotiRequest.setSHOPCODE(page.getShopCode());
                        ftLinkCancelNotiRequest.setORDERNO(orderPurchase.getOrderId());
                        ftLinkCancelNotiRequest.setAPPRDATE(callback.getApprDate());
                        ftLinkCancelNotiRequest.setAPPRTIME(callback.getApprTime());
                        ftLinkCancelNotiRequest.setAPPRTRXID(callback.getPgTranId());
                        ftLinkCancelNotiRequest.setAPPRNO(callbackResult.getApprNo());
                        ftLinkCancelNotiRequest.setPgcode("20");
                        ftLinkCancelNotiRequest.setPAYAMOUNT(String.valueOf(callback.getPrice().intValue()));

                        FTLinkPayApi.cancelNotiRequest(ftLinkCancelNotiRequest);

                        callbackResult.setErrorCode("0000");
                        callbackResult.setErrorMsg("");
                        lpngCallbackResultRepository.save(callbackResult);

                        callback.setStatus(false);
                        callback.setProcess(LpngProcess.CANCEL.getType());
                        callback = lpngCallbackRepository.save(callback);
                        return true;
                    } else {
                        if (bootPayCancelResponse.getStatus() == -2105) {
                            return true;
                        }
                        logger.error("bootpayCancel Error " + bootPayCancelResponse.getStatus());
                        return false;
                    }
                } else {
                    PayJoaCancelReadyRequest payJoaCancelReadyRequest = new PayJoaCancelReadyRequest();
                    payJoaCancelReadyRequest.setCPID(NOMEMBER_CPID);
                    payJoaCancelReadyRequest.setPAYMETHOD("CARD");
                    payJoaCancelReadyRequest.setCANCELREQ("Y");

                    PayJoaCancelReadyResponse payJoaCancelReadyResponse = readyPayJoa(payJoaCancelReadyRequest);

                    PayJoaCancelRequest payJoaCancelRequest = new PayJoaCancelRequest();
                    payJoaCancelRequest.setCPID(NOMEMBER_CPID);
                    payJoaCancelRequest.setAMOUNT(String.valueOf(callback.getPrice().intValue()));
                    payJoaCancelRequest.setTRXID(callback.getPgTranId());
                    payJoaCancelRequest.setCANCELREASON(message);
                    PayJoaCancelResponse payJoaCancelResponse = cancelPayJoa(payJoaCancelRequest, payJoaCancelReadyResponse);

                    if (payJoaCancelResponse == null) {
                        return false;
                    }

                    if (payJoaCancelResponse.getRESULTCODE().equals("0000")) {

                        FTLinkCancelNotiRequest ftLinkCancelNotiRequest = new FTLinkCancelNotiRequest();

                        ftLinkCancelNotiRequest.setTOKEN(payJoaCancelResponse.getTOKEN());
                        ftLinkCancelNotiRequest.setRESULTCODE(payJoaCancelResponse.getRESULTCODE());
                        ftLinkCancelNotiRequest.setERRORMESSAGE(payJoaCancelResponse.getERRORMESSAGE());
                        ftLinkCancelNotiRequest.setDAOUTRX(payJoaCancelResponse.getDAOUTRX());
                        ftLinkCancelNotiRequest.setAMOUNT(payJoaCancelResponse.getAMOUNT());
                        ftLinkCancelNotiRequest.setCANCELDATE(payJoaCancelResponse.getCANCELDATE());
                        ftLinkCancelNotiRequest.setSHOPCODE(page.getShopCode());
                        ftLinkCancelNotiRequest.setORDERNO(orderPurchase.getOrderId());
                        ftLinkCancelNotiRequest.setAPPRDATE(callback.getApprDate());
                        ftLinkCancelNotiRequest.setAPPRTIME(callback.getApprTime());
                        ftLinkCancelNotiRequest.setAPPRTRXID(callback.getPgTranId());
                        ftLinkCancelNotiRequest.setAPPRNO(callbackResult.getApprNo());
                        ftLinkCancelNotiRequest.setPgcode("30");
                        ftLinkCancelNotiRequest.setPAYAMOUNT(String.valueOf(callback.getPrice().intValue()));

                        FTLinkPayApi.cancelNotiRequest(ftLinkCancelNotiRequest);

                        callbackResult.setErrorCode(payJoaCancelResponse.getRESULTCODE());
                        callbackResult.setErrorMsg(payJoaCancelResponse.getERRORMESSAGE());
                        lpngCallbackResultRepository.save(callbackResult);

                        callback.setStatus(false);
                        callback.setProcess(LpngProcess.CANCEL.getType());
                        callback = lpngCallbackRepository.save(callback);
                        return true;
                    } else {
                        logger.error("payJoaCancelResponse Error " + payJoaCancelResponse.getERRORMESSAGE());
                        return false;
                    }
                }


            }

        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            //throw new InvalidBuyException("[POST]/buy/lpng/cancel", e) ;
            return false;
        }
    }

    public PayJoaCancelReadyResponse readyPayJoa(PayJoaCancelReadyRequest payJoaCancelReadyRequest) throws Exception {
        Gson gson = new Gson();
        String readyParams = gson.toJson(payJoaCancelReadyRequest);

        HttpPost post = getHttpPostPayJoa(NOMEMBER_READY_URL, readyParams);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse res = client.execute(post);
        PayJoaCancelReadyResponse payJoaCancelReadyResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), PayJoaCancelReadyResponse.class);

        return payJoaCancelReadyResponse;
    }

    public PayJoaCancelResponse cancelPayJoa(PayJoaCancelRequest payJoaCancelRequest, PayJoaCancelReadyResponse payJoaCancelReadyResponse) throws Exception {
        Gson gson = new Gson();
        String requestParams = gson.toJson(payJoaCancelRequest);
        HttpPost post = getHttpPostPayJoa(payJoaCancelReadyResponse.getRETURNURL(), requestParams);
        post.setHeader("TOKEN", payJoaCancelReadyResponse.getTOKEN());
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse res = client.execute(post);
        PayJoaCancelResponse payJoaCancelResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), PayJoaCancelResponse.class);
        logger.debug("PayJoaCancelResponse: " + gson.toJson(payJoaCancelResponse));

        return payJoaCancelResponse;
    }

    private HttpPost getHttpPostPayJoa(String url, String params) throws Exception {

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json;charset=EUC-KR");
        post.setHeader("Authorization", NOMEMBER_AUTHORIZATION);
        StringEntity entity = new StringEntity(params);
        post.setEntity(entity);
        return post;
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int completeOrderPurchase(Long pageSeqNo, Long orderPurchaseSeqNo) throws ResultCodeException {


        String currentDateTime = AppUtil.localDatetimeNowString();

        OrderPurchase orderPurchase = orderPurchaseRepository.findBySeqNo(orderPurchaseSeqNo);

        if (orderPurchase == null) {
            throw new NotFoundException();
        }

        if (!orderPurchase.getPageSeqNo().equals(pageSeqNo)) {
            throw new NotPermissionException("owner", "not matched.");
        }

        if (orderPurchase.getSalesType() == 6) {
            if (orderPurchase.getStatus() != OrderPurchaseStatus.PAY.getStatus() || orderPurchase.getStatusTicket() != 4) {
                throw new NotPermissionException();
            }

            if (orderPurchase.getExpireDatetime() != null) {
                if (System.currentTimeMillis() > orderPurchase.getExpireDatetime().getTime()) {
                    throw new ExpiredException();
                }
            }
        } else {
            if (orderPurchase.getStatus() != OrderPurchaseStatus.CONFIRM.getStatus()) {
                throw new NotPermissionException();
            }
        }

        if (!orderPurchase.getAppType().equals("cs")) {
            ftlinkPayDecide(pageSeqNo, orderPurchaseSeqNo);
        }

        orderPurchase.setChangeStatusDatetime(currentDateTime);
        if (orderPurchase.getSalesType() == 1) {//방문
            orderPurchase.setStatusShop(OrderPurchaseStatus.COMPLETE.getStatus());
        } else if (orderPurchase.getSalesType() == 2) {//배달
            orderPurchase.setStatusRider(OrderPurchaseStatus.COMPLETE.getStatus());
        } else if (orderPurchase.getSalesType() == 5) {//포장
            orderPurchase.setStatusPack(OrderPurchaseStatus.COMPLETE.getStatus());
        } else if (orderPurchase.getSalesType() == 6) {//티켓
            orderPurchase.setStatusTicket(OrderPurchaseStatus.COMPLETE.getStatus());
        }
        orderPurchase.setStatus(OrderPurchaseStatus.COMPLETE.getStatus());

        recommenderPoint(orderPurchase);

        orderPurchase = orderPurchaseRepository.save(orderPurchase);


        if (orderPurchase.getMemberSeqNo() != null) {
            NotificationBox notificationBox = new NotificationBox();

            BolHistory bolHistory = new BolHistory();
            bolHistory.setAmount(orderPurchase.getSavedPoint());
            bolHistory.setMemberSeqNo(orderPurchase.getMemberSeqNo());


            if (orderPurchase.getSalesType() == 1) {//방문
                bolHistory.setSubject("방문완료");
                notificationBox.setSubject("방문완료");
                notificationBox.setContents("방문완료되어 캐시를 적립해드립니다.");
            } else if (orderPurchase.getSalesType() == 2) {//배달
                bolHistory.setSubject("배달완료");
                notificationBox.setSubject("배달완료");
                notificationBox.setContents("배달완료되어 캐시를 적립해드립니다.");
            } else if (orderPurchase.getSalesType() == 5) {//포장
                bolHistory.setSubject("포장완료");
                notificationBox.setSubject("포장완료");
                notificationBox.setContents("포장완료되어 캐시를 적립해드립니다.");
            } else if (orderPurchase.getSalesType() == 6) {//티켓
                bolHistory.setSubject("티켓사용완료");
                notificationBox.setSubject("티켓사용완료");
                notificationBox.setContents("티켓사용이 완료되어 캐시를 적립해드립니다.");
            }

            bolHistory.setPrimaryType("increase");
            bolHistory.setSecondaryType("buy");
            bolHistory.setTargetType("member");
            bolHistory.setTargetSeqNo(orderPurchase.getMemberSeqNo());
            bolHistory.setHistoryProp(new HashMap<String, Object>());
            bolHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");
            if (orderPurchase.getSalesType() == 6) {//티켓
                bolHistory.getHistoryProp().put("적립유형", "사용완료(" + orderPurchase.getTitle() + ")");
            } else {
                bolHistory.getHistoryProp().put("적립유형", "구매확정(" + orderPurchase.getTitle() + ")");
            }


            bolService.increaseBol(orderPurchase.getMemberSeqNo(), bolHistory);

            notificationBox.setMemberSeqNo(orderPurchase.getMemberSeqNo());
            notificationBox.setMoveSeqNo(orderPurchase.getSeqNo());
            notificationBox.setMoveType1("inner");
            if (orderPurchase.getSalesType() == 6) {//티켓
                notificationBox.setMoveType2("ticketDetail");
            } else {
                notificationBox.setMoveType2("orderDetail");
            }

            notificationBoxService.save(notificationBox);
        }

        if(orderPurchase.getSalesType() == 6){
            OrderMessage message = new OrderMessage();
            message.setSeqNo(orderPurchase.getSeqNo().toString());
            message.setPageSeqNo(orderPurchase.getPageSeqNo().toString());
            message.setType("result");
            useTicketAdmin(message);
        }


        return Const.E_SUCCESS;
    }

    private void recommenderPoint(OrderPurchase orderPurchase){

        try {
            Member member = memberService.getMemberBySeqNo(orderPurchase.getMemberSeqNo());
            if (!AppUtil.isEmpty(member.getRecommendationCode())) {
                Member recommendMember = memberService.getMemberByRecommendKey(member.getRecommendationCode());

                if(recommendMember != null){
                    Float point = orderPurchase.getMenuPrice()*0.0005f;

                    orderPurchase.setRecommendedMemberType(recommendMember.getAppType());
                    orderPurchase.setRecommendedMemberSeqNo(recommendMember.getSeqNo());
                    orderPurchase.setRecommendedMemberPoint(point);

                    if (recommendMember.getAppType().equals(Const.APP_TYPE_BIZ)) {
                        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findFirstByMemberSeqNo(recommendMember.getSeqNo());

                        CashLog cashLog = new CashLog();
                        cashLog.setMemberSeqNo(recommendMember.getSeqNo());
                        cashLog.setPageSeqNo(page.getSeqNo());

                        cashLog.setCash(point.intValue());
                        cashLog.setType("charge");
                        if (orderPurchase.getSalesType() == 6) {
                            cashLog.setNote("티켓사용 추천인 수익금");
                            cashLog.setGaveType("ticket");
                        }else{
                            cashLog.setNote("메뉴주문 추천인 수익금");
                            cashLog.setGaveType("menu");
                        }

                        cashLog.setGaveMemberSeqNo(orderPurchase.getMemberSeqNo());
                        cashLog.setGaveSeqNo(orderPurchase.getSeqNo());

                        cashLogRepository.save(cashLog);
                    }else{
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(point);
                        bolHistory.setMemberSeqNo(recommendMember.getSeqNo());
                        bolHistory.setPrimaryType("increase");

                        if (orderPurchase.getSalesType() == 6) {
                            bolHistory.setSecondaryType("ticket");
                            bolHistory.setSubject("티켓사용 추천인 수익금");
                        }else{
                            bolHistory.setSecondaryType("menu");
                            bolHistory.setSubject("메뉴주문 추천인 수익금");
                        }

                        bolHistory.setTargetType("member");
                        bolHistory.setTargetSeqNo(recommendMember.getSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");

                        if (orderPurchase.getSalesType() == 6) {
                            bolHistory.getHistoryProp().put("적립유형", "내가 추천한 회원 티켓사용");
                        }else{
                            bolHistory.getHistoryProp().put("적립유형", "내가 추천한 회원 메뉴주문");
                        }


                        bolService.increaseBol(recommendMember.getSeqNo(), bolHistory);
                    }
                }

            }
        }catch (Exception e){
            logger.error(e.toString());
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int ftlinkPayDecide(Long pageSeqNo, Long orderPurchaseSeqNo) throws ResultCodeException {
        try {
            Page page = pageRepository.findBySeqNo(pageSeqNo);

            LpngCallback callback = lpngCallbackRepository.findByOrderPurchaseSeqNo(orderPurchaseSeqNo);
            FTLinkPayDecideRequest ftLinkPayDecideRequest = new FTLinkPayDecideRequest();
            if (callback.getLpngOrderNo() != null) {
                ftLinkPayDecideRequest.setOrderno(callback.getLpngOrderNo());
            }

            ftLinkPayDecideRequest.setShopcode(page.getShopCode());
            ftLinkPayDecideRequest.setAPPRTRXID(callback.getPgTranId());
            FTLinkPayCommonResponse res = FTLinkPayApi.payDecideRequest(ftLinkPayDecideRequest);

            Boolean success = false;

            if (res.getErrcode().equals("0000") || res.getErrcode().equals("00") || res.getErrcode().equals("90")) {
                return Const.E_SUCCESS;
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return Const.E_PAYMENT_FAIL;
    }

}
