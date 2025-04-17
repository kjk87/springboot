package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.VisitorPointGiveHistory;
import kr.co.pplus.store.api.jpa.service.VisitorPointService;
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

import java.util.Map;

@RestController
public class VisitorPointController extends RootController {

    private Logger logger = LoggerFactory.getLogger(VisitorPointController.class);

    @Autowired
    VisitorPointService visitorPointService;

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/givePoint")
    public Map<String,Object> givePoint(Session session, @RequestBody VisitorPointGiveHistory visitorPointGiveHistory) throws ResultCodeException {

        return result(visitorPointService.givePoint(session, visitorPointGiveHistory));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/givePointFromUser")
    public Map<String,Object> givePointByUser(Session session, @RequestBody VisitorPointGiveHistory visitorPointGiveHistory) throws ResultCodeException {

        return result(visitorPointService.givePointFromUser(session, visitorPointGiveHistory));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/givePointWithStamp")
    public Map<String,Object> givePointWithStamp(Session session, @RequestBody VisitorPointGiveHistory visitorPointGiveHistory) throws ResultCodeException {

        return result(visitorPointService.givePointWithStamp(session, visitorPointGiveHistory));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/getFirstBenefit")
    public Map<String,Object> getFirstBenefit(Session session, Long pageSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", visitorPointService.getFirstBenefit(session.getNo(), pageSeqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/getVisitorPointGiveHistoryListByPageSeqNo")
    public Map<String,Object> getVisitorPointGiveHistoryListByPageSeqNo(Session session, Long pageSeqNo, String startDatetime, String endDatetime, Pageable pageable) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", visitorPointService.getVisitorPointGiveHistoryListByPageSeqNo(pageSeqNo, startDatetime, endDatetime, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/getCountVisitorPointGiveHistoryByPageSeqNo")
    public Map<String,Object> getCountVisitorPointGiveHistoryByPageSeqNo(Session session, Long pageSeqNo, String startDatetime, String endDatetime) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", visitorPointService.getCountVisitorPointGiveHistoryByPageSeqNo(pageSeqNo, startDatetime, endDatetime));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/visitorPoint/getPriceVisitorPointGiveHistoryByPageSeqNo")
    public Map<String,Object> getPriceVisitorPointGiveHistoryByPageSeqNo(Session session, Long pageSeqNo, String startDatetime, String endDatetime) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", visitorPointService.getPriceVisitorPointGiveHistoryByPageSeqNo(pageSeqNo, startDatetime, endDatetime));
    }

}
