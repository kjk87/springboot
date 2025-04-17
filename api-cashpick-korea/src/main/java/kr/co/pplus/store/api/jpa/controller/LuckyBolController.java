package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.LuckyBolPurchase;
import kr.co.pplus.store.api.jpa.model.LuckyBolReplyOnly;
import kr.co.pplus.store.api.jpa.service.LuckyBolService;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class LuckyBolController extends RootController {

    private Logger logger = LoggerFactory.getLogger(LuckyBolController.class);

    @Autowired
    LuckyBolService luckyBolService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getActiveLuckyBol")
    public Map<String, Object> getActiveLuckyBol(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getActiveLuckyBol());
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getConcludeLuckyBol")
    public Map<String, Object> getConcludeLuckyBol(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getConcludeLuckyBol());
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getActiveLuckyBolList")
    public Map<String, Object> getActiveLuckyBolList(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", luckyBolService.getActiveLuckyBolList());
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getAnnouncedLuckyBolList")
    public Map<String, Object> getAnnouncedLuckyBolList(Session session, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getAnnouncedLuckyBolList(pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBol")
    public Map<String, Object> getLuckyBol(Session session, Long seqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBol(seqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolPurchase")
    public Map<String, Object> getLuckyBolPurchase(Session session, Long seqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolPurchase(seqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolPurchaseByPurchaseSeqNo")
    public Map<String, Object> getLuckyBolPurchaseByPurchaseSeqNo(Session session, Long purchaseSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolPurchaseByPurchaseSeqNo(purchaseSeqNo));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolColor")
    public Map<String, Object> getLuckyBolColor() throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolColor());
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolJoinNumber")
    public Map<String, Object> getJoinNumber(Session session, Long luckyBolSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.getJoinNumber(luckyBolSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/checkLuckyBolValidNumber")
    public Map<String, Object> checkValidNumber(Session session, Long luckyBolSeqNo, String number) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.checkValidNumber(luckyBolSeqNo, number));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/selectLuckyBolRandomNumber")
    public Map<String, Object> selectRandomNumber(Session session, Long luckyBolSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", luckyBolService.selectRandomNumber(luckyBolSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/deleteLuckyBolSelectNumber")
    public Map<String, Object> deleteSelectNumber(Session session, Long luckyBolSeqNo, String number) throws ResultCodeException {
        luckyBolService.deleteSelectNumber(luckyBolSeqNo, number);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolProductGroup")
    public Map<String, Object> getLuckyBolProductGroup(Session session, Long luckyBolSeqNo) {
        return result(Const.E_SUCCESS, "rows", luckyBolService.getLuckyBolProductGroup(luckyBolSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolProductList")
    public Map<String, Object> getLuckyBolProductList(Session session, Long luckyBolSeqNo, Integer exchangePrice, Pageable pageable) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolProductList(luckyBolSeqNo, exchangePrice, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolGiftList")
    public Map<String, Object> getLuckyBolGiftList(Session session, Long luckyBolSeqNo, Pageable pageable) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolGiftList(luckyBolSeqNo, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolDelegateProduct")
    public Map<String, Object> getLuckyBolDelegateProduct(Session session, Long luckyBolSeqNo) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolDelegateProduct(luckyBolSeqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolPurchaseListByLuckyBolSeqNo")
    public Map<String, Object> getLuckyBolPurchaseListByLuckyBolSeqNo(Session session, Long luckyBolSeqNo, Pageable pageable) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolPurchaseListByLuckyBolSeqNo(session.getNo(), luckyBolSeqNo, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolPurchaseList")
    public Map<String, Object> getLuckyBolPurchaseList(Session session, Pageable pageable) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolPurchaseList(session.getNo(), pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getUnUseLuckyBolPurchaseList")
    public Map<String, Object> getUnUseLuckyBolPurchaseList(Session session) {
        return result(Const.E_SUCCESS, "rows", luckyBolService.getUnUseLuckyBolPurchaseList(session.getNo()));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/saveLuckyBolPurchase")
    public Map<String, Object> saveLuckyBolPurchase(Session session, @RequestBody LuckyBolPurchase luckyBolPurchase) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBolService.saveLuckyBolPurchase(session, luckyBolPurchase));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/paymentLuckyBolPurchase")
    public Map<String, Object> paymentLuckyBolPurchase(HttpServletRequest request) throws ResultCodeException {

        luckyBolService.paymentLuckyBolPurchase(request);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/paymentBillKeyLuckyBolPurchase")
    public Map<String, Object> paymentBillKeyLuckyBolPurchase(Session session, Long luckyBolPurchaseSeqNo, String token, String installment) throws ResultCodeException {

        luckyBolService.paymentBillKeyLuckyBolPurchase(luckyBolPurchaseSeqNo, token, installment);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolWinListByLuckyBolSeqNo")
    public Map<String, Object> getLuckyBolWinListByLuckyBolSeqNo(Session session, Long luckyBolSeqNo) {
        return result(Const.E_SUCCESS, "rows", luckyBolService.getLuckyBolWinListByLuckyBolSeqNo(luckyBolSeqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolMyWinList")
    public Map<String, Object> getLuckyBolMyWinList(Session session, Long luckyBolSeqNo) {
        return result(Const.E_SUCCESS, "rows", luckyBolService.getLuckyBolMyWinList(session.getNo(), luckyBolSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolWinList")
    public Map<String, Object> getLuckyBolWinList(Session session, Pageable pageable) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolWinList(pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyBol/getLuckyBolWin")
    public Map<String, Object> getLuckyBolWin(Session session, Long seqNo) {
        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolWin(seqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBol/updateLuckyBolWinImpression")
    public Map<String,Object> updateLuckyBolWinImpression(Session session, Long seqNo, String impression) throws ResultCodeException {
        luckyBolService.updateLuckyBolWinImpression(session, seqNo, impression);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBol/insertLuckyBolReply")
    public Map<String,Object> insertLuckyBolReply(Session session, @RequestBody LuckyBolReplyOnly reply) throws ResultCodeException {
        reply.setMemberSeqNo(session.getNo());
        return result(luckyBolService.insertLuckyBolReply(reply));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBol/updateLuckyBolReply")
    public Map<String,Object> updateLuckyBolReply(Session session, @RequestBody LuckyBolReplyOnly reply) throws ResultCodeException {

        if(!session.getNo().equals(reply.getMemberSeqNo())){
            throw new NotPermissionException();
        }

        return result(luckyBolService.updateLuckyBolReply(reply));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBol/deleteLuckyBolReply")
    public Map<String,Object> deleteLuckyBolReply(Session session, Long seqNo) throws ResultCodeException {
        return result(luckyBolService.deleteLuckyBolReply(session, seqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBol/getLuckyBolReplyListByLuckyBolWinSeqNo")
    public Map<String,Object> getLuckyBolReplyListByLuckyBolWinSeqNo(Session session, Pageable pageable, Long luckyBolWinSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolReplyListByLuckyBolWinSeqNo(luckyBolWinSeqNo, pageable));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/luckyBol/getLuckyBolReplyListByLuckyBolReviewSeqNo")
    public Map<String,Object> getLuckyBolReplyListByLuckyBolReviewSeqNo(Session session, Pageable pageable, Long luckyBolReviewSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyBolService.getLuckyBolReplyListByLuckyBolReviewSeqNo(luckyBolReviewSeqNo, pageable));
    }
}
