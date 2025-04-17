package kr.co.pplus.store.api.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.repository.LottoRepository;
import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.AuthService;
import kr.co.pplus.store.mvc.service.EventService;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.dto.EventJoinParam;
import kr.co.pplus.store.type.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EventController extends RootController {
    private final static Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    EventService svc;

    @Autowired
    AuthService authSvc;

    @Autowired
    QueueService queueSvc;

    @Autowired
    LottoRepository lottoRepository;

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/random/prnumber")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    public Map<String, Object> randomPRNumber(Session session) throws Exception {

        return result(Const.E_SUCCESS, "rows", svc.getRandomVirtualNumber());
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/winner/count")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> lottoWinnerCount(Session session,
                                                @RequestParam("lottoTimes") Integer lottoTimes,
                                                @RequestParam(value = "primaryType", required = false) String primaryType) throws Exception {


        Long count = svc.getLottoWinnerCount(lottoTimes, primaryType);
        return result(Const.E_SUCCESS, "row", count);
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/winner")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventWin.class))))
    public Map<String, Object> lottoWinner(Session session, SearchOpt opt,
                                           @RequestParam("lottoTimes") Integer lottoTimes,
                                           @RequestParam(value = "primaryType", required = false) String primaryType) throws Exception {


        List<EventWin> list = svc.getLottoWinner(lottoTimes, primaryType, opt);
        return result(Const.E_SUCCESS, "rows", list);
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/user/join/count")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> LottoUserJoinCount(Session session,
                                                  @RequestParam(value = "lottoTimes", required = true) Integer lottoTimes,
                                                  @RequestParam(value = "primaryType", required = false) String primaryType) throws Exception {


        Long count = svc.getLottoUserJoinCount(session, lottoTimes, primaryType);
        return result(Const.E_SUCCESS, "row", count);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/user/join")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> lottoUserJoin(Session session,
                                             @RequestParam(value = "lottoTimes", required = true) Integer lottoTimes,
                                             @RequestParam(value = "primaryType", required = false) String primaryType) throws Exception {


        List<Event> list = svc.getLottoUserJoinList(session, lottoTimes, primaryType);
        return result(Const.E_SUCCESS, "rows", list);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/winner/user")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventWin.class))))
    public Map<String, Object> LottoWinnerUser(Session session,
                                               @RequestParam(value = "lottoTimes", required = true) Integer lottoTimes,
                                               @RequestParam(value = "primaryType", required = false) String primaryType) throws Exception {

        logger.warn("EventController.LottoWinnerUser() : session.no : " + session.getNo());
        List<EventWin> list = svc.getLottoWinnerUser(session, lottoTimes, primaryType);
        return result(Const.E_SUCCESS, "rows", list);
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/ticketHistory/count")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> lottoTicketHistoryCount(Session session, SearchOpt opt) throws Exception {


        Long count = svc.getLottoTicketHistoryCount(session, opt);
        return result(Const.E_SUCCESS, "row", count);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/ticketHistory")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BolHistory.class))))
    public Map<String, Object> lottoTicketHistory(Session session, SearchOpt opt) throws Exception {


        List<BolHistory> list = svc.getLottoTicketHistory(session, opt);
        return result(Const.E_SUCCESS, "rows", list);
    }

//    @SkipSessionCheck
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/install/**")
//    public Map<String, Object> install(HttpServletRequest request, Session session, Event event, EventJoin join,
//                                       @RequestParam("seq_no") Long seq_no) throws Exception {
//        Session reload = authSvc.getReloadSession(session);
//
//        Map<String, Object> row = null;
//
//        try {
//            row = svc.serializableInstall(reload, event, join, seq_no);
//
//
//        } catch (Exception ex) {
//            throw ex;
//        }
//
//        return result(Const.E_SUCCESS, "row", row);
//    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/checkJoinEnable/**")
    @ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> checkJoinEnable(Session session, Event event) throws Exception {
        Session reload = authSvc.getReloadSession(session);
        svc.checkJoinEnable(reload, event);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/join/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = EventResult.class)))
    public Map<String, Object> join(HttpServletRequest request, Session session, Event event, EventJoin join) throws Exception {
        Session reload = authSvc.getReloadSession(session);

        Map<String, Object> row = null;
        boolean joined = false;
        try {
            row = svc.join(reload, event, join);
            joined = true;

        } catch (Exception ex) {
            throw ex;
        } finally {
            logger.debug(this.getUri(request));
        }

        return result(Const.E_SUCCESS, "row", row);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventJoinCount/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = EventResult.class)))
    public Map<String, Object> getEventJoinCount(Session session, Long eventSeqNo) throws Exception {

        return result(Const.E_SUCCESS, "row", svc.getEventJoinCount(session, eventSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/checkLottoJoinPossible/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = EventResult.class)))
    public Map<String, Object> checkLottoJoinPossible(HttpServletRequest request, Session session, Long eventSeqNo) throws Exception {
        Session reload = authSvc.getReloadSession(session);

        Boolean result = false;
        try {
            Event event = new Event();
            event.setNo(eventSeqNo);
            event = svc.get(event);
            result = svc.checkLottoJoinPossible(reload, event);

        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            logger.debug(this.getUri(request));
        }

        return result(Const.E_SUCCESS, "row", result);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/serializableJoin/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = EventResult.class)))
    public Map<String, Object> serializableJoin(HttpServletRequest request, Session session, Event event, EventJoin join) throws Exception {
        Session reload = authSvc.getReloadSession(session);

        Map<String, Object> row = null;
        boolean joined = false;
        try {
            row = svc.serializableJoin(reload, event, join);
            joined = true;

        } catch (Exception ex) {
            throw ex;
        } finally {
            logger.debug(this.getUri(request));
        }

        return result(Const.E_SUCCESS, "row", row);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/cpaJoin/**")
    public Map<String, Object> cpaJoin(Long memberSeqNo, Long eventSeqNo) throws Exception {

        Map<String, Object> row = null;
        row = svc.cpaJoin(memberSeqNo, eventSeqNo);

        return result(Const.E_SUCCESS, "row", row);
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/joinWithProperties/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = EventResult.class)))
    public Map<String, Object> joinWithProperties(HttpServletRequest request, Session session, @RequestBody EventJoinParam param) throws Exception {

        if (param.getProperties() == null) {
            throw new InvalidArgumentException();
        }

        return join(request, session, param.getEvent(), param);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/updateEventWinAddress/**")
    @ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateEventWinAddress(Session session, @RequestBody EventWin win) throws ResultCodeException {
        return result(svc.updateEventWinAddress(session, win));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/writeImpression/**")
    @ApiResponse(responseCode = "200", description = "base", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> writeImpression(Session session, Long id, String impression) throws ResultCodeException {
        EventWin win = new EventWin();
        win.setId(id);
        win.setImpression(impression);
        return result(svc.updateImpression(session, win));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/get/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Event.class)))
    public Map<String, Object> get(Session session, Event event) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", svc.get(event));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getByCode/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Event.class)))
    public Map<String, Object> getByCode(Session session, Event event) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", svc.getByCode(session, event));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/getList")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getLottoList(Session session, SearchOpt opt,
                                            @RequestParam(value = "primaryType", required = false) String primaryType,
                                            @RequestParam(value = "active", required = false) Boolean active,
                                            @RequestParam(value = "status", required = false) String status) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", svc.getLottoList(primaryType, active, status, opt));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/lotto/getCount")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getLottoCount(Session session,
                                             @RequestParam(value = "primaryType", required = false) String primaryType,
                                             @RequestParam(value = "active", required = false) Boolean active,
                                             @RequestParam(value = "status", required = false) String status) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", svc.getLottoCount(primaryType, active, status));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getByNumber/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Event.class)))
    public Map<String, Object> getByNumber(Session session, SearchOpt opt) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", svc.getValidByNumber(session, session.getDevice(), opt));
    }

//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCountByBatch/**")
//    public Map<String, Object> getCountByBatch(Session session, SearchOpt opt,
//                                               @RequestParam("winAnnounceDate") String winAnnouceDate) {
//        return result(200, "row", svc.getCountByBatch(session, opt, winAnnouceDate));
//    }
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getListByBatch/**")
//    public Map<String, Object> getListByBatch(Session session, SearchOpt opt,
//                                              @RequestParam("winAnnounceDate") String winAnnouceDate) {
//        return result(200, "rows", svc.getListByBatch(session, opt, winAnnouceDate));
//    }

//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinListByBatch/**")
//    public Map<String, Object> getWinListByBatch(Session session, SearchOpt opt,
//                                                 @RequestParam("winAnnounceDate") String winAnnouceDate) {
//        return result(200, "rows", svc.getWinListByBatch(session, opt, winAnnouceDate));
//    }
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinCountByBatch/**")
//    public Map<String, Object> getWinCountByBatch(Session session, SearchOpt opt,
//                                                  @RequestParam("winAnnounceDate") String winAnnouceDate) {
//        return result(200, "rows", svc.getWinCountByBatch(session, opt, winAnnouceDate));
//    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getListByLottoTimes/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getLottoEventList(Session session, @RequestParam("lottoTimes") Integer lottoTimes) {
        return result(200, "rows", svc.getAllForLotto(lottoTimes));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCountByPageSeqNo/**")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCountByPageSeqNo(Session session, SearchOpt opt, String appType, String platform, Long pageSeqNo) {

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            return result(200, "row", svc.getCountByPageSeqNo(session, session.getDevice(), opt, appType, pageSeqNo));
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            return result(200, "row", svc.getCountByPageSeqNo(session, device, opt, appType, pageSeqNo));
        }
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getActiveEventByPageSeqNo/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = Event.class)))
    public Map<String, Object> getActiveEventByPageSeqNo(Session session, SearchOpt opt, String appType, String platform, Long pageSeqNo) {

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            Event event = svc.getActiveEventByPageSeqNo(session, session.getDevice(), opt, appType, pageSeqNo);
            return result(200, "row", event);
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            Event event = svc.getActiveEventByPageSeqNo(session, device, opt, appType, pageSeqNo);
            return result(200, "row", event);
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventListByPageSeqNo/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getEventListByPageSeqNo(Session session, SearchOpt opt, String appType, String platform, Long pageSeqNo) {

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            List<Event> list = svc.getEventListByPageSeqNo(session, session.getDevice(), opt, appType, pageSeqNo);
            return result(200, "rows", list);
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            List<Event> list = svc.getEventListByPageSeqNo(session, device, opt, appType, pageSeqNo);
            return result(200, "rows", list);
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCount/**")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCount(Session session, SearchOpt opt, String appType, String platform, Long groupSeqNo, Boolean isToday, Boolean isLotto) {
        if (opt == null || opt.getFilter() == null || opt.getFilter().size() == 0) {
            if (opt == null)
                opt = new SearchOpt();

            if (opt.getFilter() == null)
                opt.setFilter(new ArrayList<String>());

            opt.getFilter().add("insert");
            opt.getFilter().add("join");
            opt.getFilter().add("move");
            opt.getFilter().add("biz");

        }

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            return result(200, "row", svc.getCount(session, session.getDevice(), opt, appType, groupSeqNo, isToday, isLotto));
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            return result(200, "row", svc.getCount(session, device, opt, appType, groupSeqNo, isToday, isLotto));
        }
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventList/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getEventList(Session session, SearchOpt opt, String appType, String platform, Long groupSeqNo, Boolean isToday, Boolean isLotto) {
        if (opt == null || opt.getFilter() == null || opt.getFilter().size() == 0) {
            if (opt == null)
                opt = new SearchOpt();

            if (opt.getFilter() == null)
                opt.setFilter(new ArrayList<String>());

            opt.getFilter().add("insert");
            opt.getFilter().add("join");
            opt.getFilter().add("move");
            opt.getFilter().add("biz");

        }

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            List<Event> list = svc.getEventList(session, session.getDevice(), opt, appType, groupSeqNo, isToday, isLotto);
            return result(200, "rows", list);
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            List<Event> list = svc.getEventList(session, device, opt, appType, groupSeqNo, isToday, isLotto);
            return result(200, "rows", list);
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getList/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getList(Session session, SearchOpt opt, String appType, String platform, Long groupSeqNo) {
        if (opt == null || opt.getFilter() == null || opt.getFilter().size() == 0) {
            if (opt == null)
                opt = new SearchOpt();

            if (opt.getFilter() == null)
                opt.setFilter(new ArrayList<String>());

            opt.getFilter().add("insert");
            opt.getFilter().add("join");
            opt.getFilter().add("move");
            opt.getFilter().add("biz");

        }

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            List<Event> list = svc.getList(session, session.getDevice(), opt, appType, groupSeqNo);
            return result(200, "rows", list);
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            List<Event> list = svc.getList(session, device, opt, appType, groupSeqNo);
            return result(200, "rows", list);
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventDetailList/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventDetail.class))))
    public Map<String, Object> getEventDetailList(Session session, Long eventSeqNo) {
        return result(200, "rows", svc.getEventDetailList(eventSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventDetailImageList/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventDetailImage.class))))
    public Map<String, Object> getEventDetailImageList(Session session, Long eventSeqNo) {
        return result(200, "rows", svc.getEventDetailImageList(eventSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventDetailItemList/**")
    public Map<String, Object> getEventDetailItemList(Session session, Long eventSeqNo, Long eventDetailSeqNo) {
        return result(200, "rows", svc.getEventDetailItemList(eventSeqNo, eventDetailSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinAnnounceCount/**")
    public Map<String, Object> getWinAnnounceCount(Session session, SearchOpt opt) {
        return result(200, "row", svc.getWinAnnouncedCount(session, session.getDevice(), opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinAnnounceList/**")
    public Map<String, Object> getWinAnnounceList(Session session, SearchOpt opt) {
        return result(200, "rows", svc.getWinAnnouncedList(session, session.getDevice(), opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getGroupAll/**")
    public Map<String, Object> getGroupAll(Session session) {
        return result(200, "rows", svc.getGroupAll());
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCountByGroup/**")
    public Map<String, Object> getCountByGroup(Session session, EventGroup group, SearchOpt opt, String appType, String platform) {
        if (StringUtils.isEmpty(appType)) {
            if (group.getNo() == 1) {
                appType = "luckyball";
            } else {
                appType = "pplus";
            }

        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            return result(200, "row", svc.getCountByGroup(session, session.getDevice(), group, opt, appType));
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            return result(200, "row", svc.getCountByGroup(session, device, group, opt, appType));
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventListByGroup/**")
    public Map<String, Object> getEventListByGroup(Session session, EventGroup group, SearchOpt opt, String appType, String platform) {
        if (StringUtils.isEmpty(appType)) {
            if (group.getNo() == 1) {
                appType = "luckyball";
            } else {
                appType = "pplus";
            }
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            return result(200, "rows", svc.getEventListByGroup(session, session.getDevice(), group, opt, appType));
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            return result(200, "rows", svc.getEventListByGroup(session, device, group, opt, appType));
        }

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getListByGroup/**")
    public Map<String, Object> getListByGroup(Session session, EventGroup group, SearchOpt opt, String appType, String platform) {
        if (StringUtils.isEmpty(appType)) {
            if (group.getNo() == 1) {
                appType = "luckyball";
            } else {
                appType = "pplus";
            }
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            return result(200, "rows", svc.getListByGroup(session, session.getDevice(), group, opt, appType));
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            return result(200, "rows", svc.getListByGroup(session, device, group, opt, appType));
        }

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getAnnounceCountByGroup/**")
    public Map<String, Object> getAnnounceCountByGroup(Session session, EventGroup group, SearchOpt opt) {
        return result(200, "row", svc.getAnnounceCountByGroup(session, session.getDevice(), group, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getAnnounceListByGroup/**")
    public Map<String, Object> getAnnounceListByGroup(Session session, EventGroup group, SearchOpt opt) {
        return result(200, "rows", svc.getAnnounceListByGroup(session, session.getDevice(), group, opt));
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getBannerAll/**")
    public Map<String, Object> getBannerAll(Session session, Event event) {
        return result(Const.E_SUCCESS, "rows", svc.getEventBannerAll(event));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getGiftAll/**")
    public Map<String, Object> getGiftAll(Session session, Event event) {
        return result(Const.E_SUCCESS, "rows", svc.getEventGiftAll(event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getJoinCount/**")
    public Map<String, Object> getJoinCount(Session session, Event event) {
        return result(Const.E_SUCCESS, "row", svc.getEventJoinCount(event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getJoinList/**")
    public Map<String, Object> getJoinList(Session session, Event event, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", svc.getEventJoinList(event, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinCount/**")
    public Map<String, Object> getWinCount(Session session, Event event, SearchOpt opt) {
        return result(Const.E_SUCCESS, "row", event.getNo() == null ? svc.getWinCount(opt) : svc.getEventWinCount(event));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinList/**")
    public Map<String, Object> getWinList(Session session, Event event, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", event.getNo() == null ? svc.getWinList(session, opt) : svc.getEventWinList(session, event, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventWinCountByGiftSeqNo/**")
    public Map<String, Object> getEventWinCountByGiftSeqNo(Session session, Long giftSeqNo) {
        return result(Const.E_SUCCESS, "row", svc.getEventWinCountByGiftSeqNo(giftSeqNo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getEventWinListByGiftSeqNo/**")
    public Map<String, Object> getEventWinListByGiftSeqNo(Session session, Long giftSeqNo, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", svc.getEventWinListByGiftSeqNo(giftSeqNo, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinListOnly/**")
    public Map<String, Object> getWinListOnly(Session session, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", svc.getWinListOnly(opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinCountOnlyPresentByMemberSeqNo/**")
    public Map<String, Object> getWinCountOnlyPresentByMemberSeqNo(Session session) {
        return result(Const.E_SUCCESS, "row", svc.getWinCountOnlyPresentByMemberSeqNo(session.getNo()));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinCountOnlyPresentToday/**")
    public Map<String, Object> getWinCountOnlyPresentToday(Session session) {
        return result(Const.E_SUCCESS, "row", svc.getWinCountOnlyPresentToday());
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinCountOnlyPresent/**")
    public Map<String, Object> getWinCountOnlyPresent(Session session) {
        return result(Const.E_SUCCESS, "row", svc.getWinCountOnlyPresent());
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinListOnlyPresent/**")
    public Map<String, Object> getWinListOnlyPresent(Session session, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", svc.getWinListOnlyPresent(session, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getMyWinCountOnlyPresent/**")
    public Map<String, Object> getMyWinCountOnlyPresent(Session session) {
        return result(Const.E_SUCCESS, "row", svc.getMyWinCountOnlyPresent(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getMyWinListOnlyPresent/**")
    public Map<String, Object> getMyWinListOnlyPresent(Session session, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", svc.getMyWinListOnlyPresent(session, opt));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinBySeqNo")
    public Map<String, Object> getEventWinBySeqNo(Session session, Long eventSeqNo, Integer seqNo) {
        return result(Const.E_SUCCESS, "row", svc.getEventWinBySeqNo(session, eventSeqNo, seqNo));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/updateUseGift")
    public Map<String, Object> updateUseGift(Session session, Long eventSeqNo, Integer seqNo) {
        return result(svc.updateUseGift(eventSeqNo, seqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getImpressionCount/**")
    public Map<String, Object> getImpressionCount(Session session, Event event, SearchOpt opt) {
        return result(Const.E_SUCCESS, "row", event.getNo() == null ? svc.getImpressionCount(opt) : svc.getEventImpressionCount(event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getImpressionList/**")
    public Map<String, Object> getImpressionList(Session session, Event event, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", event.getNo() == null ? svc.getImpressionList(session, opt) : svc.getEventImpressionList(session, event, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getJoinAll/**")
    public Map<String, Object> getJoinAll(Session session, Event event) {
        return result(Const.E_SUCCESS, "rows", svc.getJoinAllByUser(session, event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinAll/**")
    public Map<String, Object> getWinAll(Session session, Event event) {
        return result(Const.E_SUCCESS, "rows", svc.getEventWinAllByUser(session, event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getUserWinCount/**")
    public Map<String, Object> getUserWinCount(Session session) {
        return result(Const.E_SUCCESS, "row", svc.getWinCountByUser(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getNewWinCountByUser/**")
    public Map<String, Object> getNewWinCountByUser(Session session) {
        return result(Const.E_SUCCESS, "row", svc.getNewWinCountByUser(session));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getUserWinList/**")
    public Map<String, Object> getUserWinList(Session session, SearchOpt opt) {
        return result(Const.E_SUCCESS, "rows", svc.getWinListByUser(session, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getPostAdvertiseCount/**")
    public Map<String, Object> getPostAdvertiseCount(Event event) {
        return result(200, "row", svc.getArticleAdvertiseCount(event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getPostAdvertiseList/**")
    public Map<String, Object> getPostAdvertiseList(Session session, Event event, SearchOpt opt) {
        return result(200, "rows", svc.getArticleAdvertiseList(session, event, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getPostAdvertiseCountForEvent/**")
    public Map<String, Object> getPostAdvertiseCountForEvent(Session session, Event event) {
        return result(200, "row", svc.getArticleAdvertiseCountForEvent(session, event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getPostAdvertiseListForEvent/**")
    public Map<String, Object> getPostAdvertiseListForEvent(Session session, Event event, SearchOpt opt) {
        return result(200, "rows", svc.getArticleAdvertiseListForEvent(session, event, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCouponTemplateAdvertiseCount/**")
    public Map<String, Object> getCouponTemplateAdvertiseCount(Session session, Event event) {
        return result(200, "row", svc.getCouponTemplateAdvertiseCount(event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCouponTemplateAdvertiseList/**")
    public Map<String, Object> getCouponTemplateAdvertiseList(Session session, Event event, SearchOpt opt) {
        return result(200, "rows", svc.getCouponTemplateAdvertiseList(session, event, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCouponTemplateAdvertiseCountForEvent/**")
    public Map<String, Object> getCouponTemplateAdvertiseCountForEvent(Session session, Event event) {
        return result(200, "row", svc.getCouponTemplateAdvertiseCountForEvent(session, event));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getCouponTemplateAdvertiseListForEvent/**")
    public Map<String, Object> getCouponTemplateAdvertiseListForEvent(Session session, Event event, SearchOpt opt) {
        return result(200, "rows", svc.getCouponTemplateAdvertiseListForEvent(session, event, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/existsResult/**")
    public Map<String, Object> existsResult(Session session, Event event) {
        Map<String, Boolean> row = new HashMap<String, Boolean>();
        row.put("join", svc.existsEventJoin(session, event) > 0 ? true : false);
        row.put("win", svc.existsEventWin(session, event) > 0 ? true : false);
        return result(200, "row", row);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getBatchWinCountByDate/**")
    public Map<String, Object> getBatchWinCountByDate(Session session, EventWin win, SearchOpt opt) {

        return result(Const.E_SUCCESS, "row", svc.getBatchWinCountByDate(session, win, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getBatchWinListByDate/**")
    public Map<String, Object> getBatchWinListByDate(Session session, EventWin win, SearchOpt opt) {
        return result(Const.E_SUCCESS, "row", svc.getBatchWinListByDate(session, win, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getBatcGiftListByDate/**")
    public Map<String, Object> getBatchGiftListByDate(Session session, EventWin win, SearchOpt opt) {
        return result(Const.E_SUCCESS, "row", svc.getBatchGiftListByDate(session, win, opt));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getBatchAnnounceDateCount/**")
    public Map<String, Object> getBatchAnnounceDateCount(Session session) {

        return result(Const.E_SUCCESS, "row", svc.getBatchAnnounceDateCount());
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getBatchAnnounceDateList/**")
    public Map<String, Object> getBatchAnnounceDateList(Session session, SearchOpt opt) {
        return result(Const.E_SUCCESS, "row", svc.getBatchAnnounceDateList(opt));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/join/ticketHistory/count")
    public Map<String, Object> eventTicketHistoryCount(Session session, SearchOpt opt) throws Exception {
        Long count = svc.getEventTicketHistoryCount(session, opt);
        return result(Const.E_SUCCESS, "row", count);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/join/ticketHistory")
    public Map<String, Object> eventTicketHistory(Session session, SearchOpt opt) throws Exception {

        List<BolHistory> list = svc.getEventTicketHistory(session, opt);
        return result(Const.E_SUCCESS, "rows", list);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/payPoint")
    public Map<String, Object> payPoint(Session session, Long eventSeqNo) throws Exception {

        svc.payPoint(session, eventSeqNo);
        return result(Const.E_SUCCESS);
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getMyBuyJoinCount/**")
    public Map<String, Object> getMyJoinCount(Session session, Event event) {
        return result(Const.E_SUCCESS, "row", svc.getMyBuyJoinCount(session, event));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getMyBuyJoinCountAndBuyType/**")
    public Map<String, Object> getMyBuyJoinCountAndBuyType(Session session, Event event) {
        return result(Const.E_SUCCESS, "row", svc.getMyBuyJoinCountAndBuyType(session, event));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/copyEvent/**")
    public Map<String, Object> copyEvent(Event event) {

        try {
            svc.copyEvent(event);
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getMainBannerLottoList/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getMainBannerLottoList(Session session, String appType, String platform) {

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            List<Event> list = svc.getMainBannerLottoList(session, session.getDevice(), appType);
            return result(200, "rows", list);
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            List<Event> list = svc.getMainBannerLottoList(session, device, appType);
            return result(200, "rows", list);
        }

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getLottoHistoryCount/**")
    @ApiResponse(responseCode = "200", description = "row : int 형태", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getLottoHistoryCount(Session session, String appType, String platform) {

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            return result(200, "row", svc.getLottoHistoryCount(session.getDevice(), appType));
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            return result(200, "row", svc.getLottoHistoryCount(device, appType));
        }

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getLottoHistoryList/**")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    public Map<String, Object> getLottoHistoryList(Session session, SearchOpt opt, String appType, String platform) {

        if (StringUtils.isEmpty(appType)) {
            appType = "pplus";
        }

        if (StringUtils.isEmpty(platform)) {
            platform = "aos";
        }

        if (session != null) {
            List<Event> list = svc.getLottoHistoryList(session, session.getDevice(), opt, appType);
            return result(200, "rows", list);
        } else {
            Device device = new Device();
            device.setPlatform(platform);
            List<Event> list = svc.getLottoHistoryList(session, device, opt, appType);
            return result(200, "rows", list);
        }

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/event/getWinId/**")
    public Map<String, Object> getWinId(Session session, Long id) {

        return result(200, "row", svc.getWinById(id));

    }
}
