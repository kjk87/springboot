package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.service.LuckyBoxService;
import kr.co.pplus.store.api.jpa.service.LuckyPickService;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.util.aws.SqsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LuckyPickController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(LuckyPickController.class);

    @Autowired
    LuckyPickService luckyPickService;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getLuckyPickList")
    public Map<String, Object> getLuckyPickList(Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickList(pageable));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/saveLuckyPickPurchase")
    public Map<String, Object> saveLuckyPickPurchase(Session session,  @RequestBody LuckyPickPurchase luckyPickPurchase) throws ResultCodeException {

        if(!session.getNo().equals(luckyPickPurchase.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(Const.E_SUCCESS, "row", luckyPickService.saveLuckyPickPurchase(luckyPickPurchase));

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/paymentLuckyPickPurchase")
    public String paymentLuckyPickPurchase(HttpServletRequest request) throws ResultCodeException {

        luckyPickService.paymentLuckyPickPurchase(request);
        return "SUCCESS";
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/paymentBillkeyLuckyPickPurchase")
    public Map<String, Object> paymentBillkeyLuckyPickPurchase(Session session, Long luckyPickPurchaseSeqNo, String token, String installment) throws ResultCodeException {

        luckyPickService.paymentBillkeyLuckyPickPurchase(luckyPickPurchaseSeqNo, token, installment);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/cancelLuckyPick")
    public Map<String, Object> cancelLuckyPick(Session session, Long luckyPickPurchaseSeqNo) throws ResultCodeException {

        luckyPickService.cancelLuckyPick(session, luckyPickPurchaseSeqNo);

        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/cashBackLuckyPickPurchaseIte")
    public Map<String, Object> cashBackLuckyPickPurchaseIte(Session session, Long luckyPickPurchaseItemSeqNo) throws ResultCodeException {

        luckyPickService.cashBackItem(session, luckyPickPurchaseItemSeqNo);

        return result(Const.E_SUCCESS);

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getCountNotOpenLuckyPickPurchaseItem")
    public Map<String, Object> getCountNotOpenLuckyPickPurchaseItem(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getCountNotOpenLuckyPickPurchaseItem(session));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getNotOpenLuckyPickPurchaseItemListByLuckyPickPurchaseSeqNo")
    public Map<String, Object> getNotOpenLuckyPickPurchaseItemListByLuckyPickPurchaseSeqNo(Session session, Long luckyPickPurchaseSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyPickService.getNotOpenLuckyPickPurchaseItemListByLuckyPickPurchaseSeqNo(session, luckyPickPurchaseSeqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getNotOpenLuckyPickPurchaseList")
    public Map<String, Object> getNotOpenLuckyPickPurchaseList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyPickService.getNotOpenLuckyPickPurchaseList(session));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getOpenLuckyPickPurchaseItemList")
    public Map<String, Object> getOpenLuckyPickPurchaseItemList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getOpenLuckyPickPurchaseItemList(session, pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getTotalLuckyPickPurchaseItemList")
    public Map<String, Object> getTotalLuckyPickPurchaseItemList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getTotalLuckyPickPurchaseItemList(pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/getLuckyPickPurchaseItem")
    public Map<String, Object> getLuckyPickPurchaseItem(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickPurchaseItem(seqNo));

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/sqs")
    public Map<String, Object> sqs(@RequestBody SqsModel model) throws ResultCodeException {


        luckyPickService.openLuckyPickPurchaseItem(model);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/openLuckyPickPurchaseItem")
    public Map<String, Object> openLuckyPickPurchaseItem(Session session, SqsModel model) throws ResultCodeException {

        if(!session.getNo().equals(model.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        luckyPickService.openLuckyPickPurchaseItem(model);
        return result(Const.E_SUCCESS);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/confirmLuckyPickPurchaseItem")
    public Map<String, Object> confirmLuckyPickPurchaseItem(Session session, Long luckyPickPurchaseItemSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.confirmLuckyPickPurchaseItem(luckyPickPurchaseItemSeqNo));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/saveLuckyPickDeliveryPurchase")
    public Map<String, Object> saveLuckyPickDeliveryPurchase(Session session, @RequestBody LuckyPickDeliveryPurchase luckyPickDeliveryPurchase) throws ResultCodeException {

        if(!session.getNo().equals(luckyPickDeliveryPurchase.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(Const.E_SUCCESS, "row", luckyPickService.saveLuckyPickDeliveryPurchase(luckyPickDeliveryPurchase));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/paymentLuckyPickDeliveryPurchase")
    public String paymentLuckyPickDeliveryPurchase(HttpServletRequest request) throws ResultCodeException {

        luckyPickService.paymentLuckyPickDeliveryPurchase(request);
        return "SUCCESS";
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyPick/paymentBillkeyLuckyPickDeliveryPurchase")
    public Map<String, Object> paymentBillkeyLuckyPickDeliveryPurchase(Session session, Long luckyPickDeliveryPurchaseSeqNo, String token, String installment) throws ResultCodeException {

        luckyPickService.paymentBillkeyLuckyPickDeliveryPurchase(luckyPickDeliveryPurchaseSeqNo, token, installment);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/insertLuckyPickReply")
    public Map<String,Object> insertLuckyPickReply(Session session, @RequestBody LuckyPickReplyOnly reply) throws ResultCodeException {
        reply.setMemberSeqNo(session.getNo());
        return result(luckyPickService.insertLuckyPickReply(reply));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/updateLuckyPickReply")
    public Map<String,Object> updateLuckyPickReply(Session session, @RequestBody LuckyPickReplyOnly reply) throws ResultCodeException {

        if(!session.getNo().equals(reply.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(luckyPickService.updateLuckyPickReply(reply));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/deleteLuckyPickReply")
    public Map<String,Object> deleteLuckyPickReply(Session session, Long seqNo) throws ResultCodeException {
        return result(luckyPickService.deleteLuckyPickReply(session, seqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/getLuckyPickReplyListByLuckyPickPurchaseItemSeqNo")
    public Map<String,Object> getLuckyPickReplyListByLuckyPickPurchaseItemSeqNo(Session session, Pageable pageable, Long luckyPickPurchaseItemSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickReplyListByLuckyPickPurchaseItemSeqNo(luckyPickPurchaseItemSeqNo, pageable));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/getLuckyPickReplyListByLuckyPickReviewSeqNo")
    public Map<String,Object> getLuckyPickReplyListByLuckyPickReviewSeqNo(Session session, Pageable pageable, Long luckyPickReviewSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickReplyListByLuckyPickReviewSeqNo(luckyPickReviewSeqNo, pageable));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/insertLuckyPickReview")
    public Map<String,Object> insertLuckyPickReview(Session session, @RequestBody LuckyPickReview luckyPickReview) throws ResultCodeException {
        return result(luckyPickService.insertLuckyPickReview(luckyPickReview));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/updateLuckyPickReview")
    public Map<String,Object> updateLuckyPickReview(Session session, @RequestBody LuckyPickReview luckyPickReview) throws ResultCodeException {
        return result(luckyPickService.updateLuckyPickReview(luckyPickReview));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/getLuckyPickReviewList")
    public Map<String,Object> getLuckyPickReviewList(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        if(session == null){
            return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickReviewList(null, pageable));
        }
        return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickReviewList(session, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/getMyLuckyPickReviewList")
    public Map<String,Object> getMyLuckyPickReviewList(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        return result(Const.E_SUCCESS, "row", luckyPickService.getMyLuckyPickReviewList(session, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyPick/getLuckyPickReview")
    public Map<String,Object> getLuckyPickReview(Session session, Long seqNo) throws ResultCodeException {

        if(session == null){
            return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickReview(null, seqNo));
        }
        return result(Const.E_SUCCESS, "row", luckyPickService.getLuckyPickReview(session, seqNo));
    }
}
