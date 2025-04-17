package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.service.BannerService;
import kr.co.pplus.store.api.jpa.service.ComboService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ComboController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ComboController.class);

    @Autowired
    ComboService comboService ;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboEventList")
    public Map<String, Object> getComboEventList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", comboService.getComboEventList());

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboEvent")
    public Map<String, Object> getComboEvent(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getComboEvent(seqNo));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getCombo")
    public Map<String, Object> getCombo(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getCombo(session.getNo()));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboGift")
    public Map<String, Object> getComboGift(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getComboGift());

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboGiftList")
    public Map<String, Object> getComboGiftList(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getComboGiftList(pageable));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboWin")
    public Map<String, Object> getComboWin(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getComboWin(session.getNo()));

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboJoin")
    public Map<String, Object> getComboJoin(Session session, Long comboEventSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getComboJoin(session.getNo(), comboEventSeqNo));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/insertComboJoin")
    public Map<String, Object> insertComboJoin(Session session, Long comboEventSeqNo, Long comboEventExampleSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.insertComboJoin(session.getNo(), comboEventSeqNo, comboEventExampleSeqNo));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/getComboReviewList")
    public Map<String, Object> getComboReviewList(Session session, Long comboEventSeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.getComboReviewList(comboEventSeqNo, pageable));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/insertComboReview")
    public Map<String, Object> insertComboReview(Session session, Long comboEventSeqNo, Long comboJoinSeqNo, String review) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.insertComboReview(session.getNo(), comboEventSeqNo, comboJoinSeqNo, review));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/updateComboReview")
    public Map<String, Object> updateComboReview(Session session, Long comboReviewSeqNo, String review) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", comboService.updateComboReview(session.getNo(), comboReviewSeqNo, review));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/combo/deleteComboReview")
    public Map<String, Object> deleteComboReview(Session session, Long comboReviewSeqNo) throws ResultCodeException {

        comboService.deleteComboReview(session.getNo(), comboReviewSeqNo);
        return result(Const.E_SUCCESS);

    }

}
