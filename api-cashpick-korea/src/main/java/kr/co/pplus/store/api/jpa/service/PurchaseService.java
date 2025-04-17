package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.controller.BootPayApi;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Cancel;
import kr.co.pplus.store.api.jpa.model.bootpay.request.Token;
import kr.co.pplus.store.api.jpa.model.bootpay.response.BootPayCancelResponse;
import kr.co.pplus.store.api.jpa.model.bootpay.response.BootPayErrorResponse;
import kr.co.pplus.store.api.jpa.model.bootpay.response.BootPayResponse;
import kr.co.pplus.store.api.jpa.model.bootpay.response.ResToken;
import kr.co.pplus.store.api.jpa.model.ftlink.*;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayBillKeyData;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayCancelData;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.CommonService;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.MsgOnly;
import kr.co.pplus.store.type.model.NoOnlyKey;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.FTLinkPayApi;
import kr.co.pplus.store.util.SetID;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PurchaseService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";

    @Value("${ADMIN.STAGE.API}")
    String adminStageUrl = "https://stg-biz.prnumber.com";

    @Value("${ADMIN.PROD.API}")
    String adminProdUrl = "https://biz.prnumber.com";

    @Value("${STORE.DAOU.NOMEMBER.CPID}")
    String NOMEMBER_CPID = "CTS16541";

    @Value("${STORE.DAOU.NOMEMBER.AUTHORIZATION}")
    String NOMEMBER_AUTHORIZATION = "2e48f6bac5a7b243bed4b647b9387e485ffc148473124aabf6f08953ea3370ac";

    @Value("${STORE.DAOU.NOMEMBER.READY_URL}")
    String NOMEMBER_READY_URL = "https://apitest.payjoa.co.kr/pay/ready";

    @Value("${STORE.TYPE}")
    String storeType = "STAGE";

    @Value("${STORE.BOOTPAY.CASH_APP_ID}")
    String CASH_APP_ID = "";

    @Value("${STORE.BOOTPAY.CASH_PRIVATE_KEY}")
    String CASH_PRIVATE_KEY = "";

    @Value("${STORE.DANAL.CPID}")
    String CPID = "9810030929";

    private final String BASE_URL = "https://api.bootpay.co.kr/";
    private final String URL_ACCESS_TOKEN = BASE_URL + "request/token";
    private final String URL_VERIFY = BASE_URL + "receipt";
    private final String URL_CANCEL = BASE_URL + "cancel";

    Float ReapPaymentFeeRatio = 0.025f;
    Float PaymentFeeRatio = 0.031f;
    Float PlatformFeeRatio = 0.039f;
    Float TicketPlatformFeeRatio = 0.004f;


    @Autowired
    PageRepository pageRepository;

    @Autowired
    UserService userService;

    @Autowired
    LpngCallbackRepository lpngCallbackRepository;

    @Autowired
    LpngCallbackResultRepository lpngCallbackResultRepository;

    @Autowired
    MsgRepository msgRepository;

    @Autowired
    PushTargetRepository pushTargetRepository;

    @Autowired
    QueueService queueService;

    @Autowired
    ProductService productService;

    @Autowired
    DomeggookService domeggookService;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    PurchaseProductRepository purchaseProductRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PlusOnlyRepository plusOnlyRepository;

    @Autowired
    PurchaseProductOptonRepository purchaseProductOptonRepository;

    @Autowired
    PurchaseDeliveryRepository purchaseDeliveryRepository;

    @Autowired
    PurchaseProductDetailRepository purchaseProductDetailRepository;

    @Autowired
    BuyCustomerRepository buyCustomerRepository;

    @Autowired
    BolService bolService;

    @Autowired
    PointService pointService;

    @Autowired
    CashService cashService;

    @Autowired
    TBL_SUBMIT_QUEUEService tbl_submit_queueService;

    @Autowired
    CommonService commonService;

    @Autowired
    PageAdvertiseHistoryService pageAdvertiseHistoryService;

    @Autowired
    MsgProducer producer;

    @Autowired
    ReapPayService reapPayService;

    @Autowired
    MemberService memberService;

    @Autowired
    LuckyCouponService luckyCouponService;

    @Autowired
    LuckyBolService luckyBolService;

    @Autowired
    LotteryService lotteryService;

    public Integer getCountPurchaseProductByMemberSeqNo(Long memberSeqNo) {
        return purchaseProductDetailRepository.countByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqual(memberSeqNo, SalesType.SHIPPING.getType(), PurchaseProductStatus.PAY.getStatus());
    }

    public org.springframework.data.domain.Page<PurchaseProductDetail> getPurchaseProductListByMemberSeqNo(Long memberSeqNo, String purchaseType, Pageable pageable) {
        if (purchaseType.equals("luckyBol")) {
            return purchaseProductDetailRepository.findAllByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqualAndLuckyBolPurchaseSeqNoIsNotNull(memberSeqNo, SalesType.SHIPPING.getType(), PurchaseProductStatus.PAY.getStatus(), pageable);
        } else {
            return purchaseProductDetailRepository.findAllByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqualAndLuckyBolPurchaseSeqNoIsNull(memberSeqNo, SalesType.SHIPPING.getType(), PurchaseProductStatus.PAY.getStatus(), pageable);
        }

    }

    public org.springframework.data.domain.Page<PurchaseProductDetail> getPurchaseProductListTicketTypeByMemberSeqNo(Long memberSeqNo, Pageable pageable) {
        return purchaseProductDetailRepository.findAllByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqual(memberSeqNo, SalesType.TICKET.getType(), PurchaseProductStatus.PAY.getStatus(), pageable);
    }

    public org.springframework.data.domain.Page<PurchaseProductDetail> getPurchaseProductListByProductPriceCode(String productPriceCode, Pageable pageable) {
        return purchaseProductDetailRepository.findAllByProductPriceCodeAndSalesTypeAndStatusIsGreaterThanEqual(productPriceCode, SalesType.SHIPPING.getType(), PurchaseProductStatus.PAY.getStatus(), pageable);
    }

    public org.springframework.data.domain.Page<PurchaseProductDetail> getPurchaseProductListBySupplyPageSeqNo(Long supplyPageSeqNo, String status, String startDuration, String endDuration, Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        if (AppUtil.isEmpty(status)) {
            statusList.add(PurchaseProductStatus.FAIL.getStatus());
            return purchaseProductDetailRepository.findAllBySupplyPageSeqNoAndSalesTypeAndStatusNotInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(supplyPageSeqNo, 3L, statusList, startDuration, endDuration, pageable);
        } else {
            String[] statuss = status.split(",");
            for (String item : statuss) {
                statusList.add(Integer.valueOf(item));
            }

            return purchaseProductDetailRepository.findAllBySupplyPageSeqNoAndSalesTypeAndStatusInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(supplyPageSeqNo, 3L, statusList, startDuration, endDuration, pageable);
        }

    }

    public org.springframework.data.domain.Page<PurchaseProductDetail> getPurchaseProductListByPageSeqNo(Long pageSeqNo, Long salesType, String status, String startDuration, String endDuration, Pageable pageable) {

        List<Integer> statusList = new ArrayList<>();
        if (AppUtil.isEmpty(status)) {
            statusList.add(PurchaseProductStatus.FAIL.getStatus());
            return purchaseProductDetailRepository.findAllByPageSeqNoAndSalesTypeAndStatusNotInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, salesType, statusList, startDuration, endDuration, pageable);
        } else {
            String[] statuss = status.split(",");
            for (String item : statuss) {
                statusList.add(Integer.valueOf(item));
            }

            return purchaseProductDetailRepository.findAllByPageSeqNoAndSalesTypeAndStatusInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, salesType, statusList, startDuration, endDuration, pageable);
        }

    }

    public PurchaseProductDetail getPurchaseProductDetailBySeqNo(Long seqNo) {
        return purchaseProductDetailRepository.findBySeqNo(seqNo);
    }

    public Purchase getPurchaseBySeqNo(Long seqNo) {
        return purchaseRepository.findBySeqNo(seqNo);
    }

    public Float sumSalePrice(Long pageSeqNo, Long supplyPageSeqNo, String startDuration, String endDuration) {

        return purchaseProductRepository.sumSalePrice(pageSeqNo, supplyPageSeqNo, startDuration, endDuration);
    }

    public Integer saleCount(Long pageSeqNo, Long supplyPageSeqNo, String startDuration, String endDuration, String productType) {

        return purchaseProductRepository.saleCount(pageSeqNo, supplyPageSeqNo, startDuration, endDuration, productType);
    }

    public List<PurchaseProduct> getPurchaseProductList(Long purchaseSeqNo) {
        return purchaseProductRepository.findAllByPurchaseSeqNo(purchaseSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePurchaseProductExpired() {


        //        purchaseProductRepository.updatePurchaseProductExpiredTicket();

        try {
            List<PurchaseProduct> list = purchaseProductRepository.findAllPurchaseProductExpiredTicket();

            String dateStr = AppUtil.localDatetimeNowString();
            for (PurchaseProduct purchaseProduct : list) {
                purchaseProduct.setReserveStatus(ReserveStatus.EXPIRED.getStatus());
                purchaseProduct.setChangeStatusDatetime(dateStr);
                purchaseProduct = purchaseProductRepository.saveAndFlush(purchaseProduct);

                LpngCallback callback = lpngCallbackRepository.findByPurchaseSeqNo(purchaseProduct.getPurchaseSeqNo());
                FTLinkPayDecideRequest ftLinkPayDecideRequest = new FTLinkPayDecideRequest();
                if (callback.getLpngOrderNo() != null) {
                    ftLinkPayDecideRequest.setOrderno(callback.getLpngOrderNo());
                }

                Page page = pageRepository.findBySeqNo(purchaseProduct.getPageSeqNo());

                ftLinkPayDecideRequest.setShopcode(page.getShopCode());
                ftLinkPayDecideRequest.setAPPRTRXID(callback.getPgTranId());
                FTLinkPayCommonResponse res = FTLinkPayApi.payDecideRequest(ftLinkPayDecideRequest);

                if (res.getErrcode().equals("0000") || res.getErrcode().equals("00") || res.getErrcode().equals("90")) {

                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

//        String dateStr = AppUtil.localDatetimeNowString();
//        purchaseProductRepository.updatePurchaseProductExpired(ReserveStatus.BOOKING.getStatus(), dateStr);
//        List<PurchaseProduct> list = purchaseProductRepository.findAllByReserveStatusAndEndDateLessThan(ReserveStatus.BOOKING.getStatus(), dateStr);

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateTransportNumber(PurchaseDelivery purchaseDelivery) throws ResultCodeException {

        String dateStr = AppUtil.localDatetimeNowString();

        PurchaseDelivery saved = purchaseDeliveryRepository.findBySeqNo(purchaseDelivery.getSeqNo());

        saved.setTransportNumber(purchaseDelivery.getTransportNumber());
        saved.setShippingCompany(purchaseDelivery.getShippingCompany());
        saved.setShippingCompanyCode(purchaseDelivery.getShippingCompanyCode());
        saved.setDeliveryStartDatetime(dateStr);
        saved = purchaseDeliveryRepository.saveAndFlush(saved);
        purchaseProductRepository.updatePurchaseProductDeliveryStatusBySeqNo(DeliveryStatus.ING.getStatus(), saved.getPurchaseProductSeqNo(), dateStr);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateCompleteBySeqNo(Long seqNo) throws ResultCodeException {
        try {
            String dateStr = AppUtil.localDatetimeNowString();
            PurchaseProduct purchaseProduct = purchaseProductRepository.findBySeqNo(seqNo);
            requestDecide(purchaseProduct, dateStr);
        } catch (Exception e) {
            throw new InvalidBuyException("updateCompleteBySeqNo", e);
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelList(String purchaseSeqNoList, String memo, String type, Boolean dbOnly) throws ResultCodeException {
        String[] purchaseSeqNos = purchaseSeqNoList.split(",");

        for (int i = 0; i < purchaseSeqNos.length; i++) {
            cancelPurchase(Long.valueOf(purchaseSeqNos[i]), memo, type, dbOnly);
        }
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int cancelPurchase(Long purchaseSeqNo, String memo, String type, Boolean dbOnly) throws ResultCodeException {

        if (StringUtils.isEmpty(memo)) {
            memo = "결제 취소";
        }

        String dateStr = AppUtil.localDatetimeNowString();

        Purchase purchase = purchaseRepository.findBySeqNo(purchaseSeqNo);

        if (purchase == null) {
            throw new NotFoundException();
        }

        List<PurchaseProduct> purchaseProductList = getPurchaseProductList(purchaseSeqNo);
        if (!type.equals("biz") && purchase.getSalesType() == SalesType.TICKET.getType()) {
            for (PurchaseProduct purchaseProduct : purchaseProductList) {

                String endTime = purchaseProduct.getEndTime();

                if (!AppUtil.isEmpty(endTime)) {

                    int endMin = Integer.valueOf(endTime.split(":")[0]) * 60 + Integer.valueOf(endTime.split(":")[1]);
                    Calendar cal = Calendar.getInstance();
                    int currentMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

                    if (currentMin >= endMin - 60) {
                        throw new NotPossibleTimeException();
                    }
                }
            }
        }


        if (!type.equals("biz") && purchase.getSalesType().equals(SalesType.SHIPPING.getType())) {

            if (!purchase.getStatus().equals(2)) {
                throw new NotPermissionException();
            }

            for (PurchaseProduct purchaseProduct : purchaseProductList) {
                logger.error("purchaseProduct.getDeliveryStatus() : " + purchaseProduct.getDeliveryStatus());
                if (purchaseProduct.getDeliveryStatus() == null || !purchaseProduct.getDeliveryStatus().equals(0)) {
                    throw new NotPermissionException();
                }
            }
        }

        Boolean success = false;

        if (type.equals("biz") && dbOnly) {
            success = true;
        } else {
            if (purchase.getPayMethod().equals("cash")) {
                CashHistory cashHistory = new CashHistory();
                cashHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                cashHistory.setType("charge");
                cashHistory.setSecondaryType("member");
                cashHistory.setCash(purchase.getPrice());
                cashHistory.setSubject("상품 구매 취소");
                cashService.updateCash(purchase.getMemberSeqNo(), cashHistory);
                success = true;
            } else if (purchase.getPayMethod().equals("point")) {
                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                pointHistory.setType("charge");
                pointHistory.setPoint(purchase.getPrice());
                pointHistory.setSubject("상품 구매 취소");
                pointService.updatePoint(purchase.getMemberSeqNo(), pointHistory);
                success = true;
            } else if (purchase.getPayMethod().equals("bol")) {
                BolHistory bolHistory = new BolHistory();
                bolHistory.setAmount(purchase.getPrice());
                bolHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                bolHistory.setSubject("상품 구매 취소");
                bolHistory.setPrimaryType("increase");
                bolHistory.setSecondaryType("buyCancel");
                bolHistory.setTargetType("member");
                bolHistory.setTargetSeqNo(purchase.getMemberSeqNo());
                bolHistory.setHistoryProp(new HashMap<String, Object>());
                bolHistory.getHistoryProp().put("사유", memo);
                bolService.increaseBol(purchase.getMemberSeqNo(), bolHistory);
                success = true;
            } else {
                if (purchase.getPayType().equals("reappay")) {
                    long seqNo = 1L;
                    if (purchase.getPayMethod().equals("card")) {
                        seqNo = 1L;
                    } else if (purchase.getPayMethod().equals("easy")) {
                        seqNo = 2L;
                    }

                    ReapPayCancelData data = reapPayService.cancel(purchase.getPayResponseTranSeq(), seqNo);
                    if (data == null) {
                        success = false;
                    } else {
                        success = true;
                    }

                    if (success) {
                        if (purchase.getUsePoint() > 0) {
                            PointHistory pointHistory = new PointHistory();
                            pointHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                            pointHistory.setType("charge");
                            pointHistory.setPoint(purchase.getUsePoint().floatValue());
                            pointHistory.setSubject("상품 구매 취소");
                            pointService.updatePoint(purchase.getMemberSeqNo(), pointHistory);
                        }

                        if (purchase.getUseCash() > 0) {
                            CashHistory cashHistory = new CashHistory();
                            cashHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                            cashHistory.setType("charge");
                            cashHistory.setSecondaryType("member");
                            cashHistory.setCash(purchase.getUseCash().floatValue());
                            cashHistory.setSubject("상품 구매 취소");
                            cashService.updateCash(purchase.getMemberSeqNo(), cashHistory);
                        }
                    }

                } else {
                    success = ftlinkCancel(purchase, memo);
                }

            }
        }


        if (!success) {
            if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                throw new PointCancelException("point canel error", "포인트 결제 취소 중 오류가 발생하였습니다.");
            } else {
                throw new CancelFailException();
            }

        } else {

            purchase.setModDatetime(dateStr);
            purchase.setStatus(PurchaseStatus.CANCEL_COMPLETE.getStatus());
            purchase = purchaseRepository.saveAndFlush(purchase);

            if (purchase.getMemberLuckyCouponSeqNo() != null) {
                luckyCouponService.cancelCoupon(purchase.getMemberLuckyCouponSeqNo());
            }


            for (PurchaseProduct purchaseProduct : purchaseProductList) {

                purchaseProduct.setStatus(PurchaseProductStatus.CANCEL_COMPLETE.getStatus());
                if (purchaseProduct.getSalesType() == SalesType.SHIPPING.getType()) {
                    purchaseProduct.setDeliveryStatus(DeliveryStatus.CANCEL.getStatus());
                } else if (purchaseProduct.getSalesType() == SalesType.TICKET.getType()) {
                    purchaseProduct.setReserveStatus(ReserveStatus.BOOKING_CANCEL.getStatus());
                }

                purchaseProduct.setCancelDatetime(dateStr);
                purchaseProduct.setChangeStatusDatetime(dateStr);
                purchaseProduct.setCancelMemo(memo);

//                if (purchaseProduct.getDomemeOrderNo() != null) {
//
//                    try {
//                        String result = domeggookService.cancel(purchaseProduct.getDomemeOrderNo(), memo);
//                        logger.error(result);
//                    } catch (Exception e) {
//                        logger.error(e.toString());
//                    }
//
//                }

                purchaseProduct = purchaseProductRepository.saveAndFlush(purchaseProduct);

                if (purchaseProduct.getIsPaymentBol()) {
                    Integer bol = purchaseProduct.getSavedBol();
                    if (bol != null && bol > 0) {
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(bol.floatValue());
                        bolHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        bolHistory.setSubject("상품구매 취소");
                        bolHistory.setPrimaryType("decrease");
                        bolHistory.setSecondaryType("buyCancel");
                        bolHistory.setTargetType("member");
                        bolHistory.setTargetSeqNo(purchaseProduct.getMemberSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("사유", memo);
                        bolService.decreaseBol(purchaseProduct.getMemberSeqNo(), bolHistory);
                    }
                }

                if (purchaseProduct.getIsPaymentPoint()) {
                    Float point = purchaseProduct.getSavedPoint();
                    if (point != null && point > 0) {

                        if (purchaseProduct.getMemberSeqNo() != null) {

                            Member member = memberRepository.findBySeqNo(purchaseProduct.getMemberSeqNo());

                            if (member.getAppType().equals("luckyball")) {


                                PointHistory pointHistory = new PointHistory();
                                pointHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                                pointHistory.setType("used");
                                pointHistory.setPoint(point);
                                pointHistory.setSubject("상품 구매 취소");
                                pointHistory.setHistoryProp(new HashMap<>());
                                pointHistory.getHistoryProp().put("사유", memo);
                                pointService.updatePoint(purchase.getMemberSeqNo(), pointHistory);

                            } else {
                                BolHistory bolHistory = new BolHistory();
                                bolHistory.setAmount(point);
                                bolHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                                bolHistory.setSubject("상품구매 취소");
                                bolHistory.setPrimaryType("decrease");
                                bolHistory.setSecondaryType("buyCancel");
                                bolHistory.setTargetType("member");
                                bolHistory.setTargetSeqNo(purchaseProduct.getMemberSeqNo());
                                bolHistory.setHistoryProp(new HashMap<String, Object>());
                                bolHistory.getHistoryProp().put("사유", memo);
                                bolService.decreaseBol(purchaseProduct.getMemberSeqNo(), bolHistory);
                            }

                        }
                    }
                }

//                        goodsService.updateGoodsMinusSoldCount(buyGoods.getGoodsSeqNo(), buyGoods.getCount());

                Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
                product.setSoldCount(product.getSoldCount() - purchaseProduct.getCount());
                if (product.getStatus().equals(GoodsStatus.SOLD_OUT.getStatus()) && product.getSoldCount() < product.getCount()) {
                    product.setStatus(GoodsStatus.SELL.getStatus());
                    productService.updateProductPriceStatusByProductSeqNoAndSoldOut(product.getSeqNo(), product.getStatus());
                }

                productService.save(product);

                List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                if (purchaseProductOptionList != null) {
                    for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {
                        productService.updateProductOptionDetailMinusSoldCount(purchaseProductOption.getProductOptionDetailSeqNo(), purchaseProductOption.getAmount());
                    }
                }

                if (purchaseProduct.getSalesType() == SalesType.SHIPPING.getType()) {

                    Page page = pageRepository.findBySeqNo(purchaseProduct.getPageSeqNo());
                    Member pageMember = memberRepository.findBySeqNo(page.getMemberSeqNo());
                    MsgJpa msgJpa = new MsgJpa();
                    msgJpa.setSeqNo(null);
                    msgJpa.setIncludeMe(false);
                    msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
                    msgJpa.setStatus(Const.MSG_STATUS_READY);
                    msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
                    msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);
                    msgJpa.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                    msgJpa.setSubject("상품결제가 취소되었습니다.");
                    msgJpa.setContents(purchase.getTitle());
                    msgJpa.setMoveSeqNo(purchaseProduct.getSeqNo());
                    msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
                    msgJpa.setReserved(false);
                    msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
                    msgJpa.setStatus("ready");
                    msgJpa.setTotalPrice(0L);
                    msgJpa.setRefundPrice(0L);
                    msgJpa.setTargetCount(1);
                    msgJpa.setSuccCount(0);
                    msgJpa.setFailCount(0);
                    msgJpa.setReadCount(0);
                    msgJpa.setRegDatetime(dateStr);
                    msgJpa = msgRepository.saveAndFlush(msgJpa);

                    PushTargetJpa pushTargetJpa = new PushTargetJpa();
                    pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
                    pushTargetJpa.setMemberSeqNo(pageMember.getSeqNo());
                    pushTargetRepository.saveAndFlush(pushTargetJpa);

                    MsgOnly msg = new MsgOnly();
                    msg.setNo(msgJpa.getSeqNo());
                    msg.setIncludeMe(false);
                    msg.setInput(Const.MSG_INPUT_SYSTEM);
                    msg.setStatus(Const.MSG_STATUS_READY);
                    msg.setType(Const.MSG_TYPE_PUSH);
                    msg.setMoveType1(Const.MOVE_TYPE_INNER);
                    msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
                    msg.setAppType(Const.APP_TYPE_BIZ);
                    msg.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                    msg.setSubject("상품결제가 취소되었습니다.");
                    msg.setContents(purchase.getTitle());
                    msg.setMoveTarget(new NoOnlyKey(purchaseProduct.getSeqNo()));
                    queueService.sendOnlyPush(msg);
                }

                if (purchaseProduct.getMemberSeqNo() == null) {

                    try {
                        String contents = memo + "\n위의 사유로 취소되었습니다.";

                        String buyerTel = purchase.getBuyerTel();
                        tbl_submit_queueService.insert(tbl_submit_queueService.generateTBL_SUBMIT_QUEUE("PRNUMBER"
                                , "02-6315-1234"
                                , buyerTel
                                , null, contents
                                , null, null, null, null, null, null, null, null));
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }

                }

            }

        }

        return Const.E_SUCCESS;
    }

    public List<PurchaseProduct> getShippingList() {
        return purchaseProductRepository.findAllByStatusAndDeliveryStatus(PurchaseProductStatus.PAY.getStatus(), DeliveryStatus.ING.getStatus());
    }

    public PurchaseDelivery getPurchaseDeliveryByPurchaseProductSeqNo(Long purchaseProductSeqNo) {
        return purchaseDeliveryRepository.findByPurchaseProductSeqNo(purchaseProductSeqNo);
    }

    public PurchaseDelivery savePurchaseDelivery(PurchaseDelivery purchaseDelivery) {
        return purchaseDeliveryRepository.saveAndFlush(purchaseDelivery);
    }

    public PurchaseProduct savePurchaseProduct(PurchaseProduct purchaseProduct) {
        return purchaseProductRepository.saveAndFlush(purchaseProduct);
    }


    public String getRequest(String urlStr, Map<String, String> params, String charset, int connectionTimeout, int readTimeout) throws Exception {
        OutputStream os = null;
        HttpURLConnection conn = null;
        URL url = null;
        PrintWriter writer = null;

        if (params != null) {
            StringBuffer buf = new StringBuffer();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
                    first = false;
                } else {
                    buf.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
                }
            }

            if (!first) {
                urlStr += "?" + buf.toString();
            }
        }

        logger.debug("url : " + urlStr);

        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setUseCaches(false);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
        StringBuffer sb = new StringBuffer();
        int read = 0;
        char[] buf = new char[1024];
        while ((read = br.read(buf)) > 0) {
            sb.append(buf, 0, read);
        }
        br.close();

        return sb.toString();
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void setCompletePurchaseProductList() throws ResultCodeException {

        try {
            String dateStr = AppUtil.localDatetimeNowString();
            List<PurchaseProduct> purchaseProductList = purchaseProductRepository.findAllNeedCompleteList();
            for (PurchaseProduct purchaseProduct : purchaseProductList) {
                requestDecide(purchaseProduct, dateStr);
            }

        } catch (Exception e) {
            throw new InvalidBuyException("CompleteBuyGoodsList", e);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void expiredTicketProductGivePoint() throws Exception {

        String today = AppUtil.localDatetimeTodayString() + " 00:00:00";

        List<PurchaseProduct> purchaseProductList = purchaseProductRepository.findAllByReserveStatusAndSalesTypeAndIsPaymentPointAndRegDatetimeLessThan(ReserveStatus.EXPIRED.getStatus(), SalesType.TICKET.getType(), false, today);

        for (PurchaseProduct purchaseProduct : purchaseProductList) {
            if (purchaseProduct.getMemberSeqNo() != null) {
                Float point = purchaseProduct.getSavedPoint();
                if (point != null && point > 0 && !purchaseProduct.getIsPaymentPoint()) {

                    Member member = memberRepository.findBySeqNo(purchaseProduct.getMemberSeqNo());

                    if (member.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {

                        PointHistory pointHistory = new PointHistory();
                        pointHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        pointHistory.setType("charge");
                        pointHistory.setPoint(point);

                        pointHistory.setSubject("상품구매 적립");
                        pointHistory.setHistoryProp(new HashMap<>());
                        pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        pointHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");
                        pointService.updatePoint(purchaseProduct.getMemberSeqNo(), pointHistory);

                    }else if (member.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                        PointHistory pointHistory = new PointHistory();
                        pointHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        pointHistory.setType("charge");
                        pointHistory.setPoint(point);

                        pointHistory.setSubject("상품구매 적립");
                        pointHistory.setHistoryProp(new HashMap<>());
                        pointHistory.getHistoryProp().put("지급처", "럭키픽 운영팀");
                        pointHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");
                        pointService.updatePoint(purchaseProduct.getMemberSeqNo(), pointHistory);
                    } else {
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(point);
                        bolHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        bolHistory.setSubject("상품구매 적립");
                        bolHistory.setPrimaryType("increase");
                        bolHistory.setSecondaryType("buy");
                        bolHistory.setTargetType("member");
                        bolHistory.setTargetSeqNo(purchaseProduct.getMemberSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");
                        bolHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");

                        bolService.increaseBol(purchaseProduct.getMemberSeqNo(), bolHistory);
                    }

                    purchaseProductRepository.updatePaymentPointBySeqNo(purchaseProduct.getSeqNo(), 1);


                    PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
                    pageAdvertiseHistory.setPageSeqNo(purchaseProduct.getPageSeqNo());
                    pageAdvertiseHistory.setType("ticket");
                    pageAdvertiseHistory.setRegDatetime(AppUtil.localDatetimeNowString());

                    Page page = pageRepository.findBySeqNo(purchaseProduct.getPageSeqNo());
                    if (page.getPreDiscountFee() != null) {
                        pageAdvertiseHistory.setPrice(page.getPreDiscountFee());
                    } else {
                        pageAdvertiseHistory.setPrice(Const.ADS_COST);
                    }

                    pageAdvertiseHistory.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                    pageAdvertiseHistory.setPurchaseSeqNo(purchaseProduct.getPurchaseSeqNo());
                    pageAdvertiseHistoryService.save(pageAdvertiseHistory, purchaseProduct.getMemberSeqNo());

                }
            }

        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void requestDecide(PurchaseProduct purchaseProduct, String dateStr) throws Exception {

//        BuffMsg buffMsg = null;

        kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(purchaseProduct.getPageSeqNo());

        Purchase purchase = purchaseRepository.findBySeqNo(purchaseProduct.getPurchaseSeqNo());

        if (purchase.getSalesType() == SalesType.TICKET.getType()) {

            String startTime = purchaseProduct.getStartTime();
            String endTime = purchaseProduct.getEndTime();

            if (!AppUtil.isEmpty(startTime) && !AppUtil.isEmpty(endTime)) {
                Integer startMin = Integer.valueOf(startTime.split(":")[0]) * 60 + Integer.valueOf(startTime.split(":")[1]);
                Integer endMin = Integer.valueOf(endTime.split(":")[0]) * 60 + Integer.valueOf(endTime.split(":")[1]);
                Calendar cal = Calendar.getInstance();
                Integer currentMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
                if (currentMin < startMin || currentMin > endMin) {
                    throw new NotPossibleTimeException();
                }
            }
        }

        boolean isComplete = false;
        if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
            isComplete = true;
        } else {

            if (purchase.getPayType().equals("reappay")) {
                isComplete = true;
            } else {
                LpngCallback callback = lpngCallbackRepository.findByPurchaseSeqNo(purchaseProduct.getPurchaseSeqNo());
                FTLinkPayDecideRequest ftLinkPayDecideRequest = new FTLinkPayDecideRequest();
                if (callback.getLpngOrderNo() != null) {
                    ftLinkPayDecideRequest.setOrderno(callback.getLpngOrderNo());
                }

                ftLinkPayDecideRequest.setShopcode(page.getShopCode());
                ftLinkPayDecideRequest.setAPPRTRXID(callback.getPgTranId());
                FTLinkPayCommonResponse res = FTLinkPayApi.payDecideRequest(ftLinkPayDecideRequest);

                if (res.getErrcode().equals("0000") || res.getErrcode().equals("00") || res.getErrcode().equals("90")) {
                    isComplete = true;
                }
            }

        }


        if (isComplete) {

            try {
                Lottery lottery = lotteryService.getLottery();
                if(lottery != null){
                    lotteryService.joinLottery(purchase.getMemberSeqNo(), lottery.getSeqNo(), 500, "shopping");
                }
            }catch (Exception e){
                logger.error(e.toString());
            }



            if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                purchaseProductRepository.updatePurchaseProductDeliveryStatusCompleteBySeqNo(PurchaseProductStatus.COMPLETE.getStatus(), DeliveryStatus.COMPLETE.getStatus(), purchaseProduct.getSeqNo(), dateStr);
            } else if (purchase.getSalesType() == SalesType.TICKET.getType()) {
                purchaseProductRepository.updatePurchaseProductReserveStatusCompleteBySeqNo(PurchaseProductStatus.COMPLETE.getStatus(), ReserveStatus.COMPLETE.getStatus(), purchaseProduct.getSeqNo(), dateStr);
            }

            purchaseRepository.updatePurchaseCompleteBySeqNo(PurchaseStatus.COMPLETE.getStatus(), purchaseProduct.getPurchaseSeqNo(), dateStr);
            if (purchaseProduct.getMemberSeqNo() != null) {

                Member member = memberRepository.findBySeqNo(purchaseProduct.getMemberSeqNo());

                if (purchaseProduct.getSavedBol() > 0 && (purchaseProduct.getIsPaymentBol() == null || !purchaseProduct.getIsPaymentBol())) {
                    BolHistory bolHistory = new BolHistory();
                    bolHistory.setAmount(purchaseProduct.getSavedBol().floatValue());
                    bolHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                    bolHistory.setSubject("상품구매 적립");
                    bolHistory.setPrimaryType("increase");
                    bolHistory.setSecondaryType("buy");
                    bolHistory.setTargetType("member");
                    bolHistory.setTargetSeqNo(purchaseProduct.getMemberSeqNo());
                    bolHistory.setHistoryProp(new HashMap<String, Object>());
                    if (member.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                        bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                    }else if (member.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                        bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                    }

                    bolHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");

                    bolService.increaseBol(purchaseProduct.getMemberSeqNo(), bolHistory);

                    purchaseProductRepository.updatePaymentBolBySeqNo(purchaseProduct.getSeqNo(), 1);
                }

                Float point = purchaseProduct.getSavedPoint();
                if (point != null && point > 0 && !purchaseProduct.getIsPaymentPoint()) {

                    if (member.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {

                        PointHistory pointHistory = new PointHistory();
                        pointHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        pointHistory.setType("charge");
                        pointHistory.setPoint(point);
                        pointHistory.setSubject("상품구매 적립");
                        pointHistory.setHistoryProp(new HashMap<>());
                        pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                        pointHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");
                        pointService.updatePoint(purchaseProduct.getMemberSeqNo(), pointHistory);

                        try {
                            Member recommenderMember = memberRepository.findByRecommendUniqueKey(member.getRecommendationCode());
                            if (recommenderMember != null) {

                                Float amount = purchaseProduct.getProductPrice() * 0.005f;

                                pointHistory = new PointHistory();
                                pointHistory.setMemberSeqNo(recommenderMember.getSeqNo());
                                pointHistory.setType("charge");
                                pointHistory.setPoint(amount);
                                pointHistory.setSubject("레퍼럴 적립");
                                pointHistory.setHistoryProp(new HashMap<>());
                                pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                                pointHistory.getHistoryProp().put("적립유형", "레퍼럴 적립");
                                pointService.updatePoint(recommenderMember.getSeqNo(), pointHistory);
                            }
                        } catch (Exception e) {
                            logger.error("추천인 에러 : " + e.toString());
                        }

                    }else if (member.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                        PointHistory pointHistory = new PointHistory();
                        pointHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        pointHistory.setType("charge");
                        pointHistory.setPoint(point);
                        pointHistory.setSubject("상품구매 적립");
                        pointHistory.setHistoryProp(new HashMap<>());
                        pointHistory.getHistoryProp().put("지급처", "럭키픽 운영팀");
                        pointHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");
                        pointService.updatePoint(purchaseProduct.getMemberSeqNo(), pointHistory);
                    } else {
                        BolHistory bolHistory = new BolHistory();
                        bolHistory.setAmount(point);
                        bolHistory.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                        bolHistory.setSubject("상품구매 적립");
                        bolHistory.setPrimaryType("increase");
                        bolHistory.setSecondaryType("buy");
                        bolHistory.setTargetType("member");
                        bolHistory.setTargetSeqNo(purchaseProduct.getMemberSeqNo());
                        bolHistory.setHistoryProp(new HashMap<String, Object>());
                        bolHistory.getHistoryProp().put("지급처", "오리마켓 운영팀");
                        bolHistory.getHistoryProp().put("적립유형", "구매확정(" + purchaseProduct.getTitle() + ")");

                        bolService.increaseBol(purchaseProduct.getMemberSeqNo(), bolHistory);
                    }

                    purchaseProductRepository.updatePaymentPointBySeqNo(purchaseProduct.getSeqNo(), 1);

                    PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
                    pageAdvertiseHistory.setPageSeqNo(purchaseProduct.getPageSeqNo());
                    pageAdvertiseHistory.setType("ticket");
                    pageAdvertiseHistory.setRegDatetime(dateStr);

                    if (page.getPreDiscountFee() != null) {
                        pageAdvertiseHistory.setPrice(page.getPreDiscountFee());
                    } else {
                        pageAdvertiseHistory.setPrice(Const.ADS_COST);
                    }
                    pageAdvertiseHistory.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                    pageAdvertiseHistory.setPurchaseSeqNo(purchaseProduct.getPurchaseSeqNo());
                    pageAdvertiseHistoryService.save(pageAdvertiseHistory, purchaseProduct.getMemberSeqNo());

                }

                BuyCustomer buyCustomer = buyCustomerRepository.findByMemberSeqNoAndPageSeqNo(purchaseProduct.getMemberSeqNo(), purchaseProduct.getPageSeqNo());
                if (buyCustomer != null) {
                    buyCustomer.setBuyCount(buyCustomer.getBuyCount());
                    buyCustomer.setLastBuyDatetime(dateStr);
                } else {
                    buyCustomer = new BuyCustomer();
                    buyCustomer.setMemberSeqNo(purchaseProduct.getMemberSeqNo());
                    buyCustomer.setPageSeqNo(purchaseProduct.getPageSeqNo());
                    buyCustomer.setBuyCount(1);
                    buyCustomer.setLastBuyDatetime(dateStr);
                }
                buyCustomerRepository.save(buyCustomer);

                try {
                    PlusOnly plusOnly = plusOnlyRepository.findByMemberSeqNoAndPageSeqNo(purchaseProduct.getMemberSeqNo(), purchase.getPageSeqNo());
                    if (plusOnly != null) {
                        plusOnly.setBuyCount(plusOnly.getBuyCount() + 1);
                        plusOnly.setLastBuyDatetime(dateStr);
                        plusOnlyRepository.save(plusOnly);
                    }
                } catch (Exception e) {
                    logger.error("plus error : " + e.toString());
                }

            }

//            BuffMemberWithMember buffMember = buffService.getBuffMember(purchaseProduct.getMemberSeqNo());
//
//            int memberCount = 0;
//
//            if (buffMember != null) {
//                memberCount = buffService.getBuffMemberCount(buffMember.getBuffSeqNo());
//
//                if (memberCount > 1) {
//                    buffMsg = new BuffMsg();
//                    buffMsg.setBuffSeqNo(buffMember.getBuffSeqNo());
//                    buffMsg.setDividerSeqNo(purchaseProduct.getMemberSeqNo());
//                    buffMsg.setAmount(purchaseProduct.getProductPrice() * 0.02f);
//                    buffMsg.setMoneyType("point");
//                    buffMsg.setType("shopping");
//                    buffMsg.setTitle(purchaseProduct.getTitle());
//
//                    ProductPriceOnly productPrice = productService.getProductPriceOnlyByCode(purchaseProduct.getProductPriceCode());
//                    buffMsg.setShoppingSeqNo(productPrice.getSeqNo());
//
//                    List<ProductImage> imageList = productService.getProductImageList(productPrice.getProductSeqNo(), true);
//                    buffMsg.setImage(imageList.get(0).getImage());
//
//                    logger.error("buffMsg : " + buffMember.getBuffSeqNo());
//                }
//            }

        }

//        if (buffMsg != null) {
//            logger.error("buffMsg : push");
//            producer.push(buffMsg);
//
//        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Purchase purchaseTicket(User user, Purchase purchase) throws ResultCodeException {
        try {

            String dateStr = AppUtil.localDatetimeNowString();
            purchase.setSeqNo(null);
            if (!purchase.isValidOrderId(REDIS_PREFIX)) {
                throw new Exception("[POST]purchaseTicket : The orderId is not valid ");
            }

            if (purchase.getPayMethod().equals("card")) {
                if (purchase.getPg() == null)
                    purchase.setPg("FTLINK");
            }

            purchase.setRegDatetime(dateStr);
            purchase.setModDatetime(dateStr);
            purchase.setMemberSeqNo(user.getNo());
            purchase.setLoginId(user.getLoginId());
            purchase.setNonMember(false);
            purchase.setSalesType(SalesType.TICKET.getType());

            Product product1 = null;
            Member member = memberRepository.findBySeqNo(user.getNo());
            String code = null;
            if (purchase.getPurchaseProductSelectList() != null) {
                List<Product> productList = new ArrayList<>();
                List<PurchaseProduct> purchaseProductList = new ArrayList<>();
                Float totalPrice = 0.0f;
                Float totalProductPrice = 0.0f;
                Float totalOptionPrice = 0.0f;
                Float totalReturnPaymentPrice = 0.0f;

                Page page = null;
                for (int i = 0; i < purchase.getPurchaseProductSelectList().size(); i++) {
                    PurchaseProduct purchaseProductSelect = purchase.getPurchaseProductSelectList().get(i);
                    List<PurchaseProductOption> purchaseProductOptionList = purchaseProductSelect.getPurchaseProductOptionSelectList();

                    PurchaseProduct purchaseProduct = new PurchaseProduct();
                    purchaseProduct.setProductSeqNo(purchaseProductSelect.getProductSeqNo());
                    purchaseProduct.setCount(purchaseProductSelect.getCount());
                    purchaseProduct.setProductPriceCode(purchaseProductSelect.getProductPriceCode());

                    Integer count = purchaseProduct.getCount();
                    Product product = productService.getProduct(purchaseProduct.getProductSeqNo());

                    purchaseProduct.setSubTitle(product.getSubName());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    if (product.getEndDate() != null) {
                        Date date = dateFormat.parse(dateStr);
                        Date endDate = product.getEndDate();
                        if (date.getTime() >= endDate.getTime()) {
                            throw new ExpiredException();
                        }
                    }


                    ProductPrice productPrice = productService.getProductPriceByCode(purchaseProduct.getProductPriceCode());

                    String startTime = productPrice.getStartTime();
                    String endTime = productPrice.getEndTime();

                    if (!AppUtil.isEmpty(startTime) && !AppUtil.isEmpty(endTime)) {
                        Integer startMin = Integer.valueOf(startTime.split(":")[0]) * 60 + Integer.valueOf(startTime.split(":")[1]);
                        Integer endMin = Integer.valueOf(endTime.split(":")[0]) * 60 + Integer.valueOf(endTime.split(":")[1]);
                        Calendar cal = Calendar.getInstance();
                        Integer currentMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

                        Integer purchaseWait = commonService.getPurchaseWait();
                        if (currentMin >= startMin - purchaseWait) {
                            throw new NotPossibleTimeException();
                        }
                    }

                    if (productPrice.getDailyCount() < (productPrice.getDailySoldCount() + count)) {
                        throw new InvalidBuyException("purchaseTicket[POST]", "purchaseTicket data count is not enough : goods.count,soldCount,buyCount : " + product.getCount() + "," + product.getSoldCount() + "," + count);
                    }

                    if (i == 0) {
                        page = pageRepository.findBySeqNo(productPrice.getPageSeqNo());
                    }

                    productList.add(product);
                    if (product.getCount() >= 0 && (!product.getStatus().equals(GoodsStatus.SELL.getStatus()) || product.getCount() < product.getSoldCount() + count)) {
                        throw new InvalidBuyException("purchaseTicket[POST]", "purchaseTicket data count is not enough : goods.count,soldCount,buyCount : " + product.getCount() + "," + product.getSoldCount() + "," + count);
                    }

                    purchaseProduct.setRegDatetime(dateStr);
                    purchaseProduct.setPayDatetime(dateStr);
                    purchaseProduct.setMemberSeqNo(purchase.getMemberSeqNo());
                    purchaseProduct.setTicketProductType(productPrice.getProductType());
                    purchaseProduct.setStartTime(productPrice.getStartTime());
                    purchaseProduct.setEndTime(productPrice.getEndTime());

                    purchaseProduct.setProductSeqNo(product.getSeqNo());
                    purchaseProduct.setTitle(product.getName());
                    purchaseProduct.setSalesType(product.getSalesType());
                    if (purchase.getPayMethod().equals("point")) {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY.getStatus()); //결제
                        purchaseProduct.setReserveStatus(ReserveStatus.BOOKING.getStatus());
                    } else {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제 대기
                    }

                    purchaseProduct.setAgentSeqNo(page.getAgentSeqNo());
                    int optionPrice = 0;
                    if (purchaseProductOptionList != null) {
                        for (int j = 0; j < purchaseProductOptionList.size(); j++) {
                            PurchaseProductOption purchaseProductOption = purchaseProductOptionList.get(j);
                            optionPrice += (purchaseProductOption.getPrice() * purchaseProductOption.getAmount());
                        }
                    }
                    purchaseProduct.setOptionPrice(optionPrice);

                    if (productPrice != null) {
                        if (productPrice.getSupplyPrice() != null) {
                            purchaseProduct.setSupplyPrice(productPrice.getSupplyPrice() * count);
                        }

                        purchaseProduct.setPageSeqNo(productPrice.getPageSeqNo());
                        purchaseProduct.setSupplyPageSeqNo(product.getPageSeqNo());
                        purchaseProduct.setPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        purchaseProduct.setProductPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        purchaseProduct.setUnitPrice(purchaseProduct.getPrice());
                        purchaseProduct.setEndDate(product.getEndDate());

                        // 수수료 계산
                        // 결제수수료 = 결제금액 * 결제수수료비율
                        // 플랫폼수수료 = 결제금액 * 플랫폼수수료비율

                        Float paymentFee = purchaseProduct.getPrice() * PaymentFeeRatio;
                        Float platformFee = purchaseProduct.getPrice() * TicketPlatformFeeRatio;

                        purchaseProduct.setPaymentFee(paymentFee);
                        purchaseProduct.setPlatformFee(platformFee);
                        purchaseProduct.setReturnPaymentPrice(platformFee);


                        if (!(purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol"))) {

                            if (productPrice.getIsPoint() != null && productPrice.getIsPoint()) {
                                Float savedPoint = productPrice.getPoint() * count;
                                purchaseProduct.setSavedPoint(savedPoint);
                            } else {
                                purchaseProduct.setSavedPoint(0f);
                            }

                        } else {
                            purchaseProduct.setSavedPoint(0f);
                        }

                        purchaseProduct.setReturnPaymentPrice(purchaseProduct.getReturnPaymentPrice() + purchaseProduct.getSavedPoint());

                        String str = productPrice.getMarketType() == 3 ? "BB" : "RB";
                        code = SetID.getID(str);
                    }

                    purchaseProduct.setPurchaseProductOptionSelectList(purchaseProductOptionList);
                    purchaseProductList.add(purchaseProduct);
                    totalPrice += purchaseProduct.getPrice();
                    totalProductPrice += purchaseProduct.getProductPrice();
                    totalOptionPrice += purchaseProduct.getOptionPrice();
                    totalReturnPaymentPrice += purchaseProduct.getReturnPaymentPrice();
                    if (i == 0) {
                        product1 = product;
                    }
                }

                purchase.setReturnPaymentPrice(totalReturnPaymentPrice);
                purchase.setPageSeqNo(purchaseProductList.get(0).getPageSeqNo());
                purchase.setPrice(totalPrice);
                purchase.setPgPrice(totalPrice.intValue());
                purchase.setUsePoint(0);
                purchase.setUseCash(0);
                purchase.setProductPrice(totalProductPrice);
                purchase.setOptionPrice(totalOptionPrice);
                if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                    purchase.setStatus(PurchaseStatus.PAY_REQ.getStatus());
                } else {
                    purchase.setStatus(PurchaseStatus.PAY_REQ.getStatus());
                }

                purchase.setAgentSeqNo(page.getAgentSeqNo());

                if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {

                    if (purchase.getPayMethod().equals("point")) {
                        if (purchase.getPrice() > member.getPoint()) {
                            throw new LackCostException("point lack");
                        }
                    } else {
                        if (purchase.getPrice() > member.getBol()) {
                            throw new LackCostException("bol lack");
                        }
                    }

                }

                purchase.setCode(code);
                purchase = purchaseRepository.saveAndFlush(purchase);
                if (purchase == null || purchase.getSeqNo() == null) {
                    throw new Exception("purchase data insert error !!!");
                }

                int i = 0;
                List<PurchaseProductOption> purchaseProductOptionList = null;
                for (PurchaseProduct purchaseProduct : purchaseProductList) {
                    purchaseProductOptionList = purchaseProduct.getPurchaseProductOptionSelectList();

                    purchaseProduct.setSeqNo(null);
                    purchaseProduct.setPurchaseSeqNo(purchase.getSeqNo());
                    purchaseProduct.setCode(code + String.format("%04d", i + 1));
                    purchaseProduct.setIsStatusCompleted(false);
                    purchaseProduct.setIsPaymentPoint(false);

                    if (!AppUtil.isEmpty(member.getRecommendationCode())) {
                        Member recommenderMember = memberRepository.findByRecommendUniqueKey(member.getRecommendationCode());
                        if (recommenderMember != null) {
                            purchaseProduct.setRecommendedMemberSeqNo(recommenderMember.getSeqNo());
                            if (recommenderMember.getAppType().equals("biz")) {
                                purchaseProduct.setRecommendedMemberType("page");
                            } else {
                                purchaseProduct.setRecommendedMemberType("user");
                            }
                        }
                    }

                    try {
                        purchaseProduct = purchaseProductRepository.saveAndFlush(purchaseProduct);
                    } catch (Exception e) {
                        throw new InvalidBuyException("purchaseTicket[POST]", "The purchaseProduct save error : " + e.getMessage() + ":" + purchaseProduct.toString());
                    }
                    if (purchaseProduct == null || purchaseProduct.getSeqNo() == null) {
                        throw new InvalidBuyException("purchaseTicket[POST]", "The purchaseProduct insert error : " + purchaseProduct.toString());
                    }

                    if (purchaseProductOptionList != null) {
                        for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {
                            purchaseProductOption.setSeqNo(null);
                            purchaseProductOption.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                            purchaseProductOption.setPurchaseSeqNo(purchase.getSeqNo());

                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            if (productOptionDetail.getItem1() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem1().getOptionSeqNo());
                                purchaseProductOption.setDepth1(productOption.getName() + " : " + productOptionDetail.getItem1().getItem());
                            }

                            if (productOptionDetail.getItem2() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem2().getOptionSeqNo());
                                purchaseProductOption.setDepth2(productOption.getName() + " : " + productOptionDetail.getItem2().getItem());
                            }

                            purchaseProductOption = purchaseProductOptonRepository.saveAndFlush(purchaseProductOption);
                        }
                    }

                    purchaseProductList.set(i, purchaseProduct);
                    i++;
                }
            }

            if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                purchasePoint(purchase, member, user, dateStr);
            }


        } catch (InvalidBuyException e) {
            logger.error(AppUtil.excetionToString(e));
            throw e;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new SqlException("[POST]/purchase/ticket", e);
        }
        return purchase;

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Purchase purchaseShip(User user, Purchase purchase) throws ResultCodeException {
        try {
            String dateStr = AppUtil.localDatetimeNowString();
            purchase.setSeqNo(null);
            if (!purchase.isValidOrderId(REDIS_PREFIX)) {
                throw new Exception("[POST]purchaseShip : The orderId is not valid ");
            }

            if (purchase.getPayMethod().equals("card")) {
                if (purchase.getPg() == null)
                    purchase.setPg("FTLINK");
            }

            purchase.setRegDatetime(dateStr);
            purchase.setModDatetime(dateStr);
            purchase.setMemberSeqNo(user.getNo());
            purchase.setLoginId(user.getLoginId());
            purchase.setNonMember(false);
            purchase.setSalesType(SalesType.SHIPPING.getType());

            Product product1 = null;
            Member member = memberRepository.findBySeqNo(user.getNo());
            String code = null;
            if (purchase.getPurchaseProductSelectList() != null) {
                List<Product> productList = new ArrayList<>();
                List<PurchaseProduct> purchaseProductList = new ArrayList<>();

                Float totalPrice = 0.0f;
                Float totalProductPrice = 0.0f;
                Float totalOptionPrice = 0.0f;
                Float totalReturnPaymentPrice = 0.0f;
                Float totalDeliveryFee = 0f;
                Float totalDeliveryAddFee = 0f;
                Page page = null;
                for (int i = 0; i < purchase.getPurchaseProductSelectList().size(); i++) {


                    PurchaseProduct purchaseProductSelect = purchase.getPurchaseProductSelectList().get(i);

                    PurchaseDelivery purchaseDelivery = purchaseProductSelect.getPurchaseDeliverySelect();
                    if (purchaseDelivery == null) {
                        throw new Exception("[POST]purchaseShip : purchaseDelivery is null ");
                    }
                    List<PurchaseProductOption> purchaseProductOptionList = purchaseProductSelect.getPurchaseProductOptionSelectList();

                    PurchaseProduct purchaseProduct = new PurchaseProduct();
                    purchaseProduct.setProductSeqNo(purchaseProductSelect.getProductSeqNo());
                    purchaseProduct.setCount(purchaseProductSelect.getCount());
                    purchaseProduct.setProductPriceCode(purchaseProductSelect.getProductPriceCode());


                    Integer count = purchaseProduct.getCount();
                    Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
                    purchaseProduct.setSubTitle(product.getSubName());

                    ProductPrice productPrice = productService.getProductPriceByCode(purchaseProduct.getProductPriceCode());

                    if (i == 0) {
                        page = pageRepository.findBySeqNo(productPrice.getPageSeqNo());
                    }

                    productList.add(product);
                    if (product.getCount() >= 0 && (!product.getStatus().equals(GoodsStatus.SELL.getStatus()) || product.getCount() < product.getSoldCount() + count)) {
                        throw new InvalidBuyException("purchaseShip[POST]", "puchaseShip data count is not enough : goods.count,soldCount,buyCount : " + product.getCount() + "," + product.getSoldCount() + "," + count);
                    }

                    purchaseProduct.setRegDatetime(dateStr);
                    purchaseProduct.setPayDatetime(dateStr);
                    purchaseProduct.setMemberSeqNo(purchase.getMemberSeqNo());

                    purchaseProduct.setProductSeqNo(product.getSeqNo());
                    purchaseProduct.setTitle(product.getName());
                    purchaseProduct.setSalesType(product.getSalesType());
                    if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제
                        purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus());
                    } else {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제 대기
                    }

                    purchaseProduct.setAgentSeqNo(page.getAgentSeqNo());
                    int optionPrice = 0;
                    if (purchaseProductOptionList != null) {
                        for (int j = 0; j < purchaseProductOptionList.size(); j++) {
                            PurchaseProductOption purchaseProductOption = purchaseProductOptionList.get(j);
                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            purchaseProductOption.setPrice(productOptionDetail.getPrice());
                            optionPrice += (purchaseProductOption.getPrice() * purchaseProductOption.getAmount());
                        }
                    }
                    purchaseProduct.setOptionPrice(optionPrice);

                    if (purchaseDelivery.getDeliveryFee() == null) {
                        purchaseDelivery.setDeliveryFee(0f);
                    }

                    if (purchaseDelivery.getDeliveryAddFee1() == null) {
                        purchaseDelivery.setDeliveryAddFee1(0f);
                    }

                    if (purchaseDelivery.getDeliveryAddFee2() == null) {
                        purchaseDelivery.setDeliveryAddFee1(0f);
                    }

                    if (productPrice != null) {
                        if (productPrice.getSupplyPrice() != null) {
                            purchaseProduct.setSupplyPrice(productPrice.getSupplyPrice() * count);
                        }

                        purchaseProduct.setPageSeqNo(productPrice.getPageSeqNo());
                        purchaseProduct.setSupplyPageSeqNo(product.getPageSeqNo());
                        if (purchaseDelivery.getPaymentMethod().equals("before")) {
                            purchaseProduct.setPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice() + purchaseDelivery.getDeliveryFee() + purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2());
                        } else {
                            purchaseProduct.setPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        }

                        purchaseProduct.setProductPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        purchaseProduct.setUnitPrice(purchaseProduct.getPrice());
                        purchaseProduct.setProductDeliverySeqNo(productPrice.getProductDelivery().getSeqNo());

                        Float deliveryFee = purchaseDelivery.getDeliveryFee() + purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2();

                        // 수수료 계산
                        if (product.getMarketType().equals("wholesale")) { // 도매상품일경우

                            Float paymentFee = purchaseProduct.getPrice() * PaymentFeeRatio; // 3.1
                            Float platformFee = purchaseProduct.getPrice() * PlatformFeeRatio; // 3.9

                            purchaseProduct.setPaymentFee(paymentFee);
                            purchaseProduct.setPlatformFee(platformFee);

                            Float sellPrice = purchaseProduct.getProductPrice(); // 판매가격
                            Float supplyPrice = purchaseProduct.getSupplyPrice() + purchaseProduct.getOptionPrice(); // 공급가격
                            Float benefitPrice = sellPrice - supplyPrice; // 수익금


                            Float supplyPricePaymentFee = supplyPrice * PaymentFeeRatio; // 공급가 결제수수료
                            Float benefitPaymentFee = benefitPrice * PaymentFeeRatio; // 수익금 결제수수료
                            Float deliveryFeePaymentFee = deliveryFee * PaymentFeeRatio; // 배송비 결제수수료


                            Float supplyPriceFee = supplyPrice * PlatformFeeRatio; // 공급가 수수료
                            Float benefitFee = benefitPrice * PlatformFeeRatio; // 수익금 수수료
                            Float deliveryFeeFee = deliveryFee * PlatformFeeRatio; // 배송비 수수료

                            purchaseProduct.setSupplyPricePaymentFee(supplyPricePaymentFee);
                            purchaseProduct.setBenefitPaymentFee(benefitPaymentFee);
                            purchaseProduct.setDeliveryFeePaymentFee(deliveryFeePaymentFee);

                            purchaseProduct.setSupplyPriceFee(supplyPriceFee);
                            purchaseProduct.setBenefitFee(benefitFee);
                            purchaseProduct.setDeliveryFeeFee(deliveryFeeFee);

                            Float returnPayment = platformFee + supplyPrice + deliveryFee - supplyPricePaymentFee - deliveryFeePaymentFee - supplyPriceFee - deliveryFeeFee;


                            purchaseProduct.setReturnPaymentPrice(returnPayment);


                        } else { // 소매상품일 경우
                            // 결제수수료 = 결제금액 * 결제수수료비율
                            // 플랫폼수수료 = 결제금액 * 플랫폼수수료비율

                            Float paymentFee = purchaseProduct.getPrice() * PaymentFeeRatio;
                            Float platformFee = purchaseProduct.getPrice() * PlatformFeeRatio;

                            purchaseProduct.setPaymentFee(paymentFee);
                            purchaseProduct.setPlatformFee(platformFee);

                            purchaseProduct.setReturnPaymentPrice(platformFee);

                        }

                        if (!(purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol"))) {

                            if (productPrice.getIsPoint() != null && productPrice.getIsPoint()) {
                                Float savedPoint = productPrice.getPoint() * count;
                                purchaseProduct.setSavedPoint(savedPoint);
                            } else {
                                purchaseProduct.setSavedPoint(0f);
                            }

                        } else {
//                            float point = (purchaseProduct.getPrice()) * 0.01f;
//                            purchaseProduct.setSavedPoint((int) point);
                            purchaseProduct.setSavedPoint(0f);
                        }

                        purchaseProduct.setReturnPaymentPrice(purchaseProduct.getReturnPaymentPrice() + purchaseProduct.getSavedPoint());

                        String str = productPrice.getMarketType() == 3 ? "BB" : "RB";
                        code = SetID.getID(str);
                    }

                    purchaseProduct.setPurchaseProductOptionSelectList(purchaseProductOptionList);
                    purchaseProduct.setPurchaseDeliverySelect(purchaseDelivery);
                    purchaseProductList.add(purchaseProduct);
                    totalPrice += purchaseProduct.getPrice();
                    totalProductPrice += purchaseProduct.getProductPrice();
                    totalOptionPrice += purchaseProduct.getOptionPrice();
                    totalDeliveryFee += purchaseDelivery.getDeliveryFee();
                    totalDeliveryAddFee += (purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2());
                    totalReturnPaymentPrice += purchaseProduct.getReturnPaymentPrice();
                    if (i == 0) {
                        product1 = product;
                    }
                }

                purchase.setReturnPaymentPrice(totalReturnPaymentPrice);
                purchase.setDeliveryFee(totalDeliveryFee);
                purchase.setDeliveryAddFee(totalDeliveryAddFee);
                purchase.setPageSeqNo(purchaseProductList.get(0).getPageSeqNo());
                purchase.setPrice(totalPrice);
                purchase.setPgPrice(totalPrice.intValue());
                purchase.setUsePoint(0);
                purchase.setUseCash(0);
                purchase.setProductPrice(totalProductPrice);
                purchase.setOptionPrice(totalOptionPrice);
                if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                    purchase.setStatus(PurchaseStatus.PAY.getStatus());
                } else {
                    purchase.setStatus(PurchaseStatus.PAY_REQ.getStatus());
                }

                purchase.setAgentSeqNo(page.getAgentSeqNo());

                if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {


                    if (purchase.getPayMethod().equals("point")) {
                        if (purchase.getPrice() > member.getPoint()) {
                            throw new LackCostException("point lack");
                        }
                    } else {
                        if (purchase.getPrice() > member.getBol()) {
                            throw new LackCostException("bol lack");
                        }
                    }

                }

                purchase.setCode(code);
                purchase.setPayType("fintech");
                purchase = purchaseRepository.saveAndFlush(purchase);
                if (purchase == null || purchase.getSeqNo() == null) {
                    throw new Exception("purchase data insert error !!!");
                }

                int i = 0;
                List<PurchaseProductOption> purchaseProductOptionList = null;
                PurchaseDelivery purchaseDelivery = null;
                for (PurchaseProduct purchaseProduct : purchaseProductList) {

                    purchaseProductOptionList = purchaseProduct.getPurchaseProductOptionSelectList();
                    purchaseDelivery = purchaseProduct.getPurchaseDeliverySelect();

                    purchaseProduct.setSeqNo(null);
                    purchaseProduct.setPurchaseSeqNo(purchase.getSeqNo());
                    purchaseProduct.setCode(code + String.format("%04d", i + 1));
                    purchaseProduct.setIsStatusCompleted(false);
                    purchaseProduct.setIsPaymentPoint(false);

                    if (!AppUtil.isEmpty(member.getRecommendationCode())) {
                        Member recommenderMember = memberRepository.findByRecommendUniqueKey(member.getRecommendationCode());
                        if (recommenderMember != null) {
                            purchaseProduct.setRecommendedMemberSeqNo(recommenderMember.getSeqNo());
                            if (recommenderMember.getAppType().equals("biz")) {
                                purchaseProduct.setRecommendedMemberType("page");
                            } else {
                                purchaseProduct.setRecommendedMemberType("user");
                            }
                        }
                    }

                    try {
                        purchaseProduct = purchaseProductRepository.saveAndFlush(purchaseProduct);
                    } catch (Exception e) {
                        throw new InvalidBuyException("/purchaseShip[POST]", "The purchaseProduct save error : " + e.getMessage() + ":" + purchaseProduct.toString());
                    }
                    if (purchaseProduct == null || purchaseProduct.getSeqNo() == null) {
                        throw new InvalidBuyException("/purchaseShip[POST]", "The purchaseProduct insert error : " + purchaseProduct.toString());
                    }

                    if (purchaseProductOptionList != null) {
                        for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {
                            purchaseProductOption.setSeqNo(null);
                            purchaseProductOption.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                            purchaseProductOption.setPurchaseSeqNo(purchase.getSeqNo());

                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            if (productOptionDetail.getItem1() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem1().getOptionSeqNo());
                                purchaseProductOption.setDepth1(productOption.getName() + " : " + productOptionDetail.getItem1().getItem());
                            }

                            if (productOptionDetail.getItem2() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem2().getOptionSeqNo());
                                purchaseProductOption.setDepth2(productOption.getName() + " : " + productOptionDetail.getItem2().getItem());
                            }

                            purchaseProductOption = purchaseProductOptonRepository.saveAndFlush(purchaseProductOption);
                        }
                    }


                    if (purchaseDelivery != null) {
                        purchaseDelivery.setSeqNo(null);
                        purchaseDelivery.setPurchaseSeqNo(purchase.getSeqNo());
                        purchaseDelivery.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                        purchaseDelivery = purchaseDeliveryRepository.saveAndFlush(purchaseDelivery);

                        purchaseProductRepository.updatePurchaseDeliverySeqNoBySeqNo(purchaseProduct.getSeqNo(), purchaseDelivery.getSeqNo());
                    }

                    purchaseProductList.set(i, purchaseProduct);

                    i++;
                }
            }

            if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                purchasePoint(purchase, member, user, dateStr);
            }

        } catch (InvalidBuyException e) {
            logger.error(AppUtil.excetionToString(e));
            throw e;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new SqlException("[POST]/purchase/ship", e);
        }
        return purchase;
    }

    private void purchasePoint(Purchase purchase, Member member, User user, String dateStr) throws ResultCodeException {
        if (member == null) {
            member = memberRepository.findBySeqNo(user.getNo());
        }

        if (purchase.getPayMethod().equals("cash")) {
            if (purchase.getPrice() > member.getCash()) {
                throw new LackCostException("cash lack");
            }

            CashHistory cashHistory = new CashHistory();
            cashHistory.setMemberSeqNo(user.getNo());
            cashHistory.setType("used");
            cashHistory.setSecondaryType("member");
            cashHistory.setCash(purchase.getUseCash().floatValue());
            cashHistory.setSubject(purchase.getTitle());
            cashService.updateCash(user.getNo(), cashHistory);

        } else if (purchase.getPayMethod().equals("point")) {
            if (purchase.getPrice() > member.getPoint()) {
                throw new LackCostException("point lack");
            }

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(user.getNo());
            pointHistory.setType("used");
            pointHistory.setPoint(purchase.getUsePoint().floatValue());
            pointHistory.setSubject(purchase.getTitle());
            pointService.updatePoint(user.getNo(), pointHistory);

        } else if (purchase.getPayMethod().equals("bol")) {
            if (purchase.getPrice() > member.getBol()) {
                throw new LackCostException("point lack");
            }

            kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
            bolHistory.setAmount(purchase.getPrice());
            bolHistory.setMemberSeqNo(user.getNo());
            bolHistory.setSubject("상품구매");
            bolHistory.setPrimaryType("decrease");
            bolHistory.setSecondaryType("buy");
            bolHistory.setTargetType("member");
            bolHistory.setTargetSeqNo(user.getNo());
            bolHistory.setHistoryProp(new HashMap<String, Object>());
            bolHistory.getHistoryProp().put("사용유형", "상품 구매");
            bolService.decreaseBol(user.getNo(), bolHistory);
        }

        List<PurchaseProduct> purchaseProductList = getPurchaseProductList(purchase.getSeqNo());
        if (purchaseProductList != null) {

            for (int i = 0; i < purchaseProductList.size(); i++) {
                PurchaseProduct purchaseProduct = purchaseProductList.get(i);
                if (purchaseProduct.getStatus() < PurchaseProductStatus.PAY.getStatus()) {

                    try {
                        Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
//                        if (storeType.equals("PROD")) {
//                            try {
//                                if (product.getSupplierSeqNo().equals(2L)) {//도매매 상품일때 처리
//                                    Long domemeOrderNo = domaemaeOrder(purchaseProduct.getSeqNo());
//                                    if (domemeOrderNo == null) {
//                                        throw new InvalidBuyException("domaemae fail");
//                                    } else {
//                                        purchaseProduct.setDomemeOrderNo(domemeOrderNo);
//                                    }
//                                }
//                            } catch (Exception e) {
//                                logger.error("domeme fail : " + e.toString());
//                            }
//
//                        }

                        purchaseProduct.setStatus(PurchaseProductStatus.PAY.getStatus()); // 결제승인
                        purchaseProduct.setPayDatetime(dateStr);
                        purchaseProduct.setChangeStatusDatetime(dateStr);
                        if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                            purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus()); // 주문확인전
                        } else if (purchase.getSalesType() == SalesType.TICKET.getType()) {
                            purchaseProduct.setReserveStatus(ReserveStatus.BOOKING.getStatus());
                        }

                        product.setSoldCount(product.getSoldCount() + purchaseProduct.getCount());

                        ProductPriceOnly productPriceOnly = productService.getProductPriceOnlyByCode(purchaseProduct.getProductPriceCode());
                        productPriceOnly.setDailySoldCount(productPriceOnly.getDailySoldCount() + purchaseProduct.getCount());
                        if (product.getCount() <= product.getSoldCount()) {
                            product.setStatus(GoodsStatus.SOLD_OUT.getStatus()); //판매종료
                            productPriceOnly.setStatus(GoodsStatus.SOLD_OUT.getStatus());
                        }
                        productService.saveProductPriceOnly(productPriceOnly);

                        product.setModDatetime(dateStr);

                        if (product.getStatus().equals(GoodsStatus.SOLD_OUT.getStatus())) {
                            productService.deleteLikeAllByProductSeqNo(product.getSeqNo());
                        }

                        productService.save(product);

                        List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                        if (purchaseProductOptionList != null) {
                            for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {

                                ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                                productOptionDetail.setSoldCount(productOptionDetail.getSoldCount() + purchaseProductOption.getAmount());
                                productService.saveProductOptionDetailRepository(productOptionDetail);
                            }
                        }

                        purchaseProductRepository.save(purchaseProduct);
                    } catch (Exception e) {
                        throw new InvalidBuyException("point", e);
                    }
                }
            }
        }

        if (purchase.getMemberLuckyCouponSeqNo() != null) {
            luckyCouponService.useCoupon(purchase.getMemberLuckyCouponSeqNo());
        }


        if (purchase.getPurchaseType().equals("luckyBol") && purchase.getLuckyBolPurchaseSeqNo() != null) {
            luckyBolService.updatePurchaseSeqNo(purchase);
        }


        try {
            if (purchase.getSalesType() == SalesType.SHIPPING.getType() && purchaseProductList != null) {

                for (PurchaseProduct purchaseProduct : purchaseProductList) {

                    Page page = pageRepository.findBySeqNo(purchaseProduct.getPageSeqNo());
                    Member pageMember = memberRepository.findBySeqNo(page.getMemberSeqNo());

                    MsgJpa msgJpa = new MsgJpa();
                    msgJpa.setSeqNo(null);
                    msgJpa.setIncludeMe(false);
                    msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
                    msgJpa.setStatus(Const.MSG_STATUS_READY);
                    msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
                    msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);
                    msgJpa.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                    msgJpa.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
                    msgJpa.setContents(purchase.getTitle());
                    msgJpa.setMoveSeqNo(purchaseProduct.getSeqNo());
                    msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
                    msgJpa.setReserved(false);
                    msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
                    msgJpa.setStatus("ready");
                    msgJpa.setTotalPrice(0L);
                    msgJpa.setRefundPrice(0L);
                    msgJpa.setTargetCount(1);
                    msgJpa.setSuccCount(0);
                    msgJpa.setFailCount(0);
                    msgJpa.setReadCount(0);
                    msgJpa.setRegDatetime(dateStr);
                    msgJpa = msgRepository.saveAndFlush(msgJpa);

                    PushTargetJpa pushTargetJpa = new PushTargetJpa();
                    pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
                    pushTargetJpa.setMemberSeqNo(pageMember.getSeqNo());
                    pushTargetRepository.saveAndFlush(pushTargetJpa);

                    MsgOnly msg = new MsgOnly();
                    msg.setNo(msgJpa.getSeqNo());
                    msg.setIncludeMe(false);
                    msg.setInput(Const.MSG_INPUT_SYSTEM);
                    msg.setStatus(Const.MSG_STATUS_READY);
                    msg.setType(Const.MSG_TYPE_PUSH);
                    msg.setMoveType1(Const.MOVE_TYPE_INNER);
                    msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
                    msg.setAppType(Const.APP_TYPE_BIZ);
                    msg.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                    msg.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
                    msg.setContents(purchase.getTitle());
                    msg.setMoveTarget(new NoOnlyKey(purchaseProduct.getSeqNo()));
                    queueService.sendOnlyPush(msg);
                }
            }

        } catch (Exception e) {
            logger.error("push error : " + e.toString());
        }
    }

    public Long domaemaeOrder(Long purchaseProductSeqNo) {

        PurchaseProduct purchaseProduct = purchaseProductRepository.findBySeqNo(purchaseProductSeqNo);
        Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
        List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());

        String deliveryType;
        PurchaseDelivery purchaseDelivery = purchaseDeliveryRepository.findByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
        if (purchaseDelivery.getType() == 1) {
            deliveryType = "S";
        } else {
            if (purchaseDelivery.getPaymentMethod().equals("before")) {
                Float deliveryFee = purchaseDelivery.getDeliveryFee() + purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2();
                if (deliveryFee > 0) {
                    deliveryType = "P";
                } else {
                    deliveryType = "S";
                }
            } else {
                deliveryType = "B";
            }
        }

        StringBuilder option = new StringBuilder();

        if (purchaseProductOptionList == null || purchaseProductOptionList.isEmpty()) {
            option.append("00|" + purchaseProduct.getCount());
        } else {

            int j = 0;
            for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {
                ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                if (j != 0) {
                    option.append("|");
                }
                option.append(productOptionDetail.getDomemeCode() + "|" + purchaseProductOption.getAmount());
                j++;
            }
        }

        String memo = "";
        if (!AppUtil.isEmpty(purchaseDelivery.getDeliveryMemo())) {
            memo = purchaseDelivery.getDeliveryMemo();
        }

        StringBuilder deliinfo = new StringBuilder();

        deliinfo.append(purchaseDelivery.getReceiverName() + "|");//받는사람
        deliinfo.append("|");//이메일
        deliinfo.append(purchaseDelivery.getReceiverPostCode() + "|");//우편번호
        deliinfo.append(purchaseDelivery.getReceiverAddress() + "|");//주소
        deliinfo.append(purchaseDelivery.getReceiverAddressDetail() + "|");//주소 상세
        deliinfo.append(AppUtil.getPhoneNumber(purchaseDelivery.getReceiverTel()) + "|");//연락처
        deliinfo.append("|");//추가전화번호
        deliinfo.append("캐시픽|");//상호명
        deliinfo.append("|");//통관고유번호


        JsonObject jsonObject = domeggookService.order(product.getOriginalSeqNo(), deliveryType, option.toString(), memo, deliinfo.toString());
        JsonObject domeggook = jsonObject.get("domeggook").getAsJsonObject();
        if (domeggook != null) {
            String result = domeggook.get("result").getAsString();
            if (result.equals("SUCCESS")) {
                return domeggook.get("order").getAsJsonArray().get(0).getAsJsonObject().get("orderNo").getAsLong();
            } else {
                return null;
            }
        }
        return null;
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

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int verifyBootPay(User user, String orderId, String receiptId) throws ResultCodeException {
        Purchase purchase = purchaseRepository.findByOrderId(orderId);
        try {
            String dateStr = AppUtil.localDatetimeNowString();
            String token = getAccessToken();

            if (token == null || token.isEmpty()) {
                throw new InvalidCashException();
            }

            HttpResponse res = verify(receiptId, token);
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
            JsonObject data = jsonObject.getAsJsonObject("data");

            if (!data.get("status").getAsString().equals("1")) {
                throw new Exception(" pay rejected !!!");
            }

            Page page = pageRepository.findBySeqNo(purchase.getPageSeqNo());

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
            lpngCallbackResult.setOrderGoodsname(purchase.getTitle());
            lpngCallbackResult.setOrderReqAmt(paymentPrice + "");
            lpngCallbackResult.setOrderName(purchase.getBuyerName());
            lpngCallbackResult.setOrderHp(purchase.getBuyerTel());
            lpngCallbackResult.setApprNo(authNo);
            lpngCallbackResult.setApprTranNo(daouTrx);
            lpngCallbackResult.setApprDate(setDay);
            lpngCallbackResult.setApprTime(setTime);
            lpngCallbackResult.setCardtxt(cardName);
            lpngCallbackResult.setReqCardNo(cardNo);
            lpngCallbackResult.setApprShopCode("");
            lpngCallbackResult.setReqInstallment(cardQuota);

            lpngCallbackResult = lpngCallbackResultRepository.saveAndFlush(lpngCallbackResult);

            LpngCallback lpngCallback = new LpngCallback();
            lpngCallback.setMemberSeqNo(user.getNo());
            lpngCallback.setPurchaseSeqNo(purchase.getSeqNo());
            lpngCallback.setOrderId(orderNo);
            lpngCallback.setPgTranId(daouTrx);
            lpngCallback.setName(purchase.getBuyerName());
            lpngCallback.setPrice(paymentPrice);
            lpngCallback.setStatus(true);
            lpngCallback.setApprDate(setDay);
            lpngCallback.setApprTime(setTime);
            lpngCallback.setRegDatetime(dateStr);
            lpngCallback.setProcess(1); // 결제완료
            lpngCallback.setResultSeqNo(lpngCallbackResult.getSeqNo());
            lpngCallbackRepository.save(lpngCallback);

            List<PurchaseProduct> purchaseProductList = getPurchaseProductList(purchase.getSeqNo());

            if (purchaseProductList != null) {

                for (int i = 0; i < purchaseProductList.size(); i++) {
                    PurchaseProduct purchaseProduct = purchaseProductList.get(i);
                    if (purchaseProduct.getStatus() < PurchaseProductStatus.PAY.getStatus()) {

                        try {
                            Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
//                            if (storeType.equals("PROD")) {
//                                try {
//                                    if (product.getSupplierSeqNo().equals(2L)) {//도매매 상품일때 처리
//                                        Long domemeOrderNo = domaemaeOrder(purchaseProduct.getSeqNo());
//                                        if (domemeOrderNo == null) {
//                                            throw new InvalidBuyException("domaemae fail");
//                                        } else {
//                                            purchaseProduct.setDomemeOrderNo(domemeOrderNo);
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                    logger.error("domeme fail : " + e.toString());
//                                }
//
//                            }

                            purchaseProduct.setStatus(PurchaseProductStatus.PAY.getStatus()); // 결제승인
                            purchaseProduct.setPayDatetime(dateStr);
                            purchaseProduct.setChangeStatusDatetime(dateStr);

                            if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                                purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus()); // 주문확인전
                            } else if (purchase.getSalesType() == SalesType.TICKET.getType()) {
                                purchaseProduct.setReserveStatus(ReserveStatus.BOOKING.getStatus());
                            }

                            product.setSoldCount(product.getSoldCount() + purchaseProduct.getCount());

                            ProductPriceOnly productPriceOnly = productService.getProductPriceOnlyByCode(purchaseProduct.getProductPriceCode());
                            productPriceOnly.setDailySoldCount(productPriceOnly.getDailySoldCount() + purchaseProduct.getCount());
                            if (product.getCount() <= product.getSoldCount()) {
                                product.setStatus(GoodsStatus.SOLD_OUT.getStatus()); //판매종료
                                productPriceOnly.setStatus(GoodsStatus.SOLD_OUT.getStatus());
                            }
                            productService.saveProductPriceOnly(productPriceOnly);

                            product.setModDatetime(dateStr);

                            if (product.getStatus().equals(GoodsStatus.SOLD_OUT.getStatus())) {
                                productService.deleteLikeAllByProductSeqNo(product.getSeqNo());
                            }

                            productService.save(product);

                            List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                            if (purchaseProductOptionList != null) {
                                for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {

                                    ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                                    productOptionDetail.setSoldCount(productOptionDetail.getSoldCount() + purchaseProductOption.getAmount());
                                    productService.saveProductOptionDetailRepository(productOptionDetail);
                                }
                            }

                            purchaseProductRepository.save(purchaseProduct);
                        } catch (Exception e) {
                            throw new InvalidBuyException("verifyBootPay", e);
                        }
                    }
                }
            }

            purchase.setReceiptId(receiptId);
            purchase.setPgTranId(daouTrx);
            purchase.setApprNo(authNo);
            //purchase.setApprTranNo("");
            purchase.setStatus(PurchaseStatus.PAY.getStatus()); // 결제완료
            purchase.setModDatetime(dateStr);


            try {
                if (purchase.getSalesType() == SalesType.SHIPPING.getType() && purchaseProductList != null) {

                    for (PurchaseProduct purchaseProduct : purchaseProductList) {

                        Member pageMember = memberRepository.findBySeqNo(page.getMemberSeqNo());

                        MsgJpa msgJpa = new MsgJpa();
                        msgJpa.setSeqNo(null);
                        msgJpa.setIncludeMe(false);
                        msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
                        msgJpa.setStatus(Const.MSG_STATUS_READY);
                        msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
                        msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);
                        msgJpa.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                        msgJpa.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
                        msgJpa.setContents(purchase.getTitle());
                        msgJpa.setMoveSeqNo(purchaseProduct.getSeqNo());
                        msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
                        msgJpa.setReserved(false);
                        msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
                        msgJpa.setStatus("ready");
                        msgJpa.setTotalPrice(0L);
                        msgJpa.setRefundPrice(0L);
                        msgJpa.setTargetCount(1);
                        msgJpa.setSuccCount(0);
                        msgJpa.setFailCount(0);
                        msgJpa.setReadCount(0);
                        msgJpa.setRegDatetime(dateStr);
                        msgJpa = msgRepository.saveAndFlush(msgJpa);

                        PushTargetJpa pushTargetJpa = new PushTargetJpa();
                        pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
                        pushTargetJpa.setMemberSeqNo(pageMember.getSeqNo());
                        pushTargetRepository.saveAndFlush(pushTargetJpa);

                        MsgOnly msg = new MsgOnly();
                        msg.setNo(msgJpa.getSeqNo());
                        msg.setIncludeMe(false);
                        msg.setInput(Const.MSG_INPUT_SYSTEM);
                        msg.setStatus(Const.MSG_STATUS_READY);
                        msg.setType(Const.MSG_TYPE_PUSH);
                        msg.setMoveType1(Const.MOVE_TYPE_INNER);
                        msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
                        msg.setAppType(Const.APP_TYPE_BIZ);
                        msg.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                        msg.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
                        msg.setContents(purchase.getTitle());
                        msg.setMoveTarget(new NoOnlyKey(purchaseProduct.getSeqNo()));
                        queueService.sendOnlyPush(msg);
                    }
                }

            } catch (Exception e) {
                logger.error("push error : " + e.toString());
            }

            String reqdephold = "Y";

            String type = "TEST";
            if (storeType.equals("PROD")) {
                type = "USE";
            }

            String[] mobiles = user.getMobile().split("##");
            String mobile = null;
            if (mobiles.length == 2) {
                mobile = mobiles[1];
            } else {
                mobile = mobiles[0];
            }

            URIBuilder uriBuilder = new URIBuilder("http://pay.ftlink.co.kr/payalert/pplus/noti_cert.asp");
            uriBuilder
                    .addParameter("PAYMETHOD", URLEncoder.encode(payMethod, "EUC-KR"))
                    .addParameter("CPID", URLEncoder.encode(cpid, "EUC-KR"))
                    .addParameter("DAOUTRX", URLEncoder.encode(daouTrx, "EUC-KR"))
                    .addParameter("ORDERNO", URLEncoder.encode(orderNo, "EUC-KR"))
                    .addParameter("AMOUNT", URLEncoder.encode(paymentPrice + "", "EUC-KR"))
                    .addParameter("PRODUCTNAME", URLEncoder.encode(purchase.getTitle(), "EUC-KR"))
                    .addParameter("SETDATE", URLEncoder.encode(settDate, "EUC-KR"))
                    .addParameter("AUTHNO", URLEncoder.encode(authNo, "EUC-KR"))
                    .addParameter("CARDCODE", URLEncoder.encode(cardCode, "EUC-KR"))
                    .addParameter("CARDNAME", URLEncoder.encode(cardName, "EUC-KR"))
                    .addParameter("CARDNO", URLEncoder.encode(cardNo, "EUC-KR"))
                    .addParameter("EMAIL", URLEncoder.encode(email, "EUC-KR"))
                    .addParameter("USERID", URLEncoder.encode(mobile, "EUC-KR"))
                    .addParameter("USERNAME", URLEncoder.encode(purchase.getBuyerName(), "EUC-KR"))
                    .addParameter("PRODUCTCODE", URLEncoder.encode(purchaseProductList.get(0).getProductPriceCode() + "", "EUC-KR"))
                    .addParameter("RESERVEDINDEX1", URLEncoder.encode(page.getShopCode(), "EUC-KR"))
                    .addParameter("RESERVEDINDEX2", URLEncoder.encode("", "EUC-KR"))
                    .addParameter("RESERVEDINDEX3", URLEncoder.encode("", "EUC-KR"))
                    .addParameter("RESERVEDSTRING", URLEncoder.encode("", "EUC-KR"))
                    .addParameter("ISTEST", URLEncoder.encode(type, "EUC-KR"))
                    .addParameter("reqdephold", URLEncoder.encode(reqdephold, "EUC-KR"))
                    .addParameter("MANUAL_USED", "Y")
                    .addParameter("MANUAL_AMT", URLEncoder.encode(purchase.getReturnPaymentPrice().intValue() + "", "EUC-KR"))
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


            purchaseRepository.save(purchase);

