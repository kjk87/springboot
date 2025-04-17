package kr.co.pplus.store.api.jpa.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.OrderPurchase;
import kr.co.pplus.store.api.jpa.model.OrderPurchaseDetail;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayRequest;
import kr.co.pplus.store.api.jpa.model.ftlink.FTLinkPayResponse;
import kr.co.pplus.store.api.jpa.service.OrderPurchaseService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class OrderPurchaseController extends RootController {

    private Logger logger = LoggerFactory.getLogger(OrderPurchaseController.class);

    @Autowired
    OrderPurchaseService orderPurchaseService;

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getOrderPurchase")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getOrderPurchase(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getOrderPurchase(seqNo));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getOrderPurchaseListByMemberSeqNo")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getOrderPurchaseListByMemberSeqNo(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getOrderPurchaseListByMemberSeqNo(session.getNo(), pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getTicketPurchaseListByMemberSeqNo")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getTicketPurchaseListByMemberSeqNo(Session session, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getTicketPurchaseListByMemberSeqNo(session.getNo(), pageable));
    }


    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getOrderPurchaseListByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getOrderPurchaseListByPageSeqNo(Session session, Long pageSeqNo, String status, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getOrderPurchaseListByPageSeqNo(pageSeqNo, status, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getOrderPurchaseTotalData")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getTotalData(Session session, Long pageSeqNo, String startDateTime, String endDateTime) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getOrderPurchaseTotalData(pageSeqNo, startDateTime, endDateTime));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getOrderPurchaseStatistics")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getOrderPurchaseStatistics(Session session, Long pageSeqNo, String startDateTime, String endDateTime) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getOrderPurchaseStatistics(pageSeqNo, startDateTime, endDateTime));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getPopularOrderPurchaseMenuList")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getPopularOrderPurchaseMenuList(Session session, Long pageSeqNo, String startDateTime, String endDateTime) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", orderPurchaseService.getPopularOrderPurchaseMenuList(pageSeqNo, startDateTime, endDateTime));
    }



    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getOrderPurchaseListByPageSeqNoAndDate")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getOrderPurchaseListByPageSeqNoAndDate(Session session, Long pageSeqNo, String startDateTime, String endDateTime, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getOrderPurchaseListByPageSeqNoAndDate(pageSeqNo, startDateTime, endDateTime, pageable));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/getTicketPurchaseListByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchaseDetail.class)))
    public Map<String, Object> getTicketPurchaseListByPageSeqNo(HttpServletRequest request, Session session, Long pageSeqNo, Integer status, String nickName, String phone, String startDateTime, String endDateTime, Pageable pageable) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.getTicketPurchaseListByPageSeqNo(request, pageSeqNo, status, nickName, phone, startDateTime, endDateTime, pageable));
    }



    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/purchase")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderPurchase.class)))
    public Map<String, Object> purchase(Session session, @RequestBody OrderPurchase orderPurchase) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", orderPurchaseService.purchase(session, orderPurchase));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/ftlink/pay")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = FTLinkPayResponse.class)))
    public Map<String, Object> ftlinkPay(Session session, @RequestBody FTLinkPayRequest ftLinkPayRequest) throws ResultCodeException {

        FTLinkPayResponse res = orderPurchaseService.ftlinkPay(ftLinkPayRequest);

        if (res.getErrCode().equals("0000") || res.getErrCode().equals("00")) {
            return result(Const.E_SUCCESS, "row", res);
        }else{
            return result(Const.E_INVALID_BUY, "row", res);
        }
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/bootpay/verify")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> verifyBootPay(Session session, String orderId, String receiptId) throws ResultCodeException {

        return result(orderPurchaseService.verifyBootPay(session, orderId, receiptId));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/useTicket")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> useTicket(Session session, Long orderPurchaseSeqNo) throws ResultCodeException {

        return result(orderPurchaseService.useTicket(session, orderPurchaseSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/cancelOrderPurchaseUser")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> cancelOrderPurchaseUser(Session session, Long orderPurchaseSeqNo, String memo) throws ResultCodeException {

        return result(orderPurchaseService.cancelOrderPurchaseUser(session, orderPurchaseSeqNo, memo));
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/cancelOrderPurchase")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> cancelOrderPurchase(Long pageSeqNo, Long orderPurchaseSeqNo, String memo) throws ResultCodeException {

        return result(orderPurchaseService.cancelOrderPurchase(pageSeqNo, orderPurchaseSeqNo, memo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/completeOrderPurchase")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> completeOrderPurchase(Session session, Long pageSeqNo, Long orderPurchaseSeqNo) throws ResultCodeException {

        return result(orderPurchaseService.completeOrderPurchase(pageSeqNo, orderPurchaseSeqNo));
    }


    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/orderPurchase/ftlinkPayDecide")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> ftlinkDecide(Long pageSeqNo, Long orderPurchaseSeqNo) throws ResultCodeException {

        return result(orderPurchaseService.ftlinkPayDecide(pageSeqNo, orderPurchaseSeqNo));
    }
}
