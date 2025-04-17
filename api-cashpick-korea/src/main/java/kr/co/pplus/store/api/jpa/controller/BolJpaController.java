package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.CashExchange;
import kr.co.pplus.store.api.jpa.service.BolService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BolJpaController extends RootController {

    private Logger logger = LoggerFactory.getLogger(BolJpaController.class);

    @Autowired
    BolService bolService;

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/bol/adReward")
    public Map<String,Object> adRewardBol(Session session) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", bolService.adRewardBol(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/bol/exchange")
    public Map<String,Object> exchangeBol(Session session, @RequestBody CashExchange cashExchange) throws Exception {
        return result(Const.E_SUCCESS, "row", bolService.exchangeBol(session, cashExchange));
    }

}
