package kr.co.pplus.store.api.jpa.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.service.ProductService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidProductException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "ProductController", description = "상품관련 api")
public class ProductController extends RootController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;


    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/option")
    @ApiResponse(responseCode = "200", description = "row : {} 형태", content = @Content(schema = @Schema(implementation = ProductOptionTotal.class)))
    public Map<String, Object> getProductOptionTotal(@RequestParam(value = "productSeqNo", required = true) Long productSeqNo) {
        ProductOptionTotal optionTotal = productService.getProductOptionTotal(productSeqNo);
        return result(Const.E_SUCCESS, "row", optionTotal);
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/getProductImageList")
    @ApiResponse(responseCode = "200", description = "rows : []형태", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductImage.class))))
    public Map<String, Object> getProductImageList(@RequestParam(value = "productSeqNo") Long productSeqNo,
                                                   @RequestParam(value = "deligate") Boolean deligate) {
        List<ProductImage> productImageList = productService.getProductImageList(productSeqNo, deligate);
        return result(Const.E_SUCCESS, "rows", productImageList);
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/getCountByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "row : int 형태", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCountByPageSeqNoOnlyNormal(Session session, Long pageSeqNo) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", productService.getCountByPageSeqNoOnlyNormal(pageSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/getCountByPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/shipTypeIsLuckybol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })

    public Map<String, Object> getProductPriceListShipTypeIsLuckyBol(Session session, Pageable pageable, HttpServletRequest request,
                                                                     @RequestParam(value = "first", required = false) Long first,
                                                                     @RequestParam(value = "second", required = false) Long second,
                                                                     @RequestParam(value = "third", required = false) Long third,
                                                                     @RequestParam(value = "pick", required = false) Boolean pick,
                                                                     @RequestParam(value = "search", required = false) String search) throws ResultCodeException {

        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = null;
            if(session != null){
                page = productService.getProductPriceListShipTypeIsLuckyBol(first, second, third, session.getNo(), pick, search, pageable);
            }else{
                page = productService.getProductPriceListShipTypeIsLuckyBol(first, second, third, 0L, pick, search, pageable);
            }

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/shipTypeIsLuckybol[GET]", "select.product ERROR");
        }

    }


    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/getProductPriceListShipTypeByShoppingGroup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })

    public Map<String, Object> getProductPriceListShipTypeByShoppingGroup(Session session, Pageable pageable, HttpServletRequest request, Long shoppingGroupSeqNo) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        Page<ProductPrice> page = null;
        if(session != null){
            page = productService.getProductPriceListShipTypeByShoppingGroup(shoppingGroupSeqNo, session.getNo(), pageable);
        }else{
            page = productService.getProductPriceListShipTypeByShoppingGroup(shoppingGroupSeqNo, 0L, pageable);
        }

        return result(Const.E_SUCCESS, "row", page);

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/getProductPriceListShipTypeByShoppingBrand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })

    public Map<String, Object> getProductPriceListShipTypeByShoppingBrand(Session session, Pageable pageable, HttpServletRequest request, Long shoppingBrandSeqNo) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
        pageable = this.nativePageable(request, pageable, sortMap);
        Page<ProductPrice> page = null;
        if(session != null){
            page = productService.getProductPriceListShipTypeByShoppingBrand(shoppingBrandSeqNo, session.getNo(), pageable);
        }else{
            page = productService.getProductPriceListShipTypeByShoppingBrand(shoppingBrandSeqNo, 0L, pageable);
        }

        return result(Const.E_SUCCESS, "row", page);

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/getProductPriceListShipTypeByRandom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListShipTypeByRandom(Session session, HttpServletRequest request) throws ResultCodeException {

        List<ProductPrice> list = null;
        if(session != null){
            list = productService.getProductPriceListShipTypeByRandom(session.getNo());
        }else{
            list = productService.getProductPriceListShipTypeByRandom(0L);
        }

        return result(Const.E_SUCCESS, "rows", list);

    }


    @GetMapping(value = baseUri + "/product/price/shipTypeByPageSeqNo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListShipTypeByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
                                                                      @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
                                                                      @RequestParam(value = "first", required = false) Long first,
                                                                      @RequestParam(value = "second", required = false) Long second,
                                                                      @RequestParam(value = "third", required = false) Long third,
                                                                      @RequestParam(value = "isWholesale", required = false) Boolean isWholesale) throws ResultCodeException {
        try {

            if (isWholesale == null) {
                isWholesale = false;
            }

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListShipTypeByPageSeqNo(pageSeqNo, first, second, third, isWholesale, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/shipTypeByPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @GetMapping(value = baseUri + "/product/price/ticketTypeByPageSeqNo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListTicketTypeByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
                                                                      @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
                                                                      @RequestParam(value = "first", required = false) Long first,
                                                                      @RequestParam(value = "second", required = false) Long second,
                                                                      @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {


            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListTicketTypeByPageSeqNo(pageSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/ticketTypeByPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/shipTypeByPageSeqNoOnlyNormal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListShipTypeByPageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                                @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
                                                                                @RequestParam(value = "first", required = false) Long first,
                                                                                @RequestParam(value = "second", required = false) Long second,
                                                                                @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPrice> page = productService.getProductPriceListShipTypeByPageSeqNoOnlyNormal(pageSeqNo, memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/shipTypeByPageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/storeTypeByPageSeqNoOnlyNormal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListStoreTypeByPageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                                @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
                                                                                @RequestParam(value = "first", required = false) Long first,
                                                                                @RequestParam(value = "second", required = false) Long second,
                                                                                @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPrice> page = productService.getProductPriceListStoreTypeByPageSeqNoOnlyNormal(pageSeqNo, memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/storeTypeByPageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/storeTypeByPageSeqNoAndDiscountOnlyNormal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListStoreTypeByPageSeqNoAndDiscountOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                                @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
                                                                                @RequestParam(value = "first", required = false) Long first,
                                                                                @RequestParam(value = "second", required = false) Long second,
                                                                                @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPrice> page = productService.getProductPriceListStoreTypeByPageSeqNoAndDiscountOnlyNormal(pageSeqNo, memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/storeTypeByPageSeqNoAndDiscountOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/isSubscriptionAndIsPrepaymentOnlyNormal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListByIsSubscriptionAndIsPrepaymentOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                                @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
                                                                                @RequestParam(value = "first", required = false) Long first,
                                                                                @RequestParam(value = "second", required = false) Long second,
                                                                                @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPrice> page = productService.getProductPriceListByIsSubscriptionAndIsPrepaymentOnlyNormal(pageSeqNo, memberSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/isSubscriptionAndIsPrepaymentOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/shipTypeByManageSeqNoOnlyNormal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListShipTypeByManageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                                  @RequestParam(value = "manageSeqNo", required = true) Long manageSeqNo,
                                                                                  @RequestParam(value = "first", required = false) Long first,
                                                                                  @RequestParam(value = "second", required = false) Long second,
                                                                                  @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPrice> page = productService.getProductPriceListShipTypeByManageSeqNoOnlyNormal(manageSeqNo, memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/shipTypeByManageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/ticketTypeByManageSeqNoOnlyNormal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPriceWithDistance.class)))

    })
    public Map<String, Object> getProductPriceListTicketTypeByManageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                                    @RequestParam(value = "latitude", required = false) Double latitude,
                                                                                    @RequestParam(value = "longitude", required = false) Double longitude,
                                                                                    @RequestParam(value = "manageSeqNo", required = true) Long manageSeqNo,
                                                                                    @RequestParam(value = "first", required = false) Long first,
                                                                                    @RequestParam(value = "second", required = false) Long second,
                                                                                    @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPriceWithDistance> page = productService.getProductPriceListTicketTypeByManageSeqNoOnlyNormal(latitude, longitude, manageSeqNo, memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/ticketTypeByManageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/shipTypeByPageAndDiscount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListShipTypeByPageAndDiscount(Session session, Pageable pageable, HttpServletRequest request,
                                                                                @RequestParam(value = "first", required = false) Long first,
                                                                                @RequestParam(value = "second", required = false) Long second,
                                                                                @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPrice> page = productService.getProductPriceListShipTypeByPageAndDiscount(memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/shipTypeByPageAndDiscount[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/storeTypeByPageAndDiscountDistanceDesc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPriceWithDistance.class)))
    })
    public Map<String, Object> getProductPriceListStoreTypeByPageAndDiscountDistanceDesc(Session session, Pageable pageable, HttpServletRequest request,
                                                                             @RequestParam(value = "latitude", required = false) Double latitude,
                                                                             @RequestParam(value = "longitude", required = false) Double longitude,
                                                                             @RequestParam(value = "first", required = false) Long first,
                                                                             @RequestParam(value = "second", required = false) Long second,
                                                                             @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPriceWithDistance> page = productService.getProductPriceListStoreTypeByPageAndDiscountDistanceDesc(latitude, longitude, memberSeqNo, first, second, third, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/storeTypeByPageAndDiscountDistanceDesc[GET]", "select.product ERROR");
        }

    }

    @GetMapping(value = baseUri + "/product/price/getPlusSubscriptionTypeOnlyNormalOrderByDistance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPriceWithDistance.class)))
    })
    public Map<String, Object> getPlusSubscriptionTypeOnlyNormalOrderByDistance(Session session, Pageable pageable, HttpServletRequest request,
                                                                                         @RequestParam(value = "latitude", required = false) Double latitude,
                                                                                         @RequestParam(value = "longitude", required = false) Double longitude) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);

            Long memberSeqNo = null;
            if(session != null){
                memberSeqNo = session.getNo();
            }

            Page<ProductPriceWithDistance> page = productService.getPlusSubscriptionTypeOnlyNormalOrderByDistance(memberSeqNo, latitude, longitude, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/getPlusSubscriptionTypeOnlyNormalOrderByDistance[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/shipTypeIsWholesale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListShipTypeIsWholesale(Session session, Pageable pageable, HttpServletRequest request,
                                                                      @RequestParam(value = "first", required = false) Long first,
                                                                      @RequestParam(value = "second", required = false) Long second,
                                                                      @RequestParam(value = "third", required = false) Long third,
                                                                      @RequestParam(value = "popular", required = false) Boolean popular,
                                                                      @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListShipTypeIsWholesale(pageSeqNo, first, second, third, popular, pageable);


            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/shipTypeIsWholesale[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getProductPriceBySeqNo(Session session, @RequestParam(value = "seqNo", required = false) Long seqNo) throws ResultCodeException {

        try {

            ProductPrice productPrice = productService.getProductPriceBySeqNo(seqNo);


            return result(Const.E_SUCCESS, "row", productPrice);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/productSeqNoAndPageSeqNo")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getProductPriceByProductSeqNoAndPageSeqNo(Session session, @RequestParam(value = "productSeqNo", required = true) Long productSeqNo,
                                                                         @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {

        try {

            ProductPrice productPrice = productService.getProductPriceByProductSeqNoAndPageSeqNo(productSeqNo, pageSeqNo);


            return result(Const.E_SUCCESS, "row", productPrice);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/productSeqNoAndPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/price/insert")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> insertProductPrice(Session session, @RequestBody ProductPriceOnly productPriceOnly) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.insertProductPriceOnly(productPriceOnly));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/price/update")
    public Map<String, Object> updateProductPrice(Session session, @RequestBody ProductPriceOnly productPriceOnly) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.saveAndFlushProductPriceOnly(productPriceOnly));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/price/updateStatus")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateProductPriceStatusBySeqNo(Session session, @RequestParam(value = "productPriceSeqNo") Long productPriceSeqNo
            , @RequestParam(value = "status") Integer status) throws ResultCodeException {
        productService.updateProductPriceStatusBySeqNo(productPriceSeqNo, status);

        return result(Const.E_SUCCESS);
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/price/delete")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> deleteProductPrice(Session session, @RequestParam(value = "productPriceSeqNo") Long productPriceSeqNo) throws ResultCodeException {
        productService.updateProductPriceStatusBySeqNo(productPriceSeqNo, -999);

        return result(Const.E_SUCCESS);
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/review/insert")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> insertProductReview(Session session, @RequestBody ProductReview productReview) throws ResultCodeException {

        return result(productService.insertProductReview(productReview));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/review/update")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateProductReview(Session session, @RequestBody ProductReview productReview) throws ResultCodeException {

        return result(productService.updateProductReview(productReview));
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/review/delete")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> deleteProductReview(Session session, Long seqNo) throws ResultCodeException {

        return result(productService.deleteProductReview(seqNo));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/reviewReply/update")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updateProductReviewReply(Session session, @RequestBody ProductReview productReview) throws ResultCodeException {

        return result(productService.updateProductReviewReply(session, productReview));
    }

    @GetMapping(value = baseUri + "/product/review/memberSeqNo")
    @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = ProductReview.class)))
    public Map<String, Object> getProductReviewByMemberSeqNo(Session session, Pageable pageable, HttpServletRequest request) throws ResultCodeException {
        try {

            Page<ProductReview> page = productService.getProductReviewByMemberSeqNo(session, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/memberSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/productSeqNo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductReview.class)))
    })
    public Map<String, Object> getProductReviewByProductSeqNo(Session session, Pageable pageable, HttpServletRequest request,
                                                              @RequestParam(value = "productSeqNo") Long productSeqNo) throws ResultCodeException {
        try {

            Page<ProductReview> page = productService.getProductReviewByProductSeqNo(productSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/productSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/countGroupByEval")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductReviewCountEval.class))))
    public Map<String, Object> getProductReviewCountGroupByEval(Long productPriceSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", productService.getProductReviewCountGroupByEval(productPriceSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/countGroupByEvalByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "rows : []", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductReviewCountEval.class))))
    public Map<String, Object> getProductReviewCountGroupByEvalByPageSeqNo(Long pageSeqNo) throws ResultCodeException {
        return result(Const.E_SUCCESS, "rows", productService.getProductReviewCountGroupByEvalByPageSeqNo(pageSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/productPriceSeqNo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductReview.class)))
    })
    public Map<String, Object> getProductReviewByProductPriceSeqNo(Session session, Pageable pageable, HttpServletRequest request, Long productPriceSeqNo) throws ResultCodeException {
        try {

            Page<ProductReview> page = productService.getProductReviewByProductPriceSeqNo(productPriceSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/productPriceSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/pageSeqNo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductReview.class)))
    })
    public Map<String, Object> getProductReviewByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request, Long pageSeqNo) throws ResultCodeException {
        try {

            Page<ProductReview> page = productService.getProductReviewByPageSeqNo(pageSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/pageSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/getLastProductReviewByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductReview.class)))
    public Map<String, Object> getLastProductReviewByPageSeqNo(Session session, Long pageSeqNo) throws ResultCodeException {
        try {


            return result(Const.E_SUCCESS, "row", productService.getLastProductReviewByPageSeqNo(pageSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/getLastProductReviewByPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/countByProductPriceSeqNo")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCountProductReviewByProductPriceSeqNo(Session session,
                                                                   @RequestParam(value = "productPriceSeqNo") Long productPriceSeqNo) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", productService.getCountProductReviewByProductPriceSeqNo(productPriceSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/countByProductPriceSeqNo[GET]", "select.product ERROR");
        }

    }

    @GetMapping(value = baseUri + "/product/review/countByMemberSeqNo")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCountProductReviewByMemberSeqNo(Session session) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", productService.getCountProductReviewByMemberSeqNo(session.getNo()));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/countByMemberSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/review/countByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCountProductReviewByPageSeqNo(Long pageSeqNo) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", productService.getCountProductReviewByPageSeqNo(pageSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/review/countByPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/like/count")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> getCountProductLike(Session session) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.getCountProductLike(session));
    }

    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/like/check")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> existProductLike(Session session, ProductLikeOnly productLikeOnly) throws ResultCodeException {

        return result(productService.existProductLike(session, productLikeOnly));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/like/insert")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> insertProductLike(Session session, @RequestBody ProductLikeOnly productLikeOnly) throws ResultCodeException {

        return result(productService.insertProductLike(session, productLikeOnly));
    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/like/delete")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> deleteProductLike(Session session, @RequestBody ProductLikeOnly productLikeOnly) throws ResultCodeException {
        return result(productService.deleteProductLike(session, productLikeOnly));
    }

    @GetMapping(value = baseUri + "/product/like/shippingList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductLike.class)))
    })
    public Map<String, Object> getProductLikeByMemberSeqNoShipping(Session session, Pageable pageable, HttpServletRequest request) throws ResultCodeException {
        try {

            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductLike> page = productService.getProductLikeByMemberSeqNoShipping(session, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/like/shippingList[GET]", "select.product ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/info")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductInfo.class)))
    public Map<String, Object> getProductInfoByProductSeqNo(Session session, Long productSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.getProductInfoByProductSeqNo(productSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/auth")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductAuth.class)))
    public Map<String, Object> getProductAuthByProductSeqNo(Session session, Long productSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.getProductAuthByProductSeqNo(productSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/notice/list")
    @ApiResponse(responseCode = "200", description = "rows:[]", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductNotice.class))))
    public Map<String, Object> getProductNoticeListByProductSeqNo(Session session, Long productSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "rows", productService.getProductNoticeListByProductSeqNo(productSeqNo));
    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/price/main")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceRef.class)))
    public Map<String, Object> getMainProductPrice(Session session, Long pageSeqNo) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.getMainProductPrice(pageSeqNo));
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/price/updatePick")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public Map<String, Object> updatePick(Session session, Long productPriceSeqNo, Long pageSeqNo) throws ResultCodeException {

        return result(productService.updatePick(productPriceSeqNo, pageSeqNo));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/regTicketProduct")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> regTicketProduct(Session session, @RequestBody ProductPrice productPrice) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.regTicketProduct(session, productPrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/updateTicketProduct")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> updateTicketProduct(Session session, @RequestBody ProductPrice productPrice) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.updateTicketProduct(session, productPrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/regSubscription")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> regSubscription(Session session, @RequestBody ProductPrice productPrice) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.regSubscription(session, productPrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/updateSubscription")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> updateSubscription(Session session, @RequestBody ProductPrice productPrice) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.updateSubscription(session, productPrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/regMoney")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> regMoney(Session session, @RequestBody ProductPrice productPrice) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.regMoney(session, productPrice));
    }

    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/product/updateMoney")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPriceOnly.class)))
    public Map<String, Object> updateMoney(Session session, @RequestBody ProductPrice productPrice) throws ResultCodeException {

        return result(Const.E_SUCCESS, "row", productService.updateMoney(session, productPrice));
    }

    @GetMapping(value = baseUri + "/product/price/subscriptionTypeByPageSeqNo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "default", description = "content", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    })
    public Map<String, Object> getProductPriceListSubscriptionTypeByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
                                                                        @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
        try {


            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListSubscriptionTypeByPageSeqNo(pageSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/subscriptionTypeByPageSeqNo[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/subscriptionTypeByPageSeqNoOnlyNormal")
    @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getProductPriceListSubscriptionTypeByPageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                              @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
        try {


            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListSubscriptionTypeByPageSeqNoOnlyNormal(pageSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/subscriptionTypeByPageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/moneyTypeByPageSeqNoOnlyNormal")
    @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getProductPriceListMoneyTypeByPageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
                                                                              @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
        try {


            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListMoneyTypeByPageSeqNoOnlyNormal(pageSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/moneyTypeByPageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/getLastSubscriptionTypeByPageSeqNoOnlyNormal")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getLastSubscriptionTypeByPageSeqNoOnlyNormal(Session session,  Long pageSeqNo) throws ResultCodeException {
        try {
            return result(Const.E_SUCCESS, "row", productService.getLastSubscriptionTypeByPageSeqNoOnlyNormal(pageSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/getLastSubscriptionTypeByPageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/product/price/getLastMoneyTypeByPageSeqNoOnlyNormal")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getLastMoneyTypeByPageSeqNoOnlyNormal(Session session,  Long pageSeqNo) throws ResultCodeException {
        try {
            return result(Const.E_SUCCESS, "row", productService.getLastMoneyTypeByPageSeqNoOnlyNormal(pageSeqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/getLastMoneyTypeByPageSeqNoOnlyNormal[GET]", "select.product ERROR");
        }

    }

    @GetMapping(value = baseUri + "/product/price/moneyTypeByPageSeqNo")
    @ApiResponse(responseCode = "200", description = "row : Page<>타입", content = @Content(schema = @Schema(implementation = ProductPrice.class)))
    public Map<String, Object> getProductPriceListMoneyTypeByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
                                                                              @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
        try {


            Map<String, String> sortMap = new HashMap<String, String>();
            pageable = this.nativePageable(request, pageable, sortMap);
            Page<ProductPrice> page = productService.getProductPriceListMoneyTypeByPageSeqNo(pageSeqNo, pageable);

            return result(Const.E_SUCCESS, "row", page);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidProductException("/product/price/moneyTypeByPageSeqNo[GET]", "select.product ERROR");
        }

    }
}
