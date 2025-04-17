//package kr.co.pplus.store.api.jpa.service;
//
//import com.google.gson.Gson;
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.model.ftlink.*;
//import kr.co.pplus.store.api.jpa.model.udonge.UdongeCancelRequest;
//import kr.co.pplus.store.api.jpa.model.udonge.UdongeRequest;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.InvalidBuyException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.exception.SqlException;
//import kr.co.pplus.store.mvc.service.CashBolService;
//import kr.co.pplus.store.mvc.service.QueueService;
//import kr.co.pplus.store.mvc.service.RootService;
//import kr.co.pplus.store.mvc.service.UserService;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.MsgOnly;
//import kr.co.pplus.store.type.model.NoOnlyKey;
//import kr.co.pplus.store.type.model.User;
//import kr.co.pplus.store.util.FTLinkPayApi;
//import kr.co.pplus.store.util.LpngPayApi;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Isolation;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@Service
//@Transactional(transactionManager = "jpaTransactionManager")
//public class BuyService extends RootService {
//    private final static Logger logger = LoggerFactory.getLogger(BuyService.class);
//
//    @Value("${STORE.REDIS_PREFIX}")
//    String REDIS_PREFIX = "pplus-";
//
//    @Value("${ADMIN.STAGE.API}")
//    String adminStageUrl = "https://stg-biz.prnumber.com";
//
//    @Value("${ADMIN.PROD.API}")
//    String adminProdUrl = "https://biz.prnumber.com";
//
//    @Value("${STORE.DAOU.NOMEMBER.CPID}")
//    String NOMEMBER_CPID = "CTS16541";
//
//    @Value("${STORE.DAOU.NOMEMBER.AUTHORIZATION}")
//    String NOMEMBER_AUTHORIZATION = "2e48f6bac5a7b243bed4b647b9387e485ffc148473124aabf6f08953ea3370ac";
//
//    @Value("${STORE.DAOU.NOMEMBER.READY_URL}")
//    String NOMEMBER_READY_URL = "https://apitest.payjoa.co.kr/pay/ready";
//
//    @Value("${STORE.TYPE}")
//    String storeType = "STAGE" ;
//
//    Float PaymentFeeRatio = 0.031f;
//    Float PlatformFeeRatio = 0.039f;
//
//    @Autowired
//    BuyRepository buyRepository;
//
//    @Autowired
//    PageRepository pageRepository;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    LpngCallbackRepository lpngCallbackRepository;
//
//    @Autowired
//    LpngCallbackResultRepository lpngCallbackResultRepository;
//
//    @Autowired
//    BuyGoodsRepository buyGoodsRepository;
//
//    @Autowired
//    QueueService queueService;
//
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Autowired
//    PlusOnlyRepository plusOnlyRepository;
//
//    @Autowired
//    CashBolService cashBolService;
//
//    @Autowired
//    GoodsLikeRepository goodsLikeRepository;
//
//    @Autowired
//    BuyGoodsOptionRepository buyGoodsOptionRepository;
//
//    @Autowired
//    GoodsService goodsService;
//
//    @Autowired
//    BolService bolService;
//
//    @Value("${SYSTEM.BASE_URL}")
//    String systemBaseUrl;
//
//    @Value("${STORE.GOODS_BUY_EXPIRE_DAYS}")
//    public static Integer expireDays = 30; // days
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public Buy addBuyShip(User user, Buy buy) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            buy.setSeqNo(null);
//            if (!buy.isValidOrderId(REDIS_PREFIX)) {
//                throw new Exception("[POST]/buy/ship : The orderId is not valid ");
//            }
//            if (buy.getPg() == null)
//                buy.setPg("DAOU");
//            if (buy.getPayMethod() == null) {
//                buy.setPayMethod("card");
//            }
//
//            buy.setPayType("online");
//
//            buy.setRegDatetime(dateStr);
//            buy.setModDatetime(dateStr);
//            buy.setMemberSeqNo(user.getNo());
//            buy.setMemberLoginId(user.getLoginId());
//            buy.setMemberWoodongyi(user.getWoodongyi());
//            buy.setCash(false);
//            buy.setOrderType(OrderType.SHIPPING_ORDER.getType());
//            String imgPath = null;
//
//            Goods goods1 = null;
//
//            if (buy.getBuyGoodsSelectList() != null) {
//                List<Goods> goodsList = new ArrayList<Goods>();
//                List<BuyGoods> buyGoodsList = new ArrayList<BuyGoods>();
//
//                Float totalPrice = 0.0f;
//                Float totalGoodsPrice = 0.0f;
//                Float totalOptionPrice = 0.0f;
//                Float totalReturnPaymentPrice = 0.0f;
//                Integer totalDeliveryFee = 0;
//                kr.co.pplus.store.api.jpa.model.Page page = null;
//                for (int i = 0; i < buy.getBuyGoodsSelectList().size(); i++) {
//                    BuyGoodsSelect buyGoodsSelect = buy.getBuyGoodsSelectList().get(i);
//                    BuyGoods buyGoods = new BuyGoods();
//                    buyGoods.setGoodsSeqNo(buyGoodsSelect.getGoodsSeqNo());
//                    buyGoods.setCount(buyGoodsSelect.getCount());
//                    buyGoods.setReceiverName(buyGoodsSelect.getReceiverName());
//                    buyGoods.setReceiverTel(buyGoodsSelect.getReceiverTel());
//                    buyGoods.setReceiverPostCode(buyGoodsSelect.getReceiverPostCode());
//                    buyGoods.setReceiverAddress(buyGoodsSelect.getReceiverAddress());
//                    buyGoods.setDeliveryMemo(buyGoodsSelect.getDeliveryMemo());
//                    buyGoods.setGoodsPriceSeqNo(buyGoodsSelect.getGoodsPriceSeqNo());
//                    buyGoods.setDeliveryFee(buyGoodsSelect.getDeliveryFee());
//                    Integer count = buyGoods.getCount();
//                    Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
//
//                    GoodsPriceOnly goodsPrice = goodsService.getGoodsPriceOnlyBySeqNo(buyGoods.getGoodsPriceSeqNo());
//
//                    if (i == 0) {
//                        if(goodsPrice != null){
//                            page = pageRepository.findBySeqNo(goodsPrice.getPageSeqNo());
//                        }else{
//                            page = pageRepository.findBySeqNo(goods.getPageSeqNo());
//                        }
//
//                    }
//
//                    if (imgPath == null && goods.getAttachments() != null) {
//                        List<String> imgIdList = (List<String>) goods.getAttachments().get("images");
//                        if (imgIdList != null && imgIdList.size() > 0) {
//                            imgPath = systemBaseUrl + "/store/api/attachment/image?id=" + imgIdList.get(0);
//                        }
//                    }
//
//                    goodsList.add(goods);
//                    if (goods.getCount() >= 0 && (goods.getStatus() != GoodsStatus.SELL.getStatus() || goods.getCount() < goods.getSoldCount() + count)) {
//                        throw new InvalidBuyException("/buy[POST]", "buy goods data count is not enough : goods.count,soldCount,buyCount : " + goods.getCount() + "," + goods.getSoldCount() + "," + count);
//                    }
//
//                    buyGoods.setRegDatetime(dateStr);
//                    buyGoods.setModDatetime(dateStr);
//                    buyGoods.setPayDatetime(dateStr);
//                    buyGoods.setMemberSeqNo(buy.getMemberSeqNo());
//
//                    buyGoods.setGoodsSeqNo(goods.getSeqNo());
//                    buyGoods.setTitle(goods.getName());
//                    buyGoods.setType(goods.getType());
////                    if (goods.getDeliveryFee() != null && goods.getDeliveryFee() > 0) {
////                        buyGoods.setDeliveryFee(goods.getDeliveryFee());
////                    } else {
////                        buyGoods.setDeliveryFee(0);
////                    }
//
//                    buyGoods.setReviewPoint(goods.getReviewPoint());
//
//                    buyGoods.setProcess(BuyProcess.WAIT.getProcess()); //결제 대기
//                    buyGoods.setOrderType(OrderType.SHIPPING_ORDER.getType());
//                    buy.setOrderProcess(OrderProcess.WAIT.getProcess());
//
//                    buyGoods.setAgentSeqNo(page.getAgentSeqNo());
//
//                    if (goods.getOptionType() != null && goods.getOptionType() > 0) {
//                        int optionPrice = 0;
//                        if (buyGoodsSelect.getBuyGoodsOptionSelectList() != null) {
//                            buyGoods.setBuyGoodsOptionSelectList(buyGoodsSelect.getBuyGoodsOptionSelectList());
//                            for (int j = 0; j < buyGoodsSelect.getBuyGoodsOptionSelectList().size(); j++) {
//                                BuyGoodsOption buyGoodsOption = buyGoodsSelect.getBuyGoodsOptionSelectList().get(j);
//                                optionPrice += (buyGoodsOption.getPrice() * buyGoodsOption.getAmount());
//
//                            }
//                        }
//                        buyGoods.setOptionPrice(optionPrice);
//                    } else {
//                        buyGoods.setOptionPrice(0);
//                    }
//
//                    if(buyGoods.getDeliveryFee() == null){
//                        buyGoods.setDeliveryFee(0);
//                    }
//
//                    if(goodsPrice != null){
//                        buyGoods.setSupplyPrice(goodsPrice.getSupplyPrice() * count);
//                        buyGoods.setPageSeqNo(goodsPrice.getPageSeqNo());
//                        buyGoods.setSupplyPageSeqNo(goods.getPageSeqNo());
//                        buyGoods.setPrice((goodsPrice.getPrice() * count) + buyGoods.getOptionPrice() + buyGoods.getDeliveryFee());
//                        buyGoods.setGoodsPrice((goodsPrice.getPrice() * count) + buyGoods.getOptionPrice());
//                        buyGoods.setUnitPrice(goodsPrice.getPrice());
//
//                        // 수수료 계산
//                        if(goods.getMarketType().equals("wholesale")) { // 도매상품일경우
//
//                            Float paymentFee = buyGoods.getPrice() * PaymentFeeRatio; // 3.1
//                            Float platformFee = buyGoods.getPrice() * PlatformFeeRatio; // 3.9
//
//                            buyGoods.setPaymentFee(paymentFee);
//                            buyGoods.setPlatformFee(platformFee);
//
//                            Float sellPrice = buyGoods.getPrice() - buyGoods.getDeliveryFee(); // 판매가격
//                            Float supplyPrice = buyGoods.getSupplyPrice() + buyGoods.getOptionPrice(); // 공급가격
//                            Float benefitPrice = sellPrice - supplyPrice; // 수익금
//
//
//                            Float supplyPricePaymentFee = supplyPrice * PaymentFeeRatio; // 공급가 결제수수료
//                            Float benefitPaymentFee = benefitPrice * PaymentFeeRatio; // 수익금 결제수수료
//                            Float deliveryFeePaymentFee = buyGoods.getDeliveryFee() * PaymentFeeRatio; // 배송비 결제수수료
//
//
//                            Float supplyPriceFee = supplyPrice * PlatformFeeRatio; // 공급가 수수료
//                            Float benefitFee = benefitPrice * PlatformFeeRatio; // 수익금 수수료
//                            Float deliveryFeeFee = buyGoods.getDeliveryFee() * PlatformFeeRatio; // 배송비 수수료
//
//                            buyGoods.setSupplyPricePaymentFee(supplyPricePaymentFee);
//                            buyGoods.setBenefitPaymentFee(benefitPaymentFee);
//                            buyGoods.setDeliveryFeePaymentFee(deliveryFeePaymentFee);
//
//                            buyGoods.setSupplyPriceFee(supplyPriceFee);
//                            buyGoods.setBenefitFee(benefitFee);
//                            buyGoods.setDeliveryFeeFee(deliveryFeeFee);
//
//                            Float returnPayment = platformFee + supplyPrice + buyGoods.getDeliveryFee() - supplyPricePaymentFee - deliveryFeePaymentFee - supplyPriceFee - deliveryFeeFee;
//
//
//                            buyGoods.setReturnPaymentPrice(returnPayment);
//
//
//                        }else { // 소매상품일 경우
//                            // 결제수수료 = 결제금액 * 결제수수료비율
//                            // 플랫폼수수료 = 결제금액 * 플랫폼수수료비율
//
//                            Float paymentFee = buyGoods.getPrice() * PaymentFeeRatio;
//                            Float platformFee = buyGoods.getPrice() * PlatformFeeRatio;
//
//                            buyGoods.setPaymentFee(paymentFee);
//                            buyGoods.setPlatformFee(platformFee);
//
//                            buyGoods.setReturnPaymentPrice(platformFee);
//
//                        }
//
//                        if(goodsPrice.getIsLuckyball()){
//                            float point = (buyGoods.getGoodsPrice())* 0.1f;
//                            buyGoods.setPointRatio(page.getPoint());
//                            buyGoods.setSavedPoint((int) point);
//                        }else{
//                            float point = buyGoods.getGoodsPrice() * (page.getPoint() / 100);
//                            buyGoods.setPointRatio(page.getPoint());
//                            buyGoods.setSavedPoint((int) point);
//                        }
//
//                    }else{
//                        buyGoods.setPageSeqNo(goods.getPageSeqNo());
//                        buyGoods.setPrice((goods.getPrice() * count) + buyGoods.getOptionPrice() + buyGoods.getDeliveryFee());
//                        buyGoods.setGoodsPrice((goods.getPrice() * count) + buyGoods.getOptionPrice());
//                        buyGoods.setUnitPrice(goods.getPrice());
//
//                        float point = buyGoods.getGoodsPrice() * (page.getPoint() / 100);
//                        buyGoods.setPointRatio(page.getPoint());
//                        buyGoods.setSavedPoint((int) point);
//                    }
//
//                    buyGoods.setCommissionRatio(page.getAgent().getPartner().getCommission());
//                    buyGoods.setVat(buyGoods.calculateVat());
//                    buyGoodsList.add(buyGoods);
//                    totalPrice += buyGoods.getPrice();
//                    totalGoodsPrice += buyGoods.getGoodsPrice();
//                    totalOptionPrice += buyGoods.getOptionPrice();
//                    totalDeliveryFee += buyGoods.getDeliveryFee();
//                    totalReturnPaymentPrice += buyGoods.getReturnPaymentPrice();
//                    if (i == 0) {
//                        goods1 = goods;
//                    }
//                }
//
//                buy.setReturnPaymentPrice(totalReturnPaymentPrice);
//                buy.setDeliveryFee(totalDeliveryFee);
//                buy.setPageSeqNo(buyGoodsList.get(0).getPageSeqNo());
//                buy.setPrice(totalPrice);
//                buy.setGoodsPrice(totalGoodsPrice);
//                buy.setOptionPrice(totalOptionPrice);
//                buy.setVat(buy.calculateVat());
//                buy.setOrderProcess(null);
//                buy.setProcess(BuyProcess.WAIT.getProcess());
//
//                buy.setType(goods1.getType());
//                buy.setIsHotdeal(goods1.getIsHotdeal());
//                buy.setIsPlus(goods1.getIsPlus());
//                buy.setAgentSeqNo(page.getAgentSeqNo());
//
////                if (page.getPoint() != null && page.getPoint() > 0) {
////                    float point = (buy.getGoodsPrice() + buy.getOptionPrice())* 0.1f;
////                    buy.setPointRatio(10f);
////                    buy.setSavedPoint((int) point);
////                }
//
////                float point = (buy.getGoodsPrice())* 0.1f;
////                buy.setPointRatio(10f);
////                buy.setSavedPoint((int) point);
//
//                buy.setCommissionRatio(page.getAgent().getPartner().getCommission());
//                buy = buyRepository.saveAndFlush(buy);
//                if (buy == null || buy.getSeqNo() == null) {
//                    throw new Exception("[POST]/buy/ship" + " :  buy data insert error !!!");
//                }
//
//                int i = 0;
//                List<BuyGoodsOption> buyGoodsOptionList = null;
//                for (BuyGoods buyGoods : buyGoodsList) {
//
//                    buyGoodsOptionList = buyGoods.getBuyGoodsOptionSelectList();
//
//                    buyGoods.setSeqNo(null);
//                    buyGoods.setBuySeqNo(buy.getSeqNo());
//                    try {
//                        buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//                    } catch (Exception e) {
//                        throw new InvalidBuyException("/buy[POST]", "The buyGoods save error : " + e.getMessage() + ":" + buyGoods.toString());
//                    }
//                    if (buyGoods == null || buyGoods.getSeqNo() == null) {
//                        throw new InvalidBuyException("/buy[POST]", "The buyGoods insert error : buy.seqNo : " + buyGoods.toString());
//                    }
//
//                    for (BuyGoodsOption buyGoodsOption : buyGoodsOptionList) {
//                        buyGoodsOption.setSeqNo(null);
//                        buyGoodsOption.setBuyGoodsSeqNo(buyGoods.getSeqNo());
//                        buyGoodsOption.setBuySeqNo(buy.getSeqNo());
//
//                        GoodsOptionDetail goodsOptionDetail = goodsService.getGoodsOptionDetailBySeqNo(buyGoodsOption.getGoodsOptionDetailSeqNo());
//                        if (goodsOptionDetail.getItem1() != null) {
//                            GoodsOption goodsOption = goodsService.getGoodsOptionBySeqNo(goodsOptionDetail.getItem1().getOptionSeqNo());
//                            buyGoodsOption.setDepth1(goodsOption.getName() + " : " + goodsOptionDetail.getItem1().getItem());
//                        }
//
//                        if (goodsOptionDetail.getItem2() != null) {
//                            GoodsOption goodsOption = goodsService.getGoodsOptionBySeqNo(goodsOptionDetail.getItem2().getOptionSeqNo());
//                            buyGoodsOption.setDepth2(goodsOption.getName() + " : " + goodsOptionDetail.getItem2().getItem());
//                        }
//
//                        if (goodsOptionDetail.getItem3() != null) {
//                            GoodsOption goodsOption = goodsService.getGoodsOptionBySeqNo(goodsOptionDetail.getItem3().getOptionSeqNo());
//                            buyGoodsOption.setDepth3(goodsOption.getName() + " : " + goodsOptionDetail.getItem3().getItem());
//                        }
//
//                        buyGoodsOption = buyGoodsOptionRepository.saveAndFlush(buyGoodsOption);
//                    }
//                    buyGoods.setBuyGoodsOptionSelectList(buyGoodsOptionList);
//                    buyGoodsList.set(i, buyGoods);
////					logger.debug(getUri(request), "buyGoodsList : " + i + ":" + buyGoodsList.get(i).getSeqNo(), buyGoodsList.get(i).getPrice(), buyGoodsList.get(i).getVat(), buyGoodsList.get(i).getCount());
//
//
//                    i++;
//                }
//            }
//
//        } catch (InvalidBuyException e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw e;
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new SqlException("[POST]/buy/ship", e);
//        }
//        return buy;
//    }
//
//    public Buy addBuyShop(User user, Buy buy) throws ResultCodeException {
//        try {
//
//            String dateStr = AppUtil.localDatetimeNowString();
//            buy.setSeqNo(null);
//            if (!buy.isValidOrderId(REDIS_PREFIX)) {
//                throw new Exception("[POST]/buy/shop : The orderId is not valid ");
//            }
//            if (buy.getPg() == null)
//                buy.setPg("DAOU");
//            if (buy.getPayMethod() == null) {
//                buy.setPayMethod("card");
//            }
//
//            buy.setPayType("online");
//
//            buy.setRegDatetime(dateStr);
//            buy.setModDatetime(dateStr);
//            buy.setMemberSeqNo(user.getNo());
//            buy.setMemberLoginId(user.getLoginId());
//            buy.setMemberWoodongyi(user.getWoodongyi());
//            buy.setCash(false);
//            String imgPath = null;
//
//
//            /*
//            if( buy.getBookDatetime() != null ) {
//                //ToDo 나라별 세팅이 필요함 (서버와 클라이언트의 타임존[나라]이 다를경우)
//                String tmp = buy.getBookDatetime() ;
//                logger.info("bookDatetime Asia/Seoul: " + tmp) ;
//                String bookDatetime = AppUtil.utcFromZoneTimeString("Asia/Seoul", tmp) ;
//                buy.setBookDatetime(bookDatetime);
//                logger.info("bookDatetime UTF: " + bookDatetime) ;
//            }
//            */
//
////			logger.debug(getUri(request), buy.toString() + "\nbuyerName : " + buy.getBuyerName());
//            Goods goods1 = null;
//
//            if (buy.getBuyGoodsSelectList() != null) {
//                List<Goods> goodsList = new ArrayList<Goods>();
//                List<BuyGoods> buyGoodsList = new ArrayList<BuyGoods>();
//
//                Float totalPrice = 0.0f;
//                Float totalGoodsPrice = 0.0f;
//                Float totalVat = 0.0f;
//                kr.co.pplus.store.api.jpa.model.Page page = null;
//                for (int i = 0; i < buy.getBuyGoodsSelectList().size(); i++) {
//                    BuyGoodsSelect buyGoodsSelect = buy.getBuyGoodsSelectList().get(i);
//                    BuyGoods buyGoods = new BuyGoods();
//                    buyGoods.setGoodsSeqNo(buyGoodsSelect.getGoodsSeqNo());
//                    buyGoods.setCount(buyGoodsSelect.getCount());
//                    Integer count = buyGoods.getCount();
//                    Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
//
//                    if (i == 0) {
//                        page = pageRepository.findBySeqNo(goods.getPageSeqNo());
//                    }
//
//                    if (imgPath == null && goods.getAttachments() != null) {
//                        List<String> imgIdList = (List<String>) goods.getAttachments().get("images");
//                        if (imgIdList != null && imgIdList.size() > 0) {
//                            imgPath = systemBaseUrl + "/store/api/attachment/image?id=" + imgIdList.get(0);
//                        }
//                    }
//
//                    goodsList.add(goods);
//                    if (goods.getCount() >= 0 && (goods.getStatus() != GoodsStatus.SELL.getStatus() || goods.getCount() < goods.getSoldCount() + count)) {
//                        throw new InvalidBuyException("/buy[POST]", "buy goods data count is not enough : goods.count,soldCount,buyCount : " + goods.getCount() + "," + goods.getSoldCount() + "," + count);
//                    }
//
//
//                    buyGoods.setRegDatetime(dateStr);
//                    buyGoods.setModDatetime(dateStr);
//                    buyGoods.setPayDatetime(dateStr);
//                    buyGoods.setMemberSeqNo(buy.getMemberSeqNo());
//                    buyGoods.setPageSeqNo(goods.getPageSeqNo());
//                    buyGoods.setGoodsSeqNo(goods.getSeqNo());
//                    buyGoods.setPrice(goods.getPrice() * count);
//                    buyGoods.setGoodsPrice(goods.getPrice() * count);
//                    buyGoods.setUnitPrice(goods.getPrice());
//                    buyGoods.setTitle(goods.getName());
//                    buyGoods.setType(goods.getType());
//                    buyGoods.setVat(buyGoods.calculateVat());
//                    buyGoods.setProcess(BuyProcess.WAIT.getProcess()); //결제 대기
//                    buyGoods.setOrderType(buy.getOrderType());
//
//                    buyGoods.setAgentSeqNo(page.getAgentSeqNo());
//                    float point = buyGoods.getGoodsPrice() * (page.getPoint() / 100);
//                    buyGoods.setPointRatio(page.getPoint());
//                    buyGoods.setSavedPoint((int) point);
//                    buyGoods.setCommissionRatio(page.getAgent().getPartner().getCommission());
//
//                    buyGoods.setAllWeeks(goods.getAllWeeks());
//                    buyGoods.setAllDays(goods.getAllDays());
//                    buyGoods.setDayOfWeeks(goods.getDayOfWeeks());
//                    buyGoods.setStartTime(goods.getStartTime());
//                    buyGoods.setEndTime(goods.getEndTime());
//
//                    if (StringUtils.isNotEmpty(goods.getServiceCondition())) {
//                        buyGoods.setServiceCondition(goods.getServiceCondition());
//                    }
//                    if (StringUtils.isNotEmpty(goods.getTimeOption())) {
//                        buyGoods.setTimeOption(goods.getTimeOption());
//                    }
//
//                    if (StringUtils.isNotEmpty(goods.getExpireDatetime())) {
//                        buyGoods.setExpireDatetime(goods.getExpireDatetime());
//                    } else {
//                        buyGoods.setExpireDatetime(AppUtil.localDatetimeNowString(expireDays));
//                    }
//
//                    buyGoods.setOrderProcess(null);
//                    buyGoodsList.add(buyGoods);
//                    totalPrice += buyGoods.getPrice();
//                    totalGoodsPrice += buyGoods.getGoodsPrice();
//
//                    if (i == 0) {
//                        goods1 = goods;
//                    }
//                }
//
//                if (buy.getOrderType() == OrderType.DELIVERY_ORDER.getType() && buy.getDeliveryFee() != null) {
//                    totalPrice += buy.getDeliveryFee();
//                }
//                buy.setPageSeqNo(buyGoodsList.get(0).getPageSeqNo());
//                buy.setPrice(totalPrice);
//                buy.setGoodsPrice(totalGoodsPrice);
//                buy.setVat(buy.calculateVat());
//                buy.setOrderProcess(null);
//                buy.setProcess(BuyProcess.WAIT.getProcess());
//
//                buy.setType(goods1.getType());
//                buy.setIsHotdeal(goods1.getIsHotdeal());
//                buy.setIsPlus(goods1.getIsPlus());
//                buy.setAgentSeqNo(page.getAgentSeqNo());
//                if (page.getPoint() != null && page.getPoint() > 0) {
//                    float point = buy.getGoodsPrice() * (page.getPoint() / 100);
//                    buy.setPointRatio(page.getPoint());
//                    buy.setSavedPoint((int) point);
//                }
//
//                buy.setCommissionRatio(page.getAgent().getPartner().getCommission());
//                buy = buyRepository.saveAndFlush(buy);
//                if (buy == null || buy.getSeqNo() == null) {
//                    throw new Exception("[POST]/buy/shop :  buy data insert error !!!");
//                }
//
//                int i = 0;
//                for (BuyGoods buyGoods : buyGoodsList) {
//
//                    buyGoods.setSeqNo(null);
//                    buyGoods.setBuySeqNo(buy.getSeqNo());
//                    try {
//                        buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//                    } catch (Exception e) {
//                        throw new InvalidBuyException("/buy[POST]", "The buyGoods save error : " + e.getMessage() + ":" + buyGoods.toString());
//                    }
//                    if (buyGoods == null || buyGoods.getSeqNo() == null) {
//                        throw new InvalidBuyException("/buy[POST]", "The buyGoods insert error : buy.seqNo : " + buyGoods.toString());
//                    }
//                    buyGoodsList.set(i, buyGoods);
////					logger.debug(getUri(request), "buyGoodsList : " + i + ":" + buyGoodsList.get(i).getSeqNo(), buyGoodsList.get(i).getPrice(), buyGoodsList.get(i).getVat(), buyGoodsList.get(i).getCount());
//
//                    i++;
//                }
//            } else {
//                throw new Exception("[POST]/buy/shop parameter error");
//            }
//            return buy;
//        } catch (InvalidBuyException e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw e;
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new SqlException("[POST]/buy/shop", e);
//        }
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public Buy addBuyQr(User user, Buy buy) throws ResultCodeException {
//
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            buy.setSeqNo(null);
//            if (!buy.isValidOrderId(REDIS_PREFIX)) {
//                throw new Exception("/buy[POST] : The orderId is not valid ");
//            }
//            buy.setPg("DAOU");
//            if (buy.getPayMethod() == null) {
//                buy.setPayMethod("card");
//            }
//            buy.setRegDatetime(dateStr);
//            buy.setModDatetime(dateStr);
//
//            buy.setMemberSeqNo(user.getNo());
//            buy.setMemberWoodongyi(user.getWoodongyi());
//            buy.setCash(false);
//            buy.setPayType("qr");
//            buy.setOrderType(OrderType.SHOP_ORDER.getType());
//            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//            buy.setAgentSeqNo(page.getAgentSeqNo());
//            if (page.getPoint() != null && page.getPoint() > 0) {
//                float point = buy.getPrice() * (page.getPoint() / 100);
//                buy.setPointRatio(page.getPoint());
//                buy.setSavedPoint((int) point);
//            }
//            buy.setCommissionRatio(page.getAgent().getPartner().getCommission());
//
//
//            buy.setVat(buy.calculateVat());
//            buy.setProcess(BuyProcess.WAIT.getProcess());
//
////			buy.setOrderProcess(OrderProcess.WAIT.getProcess());
////			buy.setType(GoodsType.MENU_GOODS.getType());
//
//
//            buy = buyRepository.saveAndFlush(buy);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new SqlException("[POST]/buy/qr error", "insert error");
//        }
//
//        return buy;
//    }
//
//
//    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    public FTLinkPayResponse ftlinkPay(FTLinkPayRequest data) throws ResultCodeException {
//        try {
//
//            if (StringUtils.isEmpty(data.getShopcode())) {
//                FTLinkPayResponse res = new FTLinkPayResponse();
//                res.setErrCode("99");
//                res.setErrMessage("결제 가능한 스토어가 아닙니다.");
//                return res;
//            }
//
//            String serverType = data.getServerType();
//            String roomId = data.getRoomId();
//
//            Buy buy = buyRepository.findByOrderId(data.getComp_orderno());
//
//            if(Integer.valueOf(data.getOrder_req_amt()) != buy.getPrice().intValue()){
//                throw new InvalidBuyException("ftlinkPay", "결제금액이 일치하지 않습니다.");
//            }
//
//            data.setManual_used("Y");
//            data.setManual_amt(String.valueOf(buy.getReturnPaymentPrice().intValue()));
//
//            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//            User pageUser = userService.getUser(page.getMemberSeqNo());
//            data.setLoginId(pageUser.getLoginId().replace(pageUser.getAppType() + "##", ""));
//            data.setServerType("");
//            data.setRoomId("");
//            if (buy.getOrderType() == OrderType.SHIPPING_ORDER.getType()) {
//                data.setReqdephold("Y");
//            }
//            if(storeType.equals("PROD")){
//                data.setISTEST("USE");
//            }else{
//                data.setISTEST("TEST");
//            }
//
//            FTLinkPayResponse res = FTLinkPayApi.payRequest(data);
//            String dateStr = AppUtil.localDatetimeNowString();
//
//
//            LpngCallbackResult callbackResult = new LpngCallbackResult();
//            callbackResult.setErrorMsg(res.getErrMessage());
//            callbackResult.setShopCode(res.getShopcode());
//            callbackResult.setOrderNo(res.getOrderno());
//            callbackResult.setErrorCode(res.getErrCode());
//            callbackResult.setCompOrderNo(res.getComp_orderno());
//            callbackResult.setCompMemNo(res.getComp_memno());
//            callbackResult.setOrderGoodsname(res.getOrder_goodsname());
//            callbackResult.setOrderReqAmt(res.getOrder_req_amt());
//            callbackResult.setOrderName(res.getOrder_name());
//            callbackResult.setOrderHp(res.getOrder_hp());
//            callbackResult.setOrderEmail(res.getOrder_email());
//            callbackResult.setCompTemp1(res.getComp_temp1());
//            callbackResult.setCompTemp2(res.getComp_temp2());
//            callbackResult.setCompTemp3(res.getComp_temp3());
//            callbackResult.setCompTemp4(res.getComp_temp4());
//            callbackResult.setCompTemp5(res.getComp_temp5());
//            callbackResult.setReqInstallment(res.getReq_installment());
//            callbackResult.setApprNo(res.getAppr_no());
//            callbackResult.setApprTranNo(res.getAppr_tranNo());
//            callbackResult.setApprShopCode(res.getAppr_shopCode());
//            callbackResult.setApprDate(res.getAppr_date());
//            callbackResult.setApprTime(res.getAppr_time());
//            callbackResult.setCardtxt(res.getCardtxt());
//
//            callbackResult = lpngCallbackResultRepository.saveAndFlush(callbackResult);
//
//            LpngCallback callback = new LpngCallback();
//            callback.setSeqNo(null);
//            callback.setBuySeqNo(buy.getSeqNo());
//            callback.setMemberSeqNo(buy.getMemberSeqNo());
//            callback.setPgTranId(res.getAppr_tranNo());
//            callback.setApprDate(res.getAppr_date());
//            callback.setApprTime(res.getAppr_time());
//            callback.setOrderId(data.getComp_orderno());
//            callback.setName(res.getOrder_name());
//            callback.setPrice(Integer.parseInt(res.getOrder_req_amt()));
//            callback.setPaymentData(AppUtil.ConverObjectToMap(res));
//            callback.setRegDatetime(dateStr);
//            callback.setLpngOrderNo(res.getOrderno());
//            callback.setResultSeqNo(callbackResult.getSeqNo());
//
//            List<BuyGoods> buyGoodsList = buyGoodsRepository.findAllByBuySeqNo(buy.getSeqNo());
//
//            if (StringUtils.isNotEmpty(roomId)) {
//                buy.setRoomId(roomId);
//            }
//
//            if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {
//
//                User user = userService.getUser(buy.getMemberSeqNo());
//
//                if (page.getWoodongyi()) {
//                    UdongeRequest udongeRequest = new UdongeRequest();
//                    udongeRequest.setGubun("AP");
//                    udongeRequest.setUserid(user.getLoginId());
//                    udongeRequest.setGa_userid(pageUser.getLoginId());
//                    udongeRequest.setAmount(res.getOrder_req_amt());
//                    udongeRequest.setOrd_num(res.getOrderno());
//                    udongeRequest.setTr_num(res.getAppr_tranNo());
//                    udongeRequest.setTrdate(res.getAppr_date() + res.getAppr_time());
//                    LpngPayApi.callUdonge(udongeRequest);
//                }
//
//                callback.setStatus(true);
//                callback.setProcess(LpngProcess.PAY.getType());
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//
//                int process = BuyProcess.PAY.getProcess(); // 결제 승인
//
//                if (buyGoodsList != null) {
//
//                    for (int i = 0; i < buyGoodsList.size(); i++) {
//                        BuyGoods buyGoods = buyGoodsList.get(i);
//                        if (buyGoods.getProcess() < process) {
//
//                            try {
//                                buyGoods.setProcess(process);
//                                buyGoods.setModDatetime(dateStr);
//                                buyGoods.setPayDatetime(dateStr);
//                                buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//                                Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
//                                goods.setSoldCount(goods.getSoldCount() + buyGoods.getCount());
//                                if (goods.getCount() <= goods.getSoldCount()) {
//                                    goods.setStatus(GoodsStatus.SOLD_OUT.getStatus()); //판매종료
//                                    goodsService.updateGoodsPriceStatusByGoodsSeqNo(goods.getSeqNo(), goods.getStatus());
//                                }
//                                goods.setModDatetime(dateStr);
//
//                                goods = goodsRepository.saveAndFlush(goods);
//
//                                List<BuyGoodsOption> buyGoodsOptionList = buyGoodsOptionRepository.findAllByBuyGoodsSeqNo(buyGoods.getSeqNo());
//                                if (buyGoodsOptionList != null) {
//                                    for (BuyGoodsOption buyGoodsOption : buyGoodsOptionList) {
//                                        goodsService.updateGoodsOptionDetailPlusSoldCount(buyGoodsOption.getGoodsOptionDetailSeqNo(), buyGoodsOption.getAmount());
//                                    }
//                                }
//
//                                if (goods.getStatus() == GoodsStatus.SOLD_OUT.getStatus()) {
//                                    goodsLikeRepository.deleteAllByGoodsSeqNo(goods.getSeqNo());
//                                }
//                            } catch (Exception e) {
//                                throw new InvalidBuyException("ftlinkPay", e);
//                            }
//                        }
//                    }
//                }
//
//                buy.setProcess(process);
//                buy.setModDatetime(dateStr);
//                buy.setCompleteDatetime(dateStr);
//                buy.setPayDatetime(dateStr);
//                buy.setPgTranId(res.getAppr_tranNo());
//
//
//                if (buy.getPayType().equals("qr")) {
//                    Integer point = buy.getSavedPoint();
//                    if (point != null && point > 0) {
//
//                        if (buy.getMemberSeqNo() != null) {
//                            kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
//                            bolHistory.setAmount(Long.valueOf(point));
//                            bolHistory.setMemberSeqNo(buy.getMemberSeqNo());
//                            bolHistory.setSubject("상품구매 적립");
//                            bolHistory.setPrimaryType("increase");
//                            bolHistory.setSecondaryType("buy");
//                            bolHistory.setTargetType("member");
//                            bolHistory.setTargetSeqNo(buy.getMemberSeqNo());
//                            bolHistory.setHistoryProp(new HashMap<String, Object>());
//                            bolHistory.getHistoryProp().put("지급처", page.getName());
//                            bolHistory.getHistoryProp().put("적립유형", "QR결제");
//
//                            bolService.increaseBol(buy.getMemberSeqNo(), bolHistory);
//                        }
//                    }
//
//                    buyGoodsRepository.updateBuyGoodsPaymentPointByBuySeqNo(buy.getSeqNo(), dateStr, 1);
//                    buy.setIsPaymentPoint(true);
//                }
//
//                buy = buyRepository.saveAndFlush(buy);
//
//                try {
//                    PlusOnly plusOnly = plusOnlyRepository.findByMemberSeqNoAndPageSeqNo(user.getNo(), buy.getPageSeqNo());
//                    if (plusOnly != null) {
//                        plusOnly.setBuyCount(plusOnly.getBuyCount() + 1);
//                        plusOnly.setLastBuyDatetime(dateStr);
//                        plusOnlyRepository.save(plusOnly);
//                    }
//                } catch (Exception e) {
//                    logger.error("plus error : " + e.toString());
//                }
//
//                try {
//                    if (buy.getOrderType() == OrderType.SHIPPING_ORDER.getType() && buyGoodsList != null) {
//
//                        for (BuyGoods buyGoods : buyGoodsList) {
//
//                            page = pageRepository.findBySeqNo(buyGoods.getPageSeqNo());
//                            pageUser = userService.getUser(page.getMemberSeqNo());
//
//                            MsgOnly msg = new MsgOnly();
//                            msg.setIncludeMe(false);
//                            msg.setInput(Const.MSG_INPUT_SYSTEM);
//                            msg.setStatus(Const.MSG_STATUS_READY);
//                            msg.setType(Const.MSG_TYPE_PUSH);
//                            msg.setMoveType1(Const.MOVE_TYPE_INNER);
//                            msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//                            msg.setAppType(Const.APP_TYPE_BIZ);
//                            msg.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
//                            msg.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
//                            msg.setContents(buy.getTitle());
//                            msg.setMoveTarget(new NoOnlyKey(buyGoods.getSeqNo()));
//                            queueService.insertMsgBox(user, msg, pageUser, Const.APP_TYPE_BIZ);
//                        }
//
//
//                    } else {
//                        MsgOnly msg = new MsgOnly();
//                        msg.setIncludeMe(false);
//                        msg.setInput(Const.MSG_INPUT_SYSTEM);
//                        msg.setStatus(Const.MSG_STATUS_READY);
//                        msg.setType(Const.MSG_TYPE_PUSH);
//                        msg.setMoveType1(Const.MOVE_TYPE_INNER);
//                        msg.setMoveTarget(new NoOnlyKey(buy.getSeqNo()));
//                        msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
//                        msg.setAppType(Const.APP_TYPE_BIZ);
//                        if (buy.getPayType().equals("qr")) {
//                            msg.setMoveType2(Const.MOVE_TYPE_BUY_QR);
//                            msg.setSubject("QR결제가 완료되었습니다.");
//                        } else {
//                            msg.setMoveType2(Const.MOVE_TYPE_BUY);
//                            msg.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
//                        }
//
//                        msg.setContents(buy.getTitle());
//                        queueService.insertMsgBox(user, msg, pageUser, Const.APP_TYPE_BIZ);
//                    }
//
//
//                } catch (Exception e) {
//                    logger.error("push error : " + e.toString());
//                }
//
//            } else {
//                try {
//                    callback.setStatus(false);
//                    callback.setProcess(LpngProcess.CANCEL.getType());
//                    callback.setMemo("결제 실패");
//                    if (!AppUtil.isEmpty(callback.getPgTranId())) {
//                        callback = lpngCallbackRepository.saveAndFlush(callback);
//                    }
//                    int process = BuyProcess.DENIED.getProcess();
//                    buy.setProcess(process);
//                } catch (Exception e) {
//                    int process = BuyProcess.ERROR.getProcess();
//                    buy.setProcess(process);
//                }
//
//                buy = buyRepository.saveAndFlush(buy);
//
//                //상품(Goods.soldCount) 원복
////				if(buyGoodsList != null){
////					for (BuyGoods buyGoods : buyGoodsList) {
////						Goods goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
////						if (goods != null) {
////							goods.setSoldCount(goods.getSoldCount() - buyGoods.getCount());
////							goods.setModDatetime(dateStr);
////							goodsRepository.saveAndFlush(goods);
////						}
////					}
////				}
//
//
////				buyRepository.delete(buy);
//                res.setReq_installment(null);
//            }
//
//
//            if (StringUtils.isNotEmpty(serverType)) {
//                adminResult(serverType, res.getErrCode(), roomId, buy.getOrderId());
//            }
//            return res;
//        } catch (Exception e) {
//            throw new InvalidBuyException("[POST]/buy/lpng/pay", e);
//        }
//
//    }
//
//    public void adminResult(String serverType, String resultCode, String roomId, String orderId) {
//        try {
//            String url = "";
//            if (serverType.equals("stage")) {
//                url = adminStageUrl + "lpng/result";
//            } else if (serverType.equals("prod")) {
//                url = adminProdUrl + "lpng/result";
//            }
//
//            if (StringUtils.isNotEmpty(url)) {
//                logger.debug(url);
//
//                List<NameValuePair> nameValuePairList = new ArrayList<>();
//                nameValuePairList.add(new BasicNameValuePair("resultCode", resultCode));
//                nameValuePairList.add(new BasicNameValuePair("roomId", roomId));
//                nameValuePairList.add(new BasicNameValuePair("orderId", orderId));
//
//                logger.debug("roomId: " + roomId + " " + "orderId : " + orderId);
//                CloseableHttpClient client = HttpClients.createDefault();
//                HttpPost post = AppUtil.getPost(url, nameValuePairList);
//                HttpResponse res = client.execute(post);
////				client.execute(post) ;
//
//                logger.debug("code : " + res.getStatusLine().getStatusCode());
//                client.close();
//            }
//        } catch (Exception e) {
//
//        }
//
//    }
//
//    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
//    public void ftLinkCancelResult(FTLinkCancelResponse res) {
//
//        if (res.getErrCode().equals("00") || res.getErrCode().equals("0000")) {
//            LpngCallback callback = lpngCallbackRepository.findByPgTranId(res.getAPPRTRXID());
//
//            if (callback != null) {
//                logger.debug("pgId : "+callback.getPgTranId());
//                LpngCallbackResult callbackResult = callback.getResult();
//
//                callbackResult.setErrorCode(res.getErrCode());
//                callbackResult.setErrorMsg(res.getErrMessage());
//                lpngCallbackResultRepository.save(callbackResult);
//
//                callback.setStatus(false);
////                Map<String, String> map = callback.getPaymentData();
////                map.put("errCode", "-1");
////                map.put("errMessage", res.getErrMessage());
////                callback.setPaymentData(map);
//                callback.setProcess(LpngProcess.CANCEL.getType());
//                callback = lpngCallbackRepository.saveAndFlush(callback);
//
//                Buy buy = buyRepository.findByOrderId(callback.getOrderId());
//                String dateStr = AppUtil.localDatetimeNowString();
//
//
//                buy.setCancelDatetime(dateStr);
//                buy.setProcess(BuyProcess.USER_CANCEL.getProcess());
//                buyRepository.saveAndFlush(buy);
//                buyGoodsRepository.updateCancelByBuySeqNo(BuyProcess.USER_CANCEL.getProcess(), buy.getSeqNo(), dateStr);
//
//                if (buy.getMemberSeqNo() != null) {
//                    User user = userService.getUser(buy.getMemberSeqNo());
//                    try {
//                        if (buy.getIsPaymentPoint()) {
//                            Integer point = buy.getSavedPoint();
//                            if (point != null && point > 0) {
//
//                                if (buy.getMemberSeqNo() != null) {
//                                    kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
//                                    bolHistory.setAmount(Long.valueOf(point));
//                                    bolHistory.setMemberSeqNo(buy.getMemberSeqNo());
//                                    bolHistory.setSubject("상품구매 취소");
//                                    bolHistory.setPrimaryType("decrease");
//                                    bolHistory.setSecondaryType("buyCancel");
//                                    bolHistory.setTargetType("member");
//                                    bolHistory.setTargetSeqNo(buy.getMemberSeqNo());
//                                    bolHistory.setHistoryProp(new HashMap<String, Object>());
//                                    bolHistory.getHistoryProp().put("사유", "구매취소에 의한 환불");
//                                    bolService.decreaseBol(buy.getMemberSeqNo(), bolHistory);
//                                }
//                            }
//                        }
//
//                        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//                        User pageUser = userService.getUser(page.getMemberSeqNo());
//
//                        if (page.getWoodongyi()) {
//                            UdongeCancelRequest udongeRequest = new UdongeCancelRequest();
//                            udongeRequest.setGubun("CP");
//                            udongeRequest.setUserid(user.getLoginId());
//                            udongeRequest.setGa_userid(pageUser.getLoginId());
//                            udongeRequest.setAmount(callback.getPrice().toString());
//                            udongeRequest.setOrd_num(callbackResult.getOrderNo());
//                            udongeRequest.setTr_num(callbackResult.getApprTranNo());
//                            udongeRequest.setTrdate(callbackResult.getApprDate() + callbackResult.getApprTime());
//                            udongeRequest.setCanceldate(dateStr);
//                            LpngPayApi.callCancelUdonge(udongeRequest);
//                        }
//                    } catch (Exception e) {
//                        logger.error(e.toString());
//                    }
//                }
//            }
//
//
//        } else {
//            logger.error("ftlinkCancel Error " + res.getErrMessage());
//        }
//    }
//
//    public Boolean ftlinkCancel(Buy buy, String message) {
//        try {
//
//            LpngCallback callback = lpngCallbackRepository.findByOrderId(buy.getOrderId());
//            String apprDate = callback.getApprDate();
//            String today = AppUtil.localTodayYYYYMMDD();
//
//            if (buy.getOrderType() != OrderType.SHIPPING_ORDER.getType() && !apprDate.equals(today)) {
//                return null;
////                throw new LpngCancelPeriodException("LpngCancel", "오늘 결제한 상품이 아닙니다. 상점주에게 직접 문의해 주세요 !!!") ;
//            }
//            kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(buy.getPageSeqNo());
//            User pageUser = userService.getUser(page.getMemberSeqNo());
//            LpngCallbackResult callbackResult = callback.getResult();
//
//            if (buy.getMemberSeqNo() != null) {
//                FTLinkCancelRequest ftLinkCancelRequest = new FTLinkCancelRequest();
//                ftLinkCancelRequest.setShopcode(callbackResult.getShopCode());
//                ftLinkCancelRequest.setLoginId(pageUser.getLoginId().replace("biz##", ""));
//                ftLinkCancelRequest.setCancelAmt(callback.getPrice().toString());
//                ftLinkCancelRequest.setOrderNo(callback.getLpngOrderNo());
//                ftLinkCancelRequest.setTranNo(callback.getPgTranId());
//                ftLinkCancelRequest.setCancelAmt(callback.getPrice().toString());
//
//                FTLinkCancelResponse res = FTLinkPayApi.cancelRequest(ftLinkCancelRequest);
//
//                if (res.getErrCode().equals("00") || res.getErrCode().equals("0000")) {
//
//                    callbackResult.setErrorCode(res.getErrCode());
//                    callbackResult.setErrorMsg(res.getErrMessage());
//                    lpngCallbackResultRepository.save(callbackResult);
//
//                    callback.setStatus(false);
//                    callback.setProcess(LpngProcess.CANCEL.getType());
//                    callback = lpngCallbackRepository.saveAndFlush(callback);
//
//                    try {
//                        if (buy.getMemberSeqNo() != null) {
//                            String dateStr = AppUtil.localDatetimeNowString();
//
//                            User user = userService.getUser(buy.getMemberSeqNo());
//
//                            if (page.getWoodongyi()) {
//                                UdongeCancelRequest udongeRequest = new UdongeCancelRequest();
//                                udongeRequest.setGubun("CP");
//                                udongeRequest.setUserid(user.getLoginId());
//                                udongeRequest.setGa_userid(pageUser.getLoginId());
//                                udongeRequest.setAmount(buy.getPrice().toString());
//                                udongeRequest.setOrd_num(callbackResult.getOrderNo());
//                                udongeRequest.setTr_num(callbackResult.getApprTranNo());
//                                udongeRequest.setTrdate(callbackResult.getApprDate() + callbackResult.getApprTime());
//                                udongeRequest.setCanceldate(dateStr);
//                                LpngPayApi.callCancelUdonge(udongeRequest);
//                            }
//                        }
//
//                    } catch (Exception e) {
//
//                    }
//
//                    return true;
//                } else {
//                    logger.error("ftlinkCancel Error " + res.getErrMessage());
//                    return false;
//                }
//            } else {
//
//                PayJoaCancelReadyRequest payJoaCancelReadyRequest = new PayJoaCancelReadyRequest();
//                payJoaCancelReadyRequest.setCPID(NOMEMBER_CPID);
//                payJoaCancelReadyRequest.setPAYMETHOD("CARD");
//                payJoaCancelReadyRequest.setCANCELREQ("Y");
//
//                PayJoaCancelReadyResponse payJoaCancelReadyResponse = readyPayJoa(payJoaCancelReadyRequest);
//
//                PayJoaCancelRequest payJoaCancelRequest = new PayJoaCancelRequest();
//                payJoaCancelRequest.setCPID(NOMEMBER_CPID);
//                payJoaCancelRequest.setAMOUNT(String.valueOf(callback.getPrice().intValue()));
//                payJoaCancelRequest.setTRXID(callback.getPgTranId());
//                payJoaCancelRequest.setCANCELREASON(message);
//                PayJoaCancelResponse payJoaCancelResponse = cancelPayJoa(payJoaCancelRequest, payJoaCancelReadyResponse);
//
//                if (payJoaCancelResponse == null) {
//                    return false;
//                }
//
//                if (payJoaCancelResponse.getRESULTCODE().equals("0000")) {
//
//                    FTLinkCancelNotiRequest ftLinkCancelNotiRequest = new FTLinkCancelNotiRequest();
//
//                    ftLinkCancelNotiRequest.setTOKEN(payJoaCancelResponse.getTOKEN());
//                    ftLinkCancelNotiRequest.setRESULTCODE(payJoaCancelResponse.getRESULTCODE());
//                    ftLinkCancelNotiRequest.setERRORMESSAGE(payJoaCancelResponse.getERRORMESSAGE());
//                    ftLinkCancelNotiRequest.setDAOUTRX(payJoaCancelResponse.getDAOUTRX());
//                    ftLinkCancelNotiRequest.setAMOUNT(payJoaCancelResponse.getAMOUNT());
//                    ftLinkCancelNotiRequest.setCANCELDATE(payJoaCancelResponse.getCANCELDATE());
//                    ftLinkCancelNotiRequest.setSHOPCODE(page.getShopCode());
//                    ftLinkCancelNotiRequest.setORDERNO(buy.getOrderId());
//                    ftLinkCancelNotiRequest.setAPPRDATE(callback.getApprDate());
//                    ftLinkCancelNotiRequest.setAPPRTIME(callback.getApprTime());
//                    ftLinkCancelNotiRequest.setAPPRTRXID(callback.getPgTranId());
//                    ftLinkCancelNotiRequest.setAPPRNO(callbackResult.getApprNo());
//                    ftLinkCancelNotiRequest.setPAYAMOUNT(String.valueOf(callback.getPrice().intValue()));
//
//                    FTLinkPayApi.cancelNotiRequest(ftLinkCancelNotiRequest);
//
//                    callbackResult.setErrorCode(payJoaCancelResponse.getRESULTCODE());
//                    callbackResult.setErrorMsg(payJoaCancelResponse.getERRORMESSAGE());
//                    lpngCallbackResultRepository.save(callbackResult);
//
//                    callback.setStatus(false);
//                    callback.setProcess(LpngProcess.CANCEL.getType());
//                    callback = lpngCallbackRepository.saveAndFlush(callback);
//                    return true;
//                } else {
//                    logger.error("payJoaCancelResponse Error " + payJoaCancelResponse.getERRORMESSAGE());
//                    return false;
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            //throw new InvalidBuyException("[POST]/buy/lpng/cancel", e) ;
//            return false;
//        }
//    }
//
//    public LpngCallback getLpngCallbackByBuySeqNo(Long buySeqNo) throws ResultCodeException {
//        LpngCallback callback = lpngCallbackRepository.findAllByBuySeqNo(buySeqNo);
//        if (callback == null) {
//            throw new InvalidBuyException();
//        }
//        return callback;
//    }
//
//    public String cancelTest(String tranId, String price, String shopCode) throws Exception {
//        PayJoaCancelReadyRequest payJoaCancelReadyRequest = new PayJoaCancelReadyRequest();
//        payJoaCancelReadyRequest.setCPID(NOMEMBER_CPID);
//        payJoaCancelReadyRequest.setPAYMETHOD("CARD");
//        payJoaCancelReadyRequest.setCANCELREQ("Y");
//
//        PayJoaCancelReadyResponse payJoaCancelReadyResponse = readyPayJoa(payJoaCancelReadyRequest);
//
//        PayJoaCancelRequest payJoaCancelRequest = new PayJoaCancelRequest();
//        payJoaCancelRequest.setCPID(NOMEMBER_CPID);
//        payJoaCancelRequest.setAMOUNT(price);
//        payJoaCancelRequest.setTRXID(tranId);
//        payJoaCancelRequest.setCANCELREASON("취소테스트");
//        PayJoaCancelResponse payJoaCancelResponse = cancelPayJoa(payJoaCancelRequest, payJoaCancelReadyResponse);
//
//        return payJoaCancelResponse.getRESULTCODE();
//    }
//
//
//    public PayJoaCancelReadyResponse readyPayJoa(PayJoaCancelReadyRequest payJoaCancelReadyRequest) throws Exception {
//        Gson gson = new Gson();
//        String readyParams = gson.toJson(payJoaCancelReadyRequest);
//
//        HttpPost post = getHttpPostPayJoa(NOMEMBER_READY_URL, readyParams);
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpResponse res = client.execute(post);
//        PayJoaCancelReadyResponse payJoaCancelReadyResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), PayJoaCancelReadyResponse.class);
//
//        return payJoaCancelReadyResponse;
//    }
//
//    public PayJoaCancelResponse cancelPayJoa(PayJoaCancelRequest payJoaCancelRequest, PayJoaCancelReadyResponse payJoaCancelReadyResponse) throws Exception {
//        Gson gson = new Gson();
//        String requestParams = gson.toJson(payJoaCancelRequest);
//        HttpPost post = getHttpPostPayJoa(payJoaCancelReadyResponse.getRETURNURL(), requestParams);
//        post.setHeader("TOKEN", payJoaCancelReadyResponse.getTOKEN());
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpResponse res = client.execute(post);
//        PayJoaCancelResponse payJoaCancelResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), PayJoaCancelResponse.class);
//        logger.debug("PayJoaCancelResponse: " + gson.toJson(payJoaCancelResponse));
//
//        return payJoaCancelResponse;
//    }
//
//    private HttpPost getHttpPostPayJoa(String url, String params) throws Exception {
//
//        HttpPost post = new HttpPost(url);
//        post.setHeader("Content-Type", "application/json;charset=EUC-KR");
//        post.setHeader("Authorization", NOMEMBER_AUTHORIZATION);
//        StringEntity entity = new StringEntity(params);
//        post.setEntity(entity);
//        return post;
//    }
//
//}
