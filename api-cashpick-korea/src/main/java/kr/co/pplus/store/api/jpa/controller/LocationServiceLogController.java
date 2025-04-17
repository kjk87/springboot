package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.LocationServiceLogService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LocationServiceLogController extends RootController {

    private Logger logger = LoggerFactory.getLogger(LocationServiceLogController.class);

    @Autowired
    LocationServiceLogService locationServiceLogService;

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/locationServiceLog/save")
    public Map<String,Object> save(Session session, String deviceId, String platform, String serviceLog) throws ResultCodeException {

        String loginId = null;
        if(session != null){
            loginId = session.getLoginId();
        }

        locationServiceLogService.saveLog(loginId, deviceId, platform, serviceLog);

        return result(Const.E_SUCCESS);
    }

}
