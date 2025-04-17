package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.BusinessLicense;
import kr.co.pplus.store.api.jpa.service.BusinessLicenseService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BusinessLicenseController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessLicenseController.class);

    @Autowired
    BusinessLicenseService businessLicenseService ;


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/businessLicense/insertOrUpdate")
    public Map<String, Object> insertOrUpdate(Session session, @RequestBody BusinessLicense businessLicense) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", businessLicenseService.insertOrUpdate(businessLicense));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/businessLicense/get")
    public Map<String, Object> getBusinessLicense(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", businessLicenseService.getBusinessLicense(pageSeqNo));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/businessLicense/validNumber")
    public Map<String, Object> isValidCorperationNumber(Session session, String number) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", businessLicenseService.isValidCorperationNumber(number));

    }
}