//            if(purchaseProductList != null){
//                for (PurchaseProduct purchaseProduct : purchaseProductList) {
//
//                    if (storeType.equals("PROD")) {
//                        if(purchaseProduct.getProductPriceCode().equals("R21072655224295")){
//                            updateCompleteBySeqNo(purchaseProduct.getSeqNo());
//                        }
//                    }else{
//                        if(purchaseProduct.getProductPriceCode().equals("R21072638289708")){
//                            updateCompleteBySeqNo(purchaseProduct.getSeqNo());
//                        }
//                    }
//                }
//            }

        } catch (Exception e) {
            throw new InvalidBuyException("verifyBootPay", e);
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public FTLinkPayResponse ftlinkPay(FTLinkPayRequest data) throws ResultCodeException {
        Purchase purchase = purchaseRepository.findByOrderId(data.getComp_orderno());
        try {
            if (StringUtils.isEmpty(data.getShopcode())) {
                FTLinkPayResponse res = new FTLinkPayResponse();
                res.setErrCode("99");
                res.setErrMessage("결제 가능한 스토어가 아닙니다.");
                return res;
            }

            String serverType = data.getServerType();
            String roomId = data.getRoomId();

            if (Integer.valueOf(data.getOrder_req_amt()) != purchase.getPrice().intValue()) {
                throw new InvalidBuyException("ftlinkPay", "결제금액이 일치하지 않습니다.");
            }

            data.setManual_used("Y");
            data.setManual_amt(String.valueOf(purchase.getReturnPaymentPrice().intValue()));

            Page page = pageRepository.findBySeqNo(purchase.getPageSeqNo());
            User pageUser = userService.getUser(page.getMemberSeqNo());
            data.setLoginId(pageUser.getLoginId().replace(pageUser.getAppType() + "##", ""));
            data.setServerType("");
            data.setRoomId("");
//            if (purchase.getSalesType() == SalesType.SHIPPING.getType() || purchase.getSalesType() == SalesType.TICKET.getType()) {
            if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                data.setReqdephold("Y");
            }
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
            callback.setPurchaseSeqNo(purchase.getSeqNo());
            callback.setMemberSeqNo(purchase.getMemberSeqNo());
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

            List<PurchaseProduct> purchaseProductList = getPurchaseProductList(purchase.getSeqNo());

//            if (StringUtils.isNotEmpty(roomId)) {
//                buy.setRoomId(roomId);
//            }

            if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {

                User user = userService.getUser(purchase.getMemberSeqNo());


                callback.setStatus(true);
                callback.setProcess(LpngProcess.PAY.getType());
                lpngCallbackRepository.save(callback);

                if (purchaseProductList != null) {

                    for (int i = 0; i < purchaseProductList.size(); i++) {
                        PurchaseProduct purchaseProduct = purchaseProductList.get(i);
                        if (purchaseProduct.getStatus() < PurchaseProductStatus.PAY.getStatus()) {

                            try {
                                Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
//                                if (storeType.equals("PROD")) {
//                                    try {
//                                        if (product.getSupplierSeqNo().equals(2L)) {//도매매 상품일때 처리
//                                            Long domemeOrderNo = domaemaeOrder(purchaseProduct.getSeqNo());
//                                            if (domemeOrderNo == null) {
//                                                throw new InvalidBuyException("domaemae fail");
//                                            } else {
//                                                purchaseProduct.setDomemeOrderNo(domemeOrderNo);
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        logger.error("domeme fail : " + e.toString());
//                                    }
//
//                                }

                                purchaseProduct.setStatus(PurchaseProductStatus.PAY.getStatus()); // 결제승인
                                purchaseProduct.setPayDatetime(dateStr);
                                purchaseProduct.setChangeStatusDatetime(dateStr);
                                if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                                    purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus()); // 주문확인전
                                } else if (purchase.getSalesType() == SalesType.TICKET.getType()) {
                                    purchaseProduct.setReserveStatus(ReserveStatus.BOOKING.getStatus());
                                }


                                product.setSoldCount(product.getSoldCount() + purchaseProduct.getCount());

                                ProductPriceOnly productPriceOnly = productService.getProductPriceOnlyByCode(purchaseProduct.getProductPriceCode());
                                productPriceOnly.setDailySoldCount(productPriceOnly.getDailySoldCount() + purchaseProduct.getCount());
                                if (product.getCount() <= product.getSoldCount()) {
                                    product.setStatus(GoodsStatus.SOLD_OUT.getStatus()); //판매종료
                                    productPriceOnly.setStatus(GoodsStatus.SOLD_OUT.getStatus());
                                }
                                productService.saveProductPriceOnly(productPriceOnly);

                                product.setModDatetime(dateStr);

                                if (product.getStatus().equals(GoodsStatus.SOLD_OUT.getStatus())) {
                                    productService.deleteLikeAllByProductSeqNo(product.getSeqNo());
                                }

                                productService.save(product);

                                List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                                if (purchaseProductOptionList != null) {
                                    for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {

                                        ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                                        productOptionDetail.setSoldCount(productOptionDetail.getSoldCount() + purchaseProductOption.getAmount());
                                        productService.saveProductOptionDetailRepository(productOptionDetail);
                                    }
                                }

                                purchaseProductRepository.save(purchaseProduct);

                            } catch (Exception e) {
                                throw new InvalidBuyException("ftlinkPay", e);
                            }
                        }
                    }
                }

                purchase.setStatus(PurchaseStatus.PAY.getStatus());
                purchase.setModDatetime(dateStr);
                purchase.setPgTranId(res.getAppr_tranNo());
                purchase.setApprNo(res.getAppr_no());


                try {
                    if (purchase.getSalesType() == SalesType.SHIPPING.getType() && purchaseProductList != null) {

                        for (PurchaseProduct purchaseProduct : purchaseProductList) {

                            Member pageMember = memberRepository.findBySeqNo(page.getMemberSeqNo());

                            MsgJpa msgJpa = new MsgJpa();
                            msgJpa.setSeqNo(null);
                            msgJpa.setIncludeMe(false);
                            msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
                            msgJpa.setStatus(Const.MSG_STATUS_READY);
                            msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
                            msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);
                            msgJpa.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                            msgJpa.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
                            msgJpa.setContents(purchase.getTitle());
                            msgJpa.setMoveSeqNo(purchaseProduct.getSeqNo());
                            msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
                            msgJpa.setReserved(false);
                            msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
                            msgJpa.setStatus("ready");
                            msgJpa.setTotalPrice(0L);
                            msgJpa.setRefundPrice(0L);
                            msgJpa.setTargetCount(1);
                            msgJpa.setSuccCount(0);
                            msgJpa.setFailCount(0);
                            msgJpa.setReadCount(0);
                            msgJpa.setRegDatetime(dateStr);
                            msgJpa = msgRepository.saveAndFlush(msgJpa);

                            PushTargetJpa pushTargetJpa = new PushTargetJpa();
                            pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
                            pushTargetJpa.setMemberSeqNo(pageMember.getSeqNo());
                            pushTargetRepository.saveAndFlush(pushTargetJpa);

                            MsgOnly msg = new MsgOnly();
                            msg.setNo(msgJpa.getSeqNo());
                            msg.setIncludeMe(false);
                            msg.setInput(Const.MSG_INPUT_SYSTEM);
                            msg.setStatus(Const.MSG_STATUS_READY);
                            msg.setType(Const.MSG_TYPE_PUSH);
                            msg.setMoveType1(Const.MOVE_TYPE_INNER);
                            msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
                            msg.setAppType(Const.APP_TYPE_BIZ);
                            msg.setMoveType2(Const.MOVE_TYPE_BUY_SHIPPING);
                            msg.setSubject(user.getNickname() + "님이 상품을 결제하였습니다.");
                            msg.setContents(purchase.getTitle());
                            msg.setMoveTarget(new NoOnlyKey(purchaseProduct.getSeqNo()));
                            queueService.sendOnlyPush(msg);
                        }
                    }

                } catch (Exception e) {
                    logger.error("push error : " + e.toString());
                }

                purchaseRepository.save(purchase);

