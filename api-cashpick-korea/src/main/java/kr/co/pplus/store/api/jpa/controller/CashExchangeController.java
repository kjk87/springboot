package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.CashExchange;
import kr.co.pplus.store.api.jpa.service.CashExchangeService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCashExchangeException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class CashExchangeController extends RootController {

    private Logger logger = LoggerFactory.getLogger(CashExchangeController.class);

    @Autowired
    CashExchangeService cashExchangeService ;

    @GetMapping(value = baseUri+"/cashExchange/getCashExchangeList")
    public Map<String,Object> getCashExchangeList(Session session, Pageable pageable) throws ResultCodeException {
        try {

            Page<CashExchange> page = cashExchangeService.getCashExchangeList(session.getNo(), pageable);
            return result(Const.E_SUCCESS, "row", page);
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidCashExchangeException("cashExchange/getCashExchange", e);
        }

    }

    @GetMapping(value = baseUri+"/cashExchange/getCashExchange")
    public Map<String,Object> getCashExchange(Session session, Long seqNo) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", cashExchangeService.getCashExchange(seqNo));
        }
        catch(Exception e){
            logger.error(AppUtil.excetionToString(e)) ;
            throw new InvalidCashExchangeException("cashExchange/getCashExchange", e);
        }

    }
}
