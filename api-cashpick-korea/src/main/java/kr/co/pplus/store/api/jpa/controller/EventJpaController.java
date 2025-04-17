package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.EventBuy;
import kr.co.pplus.store.api.jpa.model.EventReplyOnly;
import kr.co.pplus.store.api.jpa.model.EventReview;
import kr.co.pplus.store.api.jpa.service.EventJpaService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EventJpaController extends RootController {

    private Logger logger = LoggerFactory.getLogger(EventJpaController.class);

    @Autowired
    EventJpaService eventJpaService;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/policy/list")
    public Map<String,Object> getEventPolicyList(Session session, Long pageSeqNo, Long eventSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", eventJpaService.getEventPolicyList(pageSeqNo, eventSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/insertEventReview")
    public Map<String,Object> insertEventReview(Session session, @RequestBody EventReview eventReview) throws ResultCodeException {
        return result(eventJpaService.insertEventReview(eventReview));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/updateEventReview")
    public Map<String,Object> updateEventReview(Session session, @RequestBody EventReview eventReview) throws ResultCodeException {
        return result(eventJpaService.updateEventReview(eventReview));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getEventReviewList")
    public Map<String,Object> getEventReviewList(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        if(session == null){
            return result(Const.E_SUCCESS, "row", eventJpaService.getEventReviewDetailList(null, pageable));
        }
        return result(Const.E_SUCCESS, "row", eventJpaService.getEventReviewDetailList(session, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getMyEventReviewList")
    public Map<String,Object> getMyEventReviewDetailList(Session session, HttpServletRequest request, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        return result(Const.E_SUCCESS, "row", eventJpaService.getMyEventReviewDetailList(session, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getEventReview")
    public Map<String,Object> getMyEventReviewDetailList(Session session, Long seqNo) throws ResultCodeException {

        if(session == null){
            return result(Const.E_SUCCESS, "row", eventJpaService.getEventReviewDetail(null, seqNo));
        }
        return result(Const.E_SUCCESS, "row", eventJpaService.getEventReviewDetail(session, seqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/reply/insert")
    public Map<String,Object> insertEventReply(Session session, @RequestBody EventReplyOnly eventReply) throws ResultCodeException {
        eventReply.setMemberSeqNo(session.getNo());
        return result(eventJpaService.insertEventReply(eventReply));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/reply/update")
    public Map<String,Object> updateEventReply(Session session, @RequestBody EventReplyOnly eventReply) throws ResultCodeException {
        eventReply.setMemberSeqNo(session.getNo());
        return result(eventJpaService.updateEventReply(eventReply));
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/reply/delete")
    public Map<String,Object> deleteEventReply(Session session, Long seqNo) throws ResultCodeException {
        return result(eventJpaService.deleteEventReply(seqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getEventReplyListByEventReviewSeqNo")
    public Map<String,Object> getEventReplyListByEventReviewSeqNo(Session session, Pageable pageable, Long eventReviewSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", eventJpaService.getEventReplyListByEventReviewSeqNo(eventReviewSeqNo, pageable));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getEventReplyListByEventSeqNoAndEventWinSeqNo")
    public Map<String,Object> getEventReplyListByEventSeqNoAndEventWinSeqNo(Session session, Pageable pageable, Long eventSeqNo, Integer eventWinSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", eventJpaService.getEventReplyListByEventSeqNoAndEventWinSeqNo(eventSeqNo, eventWinSeqNo, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getEventReplyListByEventWinId")
    public Map<String,Object> getEventReplyListByEventWinId(Session session, Pageable pageable, Long eventWinId) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", eventJpaService.getEventReplyListByEventWinId(eventWinId, pageable));
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/updateJoinTypeAndJoinTermByCode")
    public Map<String,Object> updateJoinTypeAndJoinTermByCode(String code, String joinType, Integer joinTerm) throws ResultCodeException {
        eventJpaService.updateJoinTypeAndJoinTermByCode(code, joinType, joinTerm);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/buy")
    public Map<String,Object> eventBuy(Session session, @RequestBody EventBuy eventBuy) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", eventJpaService.eventBuy(session, eventBuy));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/buy/list")
    public Map<String,Object> eventBuyList(Session session, @RequestBody EventBuy eventBuy) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", eventJpaService.eventBuyList(session, eventBuy));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getLottoJoinList")
    public Map<String,Object> getLottoJoinList(Session session, Long eventSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", eventJpaService.getLottoJoinList(session, eventSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getLottoWinNumberList")
    public Map<String,Object> getLottoWinNumberList(Session session, Long eventSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", eventJpaService.getLottoWinNumberList(eventSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getLottoWinnerCount")
    public Map<String,Object> getLottoWinnerCount(Session session, Long eventSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", eventJpaService.getLottoWinnerCount(eventSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/event/getLottoWinnerList")
    public Map<String,Object> getLottoWinnerList(Session session, Pageable pageable, Long eventSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", eventJpaService.getLottoWinnerList(eventSeqNo, pageable));
    }

}
