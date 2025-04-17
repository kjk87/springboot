package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.LuckyBoxDeliveryPurchase;
import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchase;
import kr.co.pplus.store.api.jpa.model.LuckyBoxReplyOnly;
import kr.co.pplus.store.api.jpa.model.LuckyBoxReview;
import kr.co.pplus.store.api.jpa.service.LuckyBoxService;
import kr.co.pplus.store.api.util.AppUtil;
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
public class LuckyBoxController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(LuckyBoxController.class);

    @Autowired
    LuckyBoxService luckyBoxService;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getLuckyBoxList")
    public Map<String, Object> getLuckyBoxList() throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyBoxService.getLuckyBoxList());

    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getLuckyBoxProductList")
    public Map<String, Object> getLuckyBoxProductList(String groupSeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxProductList(groupSeqNo, pageable));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/saveLuckyBoxPurchase")
    public Map<String, Object> saveLuckyBoxPurchase(Session session,  @RequestBody LuckyBoxPurchase luckyBoxPurchase) throws ResultCodeException {

        if(!session.getNo().equals(luckyBoxPurchase.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(Const.E_SUCCESS, "row", luckyBoxService.saveLuckyBoxPurchase(luckyBoxPurchase));

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/paymentLuckyBoxPurchase")
    public String paymentLuckyBoxPurchase(HttpServletRequest request) throws ResultCodeException {

        luckyBoxService.paymentLuckyBoxPurchase(request);
        return "SUCCESS";
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/paymentBillkeyLuckyBoxPurchase")
    public Map<String, Object> paymentBillkeyLuckyBoxPurchase(Session session, Long luckyBoxPurchaseSeqNo, String token, String installment) throws ResultCodeException {

        luckyBoxService.paymentBillkeyLuckyBoxPurchase(luckyBoxPurchaseSeqNo, token, installment);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/cancelLuckyBox")
    public Map<String, Object> cancelLuckyBox(Session session, Long luckyBoxPurchaseSeqNo) throws ResultCodeException {

        luckyBoxService.cancelLuckyBox(session, luckyBoxPurchaseSeqNo);

        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/cashBackLuckyBoxPurchaseItem")
    public Map<String, Object> cashBackLuckyBoxPurchaseItem(Session session, Long luckyBoxPurchaseItemSeqNo, String type) throws ResultCodeException {

        if(AppUtil.isEmpty(type)){
            type = "point";
        }

        luckyBoxService.cashBackItem(session, luckyBoxPurchaseItemSeqNo, type);

        return result(Const.E_SUCCESS);

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/updateLuckyBoxImpression")
    public Map<String, Object> updateLuckyBoxImpression(Session session, Long luckyBoxPurchaseItemSeqNo, String impression) throws ResultCodeException {

        luckyBoxService.updateImpression(session, luckyBoxPurchaseItemSeqNo, impression);

        return result(Const.E_SUCCESS);

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getCountNotOpenLuckyBoxPurchaseItem")
    public Map<String, Object> getCountNotOpenLuckyBoxPurchaseItem(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getCountNotOpenLuckyBoxPurchaseItem(session));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getNotOpenLuckyBoxPurchaseItemList")
    public Map<String, Object> getNotOpenLuckyBoxPurchaseItemList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyBoxService.getNotOpenLuckyBoxPurchaseItemList(session));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getNotOpenLuckyBoxPurchaseItemListByLuckyBoxPurchaseSeqNo")
    public Map<String, Object> getNotOpenLuckyBoxPurchaseItemListByLuckyBoxPurchaseSeqNo(Session session, Long luckyBoxPurchaseSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyBoxService.getNotOpenLuckyBoxPurchaseItemListByLuckyBoxPurchaseSeqNo(session, luckyBoxPurchaseSeqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/isCancelableLuckyBox")
    public Map<String, Object> isCancelableLuckyBox(Session session, Long luckyBoxPurchaseSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.isCancelableLuckyBox(luckyBoxPurchaseSeqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getNotOpenLuckyBoxPurchaseList")
    public Map<String, Object> getNotOpenLuckyBoxPurchaseList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyBoxService.getNotOpenLuckyBoxPurchaseList(session));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getOpenLuckyBoxPurchaseItemList")
    public Map<String, Object> getOpenLuckyBoxPurchaseItemList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getOpenLuckyBoxPurchaseItemList(session, pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getTotalLuckyBoxPurchaseItemList")
    public Map<String, Object> getTotalLuckyBoxPurchaseItemList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getTotalLuckyBoxPurchaseItemList(pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getTotalLuckyPurchaseItemList")
    public Map<String, Object> getTotalLuckyPurchaseItemList(HttpServletRequest request, Session session, Pageable pageable) throws ResultCodeException {

//        Map<String, String> sortMap = new HashMap<String, String>();
//        pageable = this.nativePageable(request, pageable, sortMap);

        return result(Const.E_SUCCESS, "row", luckyBoxService.getTotalLuckyPurchaseItemList(pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/getLuckyBoxPurchaseItem")
    public Map<String, Object> getLuckyBoxPurchaseItem(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxPurchaseItem(seqNo));

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/sqs")
    public Map<String, Object> sqs(@RequestBody SqsModel model) throws ResultCodeException {


        luckyBoxService.openLuckyBoxPurchaseItem(model);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/openLuckyBoxPurchaseItem")
    public Map<String, Object> openLuckyBoxPurchaseItem(Session session, SqsModel model) throws ResultCodeException {

        if(!session.getNo().equals(model.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        luckyBoxService.openLuckyBoxPurchaseItem(model);
        return result(Const.E_SUCCESS);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/confirmLuckyBoxPurchaseItem")
    public Map<String, Object> confirmLuckyBoxPurchaseItem(Session session, Long luckyBoxPurchaseItemSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.confirmLuckyBoxPurchaseItem(luckyBoxPurchaseItemSeqNo));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/saveLuckyBoxDeliveryPurchase")
    public Map<String, Object> saveLuckyBoxDeliveryPurchase(Session session, @RequestBody LuckyBoxDeliveryPurchase luckyBoxDeliveryPurchase) throws ResultCodeException {

        if(!session.getNo().equals(luckyBoxDeliveryPurchase.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(Const.E_SUCCESS, "row", luckyBoxService.saveLuckyBoxDeliveryPurchase(luckyBoxDeliveryPurchase));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/paymentLuckyBoxDeliveryPurchase")
    public String paymentLuckyBoxDeliveryPurchase(HttpServletRequest request) throws ResultCodeException {

        luckyBoxService.paymentLuckyBoxDeliveryPurchase(request);
        return "SUCCESS";
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBox/paymentBillkeyLuckyBoxDeliveryPurchase")
    public Map<String, Object> paymentBillkeyLuckyBoxDeliveryPurchase(Session session, Long luckyBoxDeliveryPurchaseSeqNo, String token, String installment) throws ResultCodeException {

        luckyBoxService.paymentBillkeyLuckyBoxDeliveryPurchase(luckyBoxDeliveryPurchaseSeqNo, token, installment);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/insertLuckyBoxReply")
    public Map<String,Object> insertLuckyBoxReply(Session session, @RequestBody LuckyBoxReplyOnly reply) throws ResultCodeException {
        reply.setMemberSeqNo(session.getNo());
        return result(luckyBoxService.insertLuckyBoxReply(reply));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/updateLuckyBoxReply")
    public Map<String,Object> updateLuckyBoxReply(Session session, @RequestBody LuckyBoxReplyOnly reply) throws ResultCodeException {

        if(!session.getNo().equals(reply.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(luckyBoxService.updateLuckyBoxReply(reply));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/deleteLuckyBoxReply")
    public Map<String,Object> deleteLuckyBoxReply(Session session, Long seqNo) throws ResultCodeException {
        return result(luckyBoxService.deleteLuckyBoxReply(session, seqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/getLuckyBoxReplyListByLuckyBoxPurchaseItemSeqNo")
    public Map<String,Object> getLuckyBoxReplyListByLuckyBoxPurchaseItemSeqNo(Session session, Pageable pageable, Long luckyBoxPurchaseItemSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxReplyListByLuckyBoxPurchaseItemSeqNo(luckyBoxPurchaseItemSeqNo, pageable));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/getLuckyBoxReplyListByLuckyBoxReviewSeqNo")
    public Map<String,Object> getLuckyBoxReplyListByLuckyBoxReviewSeqNo(Session session, Pageable pageable, Long luckyBoxReviewSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxReplyListByLuckyBoxReviewSeqNo(luckyBoxReviewSeqNo, pageable));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/insertLuckyBoxReview")
    public Map<String,Object> insertLuckyBoxReview(Session session, @RequestBody LuckyBoxReview luckyBoxReview) throws ResultCodeException {
        return result(luckyBoxService.insertLuckyBoxReview(luckyBoxReview));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/updateLuckyBoxReview")
    public Map<String,Object> updateLuckyBoxReview(Session session, @RequestBody LuckyBoxReview luckyBoxReview) throws ResultCodeException {
        return result(luckyBoxService.updateLuckyBoxReview(luckyBoxReview));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/getLuckyBoxReviewList")
    public Map<String,Object> getLuckyBoxReviewList(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        if(session == null){
            return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxReviewList(null, pageable));
        }
        return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxReviewList(session, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/getMyLuckyBoxReviewList")
    public Map<String,Object> getMyLuckyBoxReviewList(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        return result(Const.E_SUCCESS, "row", luckyBoxService.getMyLuckyBoxReviewList(session, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBox/getLuckyBoxReview")
    public Map<String,Object> getLuckyBoxReview(Session session, Long seqNo) throws ResultCodeException {

        if(session == null){
            return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxReview(null, seqNo));
        }
        return result(Const.E_SUCCESS, "row", luckyBoxService.getLuckyBoxReview(session, seqNo));
    }
}
