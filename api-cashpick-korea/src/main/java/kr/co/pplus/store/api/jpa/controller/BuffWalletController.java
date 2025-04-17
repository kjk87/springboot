package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.BuffWalletService;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@RestController
public class BuffWalletController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(BuffWalletController.class);

    @Autowired
    BuffWalletService buffWalletService ;

    @Autowired
    UserService userService;

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buffWallet/walletSignUp")
    public Map<String, Object> walletSignUp(Session session, String password) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffWalletService.walletSignUp(session, password));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buffWallet/walletSync")
    public Map<String, Object> walletSync(Session session, String password) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffWalletService.walletSync(session, password));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buffWallet/walletBalance")
    public Map<String, Object> walletBalance(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffWalletService.walletBalance(session));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buffWallet/getBuffCoinBalance")
    public Map<String, Object> getBuffCoinBalance(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffWalletService.getBuffCoinBalance(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buffWallet/exchangeBuffCoinToPoint")
    public Map<String, Object> exchangeBuffCoinToPoint(Session session, BigDecimal exchangeCoin) throws ResultCodeException {

        buffWalletService.exchangeBuffCoinToPoint(session, exchangeCoin);

        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/buffWallet/updateCoin")
    public String updateCoin(HttpServletRequest request, Long memberSeqNo, boolean isIncrease, BigDecimal amount, String type) throws ResultCodeException {

        String token = request.getHeader("adminToken");

        if(!ADMIN_TOKEN.equals(token)){
            throw new NotPermissionException();
        }

        User user = userService.getUser(memberSeqNo);

        return buffWalletService.updateCoin(user, type, isIncrease, amount);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/buffWallet/duplicateUser")
    public Map<String, Object> duplicateUser(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", buffWalletService.duplicateUser(session));
    }

}