//                if(purchaseProductList != null){
//                    for (PurchaseProduct purchaseProduct : purchaseProductList) {
//
//                        if (storeType.equals("PROD")) {
//                            if(purchaseProduct.getProductPriceCode().equals("R21072655224295")){
//                                updateCompleteBySeqNo(purchaseProduct.getSeqNo());
//                            }
//                        }else{
//                            if(purchaseProduct.getProductPriceCode().equals("R21072638289708")){
//                                updateCompleteBySeqNo(purchaseProduct.getSeqNo());
//                            }
//                        }
//                    }
//                }

            } else {
                try {
                    callback.setStatus(false);
                    callback.setProcess(LpngProcess.CANCEL.getType());
                    callback.setMemo("결제 실패");
                    if (!AppUtil.isEmpty(callback.getPgTranId())) {
                        lpngCallbackRepository.save(callback);
                    }
                    purchase.setStatus(PurchaseStatus.FAIL.getStatus());

                    for (int i = 0; i < purchaseProductList.size(); i++) {
                        PurchaseProduct purchaseProduct = purchaseProductList.get(i);
                        purchaseProduct.setStatus(PurchaseStatus.FAIL.getStatus());
                        purchaseProduct.setChangeStatusDatetime(dateStr);
                        purchaseProductRepository.save(purchaseProduct);
                    }

                    purchaseRepository.save(purchase);
                } catch (Exception e) {
                    purchase.setStatus(PurchaseStatus.FAIL.getStatus());
                    purchaseRepository.save(purchase);
                }


                res.setReq_installment(null);
            }


            if (StringUtils.isNotEmpty(serverType)) {
                adminResult(serverType, res.getErrCode(), roomId, purchase.getOrderId());
            }

            return res;
        } catch (Exception e) {
            throw new InvalidBuyException("[POST]/buy/lpng/pay", e);
        }

    }

    public void adminResult(String serverType, String resultCode, String roomId, String orderId) {
        try {
            String url = "";
            if (serverType.equals("stage")) {
                url = adminStageUrl + "lpng/result";
            } else if (serverType.equals("prod")) {
                url = adminProdUrl + "lpng/result";
            }

            if (StringUtils.isNotEmpty(url)) {
                logger.debug(url);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(new BasicNameValuePair("resultCode", resultCode));
                nameValuePairList.add(new BasicNameValuePair("roomId", roomId));
                nameValuePairList.add(new BasicNameValuePair("orderId", orderId));

                logger.debug("roomId: " + roomId + " " + "orderId : " + orderId);
                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = AppUtil.getPost(url, nameValuePairList);
                HttpResponse res = client.execute(post);
//				client.execute(post) ;

                logger.debug("code : " + res.getStatusLine().getStatusCode());
                client.close();
            }
        } catch (Exception e) {

        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = InvalidBuyException.class)
    public void ftLinkCancelResult(FTLinkCancelResponse res) {

        if (res.getErrCode().equals("00") || res.getErrCode().equals("0000")) {
            LpngCallback callback = lpngCallbackRepository.findByPgTranId(res.getAPPRTRXID());

            if (callback != null) {
                logger.debug("pgId : " + callback.getPgTranId());
                LpngCallbackResult callbackResult = callback.getResult();

                callbackResult.setErrorCode(res.getErrCode());
                callbackResult.setErrorMsg(res.getErrMessage());
                lpngCallbackResultRepository.save(callbackResult);

                callback.setStatus(false);
//                Map<String, String> map = callback.getPaymentData();
//                map.put("errCode", "-1");
//                map.put("errMessage", res.getErrMessage());
//                callback.setPaymentData(map);
                callback.setProcess(LpngProcess.CANCEL.getType());
                callback = lpngCallbackRepository.saveAndFlush(callback);

                Purchase purchase = purchaseRepository.findByOrderId(callback.getOrderId());
                String dateStr = AppUtil.localDatetimeNowString();


                purchase.setModDatetime(dateStr);
                purchase.setStatus(PurchaseStatus.CANCEL_COMPLETE.getStatus());
                purchaseRepository.saveAndFlush(purchase);
                purchaseProductRepository.updateCancelByPurchaseSeqNo(purchase.getSeqNo(), dateStr);
            }


        } else {
            logger.error("ftlinkCancel Error " + res.getErrMessage());
        }
    }

    public Boolean ftlinkCancel(Purchase purchase, String message) {
        try {

            LpngCallback callback = lpngCallbackRepository.findByOrderId(purchase.getOrderId());
//            String apprDate = callback.getApprDate();
//            String today = AppUtil.localTodayYYYYMMDD();

            Page page = pageRepository.findBySeqNo(purchase.getPageSeqNo());
            User pageUser = userService.getUser(page.getMemberSeqNo());
            LpngCallbackResult callbackResult = callback.getResult();

            if (purchase.getPg().equals("FTLINK")) {
                FTLinkCancelRequest ftLinkCancelRequest = new FTLinkCancelRequest();
                ftLinkCancelRequest.setShopcode(callbackResult.getShopCode());
                ftLinkCancelRequest.setLoginId(pageUser.getLoginId().replace("biz##", ""));
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
                    callback = lpngCallbackRepository.saveAndFlush(callback);

                    return true;
                } else {
                    logger.error("ftlinkCancel Error " + res.getErrMessage());
                    return false;
                }
            } else {

                if (purchase.getPg().toUpperCase().equals("DANAL")) {
                    BootPayApi api = new BootPayApi();
                    api.getAccessTokenV2();

                    Cancel cancel = new Cancel();
                    cancel.receipt_id = purchase.getReceiptId();
                    cancel.cancel_username = purchase.getBuyerName();
                    cancel.cancel_message = message;
                    cancel.cancel_price = purchase.getPrice().intValue();

                    HttpResponse res = api.cancel(cancel);

                    Gson gson = new Gson();
                    if (res.getStatusLine().getStatusCode() != 200) {

                        BootPayErrorResponse bootPayErrorResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), BootPayErrorResponse.class);
                        if (bootPayErrorResponse.getError_code().equals("RC_ALREADY_CANCELLED")) {

                            res = api.get(purchase.getReceiptId());

                            BootPayResponse bootPayResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), BootPayResponse.class);

                            FTLinkCancelNotiRequest ftLinkCancelNotiRequest = new FTLinkCancelNotiRequest();
                            ftLinkCancelNotiRequest.setRESULTCODE("0000");
                            ftLinkCancelNotiRequest.setERRORMESSAGE("");
                            ftLinkCancelNotiRequest.setDAOUTRX(bootPayResponse.getCard_data().getTid());
                            ftLinkCancelNotiRequest.setAMOUNT(callback.getPrice().toString());
                            ftLinkCancelNotiRequest.setCANCELDATE(bootPayResponse.getCancelled_at());
                            ftLinkCancelNotiRequest.setSHOPCODE(page.getShopCode());
                            ftLinkCancelNotiRequest.setORDERNO(purchase.getOrderId());
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
                            callback = lpngCallbackRepository.saveAndFlush(callback);
                            return true;
                        } else {
                            logger.error("bootpayCancel Error : " + bootPayErrorResponse.getError_code() + " : " + bootPayErrorResponse.getMessage());
                            return false;
                        }

                    }


                    BootPayCancelResponse bootPayCancelResponse = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), BootPayCancelResponse.class);
                    if (bootPayCancelResponse.getStatus() == 20) {
                        FTLinkCancelNotiRequest ftLinkCancelNotiRequest = new FTLinkCancelNotiRequest();
                        ftLinkCancelNotiRequest.setRESULTCODE("0000");
                        ftLinkCancelNotiRequest.setERRORMESSAGE("");
                        ftLinkCancelNotiRequest.setDAOUTRX(bootPayCancelResponse.getCard_data().getTid());
                        ftLinkCancelNotiRequest.setAMOUNT(callback.getPrice().toString());
                        ftLinkCancelNotiRequest.setCANCELDATE(bootPayCancelResponse.getCancelled_at());
                        ftLinkCancelNotiRequest.setSHOPCODE(page.getShopCode());
                        ftLinkCancelNotiRequest.setORDERNO(purchase.getOrderId());
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
                        callback = lpngCallbackRepository.saveAndFlush(callback);
                        return true;
                    } else {
                        logger.error("bootpayCancel Error " + res.getStatusLine().getStatusCode());
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
                        ftLinkCancelNotiRequest.setORDERNO(purchase.getOrderId());
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
                        callback = lpngCallbackRepository.saveAndFlush(callback);
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

    public LpngCallback getLpngCallbackByPurchaseSeqNo(Long purchaseSeqNo) throws ResultCodeException {
        LpngCallback callback = lpngCallbackRepository.findAllByPurchaseSeqNo(purchaseSeqNo);
        if (callback == null) {
            throw new InvalidBuyException();
        }
        return callback;
    }

    public String cancelTest(String tranId, String price, String shopCode) throws Exception {
        PayJoaCancelReadyRequest payJoaCancelReadyRequest = new PayJoaCancelReadyRequest();
        payJoaCancelReadyRequest.setCPID(NOMEMBER_CPID);
        payJoaCancelReadyRequest.setPAYMETHOD("CARD");
        payJoaCancelReadyRequest.setCANCELREQ("Y");

        PayJoaCancelReadyResponse payJoaCancelReadyResponse = readyPayJoa(payJoaCancelReadyRequest);

        PayJoaCancelRequest payJoaCancelRequest = new PayJoaCancelRequest();
        payJoaCancelRequest.setCPID(NOMEMBER_CPID);
        payJoaCancelRequest.setAMOUNT(price);
        payJoaCancelRequest.setTRXID(tranId);
        payJoaCancelRequest.setCANCELREASON("취소테스트");
        PayJoaCancelResponse payJoaCancelResponse = cancelPayJoa(payJoaCancelRequest, payJoaCancelReadyResponse);

        return payJoaCancelResponse.getRESULTCODE();
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
    public Purchase savePurchase(User user, Purchase purchase, boolean isCash) throws ResultCodeException {
        try {

            if (purchase.getUsePoint() == null) {
                purchase.setUsePoint(0);
            }

            if (purchase.getUseCash() == null) {
                purchase.setUseCash(0);
            }

            Member member = memberService.getMemberBySeqNo(user.getNo());
            if (isCash) {
                if (purchase.getUseCash() > 0) {
                    if (purchase.getUseCash() > member.getCash()) {
                        throw new LackCostException();
                    }
                }
            } else {
                if (purchase.getUsePoint() > 0) {
                    if (purchase.getUsePoint() > member.getPoint()) {
                        throw new LackCostException();
                    }
                }
            }

            String dateStr = AppUtil.localDatetimeNowString();
            purchase.setSeqNo(null);
            purchase.setOrderId(StoreUtil.getRandomOrderId());

            if (purchase.getPayMethod().equals("card") || purchase.getPayMethod().equals("easy")) {
                if (purchase.getPg() == null)
                    purchase.setPg("ReapPay");
            }

            purchase.setRegDatetime(dateStr);
            purchase.setModDatetime(dateStr);
            purchase.setMemberSeqNo(user.getNo());
            purchase.setLoginId(user.getLoginId());
            purchase.setNonMember(false);
            purchase.setSalesType(SalesType.SHIPPING.getType());

            String code = null;
            if (purchase.getPurchaseProductSelectList() != null) {
                List<Product> productList = new ArrayList<>();
                List<PurchaseProduct> purchaseProductList = new ArrayList<>();

                Float totalPrice = 0.0f;
                Float totalProductPrice = 0.0f;
                Float totalOptionPrice = 0.0f;
                Float totalReturnPaymentPrice = 0.0f;
                Float totalDeliveryFee = 0f;
                Float totalDeliveryAddFee = 0f;
                Page page = null;
                for (int i = 0; i < purchase.getPurchaseProductSelectList().size(); i++) {


                    PurchaseProduct purchaseProductSelect = purchase.getPurchaseProductSelectList().get(i);

                    PurchaseDelivery purchaseDelivery = purchaseProductSelect.getPurchaseDeliverySelect();
                    if (purchaseDelivery == null) {
                        throw new Exception("[POST]purchaseShip : purchaseDelivery is null ");
                    }
                    List<PurchaseProductOption> purchaseProductOptionList = purchaseProductSelect.getPurchaseProductOptionSelectList();

                    PurchaseProduct purchaseProduct = new PurchaseProduct();
                    purchaseProduct.setProductSeqNo(purchaseProductSelect.getProductSeqNo());
                    purchaseProduct.setCount(purchaseProductSelect.getCount());
                    purchaseProduct.setProductPriceCode(purchaseProductSelect.getProductPriceCode());


                    Integer count = purchaseProduct.getCount();
                    Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
                    purchaseProduct.setSubTitle(product.getSubName());

                    ProductPrice productPrice = productService.getProductPriceByCode(purchaseProduct.getProductPriceCode());
                    purchaseProduct.setProductPriceData(productPrice);

                    if (i == 0) {
                        page = pageRepository.findBySeqNo(productPrice.getPageSeqNo());
                    }

                    productList.add(product);

                    if (product.getBlind() != null && product.getBlind()) {
                        throw new NotPermissionException();
                    }

                    if (product.getCount() >= 0 && (!product.getStatus().equals(GoodsStatus.SELL.getStatus()) || product.getCount() < product.getSoldCount() + count)) {
                        throw new InvalidBuyException("purchaseShip[POST]", "puchaseShip data count is not enough : goods.count,soldCount,buyCount : " + product.getCount() + "," + product.getSoldCount() + "," + count);
                    }

                    purchaseProduct.setRegDatetime(dateStr);
                    purchaseProduct.setPayDatetime(dateStr);
                    purchaseProduct.setMemberSeqNo(purchase.getMemberSeqNo());

                    purchaseProduct.setProductSeqNo(product.getSeqNo());
                    purchaseProduct.setTitle(product.getName());
                    purchaseProduct.setSalesType(product.getSalesType());
                    if (purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol")) {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제
                        purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus());
                    } else {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제 대기
                    }

                    purchaseProduct.setAgentSeqNo(page.getAgentSeqNo());
                    int optionPrice = 0;
                    if (purchaseProductOptionList != null) {
                        for (int j = 0; j < purchaseProductOptionList.size(); j++) {
                            PurchaseProductOption purchaseProductOption = purchaseProductOptionList.get(j);
                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            purchaseProductOption.setPrice(productOptionDetail.getPrice());
                            optionPrice += (purchaseProductOption.getPrice() * purchaseProductOption.getAmount());
                        }
                    }
                    purchaseProduct.setOptionPrice(optionPrice);

                    if (purchaseDelivery.getDeliveryFee() == null) {
                        purchaseDelivery.setDeliveryFee(0f);
                    }

                    if (purchaseDelivery.getDeliveryAddFee1() == null) {
                        purchaseDelivery.setDeliveryAddFee1(0f);
                    }

                    if (purchaseDelivery.getDeliveryAddFee2() == null) {
                        purchaseDelivery.setDeliveryAddFee1(0f);
                    }

                    if (productPrice != null) {
                        if (!productPrice.getStatus().equals(GoodsStatus.SELL.getStatus())) {
                            throw new InvalidBuyException();
                        }

                        if (productPrice.getSupplyPrice() != null) {
                            purchaseProduct.setSupplyPrice(productPrice.getSupplyPrice() * count);
                        }

                        purchaseProduct.setPageSeqNo(productPrice.getPageSeqNo());
                        purchaseProduct.setSupplyPageSeqNo(product.getPageSeqNo());
                        if (purchaseDelivery.getPaymentMethod().equals("before")) {
                            purchaseProduct.setPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice() + purchaseDelivery.getDeliveryFee() + purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2());
                        } else {
                            purchaseProduct.setPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        }

                        purchaseProduct.setProductPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        purchaseProduct.setUnitPrice(purchaseProduct.getPrice());
                        purchaseProduct.setProductDeliverySeqNo(productPrice.getProductDelivery().getSeqNo());

                        Float deliveryFee = purchaseDelivery.getDeliveryFee() + purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2();

                        // 수수료 계산
                        if (product.getMarketType().equals("wholesale")) { // 도매상품일경우

                            Float paymentFee = purchaseProduct.getPrice() * ReapPaymentFeeRatio; // 3.1
                            Float platformFee = purchaseProduct.getPrice() * PlatformFeeRatio; // 3.9

                            purchaseProduct.setPaymentFee(paymentFee);
                            purchaseProduct.setPlatformFee(platformFee);

                            Float sellPrice = purchaseProduct.getProductPrice(); // 판매가격
                            Float supplyPrice = purchaseProduct.getSupplyPrice() + purchaseProduct.getOptionPrice(); // 공급가격
                            Float benefitPrice = sellPrice - supplyPrice; // 수익금


                            Float supplyPricePaymentFee = supplyPrice * ReapPaymentFeeRatio; // 공급가 결제수수료
                            Float benefitPaymentFee = benefitPrice * ReapPaymentFeeRatio; // 수익금 결제수수료
                            Float deliveryFeePaymentFee = deliveryFee * ReapPaymentFeeRatio; // 배송비 결제수수료


                            Float supplyPriceFee = supplyPrice * PlatformFeeRatio; // 공급가 수수료
                            Float benefitFee = benefitPrice * PlatformFeeRatio; // 수익금 수수료
                            Float deliveryFeeFee = deliveryFee * PlatformFeeRatio; // 배송비 수수료

                            purchaseProduct.setSupplyPricePaymentFee(supplyPricePaymentFee);
                            purchaseProduct.setBenefitPaymentFee(benefitPaymentFee);
                            purchaseProduct.setDeliveryFeePaymentFee(deliveryFeePaymentFee);

                            purchaseProduct.setSupplyPriceFee(supplyPriceFee);
                            purchaseProduct.setBenefitFee(benefitFee);
                            purchaseProduct.setDeliveryFeeFee(deliveryFeeFee);

                            Float returnPayment = platformFee + supplyPrice + deliveryFee - supplyPricePaymentFee - deliveryFeePaymentFee - supplyPriceFee - deliveryFeeFee;


                            purchaseProduct.setReturnPaymentPrice(returnPayment);


                        } else { // 소매상품일 경우
                            // 결제수수료 = 결제금액 * 결제수수료비율
                            // 플랫폼수수료 = 결제금액 * 플랫폼수수료비율

                            Float paymentFee = purchaseProduct.getPrice() * ReapPaymentFeeRatio;
                            Float platformFee = purchaseProduct.getPrice() * PlatformFeeRatio;

                            purchaseProduct.setPaymentFee(paymentFee);
                            purchaseProduct.setPlatformFee(platformFee);

                            purchaseProduct.setReturnPaymentPrice(platformFee);

                        }

                        if (productPrice.getRefundBol() != null && productPrice.getRefundBol() > 0) {
                            int savedBol = productPrice.getRefundBol() * count;
                            purchaseProduct.setSavedBol(savedBol);
                        } else {
                            purchaseProduct.setSavedBol(0);
                        }

                        if (productPrice.getRefundPoint() != null && productPrice.getRefundPoint() > 0) {
                            Integer savedPoint = productPrice.getRefundPoint() * count;
                            purchaseProduct.setSavedPoint(savedPoint.floatValue());
                        } else {
                            purchaseProduct.setSavedPoint(0f);
                        }

//                        if (!(purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol"))) {
//
//                            if (productPrice.getIsPoint() != null && productPrice.getIsPoint()) {
//                                Float savedPoint = productPrice.getPoint() * count;
//                                purchaseProduct.setSavedPoint(savedPoint);
//                            } else {
//                                purchaseProduct.setSavedPoint(0f);
//                            }
//
//                        } else {
////                            float point = (purchaseProduct.getPrice()) * 0.01f;
////                            purchaseProduct.setSavedPoint((int) point);
//                            purchaseProduct.setSavedPoint(0f);
//                        }

                        purchaseProduct.setReturnPaymentPrice(purchaseProduct.getReturnPaymentPrice() + purchaseProduct.getSavedPoint());

                        String str = productPrice.getMarketType() == 3 ? "BB" : "RB";
                        code = SetID.getID(str);
                    }

                    if(purchase.getPurchaseType().equals("firstServed")){
                        purchaseProduct.setFirstServedSeqNo(purchase.getFirstServedSeqNo());
                    }

                    purchaseProduct.setPurchaseProductOptionSelectList(purchaseProductOptionList);
                    purchaseProduct.setPurchaseDeliverySelect(purchaseDelivery);
                    purchaseProductList.add(purchaseProduct);
                    totalPrice += purchaseProduct.getPrice();
                    totalProductPrice += purchaseProduct.getProductPrice();
                    totalOptionPrice += purchaseProduct.getOptionPrice();
                    totalDeliveryFee += purchaseDelivery.getDeliveryFee();
                    totalDeliveryAddFee += (purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2());
                    totalReturnPaymentPrice += purchaseProduct.getReturnPaymentPrice();
                }

                purchase.setReturnPaymentPrice(totalReturnPaymentPrice);
                purchase.setDeliveryFee(totalDeliveryFee);
                purchase.setDeliveryAddFee(totalDeliveryAddFee);
                purchase.setPageSeqNo(purchaseProductList.get(0).getPageSeqNo());
                purchase.setPrice(totalPrice);
                purchase.setProductPrice(totalProductPrice);
                purchase.setOptionPrice(totalOptionPrice);

                if (purchase.getMemberLuckyCouponSeqNo() != null) {
                    MemberLuckyCoupon memberLuckyCoupon = luckyCouponService.getMemberLuckyCoupon(purchase.getMemberLuckyCouponSeqNo());
                    if (memberLuckyCoupon.getStatus() != 1) {
                        throw new InvalidBuyException();
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date validDate = dateFormat.parse(memberLuckyCoupon.getValidDatetime());
                    if (validDate.getTime() < System.currentTimeMillis()) {
                        throw new InvalidBuyException();
                    }

                    LuckyCoupon luckyCoupon = memberLuckyCoupon.getLuckyCoupon();

                    boolean useEnable = false;

                    PurchaseProduct couponPurchaseProduct = null;

                    if (luckyCoupon.getUseTarget().equals("all")) {
                        useEnable = true;
                    } else {
                        for (LuckyCouponItem luckyCouponItem : luckyCoupon.getLuckyCouponItemList()) {
                            for (PurchaseProduct purchaseProduct : purchaseProductList) {
                                if (luckyCouponItem.getProductCode().equals(purchaseProduct.getProductPriceCode())) {
                                    couponPurchaseProduct = purchaseProduct;
                                    useEnable = true;
                                    break;
                                }
                            }
                            if(useEnable){
                                break;
                            }
                        }
                    }


                    if (!useEnable) {
                        throw new InvalidBuyException();
                    }

                    if (luckyCoupon.getType().equals("discount")) {
                        purchase.setCouponPrice(luckyCoupon.getDiscountPrice());
                    } else if (luckyCoupon.getType().equals("exchange")) {

                        if(couponPurchaseProduct != null){
                            purchase.setCouponPrice(couponPurchaseProduct.getProductPriceData().getPrice().intValue());
                        }
                    }

                } else {
                    purchase.setCouponPrice(0);
                }

                if (totalPrice < purchase.getCouponPrice()) {
                    purchase.setCouponPrice(totalPrice.intValue());
                }

                if (purchase.getPrice().intValue() == purchase.getCouponPrice()) {
                    purchase.setUseCash(0);
                    purchase.setPgPrice(0);
                    purchase.setPayMethod("coupon");
                    purchase.setPg("coupon");
                    purchase.setStatus(PurchaseStatus.PAY.getStatus());
                } else {
                    Integer payPrice = totalPrice.intValue() - purchase.getCouponPrice();

                    if (isCash) {

                        if (payPrice < purchase.getUseCash()) {
                            purchase.setUseCash(payPrice);
                        }

                        if (purchase.getUseCash() > 0) {
                            purchase.setPgPrice(payPrice - purchase.getUseCash());
                        } else {
                            purchase.setPgPrice(payPrice);
                            purchase.setUseCash(0);
                        }

                        if (payPrice.equals(purchase.getUseCash())) {
                            purchase.setPayMethod("cash");
                            purchase.setPg("cash");

                            purchase.setStatus(PurchaseStatus.PAY.getStatus());
                        } else {
                            purchase.setStatus(PurchaseStatus.PAY_REQ.getStatus());
                        }
                    } else {
                        if (payPrice < purchase.getUsePoint()) {
                            purchase.setUsePoint(payPrice);
                        }

                        if (purchase.getUsePoint() > 0) {
                            purchase.setPgPrice(payPrice - purchase.getUsePoint());
                        } else {
                            purchase.setPgPrice(payPrice);
                            purchase.setUsePoint(0);
                        }

                        if (payPrice.equals(purchase.getUsePoint())) {
                            purchase.setPayMethod("point");
                            purchase.setPg("point");

                            purchase.setStatus(PurchaseStatus.PAY.getStatus());
                        } else {
                            purchase.setStatus(PurchaseStatus.PAY_REQ.getStatus());
                        }
                    }
                }

                purchase.setAgentSeqNo(page.getAgentSeqNo());

                purchase.setCode(code);
                purchase.setPayType("reappay");

                if(AppUtil.isEmpty(purchase.getPurchaseType())){
                    purchase.setPurchaseType("store");
                }

                purchase = purchaseRepository.saveAndFlush(purchase);
                if (purchase == null || purchase.getSeqNo() == null) {
                    throw new Exception("purchase data insert error !!!");
                }

                int i = 0;
                List<PurchaseProductOption> purchaseProductOptionList = null;
                PurchaseDelivery purchaseDelivery = null;
                for (PurchaseProduct purchaseProduct : purchaseProductList) {

                    purchaseProductOptionList = purchaseProduct.getPurchaseProductOptionSelectList();
                    purchaseDelivery = purchaseProduct.getPurchaseDeliverySelect();

                    purchaseProduct.setSeqNo(null);
                    purchaseProduct.setPurchaseSeqNo(purchase.getSeqNo());
                    purchaseProduct.setCode(code + String.format("%04d", i + 1));
                    purchaseProduct.setIsStatusCompleted(false);
                    purchaseProduct.setIsPaymentPoint(false);
                    purchaseProduct.setIsPaymentBol(false);

                    if (!AppUtil.isEmpty(member.getRecommendationCode())) {
                        Member recommenderMember = memberRepository.findByRecommendUniqueKey(member.getRecommendationCode());
                        if (recommenderMember != null) {
                            purchaseProduct.setRecommendedMemberSeqNo(recommenderMember.getSeqNo());
                            if (recommenderMember.getAppType().equals("biz")) {
                                purchaseProduct.setRecommendedMemberType("page");
                            } else {
                                purchaseProduct.setRecommendedMemberType("user");
                            }
                        }
                    }

                    try {
                        purchaseProduct = purchaseProductRepository.saveAndFlush(purchaseProduct);
                    } catch (Exception e) {
                        throw new InvalidBuyException("/purchase[POST]", "The purchaseProduct save error : " + e.getMessage() + ":" + purchaseProduct.toString());
                    }
                    if (purchaseProduct == null || purchaseProduct.getSeqNo() == null) {
                        throw new InvalidBuyException("/purchase[POST]", "The purchaseProduct insert error : " + purchaseProduct.toString());
                    }

                    if (purchaseProductOptionList != null) {
                        for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {
                            purchaseProductOption.setSeqNo(null);
                            purchaseProductOption.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                            purchaseProductOption.setPurchaseSeqNo(purchase.getSeqNo());

                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            if (productOptionDetail.getItem1() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem1().getOptionSeqNo());
                                purchaseProductOption.setDepth1(productOption.getName() + " : " + productOptionDetail.getItem1().getItem());
                            }

                            if (productOptionDetail.getItem2() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem2().getOptionSeqNo());
                                purchaseProductOption.setDepth2(productOption.getName() + " : " + productOptionDetail.getItem2().getItem());
                            }

                            purchaseProductOption = purchaseProductOptonRepository.saveAndFlush(purchaseProductOption);
                        }
                    }


                    if (purchaseDelivery != null) {
                        purchaseDelivery.setSeqNo(null);
                        purchaseDelivery.setPurchaseSeqNo(purchase.getSeqNo());
                        purchaseDelivery.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                        purchaseDelivery = purchaseDeliveryRepository.saveAndFlush(purchaseDelivery);

                        purchaseProductRepository.updatePurchaseDeliverySeqNoBySeqNo(purchaseProduct.getSeqNo(), purchaseDelivery.getSeqNo());
                    }

                    purchaseProductList.set(i, purchaseProduct);

                    i++;
                }
            }

            if (purchase.getPayMethod().equals("cash") || purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("coupon")) {
                purchasePoint(purchase, member, user, dateStr);
            }

        } catch (InvalidBuyException e) {
            logger.error(AppUtil.excetionToString(e));
            throw e;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new SqlException("[POST]/purchase/ship", e);
        }
        return purchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Purchase savePurchaseWithLuckyBol(User user, Purchase purchase) throws ResultCodeException {
        try {

            if (purchase.getUsePoint() == null) {
                purchase.setUsePoint(0);
            }

            if (purchase.getUseCash() == null) {
                purchase.setUseCash(0);
            }

            Member member = memberService.getMemberBySeqNo(user.getNo());
            if (purchase.getUsePoint() > 0) {
                if (purchase.getUsePoint() > member.getPoint()) {
                    throw new LackCostException();
                }
            }

            String dateStr = AppUtil.localDatetimeNowString();
            purchase.setSeqNo(null);
            purchase.setOrderId(StoreUtil.getRandomOrderId());

            if (purchase.getPayMethod().equals("card") || purchase.getPayMethod().equals("easy")) {
                if (purchase.getPg() == null)
                    purchase.setPg("ReapPay");
            }

            purchase.setRegDatetime(dateStr);
            purchase.setModDatetime(dateStr);
            purchase.setMemberSeqNo(user.getNo());
            purchase.setLoginId(user.getLoginId());
            purchase.setNonMember(false);
            purchase.setSalesType(SalesType.SHIPPING.getType());

            String code = null;
            if (purchase.getPurchaseProductSelectList() != null) {
                List<Product> productList = new ArrayList<>();
                List<PurchaseProduct> purchaseProductList = new ArrayList<>();

                Float totalPrice = 0.0f;
                Float totalProductPrice = 0.0f;
                Float totalOptionPrice = 0.0f;
                Float totalReturnPaymentPrice = 0.0f;
                Float totalDeliveryFee = 0f;
                Float totalDeliveryAddFee = 0f;
                Page page = null;
                for (int i = 0; i < purchase.getPurchaseProductSelectList().size(); i++) {


                    PurchaseProduct purchaseProductSelect = purchase.getPurchaseProductSelectList().get(i);

                    PurchaseDelivery purchaseDelivery = purchaseProductSelect.getPurchaseDeliverySelect();
                    if (purchaseDelivery == null) {
                        throw new Exception("[POST]purchaseShip : purchaseDelivery is null ");
                    }
                    List<PurchaseProductOption> purchaseProductOptionList = purchaseProductSelect.getPurchaseProductOptionSelectList();

                    PurchaseProduct purchaseProduct = new PurchaseProduct();
                    purchaseProduct.setProductSeqNo(purchaseProductSelect.getProductSeqNo());
                    purchaseProduct.setCount(purchaseProductSelect.getCount());
                    purchaseProduct.setProductPriceCode(purchaseProductSelect.getProductPriceCode());
                    purchaseProduct.setLuckyBolPurchaseSeqNo(purchase.getLuckyBolPurchaseSeqNo());
                    purchaseProduct.setLuckyBolSeqNo(purchase.getLuckyBolSeqNo());
                    purchaseProduct.setLuckyBolSelectType("member");


                    Integer count = purchaseProduct.getCount();
                    Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
                    purchaseProduct.setSubTitle(product.getSubName());

                    ProductPrice productPrice = productService.getProductPriceByCode(purchaseProduct.getProductPriceCode());

                    if (i == 0) {
                        page = pageRepository.findBySeqNo(productPrice.getPageSeqNo());
                    }

                    productList.add(product);

                    if (product.getBlind() != null && product.getBlind()) {
                        throw new NotPermissionException();
                    }

                    if (product.getCount() >= 0 && (!product.getStatus().equals(GoodsStatus.SELL.getStatus()) || product.getCount() < product.getSoldCount() + count)) {
                        throw new InvalidBuyException("purchaseShip[POST]", "puchaseShip data count is not enough : goods.count,soldCount,buyCount : " + product.getCount() + "," + product.getSoldCount() + "," + count);
                    }

                    purchaseProduct.setRegDatetime(dateStr);
                    purchaseProduct.setPayDatetime(dateStr);
                    purchaseProduct.setMemberSeqNo(purchase.getMemberSeqNo());

                    purchaseProduct.setProductSeqNo(product.getSeqNo());
                    purchaseProduct.setTitle(product.getName());
                    purchaseProduct.setSalesType(product.getSalesType());
                    if (purchase.getPayMethod().equals("point")) {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제
                        purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus());
                    } else {
                        purchaseProduct.setStatus(PurchaseProductStatus.PAY_REQ.getStatus()); //결제 대기
                    }

                    purchaseProduct.setAgentSeqNo(page.getAgentSeqNo());
                    int optionPrice = 0;
                    if (purchaseProductOptionList != null) {
                        for (int j = 0; j < purchaseProductOptionList.size(); j++) {
                            PurchaseProductOption purchaseProductOption = purchaseProductOptionList.get(j);
                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            purchaseProductOption.setPrice(productOptionDetail.getPrice());
                            optionPrice += (purchaseProductOption.getPrice() * purchaseProductOption.getAmount());
                        }
                    }
                    purchaseProduct.setOptionPrice(optionPrice);

                    purchaseDelivery.setDeliveryFee(0f);
                    purchaseDelivery.setDeliveryAddFee1(0f);
                    purchaseDelivery.setDeliveryAddFee1(0f);

                    if (productPrice != null) {
                        if (!productPrice.getStatus().equals(GoodsStatus.SELL.getStatus())) {
                            throw new InvalidBuyException();
                        }

                        if (productPrice.getSupplyPrice() != null) {
                            purchaseProduct.setSupplyPrice(productPrice.getSupplyPrice() * count);
                        }

                        purchaseProduct.setPageSeqNo(productPrice.getPageSeqNo());
                        purchaseProduct.setSupplyPageSeqNo(product.getPageSeqNo());
                        purchaseProduct.setPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());

                        purchaseProduct.setProductPrice((productPrice.getPrice() * count) + purchaseProduct.getOptionPrice());
                        purchaseProduct.setUnitPrice(purchaseProduct.getPrice());
                        purchaseProduct.setProductDeliverySeqNo(productPrice.getProductDelivery().getSeqNo());

                        Float deliveryFee = 0f;

                        // 수수료 계산
                        if (product.getMarketType().equals("wholesale")) { // 도매상품일경우

                            Float paymentFee = purchaseProduct.getPrice() * ReapPaymentFeeRatio; // 3.1
                            Float platformFee = purchaseProduct.getPrice() * PlatformFeeRatio; // 3.9

                            purchaseProduct.setPaymentFee(paymentFee);
                            purchaseProduct.setPlatformFee(platformFee);

                            Float sellPrice = purchaseProduct.getProductPrice(); // 판매가격
                            Float supplyPrice = purchaseProduct.getSupplyPrice() + purchaseProduct.getOptionPrice(); // 공급가격
                            Float benefitPrice = sellPrice - supplyPrice; // 수익금


                            Float supplyPricePaymentFee = supplyPrice * ReapPaymentFeeRatio; // 공급가 결제수수료
                            Float benefitPaymentFee = benefitPrice * ReapPaymentFeeRatio; // 수익금 결제수수료
                            Float deliveryFeePaymentFee = deliveryFee * ReapPaymentFeeRatio; // 배송비 결제수수료


                            Float supplyPriceFee = supplyPrice * PlatformFeeRatio; // 공급가 수수료
                            Float benefitFee = benefitPrice * PlatformFeeRatio; // 수익금 수수료
                            Float deliveryFeeFee = deliveryFee * PlatformFeeRatio; // 배송비 수수료

                            purchaseProduct.setSupplyPricePaymentFee(supplyPricePaymentFee);
                            purchaseProduct.setBenefitPaymentFee(benefitPaymentFee);
                            purchaseProduct.setDeliveryFeePaymentFee(deliveryFeePaymentFee);

                            purchaseProduct.setSupplyPriceFee(supplyPriceFee);
                            purchaseProduct.setBenefitFee(benefitFee);
                            purchaseProduct.setDeliveryFeeFee(deliveryFeeFee);

                            Float returnPayment = platformFee + supplyPrice + deliveryFee - supplyPricePaymentFee - deliveryFeePaymentFee - supplyPriceFee - deliveryFeeFee;


                            purchaseProduct.setReturnPaymentPrice(returnPayment);


                        } else { // 소매상품일 경우
                            // 결제수수료 = 결제금액 * 결제수수료비율
                            // 플랫폼수수료 = 결제금액 * 플랫폼수수료비율

                            Float paymentFee = purchaseProduct.getPrice() * ReapPaymentFeeRatio;
                            Float platformFee = purchaseProduct.getPrice() * PlatformFeeRatio;

                            purchaseProduct.setPaymentFee(paymentFee);
                            purchaseProduct.setPlatformFee(platformFee);

                            purchaseProduct.setReturnPaymentPrice(platformFee);

                        }

                        if (productPrice.getRefundBol() != null && productPrice.getRefundBol() > 0) {
                            int savedBol = productPrice.getRefundBol() * count;
                            purchaseProduct.setSavedBol(savedBol);
                        } else {
                            purchaseProduct.setSavedBol(0);
                        }

                        if (productPrice.getRefundPoint() != null && productPrice.getRefundPoint() > 0) {
                            Integer savedPoint = productPrice.getRefundPoint() * count;
                            purchaseProduct.setSavedPoint(savedPoint.floatValue());
                        } else {
                            purchaseProduct.setSavedPoint(0f);
                        }

//                        if (!(purchase.getPayMethod().equals("point") || purchase.getPayMethod().equals("bol"))) {
//
//                            if (productPrice.getIsPoint() != null && productPrice.getIsPoint()) {
//                                Float savedPoint = productPrice.getPoint() * count;
//                                purchaseProduct.setSavedPoint(savedPoint);
//                            } else {
//                                purchaseProduct.setSavedPoint(0f);
//                            }
//
//                        } else {
////                            float point = (purchaseProduct.getPrice()) * 0.01f;
////                            purchaseProduct.setSavedPoint((int) point);
//                            purchaseProduct.setSavedPoint(0f);
//                        }

                        purchaseProduct.setReturnPaymentPrice(purchaseProduct.getReturnPaymentPrice() + purchaseProduct.getSavedPoint());

                        String str = productPrice.getMarketType() == 3 ? "BB" : "RB";
                        code = SetID.getID(str);
                    }

                    purchaseProduct.setPurchaseProductOptionSelectList(purchaseProductOptionList);
                    purchaseProduct.setPurchaseDeliverySelect(purchaseDelivery);
                    purchaseProductList.add(purchaseProduct);
                    totalPrice += purchaseProduct.getPrice();
                    totalProductPrice += purchaseProduct.getProductPrice();
                    totalOptionPrice += purchaseProduct.getOptionPrice();
                    totalDeliveryFee += purchaseDelivery.getDeliveryFee();
                    totalDeliveryAddFee += (purchaseDelivery.getDeliveryAddFee1() + purchaseDelivery.getDeliveryAddFee2());
                    totalReturnPaymentPrice += purchaseProduct.getReturnPaymentPrice();
                }

                purchase.setReturnPaymentPrice(totalReturnPaymentPrice);
                purchase.setDeliveryFee(totalDeliveryFee);
                purchase.setDeliveryAddFee(totalDeliveryAddFee);
                purchase.setPageSeqNo(purchaseProductList.get(0).getPageSeqNo());
                purchase.setPrice(totalPrice);
                purchase.setProductPrice(totalProductPrice);
                purchase.setOptionPrice(totalOptionPrice);

                purchase.setCouponPrice(0);

                if (totalOptionPrice > 0) {
                    if (totalOptionPrice < purchase.getUsePoint()) {
                        purchase.setUsePoint(totalOptionPrice.intValue());
                    }

                    if (purchase.getUsePoint() > 0) {
                        purchase.setPgPrice(totalOptionPrice.intValue() - purchase.getUsePoint());
                    } else {
                        purchase.setPgPrice(totalOptionPrice.intValue());
                        purchase.setUsePoint(0);
                    }

                    if (totalOptionPrice.intValue() == purchase.getUsePoint()) {
                        purchase.setPayMethod("point");
                        purchase.setPg("point");
                        purchase.setStatus(PurchaseStatus.PAY.getStatus());
                    } else {
                        purchase.setStatus(PurchaseStatus.PAY_REQ.getStatus());
                    }
                } else {
                    purchase.setUsePoint(0);
                    purchase.setPgPrice(0);
                    purchase.setPayMethod("free");
                    purchase.setPg("free");
                    purchase.setStatus(PurchaseStatus.PAY.getStatus());
                }


                purchase.setAgentSeqNo(page.getAgentSeqNo());

                purchase.setCode(code);
                purchase.setPayType("reappay");
                purchase.setPurchaseType("luckyBol");
                purchase = purchaseRepository.saveAndFlush(purchase);
                if (purchase == null || purchase.getSeqNo() == null) {
                    throw new Exception("purchase data insert error !!!");
                }

                int i = 0;
                List<PurchaseProductOption> purchaseProductOptionList = null;
                PurchaseDelivery purchaseDelivery = null;
                for (PurchaseProduct purchaseProduct : purchaseProductList) {

                    purchaseProductOptionList = purchaseProduct.getPurchaseProductOptionSelectList();
                    purchaseDelivery = purchaseProduct.getPurchaseDeliverySelect();

                    purchaseProduct.setSeqNo(null);
                    purchaseProduct.setPurchaseSeqNo(purchase.getSeqNo());
                    purchaseProduct.setCode(code + String.format("%04d", i + 1));
                    purchaseProduct.setIsStatusCompleted(false);
                    purchaseProduct.setIsPaymentPoint(false);
                    purchaseProduct.setIsPaymentBol(false);

                    if (!AppUtil.isEmpty(member.getRecommendationCode())) {
                        Member recommenderMember = memberRepository.findByRecommendUniqueKey(member.getRecommendationCode());
                        if (recommenderMember != null) {
                            purchaseProduct.setRecommendedMemberSeqNo(recommenderMember.getSeqNo());
                            if (recommenderMember.getAppType().equals("biz")) {
                                purchaseProduct.setRecommendedMemberType("page");
                            } else {
                                purchaseProduct.setRecommendedMemberType("user");
                            }
                        }
                    }

                    try {
                        purchaseProduct = purchaseProductRepository.saveAndFlush(purchaseProduct);
                    } catch (Exception e) {
                        throw new InvalidBuyException("/purchase[POST]", "The purchaseProduct save error : " + e.getMessage() + ":" + purchaseProduct.toString());
                    }
                    if (purchaseProduct == null || purchaseProduct.getSeqNo() == null) {
                        throw new InvalidBuyException("/purchase[POST]", "The purchaseProduct insert error : " + purchaseProduct.toString());
                    }

                    if (purchaseProductOptionList != null) {
                        for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {
                            purchaseProductOption.setSeqNo(null);
                            purchaseProductOption.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                            purchaseProductOption.setPurchaseSeqNo(purchase.getSeqNo());

                            ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                            if (productOptionDetail.getItem1() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem1().getOptionSeqNo());
                                purchaseProductOption.setDepth1(productOption.getName() + " : " + productOptionDetail.getItem1().getItem());
                            }

                            if (productOptionDetail.getItem2() != null) {
                                ProductOption productOption = productService.getProductOptionBySeqNo(productOptionDetail.getItem2().getOptionSeqNo());
                                purchaseProductOption.setDepth2(productOption.getName() + " : " + productOptionDetail.getItem2().getItem());
                            }

                            purchaseProductOption = purchaseProductOptonRepository.saveAndFlush(purchaseProductOption);
                        }
                    }


                    if (purchaseDelivery != null) {
                        purchaseDelivery.setSeqNo(null);
                        purchaseDelivery.setPurchaseSeqNo(purchase.getSeqNo());
                        purchaseDelivery.setPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                        purchaseDelivery = purchaseDeliveryRepository.saveAndFlush(purchaseDelivery);

                        purchaseProductRepository.updatePurchaseDeliverySeqNoBySeqNo(purchaseProduct.getSeqNo(), purchaseDelivery.getSeqNo());
                    }

                    purchaseProductList.set(i, purchaseProduct);

                    i++;
                }
            }

            if (purchase.getPayMethod().equals("free") || purchase.getPayMethod().equals("point")) {
                purchasePoint(purchase, member, user, dateStr);
            }

        } catch (InvalidBuyException e) {
            logger.error(AppUtil.excetionToString(e));
            throw e;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new SqlException("[POST]/purchase/ship", e);
        }
        return purchase;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentPurchase(HttpServletRequest request) throws ResultCodeException {
        String resultStatus = request.getParameter("resultStatus");//승인결과(0000: 성공, 나머지: 실패)
        if (resultStatus.equals("0000")) {
            String orderSeq = request.getParameter("orderSeq");
            String tranSeq = request.getParameter("tranSeq");
            String tranCate = request.getParameter("tranCate");
            String cardNum = request.getParameter("cardNum");
            String appDt = request.getParameter("appDt");
            String appTm = request.getParameter("appTm");
            String appNo = request.getParameter("appNo");
            String totAmt = request.getParameter("totAmt");
            String supplyAmt = request.getParameter("supplyAmt");
            String vatAmt = request.getParameter("vatAmt");
            String tranDt = request.getParameter("tranDt");//yyyyMMdd
            String tranTm = request.getParameter("tranTm");//HHmmss
            String installment = request.getParameter("installment");
            String issCd = request.getParameter("issCd");//카드사코드
            String issNm = request.getParameter("issNm");//카드사이름
            String cnclReason = request.getParameter("cnclReason");
            String orgTranSeq = request.getParameter("orgTranSeq");

            Purchase purchase = purchaseRepository.findByOrderId(orderSeq);
            purchase.setStatus(PurchaseStatus.PAY.getStatus()); // 결제완료

            String paymentDate = appDt + " " + appTm;
            purchase.setModDatetime(paymentDate);
            purchase.setPayResponseApprovalNo(appNo);
            purchase.setPayResponseCardId(issCd);
            purchase.setPayResponseCardNm(issNm);
            purchase.setPayResponseCardNo(cardNum);
            purchase.setPayResponseCertYn(true);
            purchase.setPayResponseCode(resultStatus);
            purchase.setPayResponseInstallment(installment);
            purchase.setPayResponseOrderNo(orderSeq);
            purchase.setPayResponsePayDate(appDt);
            purchase.setPayResponsePayTime(appTm);
            purchase.setPayResponsePayType("card");
            purchase.setPayResponseProductType("R");
            purchase.setPayResponseSellMm(installment);
            if (storeType.equals("PROD")) {
                purchase.setPayResponseTestYn(false);
            } else {
                purchase.setPayResponseTestYn(true);
            }

            purchase.setPayResponseTranSeq(tranSeq);
            purchase.setPayResponseZerofeeYn(false);

            purchase.setPgTranId(tranSeq);
            purchase.setApprNo(appNo);

            List<PurchaseProduct> purchaseProductList = getPurchaseProductList(purchase.getSeqNo());

            if (purchaseProductList != null) {

                for (int i = 0; i < purchaseProductList.size(); i++) {
                    PurchaseProduct purchaseProduct = purchaseProductList.get(i);
                    if (purchaseProduct.getStatus() < PurchaseProductStatus.PAY.getStatus()) {

                        try {
                            Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
//                            if (storeType.equals("PROD")) {
//                                try {
//                                    if (product.getSupplierSeqNo().equals(2L)) {//도매매 상품일때 처리
//                                        Long domemeOrderNo = domaemaeOrder(purchaseProduct.getSeqNo());
//                                        if (domemeOrderNo == null) {
//                                            throw new InvalidBuyException("domaemae fail");
//                                        } else {
//                                            purchaseProduct.setDomemeOrderNo(domemeOrderNo);
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                    logger.error("domeme fail : " + e.toString());
//                                }
//
//                            }

                            purchaseProduct.setStatus(PurchaseProductStatus.PAY.getStatus()); // 결제승인
                            purchaseProduct.setPayDatetime(paymentDate);
                            purchaseProduct.setChangeStatusDatetime(paymentDate);

                            if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                                purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus()); // 주문확인전
                            } else if (purchase.getSalesType() == SalesType.TICKET.getType()) {
                                purchaseProduct.setReserveStatus(ReserveStatus.BOOKING.getStatus());
                            }

                            product.setSoldCount(product.getSoldCount() + purchaseProduct.getCount());

                            ProductPriceOnly productPriceOnly = productService.getProductPriceOnlyByCode(purchaseProduct.getProductPriceCode());
                            productPriceOnly.setDailySoldCount(productPriceOnly.getDailySoldCount() + purchaseProduct.getCount());
                            if (product.getCount() <= product.getSoldCount()) {
                                product.setStatus(GoodsStatus.SOLD_OUT.getStatus()); //판매종료
                                productPriceOnly.setStatus(GoodsStatus.SOLD_OUT.getStatus());
                            }
                            productService.saveProductPriceOnly(productPriceOnly);

                            product.setModDatetime(paymentDate);

                            if (product.getStatus().equals(GoodsStatus.SOLD_OUT.getStatus())) {
                                productService.deleteLikeAllByProductSeqNo(product.getSeqNo());
                            }

                            productService.save(product);

                            List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                            if (purchaseProductOptionList != null) {
                                for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {

                                    ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                                    productOptionDetail.setSoldCount(productOptionDetail.getSoldCount() + purchaseProductOption.getAmount());
                                    productService.saveProductOptionDetailRepository(productOptionDetail);
                                }
                            }

                            purchaseProductRepository.save(purchaseProduct);
                        } catch (Exception e) {
                            throw new InvalidBuyException(e);
                        }
                    }
                }
            }

            if (purchase.getMemberLuckyCouponSeqNo() != null) {
                luckyCouponService.useCoupon(purchase.getMemberLuckyCouponSeqNo());
            }

            if (purchase.getUseCash() != null && purchase.getUseCash() > 0) {
                CashHistory cashHistory = new CashHistory();
                cashHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                cashHistory.setType("used");
                cashHistory.setSecondaryType("member");
                cashHistory.setCash(purchase.getUseCash().floatValue());
                cashHistory.setSubject(purchase.getTitle() + " 구매");
                cashService.updateCash(purchase.getMemberSeqNo(), cashHistory);
            }

            if (purchase.getUsePoint() != null && purchase.getUsePoint() > 0) {

                PointHistory pointHistory = new PointHistory();
                pointHistory.setMemberSeqNo(purchase.getMemberSeqNo());
                pointHistory.setType("used");
                pointHistory.setPoint(purchase.getUsePoint().floatValue());
                pointHistory.setSubject(purchase.getTitle() + " 구매");
                pointService.updatePoint(purchase.getMemberSeqNo(), pointHistory);

            }

            purchase = purchaseRepository.save(purchase);

            if (purchase.getPurchaseType().equals("luckyBol") && purchase.getLuckyBolPurchaseSeqNo() != null) {
                luckyBolService.updatePurchaseSeqNo(purchase);
            }

        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void paymentBillkeyPurchase(Long seqNo, String token, String installment) throws ResultCodeException {

        Purchase purchase = purchaseRepository.findBySeqNo(seqNo);

        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeypay(purchase, null, null, null, null, token, installment);

        if (reapPayBillKeyData == null) {
            throw new InvalidBuyException();
        }

        String paymentDate = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(reapPayBillKeyData.getBillkeytradeDateTime());

            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            logger.error(e.toString());
            paymentDate = AppUtil.localDatetimeNowString();
        }

        purchase.setStatus(PurchaseStatus.PAY.getStatus()); // 결제완료
        purchase.setPayResponseApprovalNo(reapPayBillKeyData.getBillkeyapprovalNumb());
        purchase.setPayResponseCardId(reapPayBillKeyData.getBillkeypurchaseCardType());
        purchase.setPayResponseCardNm(reapPayBillKeyData.getBillkeyissuerCardName());
        purchase.setPayResponseCardNo(reapPayBillKeyData.getBillkeymaskedCardNumb());
        purchase.setPayResponseCertYn(true);
        purchase.setPayResponseCode(reapPayBillKeyData.getBillkeyrespCode());
        purchase.setPayResponseInstallment(installment);
        purchase.setPayResponseOrderNo(purchase.getOrderId());
        purchase.setPayResponsePayDate(paymentDate.split(" ")[0]);
        purchase.setPayResponsePayTime(paymentDate.split(" ")[1]);
        purchase.setPayResponsePayType("easy");
        purchase.setPayResponseProductType("R");
        purchase.setPayResponseSellMm(installment);
        if (storeType.equals("PROD")) {
            purchase.setPayResponseTestYn(false);
        } else {
            purchase.setPayResponseTestYn(true);
        }

        purchase.setPayResponseTranSeq(reapPayBillKeyData.getBillkeyTranseq());
        purchase.setPayResponseZerofeeYn(false);

        purchase.setModDatetime(paymentDate);
        purchase.setPgTranId(purchase.getPayResponseTranSeq());
        purchase.setApprNo(purchase.getPayResponseApprovalNo());

        List<PurchaseProduct> purchaseProductList = getPurchaseProductList(purchase.getSeqNo());

        if (purchaseProductList != null) {

            for (int i = 0; i < purchaseProductList.size(); i++) {
                PurchaseProduct purchaseProduct = purchaseProductList.get(i);
                if (purchaseProduct.getStatus() < PurchaseProductStatus.PAY.getStatus()) {

                    try {
                        Product product = productService.getProduct(purchaseProduct.getProductSeqNo());
//                        if (storeType.equals("PROD")) {
//                            try {
//                                if (product.getSupplierSeqNo().equals(2L)) {//도매매 상품일때 처리
//                                    Long domemeOrderNo = domaemaeOrder(purchaseProduct.getSeqNo());
//                                    if (domemeOrderNo == null) {
//                                        throw new InvalidBuyException("domaemae fail");
//                                    } else {
//                                        purchaseProduct.setDomemeOrderNo(domemeOrderNo);
//                                    }
//                                }
//                            } catch (Exception e) {
//                                logger.error("domeme fail : " + e.toString());
//                            }
//
//                        }

                        purchaseProduct.setStatus(PurchaseProductStatus.PAY.getStatus()); // 결제승인
                        purchaseProduct.setPayDatetime(paymentDate);
                        purchaseProduct.setChangeStatusDatetime(paymentDate);

                        if (purchase.getSalesType() == SalesType.SHIPPING.getType()) {
                            purchaseProduct.setDeliveryStatus(DeliveryStatus.BEFORE_READY.getStatus()); // 주문확인전
                        } else if (purchase.getSalesType() == SalesType.TICKET.getType()) {
                            purchaseProduct.setReserveStatus(ReserveStatus.BOOKING.getStatus());
                        }

                        product.setSoldCount(product.getSoldCount() + purchaseProduct.getCount());

                        ProductPriceOnly productPriceOnly = productService.getProductPriceOnlyByCode(purchaseProduct.getProductPriceCode());
                        productPriceOnly.setDailySoldCount(productPriceOnly.getDailySoldCount() + purchaseProduct.getCount());
                        if (product.getCount() <= product.getSoldCount()) {
                            product.setStatus(GoodsStatus.SOLD_OUT.getStatus()); //판매종료
                            productPriceOnly.setStatus(GoodsStatus.SOLD_OUT.getStatus());
                        }
                        productService.saveProductPriceOnly(productPriceOnly);

                        product.setModDatetime(paymentDate);

                        if (product.getStatus().equals(GoodsStatus.SOLD_OUT.getStatus())) {
                            productService.deleteLikeAllByProductSeqNo(product.getSeqNo());
                        }

                        productService.save(product);

                        List<PurchaseProductOption> purchaseProductOptionList = purchaseProductOptonRepository.findAllByPurchaseProductSeqNo(purchaseProduct.getSeqNo());
                        if (purchaseProductOptionList != null) {
                            for (PurchaseProductOption purchaseProductOption : purchaseProductOptionList) {

                                ProductOptionDetail productOptionDetail = productService.getProductOptionDetailBySeqNo(purchaseProductOption.getProductOptionDetailSeqNo());
                                productOptionDetail.setSoldCount(productOptionDetail.getSoldCount() + purchaseProductOption.getAmount());
                                productService.saveProductOptionDetailRepository(productOptionDetail);
                            }
                        }

                        purchaseProductRepository.save(purchaseProduct);
                    } catch (Exception e) {
                        throw new InvalidBuyException(e);
                    }
                }
            }
        }

        if (purchase.getMemberLuckyCouponSeqNo() != null) {
            luckyCouponService.useCoupon(purchase.getMemberLuckyCouponSeqNo());
        }

        if (purchase.getUseCash() != null && purchase.getUseCash() > 0) {
            CashHistory cashHistory = new CashHistory();
            cashHistory.setMemberSeqNo(purchase.getMemberSeqNo());
            cashHistory.setType("used");
            cashHistory.setSecondaryType("member");
            cashHistory.setCash(purchase.getUseCash().floatValue());
            cashHistory.setSubject(purchase.getTitle() + " 구매");
            cashService.updateCash(purchase.getMemberSeqNo(), cashHistory);
        }

        if (purchase.getUsePoint() != null && purchase.getUsePoint() > 0) {

            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(purchase.getMemberSeqNo());
            pointHistory.setType("used");
            pointHistory.setPoint(purchase.getUsePoint().floatValue());
            pointHistory.setSubject(purchase.getTitle() + " 구매");
            pointService.updatePoint(purchase.getMemberSeqNo(), pointHistory);

        }

        purchase = purchaseRepository.save(purchase);

        if (purchase.getPurchaseType().equals("luckyBol") && purchase.getLuckyBolPurchaseSeqNo() != null) {
            luckyBolService.updatePurchaseSeqNo(purchase);
        }
    }

}
