package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.VisitLogService;
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
public class VisitLogController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(VisitLogController.class);

    @Autowired
    VisitLogService visitLogService ;


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/visitLog/get")
    public Map<String, Object> getVisitLog(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", visitLogService.getVisitLog(pageSeqNo, session.getNo()));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/visitLog/getVisitLogListByPageSeqNo")
    public Map<String, Object> getVisitLogListByPageSeqNo(Session session, Long pageSeqNo, String status, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", visitLogService.getVisitLogListByPageSeqNo(pageSeqNo, status, pageable));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitLog/receive")
    public Map<String,Object> receive(Session session, Long pageSeqNo) throws ResultCodeException {
        return result(visitLogService.receive(pageSeqNo, session.getNo()));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitLog/receiveResult")
    public Map<String,Object> receiveResult(Session session, Long visitLogSeqNo, Long pageSeqNo, String status) throws ResultCodeException {
        return result(visitLogService.receiveResult(visitLogSeqNo, pageSeqNo, status));
    }

}
