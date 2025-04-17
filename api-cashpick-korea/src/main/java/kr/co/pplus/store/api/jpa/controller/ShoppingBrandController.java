package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.ShoppingBrandService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ShoppingBrandController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingBrandController.class);

    @Autowired
    ShoppingBrandService shoppingBrandService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shoppingBrand/getShoppingBrand")
    public Map<String, Object> getShoppingBrand(Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", shoppingBrandService.getShoppingBrand(seqNo));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shoppingBrand/getShoppingBrandCategoryList")
    public Map<String, Object> getShoppingBrandCategoryList() throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", shoppingBrandService.getShoppingBrandCategoryList());

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shoppingBrand/getShoppingBrandListByCategory")
    public Map<String, Object> getShoppingBrandListByCategory(Session session, Long shoppingBrandCategorySeqNo, Pageable pageable) throws ResultCodeException {

        if(session !=null){
            return result(Const.E_SUCCESS, "row", shoppingBrandService.getShoppingBrandListByCategory(session.getNo(), shoppingBrandCategorySeqNo, pageable));
        }else{
            return result(Const.E_SUCCESS, "row", shoppingBrandService.getShoppingBrandListByCategory(null, shoppingBrandCategorySeqNo, pageable));
        }



    }

}
