package kr.co.pplus.store.api.jpa.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.service.PageAdvertiseHistoryService;
import kr.co.pplus.store.api.jpa.service.PageJpaService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class PageJpaController extends RootController {

    private Logger logger = LoggerFactory.getLogger(PageJpaController.class);

    @Autowired
    private PageJpaService pageJpaService;

    @Autowired
    private PageAdvertiseHistoryService pageAdvertiseHistoryService;

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageAdvertiseHistoryListByPageSeqNo/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageAdvertiseHistory.class)))
    })
    public Map<String,Object> getPageAdvertiseHistoryListByPageSeqNo(Session session, Pageable pageable, Long pageSeqNo, String startDatetime, String endDatetime) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageAdvertiseHistoryService.getPageAdvertiseHistoryListByPageSeqNo(pageSeqNo, startDatetime, endDatetime, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageAdvertiseTotalPriceByPageSeqNo/**")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String,Object> getPageAdvertiseTotalPriceByPageSeqNo(Session session, Long pageSeqNo, String startDatetime, String endDatetime) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageAdvertiseHistoryService.getPageAdvertiseTotalPriceByPageSeqNo(pageSeqNo, startDatetime, endDatetime));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageBalanceListByPageSeqNo/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = PageBalance.class)))
    public Map<String,Object> getPageBalanceListByPageSeqNo(Session session, Pageable pageable, Long pageSeqNo, String startDate, String endDate) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageBalanceListByPageSeqNo(pageSeqNo, startDate, endDate, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageBalanceTotalPriceByPageSeqNo/**")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String,Object> getPageBalanceTotalPriceByPageSeqNo(Session session, Long pageSeqNo, String startDate, String endDate) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageBalanceTotalPriceByPageSeqNo(pageSeqNo, startDate, endDate));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageListWithProductPrice/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    })
    public Map<String,Object> getPageListWithProductPrice(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageListWithProductPrice(session, request, latitude, longitude, pageable));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageListWithSubscription/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = kr.co.pplus.store.api.jpa.model.Page.class)))
    })
    public Map<String,Object> getPageListWithSubscription(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageListWithSubscription(session, request, latitude, longitude, pageable));
    }


    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getDeliveryPageList/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    })
    public Map<String,Object> getDeliveryPageList(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getDeliveryPageList(session, request, latitude, longitude, categoryMajorSeqNo, categoryMinorSeqNo, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getDeliveryPageListByKeyword/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    })
    public Map<String,Object> getDeliveryPageListByKeyword(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, String keyword) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getDeliveryPageListByKeyword(session, request, latitude, longitude, categoryMajorSeqNo, categoryMinorSeqNo, keyword, pageable));
    }



    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getVisitPageList/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    })
    public Map<String,Object> getVisitPageList(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getVisitPageList(session, request, latitude, longitude, categoryMajorSeqNo, categoryMinorSeqNo, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getVisitPageListByKeyword/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageWithAvgEval.class)))
    })
    public Map<String,Object> getVisitPageListByKeyword(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, String keyword) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getVisitPageListByKeyword(session, request, latitude, longitude, categoryMajorSeqNo, categoryMinorSeqNo, keyword, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getServicePageList/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    })
    public Map<String,Object> getServicePageList(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getServicePageList(session, request, latitude, longitude, categoryMajorSeqNo, categoryMinorSeqNo, pageable));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPage2/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = PageWithAvgEval.class)))
    public Map<String,Object> getPage2(Session session, Long seqNo, Double latitude, Double longitude) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageBySeqNo(session, seqNo, latitude, longitude));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPage2ByMemberSeqNo/**")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    public Map<String,Object> getPage2ByMemberSeqNo(Long memberSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageByMemberSeqNo(memberSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getVisitPageListByArea/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageWithAvgEval.class)))
    })
    public Map<String,Object> getVisitPageListByArea(Session session, HttpServletRequest request, Pageable pageable, Double latitude, Double longitude, Double top, Double bottom, Double left, Double right, Long categoryMajorSeqNo, Long categoryMinorSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getVisitPageListByArea(session, request, latitude, longitude, top, bottom, left, right, categoryMajorSeqNo, categoryMinorSeqNo, pageable));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/updateOrderable/**")
    @ApiResponse(responseCode = "200", description = "row : int", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String,Object> updateOrderable(Session session, Long pageSeqNo, Boolean orderable) throws ResultCodeException {
        pageJpaService.updateOrderable(pageSeqNo, orderable);
        return result(Const.E_SUCCESS);
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageListWithPrepayment/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageWithPrepayment.class)))
    })
    public Map<String,Object> getPageListWithPrepayment(Session session, Pageable pageable, Double latitude, Double longitude) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageListWithPrepayment(session, latitude, longitude, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageListWithPrepaymentExistVisitLog/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageWithPrepayment.class)))
    })
    public Map<String,Object> getPageListWithPrepaymentExistVisitLog(Session session, Pageable pageable, Double latitude, Double longitude) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageListWithPrepaymentExistVisitLog(session, latitude, longitude, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/page/getPageListWithPageWithPrepaymentPublish/**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = PageDetail.class)))
    })
    public Map<String,Object> getPageListWithPageWithPrepaymentPublish(Session session, Pageable pageable, Double latitude, Double longitude) throws ResultCodeException {
        return result(Const.E_SUCCESS, "row", pageJpaService.getPageListWithPageWithPrepaymentPublish(session, latitude, longitude, pageable));
    }


}
