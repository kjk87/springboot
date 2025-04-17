package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.PopupManageService;
import kr.co.pplus.store.api.util.AppUtil;
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
public class PopupManageController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(PopupManageController.class);

    @Autowired
    PopupManageService popupManageService;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/popupMange/getPopupList")
    public Map<String, Object> getPopupList(Session session, String platform, String appType) throws ResultCodeException {
        if (AppUtil.isEmpty(appType)) {
            appType = Const.APP_TYPE_LUCKYBOL;
        }


        return result(Const.E_SUCCESS, "rows", popupManageService.getPopupList(platform, appType));

    }

}
