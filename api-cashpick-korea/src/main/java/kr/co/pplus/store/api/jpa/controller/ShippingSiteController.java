package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.ShippingSite;
import kr.co.pplus.store.api.jpa.service.ShippingSiteService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCardException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ShippingSiteController extends RootController {

    private Logger logger = LoggerFactory.getLogger(ShippingSiteController.class);

    @Autowired
    ShippingSiteService shippingSiteService;

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/shippingSite/checkIslandsRegion")
    public Map<String, Object> checkIslandsRegion(String postCode) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "row", shippingSiteService.checkIslandsRegion(postCode));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/shippingSite/checkIslandsRegion", "ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/shippingSite/getIsLandsRegion")
    public Map<String, Object> getIsLandsRegion(String postCode) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "row", shippingSiteService.getIsLandsRegion(postCode));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/shippingSite/getIsLandsRegion", "ERROR");
        }
    }

    @GetMapping(value = baseUri + "/shippingSite/list")
    public Map<String, Object> getShippingSiteList(Session session) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", shippingSiteService.getShippingSiteByMemberSeqNo(session.getNo()));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/shippingSite/list", "ERROR");
        }
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shippingSite/insert")
    public Map<String, Object> cardRegister(Session session, @RequestBody ShippingSite shippingSite) throws ResultCodeException {

        ShippingSite site = shippingSiteService.insertSite(session, shippingSite);
        return result(Const.E_SUCCESS, "row", site);

    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shippingSite/update")
    public Map<String, Object> updateRepresentCard(Session session, @RequestBody ShippingSite shippingSite) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", shippingSiteService.updateShippingSite(session, shippingSite));
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/shippingSite/delete")
    public Map<String, Object> deleteCard(Session session, @RequestParam(value = "seqNo") Long seqNo) throws ResultCodeException {
        shippingSiteService.deleteShippingSite(session, seqNo);
        return result(Const.E_SUCCESS);

    }


}
