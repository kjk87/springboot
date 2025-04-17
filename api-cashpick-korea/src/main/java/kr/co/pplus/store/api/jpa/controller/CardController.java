package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Card;
import kr.co.pplus.store.api.jpa.model.DaouCardRequest;
import kr.co.pplus.store.api.jpa.service.CardService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCardException;
import kr.co.pplus.store.exception.InvalidGoodsException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CardController extends RootController {

    private Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    CardService cardService ;

    @GetMapping(value = baseUri + "/card/list")
    public Map<String, Object> selectGoodsDetailByPageSeqNo(Session session) throws ResultCodeException {

        try {
            List<Card> cardList = cardService.getCardListByMemberSeqNo(session.getNo());
            return result(Const.E_SUCCESS, "rows", cardList);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/card/list", "ERROR");
        }

    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/updateRepresent")
    public Map<String, Object> updateRepresentCard(Session session, @RequestParam(value="id", required=true) Long id) throws ResultCodeException {
        try {
            return result(Const.E_SUCCESS, "row", cardService.updateRepresentCard(session.getNo(), id));
        }catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/card/updateRepresent", "ERROR");
        }
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/register")
    public Map<String, Object> cardRegister(Session session, @RequestBody DaouCardRequest daouCardRequest) throws ResultCodeException {

        Card card = cardService.cardRegister(session, daouCardRequest);
        if(card.getId() != null){
            return result(Const.E_SUCCESS, "row", card);
        }else{
            return result(Const.E_INVALID_CARD, "row", card);
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/onlyRegister")
    public Map<String, Object> onlyCardRegister(DaouCardRequest daouCardRequest) throws ResultCodeException {

        Card card = cardService.onlyCardRegister(daouCardRequest);
        if(card.getAutoKey() != null){
            return result(Const.E_SUCCESS, "row", card);
        }else{
            return result(Const.E_INVALID_CARD, "row", card);
        }

    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/card/delete")
    public Map<String,Object> deleteCard(Session session, @RequestParam(value="id", required=true) Long id) throws ResultCodeException {
        try {
            cardService.deleteCard(session.getNo(), id);
            return result(Const.E_SUCCESS);
        }catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/card/delete", "ERROR");
        }

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/regReapPayBillkey")
    public Map<String, Object> regReapPayBillkey(Session session, String data) throws ResultCodeException {

        cardService.regReapPayBillkey(session, data);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/deleteReapPayBillkey")
    public Map<String, Object> deleteReapPayBillkey(Session session, Long reapPayBillKeySeqNo) throws ResultCodeException {

        cardService.deleteReapPayBillkey(session, reapPayBillKeySeqNo);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/changeReaPayBillkeyRepresent")
    public Map<String, Object> changeReaPayBillkeyRepresent(Session session, Long reapPayBillKeySeqNo) throws ResultCodeException {

        cardService.changeReaPayBillkeyRepresent(session, reapPayBillKeySeqNo);

        return result(Const.E_SUCCESS);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/card/getReapPayBillkeyList")
    public Map<String, Object> getReapPayBillkeyList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", cardService.getReapPayBillkeyList(session));
    }




}
