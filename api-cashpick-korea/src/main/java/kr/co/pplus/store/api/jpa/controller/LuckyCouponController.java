package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.LuckyCouponService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LuckyCouponController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(LuckyCouponController.class);

    @Autowired
    LuckyCouponService luckyCouponService ;


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyCoupon/getMemberLuckyCouponList")
    public Map<String, Object> getMemberLuckyCouponList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", luckyCouponService.getMemberLuckyCouponList(session.getNo()));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/luckyCoupon/getMemberLuckyCouponCount")
    public Map<String, Object> getMemberLuckyCouponCount(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", luckyCouponService.getMemberLuckyCouponCount(session.getNo()));

    }

}
