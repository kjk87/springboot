package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.PrepaymentService;
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
public class PrepaymentController extends RootController {

    private Logger logger = LoggerFactory.getLogger(PrepaymentController.class);

    @Autowired
    PrepaymentService prepaymentService;

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentRetentionCount")
    public Map<String,Object> getRetentionCount(Session session, Long pageSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepaymentRetentionCount(session.getNo()));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepayment")
    public Map<String,Object> getPrepayment(Session session, Long seqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepayment(seqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentCountData")
    public Map<String,Object> getPrepaymentCountData(Session session, Long prepaymentSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepaymentCountData(prepaymentSeqNo));
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/updateStatus")
    public Map<String,Object> prepaymentService(Session session, Long prepaymentSeqNo, String status) throws ResultCodeException {
        return result(prepaymentService.updateStatus(prepaymentSeqNo, status));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentListByPageSeqNo")
    public Map<String,Object> getPrepaymentListByPageSeqNo(Session session, Long pageSeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepaymentListByPageSeqNo(pageSeqNo, pageable));
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentPublish")
    public Map<String,Object> getPrepaymentPublish(Session session, Long seqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepaymentPublish(seqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentPublishByPageSeqNo")
    public Map<String,Object> getPrepaymentPublishByPageSeqNo(Session session, Long pageSeqNo, String status, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepaymentPublishByPageSeqNo(pageSeqNo, status, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentLogByPageSeqNo")
    public Map<String,Object> getPrepaymentLogByPageSeqNo(Session session, Long pageSeqNo, String status, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", prepaymentService.getPrepaymentLogByPageSeqNo(pageSeqNo, status, pageable));
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/getPrepaymentLogList")
    public Map<String,Object> getPrepaymentLogList(Session session, Long prepaymentPublishSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", prepaymentService.getPrepaymentLogList(prepaymentPublishSeqNo));
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/publish")
    public Map<String,Object> publish(Session session, Long prepaymentSeqNo) throws ResultCodeException {
        return result(prepaymentService.publish(session.getNo(), prepaymentSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/use")
    public Map<String,Object> use(Session session, Long prepaymentPublishSeqNo, Integer usePrice) throws ResultCodeException {
        return result(prepaymentService.use(session.getNo(), prepaymentPublishSeqNo, usePrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/publishResult")
    public Map<String,Object> publishResult(Session session, Long pageSeqNo, Long prepaymentPublishSeqNo, String status) throws ResultCodeException {
        return result(prepaymentService.publishResult(pageSeqNo, prepaymentPublishSeqNo, status));
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/prepayment/useResult")
    public Map<String,Object> useResult(Session session, Long pageSeqNo, Long prepaymentLogSeqNo, String status) throws ResultCodeException {
        return result(prepaymentService.useResult(pageSeqNo, prepaymentLogSeqNo, status));
    }

}
