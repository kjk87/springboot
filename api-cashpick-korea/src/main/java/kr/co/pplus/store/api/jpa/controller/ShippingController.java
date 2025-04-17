package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.ShippingService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCardException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ShippingController extends RootController {

    private Logger logger = LoggerFactory.getLogger(ShippingController.class);

    @Autowired
    ShippingService shippingService;

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/shipping/company/list")
    public Map<String, Object> getShippingSiteList() throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", shippingService.getCompanyList());
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/shipping/company/list", "ERROR");
        }
    }


}
