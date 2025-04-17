package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Cart;
import kr.co.pplus.store.api.jpa.service.CartService;
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
public class CartController extends RootController {

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    CartService cartService;

    @GetMapping(value = baseUri+"/cart/getCartCount")
    public Map<String,Object> getCartCount(Session session, Integer salesType) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", cartService.getCartCount(session.getNo(), salesType));
    }

    @GetMapping(value = baseUri+"/cart/getCartList")
    public Map<String,Object> getCartList(Session session, Integer salesType) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", cartService.getCartList(session.getNo(), salesType));
    }

    @GetMapping(value = baseUri+"/cart/checkCartPage")
    public Map<String,Object> checkCartPage(Session session, Long pageSeqNo, Integer salesType) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", cartService.checkCartPage(session.getNo(), pageSeqNo, salesType));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cart/saveCart")
    public Map<String,Object> saveCart(Session session, @RequestBody Cart cart) throws ResultCodeException {
        cart.setMemberSeqNo(session.getNo());
        return result(cartService.saveCart(cart));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cart/updateAmount")
    public Map<String,Object> updateAmount(Session session, Long cartSeqNo, Integer amount) throws ResultCodeException {
        return result(cartService.updateAmount(session, cartSeqNo, amount));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/cart/deleteCart")
    public Map<String,Object> deleteCart(Session session, Long cartSeqNo) throws ResultCodeException {
        return result(cartService.deleteCart(session, cartSeqNo));
    }
}
