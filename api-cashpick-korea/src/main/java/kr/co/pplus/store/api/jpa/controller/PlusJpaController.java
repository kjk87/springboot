package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.PlusJpaService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class PlusJpaController extends RootController {

    private Logger logger = LoggerFactory.getLogger(PlusJpaController.class);

    @Autowired
    PlusJpaService plusJpaService;

//    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getListWithPlusGoods/**")
//    public Map<String,Object> getListWithPlusGoods(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {
//        return result(200, "row", plusJpaService.getListWithPlusGoods(session, request, pageable));
//    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getPlusListWithNews/**")
    public Map<String,Object> getPlusListWithNews(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {
        return result(200, "row", plusJpaService.getPlusListWithNews(session, request, pageable));
    }

    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getPlusListByMemberSeqNoExistNews/**")
    public Map<String,Object> getPlusListByMemberSeqNo(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {
        return result(200, "row", plusJpaService.getPlusListByMemberSeqNo(session, request, pageable));
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getPlusListByPageSeqNo/**")
    public Map<String,Object> getPlusListByPageSeqNo(Session session, HttpServletRequest request, Pageable pageable, @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo
            , @RequestParam(value = "male", required = false) Boolean male
            , @RequestParam(value = "female", required = false) Boolean female
            , @RequestParam(value = "age10", required = false) Boolean age10
            , @RequestParam(value = "age20", required = false) Boolean age20
            , @RequestParam(value = "age30", required = false) Boolean age30
            , @RequestParam(value = "age40", required = false) Boolean age40
            , @RequestParam(value = "age50", required = false) Boolean age50
            , @RequestParam(value = "age60", required = false) Boolean age60
            , @RequestParam(value = "buyCount", required = false) Integer buyCount
            , @RequestParam(value = "lastBuyDay", required = false) Integer lastBuyDay) throws ResultCodeException {
        return result(200, "row", plusJpaService.getPlusListByPageSeqNo(request, pageable, pageSeqNo, male, female, age10, age20, age30, age40, age50, age60, buyCount, lastBuyDay));
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/plus/getPlusCountByPageSeqNo/**")
    public Map<String,Object> getPlusCountByPageSeqNo(@RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
        return result(200, "row", plusJpaService.getPlusCountByPageSeqNo(pageSeqNo));
    }


}
