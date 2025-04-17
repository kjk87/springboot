package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.GiftishowBuy;
import kr.co.pplus.store.api.jpa.service.GiftishowService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GiftishowController extends RootController {


    @Autowired
    GiftishowService giftishowService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/list")
    public Map<String, Object> getGoodsList(Session session, Pageable pageable, Long categorySeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.getGoodsList(categorySeqNo, pageable)) ;

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/listByBrand")
    public Map<String, Object> getGoodsListByBrand(Session session, Pageable pageable, Long brandSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.getGoodsListByBrand(brandSeqNo, pageable)) ;

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/listByBrandAndMinPrice")
    public Map<String, Object> getGoodsListByBrandAndMinPrice(Session session, Pageable pageable, Long brandSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.getGoodsListByBrandAndMinPrice(brandSeqNo, 5000, pageable)) ;

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/category/list")
    public Map<String, Object> getGifitishowCategoryList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", giftishowService.getGifitishowCategoryList()) ;

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/buy/list")
    public Map<String, Object> getGfitishowBuyList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.getGfitishowBuyList(session.getNo(), pageable)) ;

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/buy/count")
    public Map<String, Object> getGiftishowBuyCount(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.getGiftishowBuyCount(session.getNo())) ;

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/buy/get")
    public Map<String, Object> getGiftishowBuyDetailBySeqNo(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.getGiftishowBuyDetailBySeqNo(seqNo)) ;

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/buy")
    public Map<String, Object> buy(Session session, @RequestBody GiftishowBuy giftishowBuy) throws ResultCodeException {
        giftishowBuy.setMemberSeqNo(session.getNo());
        return result(giftishowService.buy(session, giftishowBuy, false)) ;

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/buyByBol")
    public Map<String, Object> buyByBol(Session session, @RequestBody GiftishowBuy giftishowBuy) throws ResultCodeException {
        giftishowBuy.setMemberSeqNo(session.getNo());
        return result(giftishowService.buy(session, giftishowBuy, true)) ;

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/check")
    public Map<String, Object> checkStatus(Session session, String trId) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", giftishowService.check(trId)) ;

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/resend")
    public Map<String, Object> resendGiftishow(Session session, Long giftshowBuySeqNo) throws ResultCodeException {

        return result(giftishowService.resendGiftishow(giftshowBuySeqNo)) ;

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/getMobileCategoryList")
    public Map<String, Object> getMobileCategoryList() throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", giftishowService.getMobileCategoryList()) ;

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/giftshow/getMobileBrandList")
    public Map<String, Object> getMobileBrandList(Long categorySeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", giftishowService.getMobileBrandList(categorySeqNo)) ;

    }
}
