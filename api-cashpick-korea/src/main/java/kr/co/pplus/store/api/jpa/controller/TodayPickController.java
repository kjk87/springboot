package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.TodayPickJoinOnly;
import kr.co.pplus.store.api.jpa.service.TodayPickService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TodayPickController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(TodayPickController.class);

    @Autowired
    TodayPickService todayPickService;


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getTodayPickList")
    public Map<String, Object> getTodayPickList(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", todayPickService.getTodayPickList());

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getTodayPickList2")
    public Map<String, Object> getTodayPickList2(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.getTodayPickList(pageable));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getTodayPick")
    public Map<String, Object> getTodayPick(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.getTodayPick(seqNo));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getTodayPickQuestionList")
    public Map<String, Object> getTodayPickQuestionList(Session session, Long todayPickSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", todayPickService.getTodayPickQuestionList(todayPickSeqNo));

    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getMyTodayPick")
    public Map<String, Object> getMyTodayPick(Session session, Long todayPickSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.getMyTodayPick(session, todayPickSeqNo));

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getTodayPickWinnerList")
    public Map<String, Object> getTodayPickWinnerList(Session session, Long todayPickSeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.getTodayPickWinnerList(todayPickSeqNo, pageable));

    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/todayPickWinConfirm")
    public Map<String, Object> todayPickWinConfirm(Session session, Long todayPickJoinSeqNo) throws ResultCodeException {

        todayPickService.todayPickWinConfirm(session, todayPickJoinSeqNo);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/todayPickJoin")
    public Map<String, Object> todayPickJoin(Session session, @RequestBody TodayPickJoinOnly todayPickJoin) throws ResultCodeException {

        todayPickService.todayPickJoin(session, todayPickJoin);
        return result(Const.E_SUCCESS);

    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/getTodayPickReviewList")
    public Map<String, Object> getTodayPickReviewList(Session session, Long todayPickSeqNo, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.getTodayPickReviewList(todayPickSeqNo, pageable));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/insertTodayPickReview")
    public Map<String, Object> insertTodayPickReview(Session session, Long todayPickSeqNo, String review) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.insertTodayPickReview(session.getNo(), todayPickSeqNo, review));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/updateTodayPickReview")
    public Map<String, Object> updateTodayPickReview(Session session, Long todayPickReviewSeqNo, String review) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", todayPickService.updateTodayPickReview(session.getNo(), todayPickReviewSeqNo, review));

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/todayPick/deleteTodayPickReview")
    public Map<String, Object> deleteTodayPickReview(Session session, Long todayPickReviewSeqNo) throws ResultCodeException {

        todayPickService.deleteTodayPickReview(session.getNo(), todayPickReviewSeqNo);
        return result(Const.E_SUCCESS);

    }

}
