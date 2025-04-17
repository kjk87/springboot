package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.ThemeCategoryService;
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
public class ThemeCategoryController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ThemeCategoryController.class);

    @Autowired
    ThemeCategoryService themeCategoryService ;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/themeCategory/list")
    public Map<String, Object> getList(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", themeCategoryService.getList());

    }

}
