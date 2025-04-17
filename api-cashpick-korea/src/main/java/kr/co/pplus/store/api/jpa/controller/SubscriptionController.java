package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.SubscriptionService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SubscriptionController extends RootController {

    private Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    SubscriptionService subscriptionService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getCountByProductPriceSeqNo")
    public Map<String,Object> getSubscriptionCountByProductPriceSeqNo(Session session, Long productPriceSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.getSubscriptionCountByProductPriceSeqNo(productPriceSeqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getSubscriptionDownloadCountByMemberSeqNoAndStatus")
    public Map<String,Object> getSubscriptionDownloadCountByMemberSeqNoAndStatus(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.getSubscriptionDownloadCountByMemberSeqNoAndStatus(session.getNo()));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getSubscriptionDownloadListByProductPriceSeqNo")
    public Map<String,Object> getSubscriptionDownloadListByProductPriceSeqNo(Session session, Long productPriceSeqNo, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.getSubscriptionDownloadListByProductPriceSeqNo(productPriceSeqNo, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getSubscriptionDownloadListByMemberSeqNo")
    public Map<String,Object> getSubscriptionDownloadListByMemberSeqNo(Session session, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.getSubscriptionDownloadListByMemberSeqNo(session.getNo(), pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getSubscriptionLogListByProductPriceSeqNo")
    public Map<String,Object> getSubscriptionLogListByProductPriceSeqNo(Session session, Long productPriceSeqNo, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.getSubscriptionLogListByProductPriceSeqNo(productPriceSeqNo, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getSubscriptionLogListBySubscriptionDownloadSeqNo")
    public Map<String,Object> getSubscriptionLogListBySubscriptionDownloadSeqNo(Session session, Long subscriptionDownSeqNo, String sort) throws ResultCodeException {
        if(AppUtil.isEmpty(sort)){
            sort = "recent";
        }
        return result(Const.E_SUCCESS, "rows", subscriptionService.getSubscriptionLogListBySubscriptionDownloadSeqNo(subscriptionDownSeqNo, sort));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/getSubscriptionDownloadBySeqNo")
    public Map<String,Object> getSubscriptionLogListByProductPriceSeqNo(Session session, Long seqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.getSubscriptionDownloadBySeqNo(seqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/download")
    public Map<String,Object> download(Session session, Long memberSeqNo, Long productPriceSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.download(session, memberSeqNo, productPriceSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/downloadFromUser")
    public Map<String,Object> downloadFromUser(Session session, Long productPriceSeqNo, String authCode, String echossId) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.downloadFromUser(session.getNo(), productPriceSeqNo, authCode, echossId));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/downloadWithStamp")
    public Map<String,Object> downloadWithStamp(Session session, Long productPriceSeqNo, String token) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", subscriptionService.downloadWithStamp(session.getNo(), productPriceSeqNo, token));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/use")
    public Map<String,Object> use(Session session, Long memberSeqNo, Long subscriptionDownloadSeqNo, Integer useCount, Integer usePrice) throws ResultCodeException {
        return result(subscriptionService.use(session, memberSeqNo, subscriptionDownloadSeqNo, useCount, usePrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/useFromUser")
    public Map<String,Object> useFromUser(Session session, Long subscriptionDownloadSeqNo, Integer useCount, Integer usePrice, String authCode, String echossId) throws ResultCodeException {
        return result(subscriptionService.use(session.getNo(), subscriptionDownloadSeqNo, useCount, usePrice, authCode, echossId));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/subscription/useWithStamp")
    public Map<String,Object> useWithStamp(Session session, Long subscriptionDownloadSeqNo, Integer useCount, Integer usePrice, String token) throws ResultCodeException {
        return result(subscriptionService.useWithStamp(session.getNo(), subscriptionDownloadSeqNo, useCount, usePrice, token));
    }

}
