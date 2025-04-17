package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.ShoppingGroupService;
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
public class ShoppingGroupController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingGroupController.class);

    @Autowired
    ShoppingGroupService shoppingGroupService ;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shoppingGroup/getShoppingGroup")
    public Map<String, Object> getShoppingGroup(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", shoppingGroupService.getShoppingGroup(seqNo));

    }

}
