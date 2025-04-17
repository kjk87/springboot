package kr.co.pplus.store.api.jpa.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.service.MenuService;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.BaseResponse;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class MenuController extends RootController {

    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    MenuService menuService ;

    @SkipSessionCheck
    @GetMapping(value = baseUri+"/menu/getOrderMenuGroupList")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(schema = @Schema(implementation = OrderMenuGroup.class)))
    public Map<String,Object> getOrderMenuGroupList(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", menuService.getOrderMenuGroupList(pageSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri+"/menu/getOrderMenuGroupWithMenuList")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(schema = @Schema(implementation = OrderMenuGroupWithMenu.class)))
    public Map<String,Object> getOrderMenuGroupWithMenuList(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", menuService.getOrderMenuGroupWithMenuList(pageSeqNo));
    }

    @GetMapping(value = baseUri+"/menu/getOrderMenuListFromCS")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(schema = @Schema(implementation = OrderMenu.class)))
    public Map<String,Object> getOrderMenuListFromCS(Session session, Long pageSeqNo, Long groupSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", menuService.getOrderMenuListFromCS(pageSeqNo, groupSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri+"/menu/getDelegateOrderMenuList")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(schema = @Schema(implementation = OrderMenu.class)))
    public Map<String,Object> getDelegateOrderMenuList(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", menuService.getDelegateOrderMenuList(pageSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri+"/menu/getMenu")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = OrderMenuWithOption.class)))
    public Map<String,Object> getMenu(Session session, Long seqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", menuService.getMenu(seqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/insertReview")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> insertReview(Session session, @RequestBody OrderMenuReview orderMenuReview) throws ResultCodeException {

        return result(menuService.insertReview(orderMenuReview));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/updateReview")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateReview(Session session, @RequestBody OrderMenuReview orderMenuReview) throws ResultCodeException {

        return result(menuService.updateReview(orderMenuReview));
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/deleteReview")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> deleteReview(Session session, Long seqNo) throws ResultCodeException {

        return result(menuService.deleteReview(seqNo));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/updateReviewReply")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateReviewReply(Session session, @RequestBody OrderMenuReview orderMenuReview) throws ResultCodeException {

        return result(menuService.updateReviewReply(session, orderMenuReview));
    }

    @GetMapping(value = baseUri + "/menu/getReviewByMemberSeqNo")
    @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = OrderMenuReview.class)))
    public Map<String, Object> getReviewByMemberSeqNo(Session session, Pageable pageable, HttpServletRequest request) throws ResultCodeException {
        Page<OrderMenuReview> page = menuService.getReviewByMemberSeqNo(session, pageable);

        return result(Const.E_SUCCESS, "row", page);

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/menu/getReviewByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = OrderMenuReview.class)))
    public Map<String, Object> getReviewByPageSeqNo(Session session, Pageable pageable, Long pageSeqNo) throws ResultCodeException {
        Page<OrderMenuReview> page = menuService.getReviewByPageSeqNo(pageSeqNo, pageable);

        return result(Const.E_SUCCESS, "row", page);

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/menu/getReviewCountGroupByEvalByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewCountEval.class))))
    public Map<String, Object> getReviewCountGroupByEvalByPageSeqNo(Long pageSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", menuService.getReviewCountGroupByEvalByPageSeqNo(pageSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/updateOrderMenuDelegate")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateOrderMenuDelegate(Session session, Long seqNo, Boolean delegate) throws ResultCodeException {

        menuService.updateOrderMenuDelegate(seqNo, delegate);
        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/updateOrderMenuSoldOut")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateOrderMenuSoldOut(Session session, Long seqNo, Boolean isSoldOut) throws ResultCodeException {

        menuService.updateOrderMenuSoldOut(seqNo, isSoldOut);
        return result(Const.E_SUCCESS);
    }


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/menu/updateOrderMenuTodaySoldOut")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateOrderMenuTodaySoldOut(Session session, Long seqNo, Boolean isResume) throws ResultCodeException {

        menuService.updateOrderMenuTodaySoldOut(seqNo, isResume);
        return result(Const.E_SUCCESS);
    }


}
