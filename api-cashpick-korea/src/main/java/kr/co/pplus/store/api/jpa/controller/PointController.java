package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.CashExchange;
import kr.co.pplus.store.api.jpa.model.PointBuy;
import kr.co.pplus.store.api.jpa.service.PointService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PointController extends RootController {


    @Autowired
    PointService pointService;


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/point/insert")
    public Map<String, Object> insertPointBuy(Session session, @RequestBody PointBuy pointBuy) throws ResultCodeException {

        return result(pointService.insertPointBuy(session, pointBuy));

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/point/cancel")
    public Map<String, Object> pointCancel(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", pointService.pointCancel(seqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/point/history/list")
    public Map<String, Object> getPointHistoryList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", pointService.getPointHistoryList(pageable, session.getNo()));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/point/history/get")
    public Map<String, Object> getPointHistory(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", pointService.gePointHistory(seqNo));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/point/exchange")
    public Map<String, Object> exchangePointByBol(Session session, Integer point) throws ResultCodeException {

        return result(pointService.exchangePointByBol(session, point));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/point/cashExchange")
    public Map<String,Object> exchangeBol(Session session, @RequestBody CashExchange cashExchange) throws Exception {
        return result(Const.E_SUCCESS, "row", pointService.cashExchange(session, cashExchange));
    }
}
