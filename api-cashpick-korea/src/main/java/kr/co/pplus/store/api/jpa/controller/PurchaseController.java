package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.LpngCallback;
import kr.co.pplus.store.api.jpa.model.Purchase;
import kr.co.pplus.store.api.jpa.model.PurchaseDelivery;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkCancelResponse;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayRequest;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayResponse;
import kr.co.pplus.store.api.jpa.service.PurchaseService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidBuyException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.util.RedisUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class PurchaseController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);


    @Autowired
    PurchaseService purchaseService;

    @Value("${STORE.REDIS_PREFIX}")
    String REDIS_PREFIX = "pplus-";

    Integer buyTimeout = 30; // minutes


    @CrossOrigin
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/orderId")
    public Map<String, Object> purchaseOrderId(Session session) throws ResultCodeException {

        Integer count = null;
        try {

            String orderId = StoreUtil.getRandomOrderId();

            //orderId Redis 캐쉬 저장
            String key = REDIS_PREFIX + orderId;
            RedisUtil.getInstance().putOpsHash(key, "orderId", orderId);
            RedisUtil.getInstance().hashExpire(key, buyTimeout, TimeUnit.MINUTES);

            return result(Const.E_SUCCESS, "row", orderId);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidBuyException("purchase/orderId", e);
        }
    }

    @CrossOrigin
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/ship")
    public Map<String, Object> purchaseShip(Session session, @RequestBody Purchase purchase) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.purchaseShip(session, purchase));
    }

    @CrossOrigin
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/ticket")
    public Map<String, Object> purchaseTicket(Session session, @RequestBody Purchase purchase) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.purchaseTicket(session, purchase));
    }

    @CrossOrigin
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/admin/result")
    public Map<String, Object> adminResult(HttpServletRequest request, String type, String roomId, String orderId) throws ResultCodeException {
        purchaseService.adminResult(type, "0000", roomId, orderId);
        return result(Const.E_SUCCESS, "row", "");
    }

    @CrossOrigin
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/ftlink/pay") // 주문/배달 상품 구매
    public Map<String, Object> ftlinkPay(Session session, @RequestBody FTLinkPayRequest ftLinkPayRequest) throws ResultCodeException {

        FTLinkPayResponse res = purchaseService.ftlinkPay(ftLinkPayRequest);

        if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {
            return result(Const.E_SUCCESS, "row", res);
        } else {
            return result(Const.E_INVALID_BUY, "row", res);
        }
    }

    @CrossOrigin
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/bootpay/verify")
    // 주문/배달 상품 구매
    public Map<String, Object> verifyBootPay(Session session, String orderId, String receiptId) throws ResultCodeException {

        return result(purchaseService.verifyBootPay(session, orderId, receiptId));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/callbackByPurchaseSeqNo")
    // lpngcallback 데이터
    public Map<String, Object> getLpngCallbackByPurchaseSeqNo(HttpServletRequest request, Session session, @RequestParam(value = "purchaseSeqNo", required = true) Long purchaseSeqNo) throws ResultCodeException {

        LpngCallback callback = purchaseService.getLpngCallbackByPurchaseSeqNo(purchaseSeqNo);
        return result(Const.E_SUCCESS, "row", callback);

    }

    @CrossOrigin
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/ftlink/cancel/result")
    public Map<String, Object> ftLinkCancelRelust(FTLinkCancelResponse data) throws ResultCodeException {
        logger.debug(" orderNo : " + data.getOrderNo() + " errorCode : " + data.getErrCode() + " errorMessage : " + data.getErrMessage());
        purchaseService.ftLinkCancelResult(data);
        return result(Const.E_SUCCESS);
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/count")
    public Map<String, Object> getCountPurchaseProductByMemberSeqNo(Session session, HttpServletRequest request) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.getCountPurchaseProductByMemberSeqNo(session.getNo()));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/list")
    public Map<String, Object> getPurchaseProductListByMemberSeqNo(Session session, String purchaseType, Pageable pageable) throws ResultCodeException {
        if(AppUtil.isEmpty(purchaseType)){
            purchaseType = "store";
        }

        return result(Const.E_SUCCESS, "row", purchaseService.getPurchaseProductListByMemberSeqNo(session.getNo(), purchaseType, pageable));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/ticketList")
    public Map<String, Object> getPurchaseProductListTicketTypeByMemberSeqNo(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.getPurchaseProductListTicketTypeByMemberSeqNo(session.getNo(), pageable));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/listByProductPriceCode")
    public Map<String, Object> getPurchaseProductListByProductPriceCode(Session session, HttpServletRequest request, Pageable pageable, String productPriceCode) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.getPurchaseProductListByProductPriceCode(productPriceCode, pageable));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/listBySupplyPageSeqNo")
    public Map<String, Object> getPurchaseProductListBySupplyPageSeqNo(Session session, HttpServletRequest request, Pageable pageable, Long supplyPageSeqNo, String status,
                                                                       String startDuration, String endDuration) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.getPurchaseProductListBySupplyPageSeqNo(supplyPageSeqNo, status, startDuration, endDuration, pageable));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/listByPageSeqNo")
    public Map<String, Object> getPurchaseProductListByPageSeqNo(Session session, HttpServletRequest request, Pageable pageable, Long pageSeqNo, Long salesType, String status,
                                                                 String startDuration, String endDuration) throws ResultCodeException {

        if (salesType == null) {
            salesType = 3L;
        }

        return result(Const.E_SUCCESS, "row", purchaseService.getPurchaseProductListByPageSeqNo(pageSeqNo, salesType, status, startDuration, endDuration, pageable));
    }


    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product")
    public Map<String, Object> getPurchaseProductListByMemberSeqNo(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.getPurchaseProductDetailBySeqNo(seqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/getPurchaseProductList")
    public Map<String, Object> getPurchaseProductList(Session session, Long purchaseSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", purchaseService.getPurchaseProductList(purchaseSeqNo));
    }

    @SkipSessionCheck
    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/complete")
    public Map<String, Object> updateCompleteBySeqNo(Session session, Long seqNo) throws ResultCodeException {
        try {

            purchaseService.updateCompleteBySeqNo(seqNo);
            return result(Const.E_SUCCESS);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidBuyException("/purchase/product/complete", e);
        }
    }

    @CrossOrigin
    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/cancelList")
    public Map<String, Object> cancelList(Session session, String purchaseSeqNoList, String memo, String type, Boolean dbOnly) throws ResultCodeException {
        try {

            if (AppUtil.isEmpty(type)) {
                type = "biz";
            }

            if(dbOnly == null){
                dbOnly = false;
            }

            purchaseService.cancelList(purchaseSeqNoList, memo, type, dbOnly);
            return result(Const.E_SUCCESS);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidBuyException("/purchase/product/cancel", e);
        }
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/cancel")
    public Map<String, Object> cancelPurchase(Session session, Long purchaseSeqNo, String memo, String type) throws ResultCodeException {
        if (AppUtil.isEmpty(type)) {
            type = "user";
        }

        purchaseService.cancelPurchase(purchaseSeqNo, memo, type, false);
        return result(Const.E_SUCCESS);
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/salePrice")
    public Map<String, Object> sumSalePrice(Session session, Long pageSeqNo, Long supplyPageSeqNo, String startDuration, String endDuration) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.sumSalePrice(pageSeqNo, supplyPageSeqNo, startDuration, endDuration));
    }

    @CrossOrigin
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/saleCount")
    public Map<String, Object> saleCount(Session session, Long pageSeqNo, Long supplyPageSeqNo, String startDuration, String endDuration, String productType) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.saleCount(pageSeqNo, supplyPageSeqNo, startDuration, endDuration, productType));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/delivery/transportNumber")
    public Map<String, Object> updateTransportNumber(Session session, @RequestBody PurchaseDelivery purchaseDelivery) throws ResultCodeException {
        try {

            purchaseService.updateTransportNumber(purchaseDelivery);
            return result(Const.E_SUCCESS);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidBuyException("/buyGoods/transportNumber", e);
        }
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/product/domaemaeOrder")
    public Map<String, Object> domaemaeOrder(Session session, Long purchaseProductSeqNo) throws ResultCodeException {
        try {
            return result(Const.E_SUCCESS, "row", purchaseService.domaemaeOrder(purchaseProductSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidBuyException("/purchase/product/domaemaeOrder", e);
        }
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/savePurchase")
    public Map<String, Object> savePurchase(Session session, @RequestBody Purchase purchase) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.savePurchase(session, purchase, false));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/savePurchaseWithLuckyBol")
    public Map<String, Object> savePurchaseWithLuckyBol(Session session, @RequestBody Purchase purchase) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", purchaseService.savePurchaseWithLuckyBol(session, purchase));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/paymentPurchase")
    public String paymentPurchase(HttpServletRequest request) throws ResultCodeException {

        purchaseService.paymentPurchase(request);
        return "SUCCESS";
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/purchase/paymentBillkeyPurchase")
    public Map<String, Object> paymentBillkeyPurchase(Session session, Long purchaseSeqNo, String token, String installment) throws ResultCodeException {

        purchaseService.paymentBillkeyPurchase(purchaseSeqNo, token, installment);
        return result(Const.E_SUCCESS);
    }

}
