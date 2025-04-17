//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.annotation.SkipSessionCheck;
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.*;
//import kr.co.pplus.store.api.jpa.repository.*;
//import kr.co.pplus.store.api.jpa.service.GoodsService;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.InvalidGoodsDeleteException;
//import kr.co.pplus.store.exception.InvalidGoodsException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class GoodsController extends RootController {
//
//    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);
//
//    private final Long generalCategoryNo = 1L;
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Autowired
//    GoodsLikeRepository goodsLikeRepository;
//
//    @Autowired
//    GoodsWithDateRepository goodsWithDateRepository;
//
//    @Autowired
//    GoodsService goodsService;
//
//    @Autowired
//    GoodsDetailRepository goodsDetailRepository;
//
//    @Autowired
//    private PageRepository pageRepository;
//
//    @Autowired
//    private AttachmentRepository attachmentRepository;
//
//    @Autowired
//    private GoodsImageRepository goodsImageRepository;
//
//    @Autowired
//    private GoodsSalesTypeRepository goodsSalesTypeRepository;
//
//    @Value("${spring.profiles.active}")
//    String activeSpringProfile = "local";
//
//    @Value("${STORE.DEFAULT.LANG}")
//    String defaultLang = "ko";
//
//    @Value("${STORE.GOODS_NEWS_MAX}")
//    Long goodsNewsMax = 1L;
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsException.class)
//    public Map<String, Object> addGoods(Session session, @RequestBody Goods goods) throws ResultCodeException {
//
//
//        try {
//
//            Boolean isLuckyball = false;
//            if (goods.getIsLuckyball() != null) {
//                isLuckyball = goods.getIsLuckyball();
//            }
//
//            String dateStr = AppUtil.localDatetimeNowString();
//
//            goods.setSeqNo(null);
//
//            if (goods.getRegisterType() == null) {
//                goods.setRegister("user");
//                goods.setRegister(session.getName());
//            }
//
//            if (goods.getLang() == null || goods.getLang().trim().length() == 0) {
//                goods.setLang(defaultLang);
//            }
//
//            if (goods.getCategorySeqNo() == null) {
//                goods.setCategorySeqNo(generalCategoryNo);
//            }
//
//            if (goods.getStatus() == null) {
//                goods.setStatus(GoodsStatus.SELL.getStatus());
//            }
//
//            if (goods.getIsHotdeal() == null) {
//                goods.setIsHotdeal(false);
//            }
//
//            if (goods.getIsPlus() == null) {
//                goods.setIsPlus(false);
//            }
//
//            if (goods.getSalesType() == null) {
//                goods.setSalesType(1L);
//            }
//
//            if (StringUtils.isEmpty(goods.getSalesTypes())) {
//                goods.setSalesTypes(goods.getSalesType().toString());
//            }
//
//            if (goods.getBuyableCount() == null) {
//                goods.setBuyableCount(-1);
//            }
//
//            if (goods.getDeliveryFee() == null) {
//                goods.setDeliveryFee(-1);
//            }
//
//            if (goods.getDeliveryAddFee() == null) {
//                goods.setDeliveryAddFee(-1);
//            }
//
//            if (goods.getRefundDeliveryFee() == null) {
//                goods.setRefundDeliveryFee(-1);
//            }
//
//            if (StringUtils.isEmpty(goods.getDetailType())) {
//                goods.setDetailType("general");
//            }
//
//            if (StringUtils.isEmpty(goods.getMarketType())) {
//                goods.setMarketType("retail");
//            }
//
//            List<GoodsImage> detailImageList = null;
//
//            if (goods.getDetailType().equals("image")) {
//                detailImageList = goods.getDetailImageList();
//            }
//
//            goods.setRegDatetime(dateStr);
//            goods.setModDatetime(dateStr);
//            goods.setNewsDatetime(dateStr);
//
//            if (goods.getRewardLuckybol() == null) {
//                goods.setRewardLuckybol(0);
//            }
//
////            if (goods.getPageSeqNo() == null) {
////                Long pageSeqNo = null;
////                //MGK_IMSI
////                if ((activeSpringProfile.equals("local") || activeSpringProfile.equals("dev")) &&
////                        (session.getPage() == null || session.getPage().getNo() == null))
////                    pageSeqNo = 1L;
////                    //MGK_IMSI
////                else
////                    pageSeqNo = session.getPage().getNo();
////                goods.setPageSeqNo(pageSeqNo);
////            }
//
//            if (goods.getIsHotdeal() != null && goods.getIsHotdeal()) {
//                goodsRepository.updateHotdealAsFinish(goods.getPageSeqNo());
//            }
//
//            if (goods.getIsCoupon() != null && goods.getIsCoupon()) {
//                goodsRepository.updateCouponAsFinish(goods.getPageSeqNo());
//            }
//
//            if (goods.getOriginPrice() != null && goods.getPrice() != null) {
//                Float discountRatio = 100 - (goods.getPrice() / goods.getOriginPrice() * 100);
//                goods.setDiscountRatio(discountRatio);
//            }
//
//            List<GoodsImage> goodsImageList = goods.getGoodsImageList();
//
//            if (goods.getIsCoupon() != null && goods.getIsCoupon()) {
//                Integer count = goodsRepository.countByPageSeqNoAndIsCoupon(goods.getPageSeqNo(), true);
//                logger.debug("luckyCoupon count : " + count);
//                if (count == 0) {
//                    logger.debug("luckyCoupon is zero");
//                    goods.setRepresent(true);
//                    goods = goodsRepository.saveAndFlush(goods);
//
//                    kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(goods.getPageSeqNo());
//                    page.setMainGoodsSeqNo(goods.getSeqNo());
//                    pageRepository.save(page);
//                } else {
//                    goods.setRepresent(false);
//                    goods = goodsRepository.saveAndFlush(goods);
//                }
//            } else {
//                goods = goodsRepository.saveAndFlush(goods);
//            }
//
//            if (goodsImageList != null && goodsImageList.size() > 0) {
//
//                for (GoodsImage goodsImage : goodsImageList) {
//                    goodsImage.setGoodsSeqNo(goods.getSeqNo());
//                    goodsImage.setType("thumbnail");
//                    goodsImageRepository.save(goodsImage);
//                }
//
//            } else if (goods.getAttachments() != null) {
//                List<String> imgIdList = (List<String>) goods.getAttachments().get("images");
//                if (imgIdList != null && imgIdList.size() > 0) {
//                    GoodsImage goodsImage = null;
//                    int i = 0;
//                    for (String id : imgIdList) {
//                        i++;
//                        Attachment attachment = attachmentRepository.findById(id);
//                        attachment.getUrl();
//
//                        goodsImage = new GoodsImage();
//                        goodsImage.setImage(attachment.getUrl());
//                        goodsImage.setGoodsSeqNo(goods.getSeqNo());
//                        goodsImage.setArray(i);
//                        goodsImage.setType("thumbnail");
//
//                        goodsImageRepository.save(goodsImage);
//                    }
//                }
//            }
//
//            if (detailImageList != null && detailImageList.size() > 0) {
//
//                for (GoodsImage goodsImage : detailImageList) {
//                    goodsImage.setGoodsSeqNo(goods.getSeqNo());
//                    goodsImage.setType("detail");
//                    goodsImageRepository.save(goodsImage);
//                }
//            }
//
//            String[] salesTypes = goods.getSalesTypes().split(",");
//            for (String salesType : salesTypes) {
//                GoodsSalesType goodsSalesType = new GoodsSalesType();
//                goodsSalesType.setGoodsSeqNo(goods.getSeqNo());
//                goodsSalesType.setSalesTypeSeqNo(Long.valueOf(salesType));
//                goodsSalesTypeRepository.save(goodsSalesType);
//            }
//
//            GoodsPriceOnly goodsPriceOnly = new GoodsPriceOnly();
//            goodsPriceOnly.setPageSeqNo(goods.getPageSeqNo());
//            goodsPriceOnly.setGoodsSeqNo(goods.getSeqNo());
//
//            if (goods.getMarketType().equals("wholesale")) {
//                goodsPriceOnly.setConsumerPrice(goods.getOriginPrice());
//                goodsPriceOnly.setSupplyPrice(goods.getPrice());
//            } else {
//                goodsPriceOnly.setOriginPrice(goods.getOriginPrice());
//                goodsPriceOnly.setPrice(goods.getPrice());
//                goodsPriceOnly.setDiscountRatio(goods.getDiscountRatio());
//            }
//
//            goodsPriceOnly.setRegDatetime(dateStr);
//            goodsPriceOnly.setIsLuckyball(isLuckyball);
//            goodsPriceOnly.setIsWholesale(goods.getMarketType().equals("wholesale"));
//            goodsPriceOnly.setStatus(goods.getStatus());
//            goodsService.saveGoodsPriceOnly(goodsPriceOnly);
//
//
//            return result(Const.E_SUCCESS, "row", goods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("goods data", e);
//        }
//
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsException.class)
//    public Map<String, Object> updateGoods(Session session, @RequestBody Goods goods,
//                                           @RequestParam(value = "countReset", required = false) Boolean countReset) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//
//            if (goods.getSeqNo() == null) {
//                throw new InvalidGoodsException("/goods[PUT]", "goods.seq_no cannot be null");
//            }
//
//            Goods goodsTmp = goodsRepository.findBySeqNo(goods.getSeqNo());
//            if (goodsTmp == null) {
//                throw new InvalidGoodsException("/goods[PUT]", "goods.seq_no not found");
//            }
//
//            goods.setStatus(goodsTmp.getStatus());
//            goods.setSoldCount(goodsTmp.getSoldCount());
//            goods.setModDatetime(dateStr);
//            if (countReset != null && countReset) {
//                goods.setRegDatetime(dateStr);
//                goods.setSoldCount(0L);
//            }
//
//            if (goods.getLang() == null || goods.getLang().trim().length() == 0) {
//                goods.setLang(goodsTmp.getLang());
//            }
//
//            if (goods.getRewardLuckybol() == null) {
//                goods.setRewardLuckybol(goodsTmp.getRewardLuckybol());
//            }
//
//            if (goods.getOriginPrice() != null && goods.getPrice() != null) {
//                Float discountRatio = 100 - (goods.getPrice() / goods.getOriginPrice() * 100);
//                goods.setDiscountRatio(discountRatio);
//            }
//
//            if (goods.getSalesType() == null) {
//                goods.setSalesType(1L);
//            }
//
//            if (StringUtils.isEmpty(goods.getSalesTypes())) {
//                goods.setSalesTypes(goods.getSalesType().toString());
//            }
//
//            if (goods.getBuyableCount() == null) {
//                goods.setBuyableCount(-1);
//            }
//
//            if (goods.getDeliveryFee() == null) {
//                goods.setDeliveryFee(-1);
//            }
//
//            if (goods.getDeliveryAddFee() == null) {
//                goods.setDeliveryAddFee(-1);
//            }
//
//            if (goods.getRefundDeliveryFee() == null) {
//                goods.setRefundDeliveryFee(-1);
//            }
//
//            if (StringUtils.isEmpty(goods.getDetailType())) {
//                goods.setDetailType("general");
//            }
//
//            List<GoodsImage> detailImageList = null;
//
//            if (goods.getDetailType().equals("image")) {
//                detailImageList = goods.getDetailImageList();
//            }
//
//            List<GoodsImage> goodsImageList = goods.getGoodsImageList();
//            goods = goodsRepository.saveAndFlush(goods);
//            goodsLikeRepository.updateExpiredDatetime(goods.getSeqNo(), goods.getExpireDatetime());
//            goodsImageRepository.deleteAllByGoodsSeqNo(goods.getSeqNo());
//
//            if (goodsImageList != null && goodsImageList.size() > 0) {
//
//                for (GoodsImage goodsImage : goodsImageList) {
//                    logger.debug("goodsImage url : " + goodsImage.getImage());
//                    goodsImage.setGoodsSeqNo(goods.getSeqNo());
//                    goodsImage.setType("thumbnail");
//                    goodsImageRepository.save(goodsImage);
//                }
//
//            } else if (goods.getAttachments() != null) {
//                List<String> imgIdList = (List<String>) goods.getAttachments().get("images");
//                if (imgIdList != null && imgIdList.size() > 0) {
//                    GoodsImage goodsImage = null;
//                    Attachment attachment;
//                    int i = 0;
//
//                    for (String id : imgIdList) {
//                        i++;
//                        attachment = attachmentRepository.findById(id);
//                        goodsImage = new GoodsImage();
//                        goodsImage.setImage(attachment.getUrl());
//                        goodsImage.setGoodsSeqNo(goods.getSeqNo());
//                        goodsImage.setArray(i);
//                        goodsImage.setType("thumbnail");
//                        goodsImageRepository.save(goodsImage);
//                    }
//                }
//            }
//
//            if (detailImageList != null && detailImageList.size() > 0) {
//
//                for (GoodsImage goodsImage : detailImageList) {
//                    goodsImage.setGoodsSeqNo(goods.getSeqNo());
//                    goodsImage.setType("detail");
//                    goodsImageRepository.save(goodsImage);
//                }
//            }
//
//            goodsSalesTypeRepository.deleteAllByGoodsSeqNo(goods.getSeqNo());
//            String[] salesTypes = goods.getSalesTypes().split(",");
//            for (String salesType : salesTypes) {
//                GoodsSalesType goodsSalesType = new GoodsSalesType();
//                goodsSalesType.setGoodsSeqNo(goods.getSeqNo());
//                goodsSalesType.setSalesTypeSeqNo(Long.valueOf(salesType));
//                goodsSalesTypeRepository.save(goodsSalesType);
//            }
//
//            List<GoodsPriceOnly> goodsPriceOnlyList = goodsService.getGoodsPriceOnlyByGoodsSeqNo(goods.getSeqNo());
//
//            for (GoodsPriceOnly goodsPriceOnly : goodsPriceOnlyList) {
//
//                if (goodsPriceOnly.getIsWholesale()) {
//                    goodsPriceOnly.setConsumerPrice(goods.getOriginPrice());
//                    goodsPriceOnly.setSupplyPrice(goods.getPrice());
//                } else {
//                    goodsPriceOnly.setOriginPrice(goods.getOriginPrice());
//                    goodsPriceOnly.setPrice(goods.getPrice());
//                    goodsPriceOnly.setDiscountRatio(goods.getDiscountRatio());
//                }
//
//                goodsPriceOnly.setStatus(goods.getStatus());
//                goodsService.saveGoodsPriceOnly(goodsPriceOnly);
//            }
//
//
//            return result(Const.E_SUCCESS, "row", goods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("goods data", e);
//        }
//
//    }
//
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/news")
//    public Map<String, Object> setGoodsAsNews(HttpServletRequest request, Session session, @RequestParam(value = "seqNo", required = true) Long seqNo) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//
//            Goods goods = goodsRepository.findBySeqNo(seqNo);
//            if (goods == null) {
//                throw new Exception("goods not found : " + seqNo);
//            }
//
//            String today = AppUtil.localTodayString();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
//            ZonedDateTime zdt = ZonedDateTime.parse(today, formatter);
//            Date todayTime = Date.from(zdt.toInstant());
//            Long count = goodsRepository.countAllByPageSeqNoAndNewsDatetimeGreaterThan(goods.getPageSeqNo(), todayTime);
//            logger.debug(getUri(request) + todayTime.toString() + ":" + count);
//            if (count >= goodsNewsMax) {
//                throw new Exception("Today maximum count limit(set goods as news) is over !!!");
//            }
//
//            goods.setNewsDatetime(dateStr);
//            goods.setModDatetime(dateStr);
//            goods = goodsRepository.saveAndFlush(goods);
//            return result(Const.E_SUCCESS, "row", goods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/news[PUT]", e);
//        }
//    }
//
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/status")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsException.class)
//    public Map<String, Object> updateGoodsStatus(Session session,
//                                                 @RequestParam(value = "seqNo", required = true) Long seqNo,
//                                                 @RequestParam(value = "status", required = true) Integer status,
//                                                 @RequestParam(value = "count", required = false) Long count,
//                                                 @RequestParam(value = "soldCount", required = false) Long soldCount) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            GoodsWithDate goodsWithDate = goodsWithDateRepository.findBySeqNo(seqNo);
//
//            if (goodsWithDate == null) {
//                throw new InvalidGoodsException("/goods/status[PUT]", "goods.seq_no not found : " + seqNo);
//            }
//
//            goodsWithDate.setStatus(status);
//            if (-2 > status || status > 1)
//                throw new InvalidGoodsException("/goods/status[PUT]", "status is out of range(-2 ~ 1) : " + status);
//
//            if (count != null && count > 0)
//                goodsWithDate.setCount(count);
//            if (soldCount != null) {
//                if (status == 0 && goodsWithDate.getCount() != null && soldCount == goodsWithDate.getCount())
//                    goodsWithDate.setSoldCount(soldCount);
//                else if (status != 0 && goodsWithDate.getCount() != null && soldCount < goodsWithDate.getCount())
//                    goodsWithDate.setSoldCount(soldCount);
//            }
//
//            goodsWithDate.setModDatetime(dateStr);
//            goodsWithDate = goodsWithDateRepository.saveAndFlush(goodsWithDate);
//            goodsService.updateGoodsPriceStatusByGoodsSeqNo(seqNo, status);
//
//            Goods goods = goodsRepository.findBySeqNo(goodsWithDate.getSeqNo());
//
//            if (status == GoodsStatus.SOLD_OUT.getStatus() || status == GoodsStatus.STOP.getStatus()) {
//                goodsLikeRepository.deleteAllByGoodsSeqNo(seqNo);
//            }
//            return result(Const.E_SUCCESS, "row", goods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("goods data", e);
//        }
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/resetCount")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsException.class)
//    public Map<String, Object> updateGoods(Session session,
//                                           @RequestParam(value = "seqNo", required = true) Long seqNo,
//                                           @RequestParam(value = "status", required = true) Integer status,
//                                           @RequestParam(value = "count", required = true) Long count,
//                                           @RequestParam(value = "soldCount", required = false) Long soldCount) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            Goods goods = goodsRepository.findBySeqNo(seqNo);
//            if (goods == null) {
//                throw new Exception("goods data is not exist");
//            }
//            goods.setModDatetime(dateStr);
//            goods.setStatus(status);
//            goods.setCategorySeqNo(count);
//            if (soldCount != null && soldCount == 0L) {
//                goods.setSoldCount(0L);
//            } else if (soldCount != null) { //abnormal case : soldCount 는 0 으로 reset 만 가능..
//                throw new InvalidGoodsException("/goods/resetCount", "cannot modify goods.soldCount but only reset as 0");
//            }
//            goods = goodsRepository.saveAndFlush(goods);
//            return result(Const.E_SUCCESS, "row", goods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/resetCount", e);
//        }
//    }
//
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsDeleteException.class)
//    public Map<String, Object> deleteGoods(Session session, @RequestParam(value = "seqNo", required = true) Long seqNo) throws ResultCodeException {
//        try {
//            String dateStr = AppUtil.localDatetimeNowString();
//            Goods goods = goodsRepository.findBySeqNo(seqNo);
//            logger.warn("goods get : " + goods.getName());
//            goods.setStatus(GoodsStatus.DELETE.getStatus());
//            goods.setCategorySeqNo(1L); // 기본 카테고리로 변경
//            goods.setModDatetime(dateStr);
//            goods = goodsRepository.saveAndFlush(goods);
//            goodsService.updateGoodsPriceStatusByGoodsSeqNo(goods.getSeqNo(), goods.getStatus());
//            goodsLikeRepository.deleteAllByGoodsSeqNo(goods.getSeqNo());
//            logger.warn("goods save : " + goods.getName());
//            return result(Const.E_SUCCESS, "row", goods);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsDeleteException("goods data", e);
//        }
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods")
//    public Map<String, Object> selectGoods(Session session, Pageable pageable, HttpServletRequest request,
//                                           @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                           @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                           @RequestParam(value = "categorySeqNo", required = false) Long categorySeqNo,
//                                           @RequestParam(value = "name", required = false) String name,
//                                           @RequestParam(value = "minPrice", required = false) Float minPrice,
//                                           @RequestParam(value = "maxPrice", required = false) Float maxPrice,
//                                           @RequestParam(value = "minOriginPrice", required = false) Float minOriginPrice,
//                                           @RequestParam(value = "maxOriginPrice", required = false) Float maxOriginPrice,
//                                           @RequestParam(value = "expired", required = false) Boolean expired,
//                                           @RequestParam(value = "status", required = false) Integer status,
//                                           @RequestParam(value = "type", required = false) Integer type,
//                                           @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                           @RequestParam(value = "isPlus", required = false) Boolean isPlus) throws ResultCodeException {
//
//        try {
//
//            if (isHotdeal != null || isPlus != null) {
//                if (isHotdeal == null && isPlus == true) {
//                    isHotdeal = false;
//                } else if (isPlus == null && isHotdeal == true) {
//                    isPlus = false;
//                }
//            }
//
//
//            Page<Goods> page = null;
//            if (name != null)
//                name = "%" + name + "%";
//
//
//            if (seqNo != null) {
//                Goods goods = goodsRepository.findBySeqNo(seqNo);
//                logger.debug(getUri(request) + goods.toString());
//                System.out.println("goods : " + goods.toString());
//                return result(Const.E_SUCCESS, "row", goods);
//            } else {
//
//                Map<String, String> sortMap = new HashMap<String, String>();
//                pageable = this.nativePageable(request, pageable, sortMap);
//                page = goodsRepository.findAllByWith(pageSeqNo,
//                        name, minPrice, maxPrice, minOriginPrice, maxOriginPrice, expired, status,
//                        type, isHotdeal, isPlus, pageable);
//            }
//
//            System.out.println("/goods[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("[GET]/goods", "select.goods ERROR");
//        }
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/luckyCoupon")
//    public Map<String, Object> selectCoupon(Session session, HttpServletRequest request,
//                                            @RequestParam(value = "pageSeqNo") Long pageSeqNo) throws ResultCodeException {
//
//        if (pageSeqNo != null) {
//            GoodsDetail goodsDetail = goodsDetailRepository.findFirstByPageSeqNoAndIsCouponTrue(pageSeqNo);
//            if (goodsDetail != null) {
//                return result(Const.E_SUCCESS, "row", goodsDetail);
//            } else {
//                return result(Const.E_SUCCESS, "row", null);
//            }
//
//        } else {
//            throw new InvalidGoodsException("[GET]/goods/detail/luckyCoupon", "pageSeqNo is null");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/oldCoupon")
//    public Map<String, Object> selectOldCoupon(Session session, Pageable pageable, HttpServletRequest request,
//                                               @RequestParam(value = "pageSeqNo") Long pageSeqNo) throws ResultCodeException {
//
//        if (pageSeqNo != null) {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "status,seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsDetail> page = goodsDetailRepository.findAllByPageSeqNoOldCoupon(pageSeqNo, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } else {
//            throw new InvalidGoodsException("[GET]/goods/detail/oldCoupon", "pageSeqNo is null");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/hotdeal")
//    public Map<String, Object> selectHotdeal(Session session, HttpServletRequest request,
//                                             @RequestParam(value = "pageSeqNo") Long pageSeqNo) throws ResultCodeException {
//
//        if (pageSeqNo != null) {
//            GoodsDetail goodsDetail = goodsDetailRepository.findFirstByPageSeqNoAndIsHotdealTrue(pageSeqNo);
//            if (goodsDetail != null) {
//                return result(Const.E_SUCCESS, "row", goodsDetail);
//            } else {
//                return result(Const.E_SUCCESS, "row", null);
//            }
//
//        } else {
//            throw new InvalidGoodsException("[GET]/goods/detail", "pageSeqNo is null");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/goods/detail/hotdealShipType")
//    public Map<String, Object> selectHotdealShipType(Session session, HttpServletRequest request,
//                                                     @RequestParam(value = "pageSeqNo") Long pageSeqNo) throws ResultCodeException {
//
//        if (pageSeqNo != null) {
//            GoodsDetail goodsDetail = goodsDetailRepository.findFirstByPageSeqNoAndIsHotdealTrueShipType(pageSeqNo);
//            if (goodsDetail != null) {
//                return result(Const.E_SUCCESS, "row", goodsDetail);
//            } else {
//                return result(Const.E_SUCCESS, "row", null);
//            }
//
//        } else {
//            throw new InvalidGoodsException("[GET]/goods/detail", "pageSeqNo is null");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/oldHotdeal")
//    public Map<String, Object> selectOldHotdeal(Session session, Pageable pageable, HttpServletRequest request,
//                                                @RequestParam(value = "pageSeqNo") Long pageSeqNo) throws ResultCodeException {
//
//        if (pageSeqNo != null) {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "status,seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsDetail> page = goodsDetailRepository.findAllByPageSeqNoOldHotdeal(pageSeqNo, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } else {
//            throw new InvalidGoodsException("[GET]/goods/detail", "pageSeqNo is null");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/goods/detail/oldHotdealShipType")
//    public Map<String, Object> selectOldHotdealShipType(Session session, Pageable pageable, HttpServletRequest request,
//                                                        @RequestParam(value = "pageSeqNo") Long pageSeqNo) throws ResultCodeException {
//
//        if (pageSeqNo != null) {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "status,seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsDetail> page = goodsDetailRepository.findAllByPageSeqNoOldHotdealShipType(pageSeqNo, pageable);
//            return result(Const.E_SUCCESS, "row", page);
//        } else {
//            throw new InvalidGoodsException("[GET]/goods/detail", "pageSeqNo is null");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail")
//    public Map<String, Object> selectGoodsDetail(Session session, Pageable pageable, HttpServletRequest request,
//                                                 @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
//                                                 @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
//                                                 @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                                 @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                 @RequestParam(value = "categorySeqNo", required = false) Long goodsCategorySeqNo,
//                                                 @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                 @RequestParam(value = "name", required = false) String name,
//                                                 @RequestParam(value = "minPrice", required = false) Float minPrice,
//                                                 @RequestParam(value = "maxPrice", required = false) Float maxPrice,
//                                                 @RequestParam(value = "minOriginPrice", required = false) Float minOriginPrice,
//                                                 @RequestParam(value = "maxOriginPrice", required = false) Float maxOriginPrice,
//                                                 @RequestParam(value = "openBounds", required = false) String openBounds,
//                                                 @RequestParam(value = "expired", required = false) Boolean expired,
//                                                 @RequestParam(value = "status", required = false) Integer status,
//                                                 @RequestParam(value = "type", required = false) Integer type,
//                                                 @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                 @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                 @RequestParam(value = "isCoupon", required = false) Boolean isCoupon,
//                                                 @RequestParam(value = "rewardPrLink", required = false) Integer rewardPrLink
//
//    ) throws ResultCodeException {
//
//        try {
//
//            if (isHotdeal != null || isPlus != null) {
//                if (isHotdeal == null && isPlus == true) {
//                    isHotdeal = false;
//                } else if (isPlus == null && isHotdeal == true) {
//                    isPlus = false;
//                }
//            }
//
//            if (isCoupon == null) {
//                isCoupon = false;
//            }
//
////            if( isHotdeal != null || isPlus != null) {
////                if( isHotdeal == null && isPlus == true ) {
////                    isHotdeal = false ;
////                } else if( isPlus == null && isHotdeal == true ) {
////                    isPlus = false ;
////                }
////            }
//
//            Page<GoodsDetail> page = null;
//            if (name != null)
//                name = "%" + name + "%";
//
//
//            if (seqNo != null) {
//                GoodsDetail goodsDetail = goodsDetailRepository.findBySeqNo(seqNo);
//                logger.debug(getUri(request) + goodsDetail.toString());
//                System.out.println("goodsDetail : " + goodsDetail.toString());
//                return result(Const.E_SUCCESS, "row", goodsDetail);
//            } else {
//
//                Boolean woodongyi = false;
////                if (session != null && session.getWoodongyi() != null && session.getWoodongyi()) {
////                    woodongyi = true;
////                }
//
//                if (session != null && session.getDevice().getInstalledApp().getAppKey().equals("com.pplus.prnumberbiz")) {
//                    woodongyi = false;
//                }
//
//                Map<String, String> sortMap = new HashMap<String, String>();
//                if (request.getParameter("sort") == null) {
//                    sortMap.put("#SORT#", "status,seq_no");
//                }
//                pageable = this.nativePageable(request, pageable, sortMap);
//                page = goodsDetailRepository.findAllByWith(categoryMinorSeqNo, categoryMajorSeqNo,
//                        memberSeqNo, pageSeqNo, goodsCategorySeqNo,
//                        name, minPrice, maxPrice, minOriginPrice, maxOriginPrice, expired, openBounds, status,
//                        type, isHotdeal, isPlus, isCoupon, rewardPrLink, woodongyi, pageable);
//            }
//
//            System.out.println("/goods/detail[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("[GET]/goods/detail", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/locationArea")
//    public Map<String, Object> selectGoodsDetailWithLocationArea(Session session, Pageable pageable, HttpServletRequest request,
//                                                                 @RequestParam(value = "latitude", required = false) Double latitude,
//                                                                 @RequestParam(value = "longitude", required = false) Double longitude,
//                                                                 @RequestParam(value = "distance", required = false) Double distance,
//                                                                 @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
//                                                                 @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
//                                                                 @RequestParam(value = "categorySeqNo", required = false) Long goodsCategorySeqNo,
//                                                                 @RequestParam(value = "openBounds", required = false) String openBounds,
//                                                                 @RequestParam(value = "expired", required = false) Boolean expired,
//                                                                 @RequestParam(value = "status", required = false) Integer status,
//                                                                 @RequestParam(value = "type", required = false) Integer type,
//                                                                 @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                                 @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                                 @RequestParam(value = "isCoupon", required = false) Boolean isCoupon,
//                                                                 @RequestParam(value = "represent", required = false) Boolean represent,
//                                                                 @RequestParam(value = "isRealTime", required = false) Boolean isRealTime,
//                                                                 @RequestParam(value = "top", required = false) Double top,
//                                                                 @RequestParam(value = "bottom", required = false) Double bottom,
//                                                                 @RequestParam(value = "left", required = false) Double left,
//                                                                 @RequestParam(value = "right", required = false) Double right
//    ) throws ResultCodeException {
//
//        try {
//
//            if (isHotdeal != null || isPlus != null) {
//                if (isHotdeal == null && isPlus == true) {
//                    isHotdeal = false;
//                } else if (isPlus == null && isHotdeal == true) {
//                    isPlus = false;
//                }
//            }
//
//            if (isCoupon == null) {
//                isCoupon = false;
//            }
//
//            if (represent == null) {
//                represent = false;
//            }
//
//            if (isRealTime == null) {
//                isRealTime = false;
//            }
//
//            Page<GoodsDetail> page = null;
//
//            if (distance == null && longitude != null && latitude != null)
//                distance = 10000.0;
//
//            Boolean woodongyi = false;
//
////            if (session != null && session.getWoodongyi() != null && session.getWoodongyi()) {
////                woodongyi = true;
////            }
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = goodsDetailRepository.findAllByWithLocationArea(latitude, longitude, distance, top, bottom, left, right,
//                    categoryMinorSeqNo, categoryMajorSeqNo, goodsCategorySeqNo,
//                    expired, openBounds, status, type, isHotdeal, isPlus, isCoupon, represent, woodongyi, isRealTime, pageable);
//
//            System.out.println("/goods/detail/locationArea[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("[GET]/goods/detail/locationArea", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/shipType")
//    public Map<String, Object> selectGoodsDetailShipType(Session session, Pageable pageable, HttpServletRequest request,
//                                                         @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
//                                                         @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
//                                                         @RequestParam(value = "categorySeqNo", required = false) Long goodsCategorySeqNo,
//                                                         @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                         @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                         @RequestParam(value = "name", required = false) String name,
//                                                         @RequestParam(value = "minPrice", required = false) Float minPrice,
//                                                         @RequestParam(value = "maxPrice", required = false) Float maxPrice,
//                                                         @RequestParam(value = "minOriginPrice", required = false) Float minOriginPrice,
//                                                         @RequestParam(value = "maxOriginPrice", required = false) Float maxOriginPrice,
//                                                         @RequestParam(value = "openBounds", required = false) String openBounds,
//                                                         @RequestParam(value = "type", required = false) Integer type,
//                                                         @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                         @RequestParam(value = "isPlus", required = false) Boolean isPlus
//
//    ) throws ResultCodeException {
//
//        try {
//
////            if(isHotdeal == null){
////                isHotdeal = false;
////            }
////
////            if(isPlus == null){
////                isPlus = false;
////            }
//
//            Page<GoodsDetail> page = null;
//            if (name != null)
//                name = "%" + name + "%";
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = goodsDetailRepository.findAllShipType(categoryMinorSeqNo, categoryMajorSeqNo, goodsCategorySeqNo,
//                    memberSeqNo, pageSeqNo, name, minPrice, maxPrice, minOriginPrice, maxOriginPrice, openBounds, type, isHotdeal, isPlus, pageable);
//
//
//            System.out.println("/goods/detail/shipType[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("[GET]/goods/detail/location", "select.goods ERROR");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/goods/detail/shipTypeAllByPageSeqNo")
//    public Map<String, Object> getGoodsListShipTypeAllByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
//                                                                  @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                                  @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                                  @RequestParam(value = "categorySeqNo", required = false) Long goodsCategorySeqNo,
//                                                                  @RequestParam(value = "isPlus", required = false) Boolean isPlus) throws ResultCodeException {
//
//        try {
//
////            if(isHotdeal == null){
////                isHotdeal = false;
////            }
////
////            if(isPlus == null){
////                isPlus = false;
////            }
//
//            Page<GoodsDetail> page = null;
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            page = goodsService.getGoodsListShipTypeAllByPageSeqNo(pageSeqNo, goodsCategorySeqNo, isHotdeal, isPlus, pageable);
//
//
//            System.out.println("/goods/detail/shipTypeByPageSeqNo[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/detail/shipTypeByPageSeqNo[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/location")
//    public Map<String, Object> selectGoodsDetailWithLocation(Session session, Pageable pageable, HttpServletRequest request,
//                                                             @RequestParam(value = "latitude", required = false) Double latitude,
//                                                             @RequestParam(value = "longitude", required = false) Double longitude,
//                                                             @RequestParam(value = "distance", required = false) Double distance,
//                                                             @RequestParam(value = "categoryMinorSeqNo", required = false) Long categoryMinorSeqNo,
//                                                             @RequestParam(value = "categoryMajorSeqNo", required = false) Long categoryMajorSeqNo,
//                                                             @RequestParam(value = "categorySeqNo", required = false) Long goodsCategorySeqNo,
//                                                             @RequestParam(value = "seqNo", required = false) Long seqNo,
//                                                             @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                             @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                             @RequestParam(value = "name", required = false) String name,
//                                                             @RequestParam(value = "minPrice", required = false) Float minPrice,
//                                                             @RequestParam(value = "maxPrice", required = false) Float maxPrice,
//                                                             @RequestParam(value = "minOriginPrice", required = false) Float minOriginPrice,
//                                                             @RequestParam(value = "maxOriginPrice", required = false) Float maxOriginPrice,
//                                                             @RequestParam(value = "openBounds", required = false) String openBounds,
//                                                             @RequestParam(value = "type", required = false) Integer type,
//                                                             @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                             @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                             @RequestParam(value = "represent", required = false) Boolean represent,
//                                                             @RequestParam(value = "isRealTime", required = false) Boolean isRealTime
//
//    ) throws ResultCodeException {
//
//        try {
//
//            if (isHotdeal != null || isPlus != null) {
//                if (isHotdeal == null && isPlus == true) {
//                    isHotdeal = false;
//                } else if (isPlus == null && isHotdeal == true) {
//                    isPlus = false;
//                }
//            }
//
//            if (represent == null) {
//                represent = false;
//            }
//
//            if (isRealTime == null) {
//                isRealTime = false;
//            }
//
//            Page<GoodsDetail> page = null;
//            if (name != null)
//                name = "%" + name + "%";
//
//
//            if (seqNo != null) {
//                GoodsDetail goodsDetail = goodsDetailRepository.findBySeqNo(seqNo);
//                logger.debug(getUri(request) + goodsDetail.toString());
//                System.out.println("goodsDetail : " + goodsDetail.toString());
//                return result(Const.E_SUCCESS, "row", goodsDetail);
//            } else {
//
//                if (distance == null && longitude != null && latitude != null)
//                    distance = 10000.0;
//
//                String sort = request.getParameter("sort");
//
//                Boolean woodongyi = false;
//
////                if (session != null && session.getWoodongyi() != null && session.getWoodongyi()) {
////                    woodongyi = true;
////                }
//
//                if (StringUtils.isEmpty(sort)) {
//                    Map<String, String> sortMap = new HashMap<String, String>();
//                    pageable = this.nativePageable(request, pageable, sortMap);
//                    page = goodsDetailRepository.findAllByWithLocation(latitude, longitude, distance,
//                            categoryMinorSeqNo, categoryMajorSeqNo, goodsCategorySeqNo,
//                            memberSeqNo, pageSeqNo, name, minPrice, maxPrice, minOriginPrice, maxOriginPrice, openBounds, type, isHotdeal, isPlus, represent, woodongyi, isRealTime, pageable);
//                } else {
//                    Map<String, String> sortMap = new HashMap<String, String>();
//                    pageable = this.nativePageable(request, pageable, sortMap);
//                    page = goodsDetailRepository.findAllByWithLocationNotOrderBy(latitude, longitude, distance,
//                            categoryMinorSeqNo, categoryMajorSeqNo, goodsCategorySeqNo,
//                            memberSeqNo, pageSeqNo, name, minPrice, maxPrice, minOriginPrice, maxOriginPrice, openBounds, type, isHotdeal, isPlus, represent, woodongyi, isRealTime, pageable);
//                }
//
//            }
//
//            System.out.println("/goods/detail/location[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("[GET]/goods/detail/location", "select.goods ERROR");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/goods/detail/plus")
//    public Map<String, Object> selectGoodsDetailForPlusPages(Session session, Pageable pageable, HttpServletRequest request,
//                                                             @RequestParam(value = "name", required = false) String name,
//                                                             @RequestParam(value = "minPrice", required = false) Float minPrice,
//                                                             @RequestParam(value = "maxPrice", required = false) Float maxPrice,
//                                                             @RequestParam(value = "minOriginPrice", required = false) Float minOriginPrice,
//                                                             @RequestParam(value = "maxOriginPrice", required = false) Float maxOriginPrice,
//                                                             @RequestParam(value = "openBounds", required = false) String openBounds,
//                                                             @RequestParam(value = "expired", required = false) Boolean expired,
//                                                             @RequestParam(value = "status", required = false) Integer status,
//                                                             @RequestParam(value = "type", required = false) Integer type,
//                                                             @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                             @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                             @RequestParam(value = "rewardPrLink", required = false) Integer rewardPrLink
//    ) throws ResultCodeException {
//
//        try {
//
//            if (isHotdeal != null || isPlus != null) {
//                if (isHotdeal == null && isPlus == true) {
//                    isHotdeal = false;
//                } else if (isPlus == null && isHotdeal == true) {
//                    isPlus = false;
//                }
//            }
//
//            Long memberSeqNo = session.getNo();
//
//            Page<GoodsDetail> page = null;
//            if (name != null)
//                name = "%" + name + "%";
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "status,seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//
//            page = goodsDetailRepository.findPlusAllByWith(memberSeqNo, name, minPrice, maxPrice,
//                    minOriginPrice, maxOriginPrice, expired, openBounds, status, type,
//                    isHotdeal, isPlus, rewardPrLink, pageable);
//
//
//            logger.debug(getUri(request) + page.toString());
//            return
//
//                    result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("select.goods", e);
//        }
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/detail/pageSeqNo")
//    public Map<String, Object> selectGoodsDetailByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
//                                                            @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                            @RequestParam(value = "expired", required = false) Boolean expired,
//                                                            @RequestParam(value = "status", required = false) Integer status,
//                                                            @RequestParam(value = "type", required = false) Integer type,
//                                                            @RequestParam(value = "isHotdeal", required = false) Boolean isHotdeal,
//                                                            @RequestParam(value = "isPlus", required = false) Boolean isPlus,
//                                                            @RequestParam(value = "isCoupon", required = false) Boolean isCoupon
//    ) throws ResultCodeException {
//
//        try {
//
//            if (isHotdeal != null || isPlus != null) {
//                if (isHotdeal == null && isPlus == true) {
//                    isHotdeal = false;
//                } else if (isPlus == null && isHotdeal == true) {
//                    isPlus = false;
//                }
//            }
//
//            if (isCoupon == null) {
//                isCoupon = false;
//            }
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            if (request.getParameter("sort") == null) {
//                sortMap.put("#SORT#", "status,seq_no");
//            }
//            pageable = this.nativePageable(request, pageable, sortMap);
//
//            Page<GoodsDetail> page = goodsService.getGoodsListByPageSeqNo(pageSeqNo, expired, status, type, isHotdeal, isPlus, isCoupon, pageable);
//
//            System.out.println("/goods/detail/pageSeqNo[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("[GET]/goods/detail/pageSeqNo", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/option")
//    public Map<String, Object> selectGoodsDetailByPageSeqNo(@RequestParam(value = "goodsSeqNo", required = true) Long goodsSeqNo) {
//        GoodsOptionTotal optionTotal = goodsService.getGoodsOptionTotal(goodsSeqNo);
//        return result(Const.E_SUCCESS, "row", optionTotal);
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goodsImage/list")
//    public Map<String, Object> getGoodsImageList(@RequestParam(value = "goodsSeqNo") Long goodsSeqNo,
//                                                 @RequestParam(value = "type") String type) {
//        List<GoodsImage> goodsImageList = goodsService.getGoodsImageList(goodsSeqNo, type);
//        return result(Const.E_SUCCESS, "rows", goodsImageList);
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/price/shipTypeIsLuckybol")
//    public Map<String, Object> getGoodsPriceListShipTypeIsLuckyBol(Session session, Pageable pageable, HttpServletRequest request,
//                                                                   @RequestParam(value = "first", required = false) Long first,
//                                                                   @RequestParam(value = "second", required = false) Long second,
//                                                                   @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
//
//        try {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsPrice> page = goodsService.getGoodsPriceListShipTypeIsLuckyBol(first, second, third, pageable);
//
//
//            System.out.println("/goods/price/shipTypeIsLuckybol[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/price/shipTypeIsLuckybol[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @GetMapping(value = baseUri + "/goods/price/shipTypeByPageSeqNo")
//    public Map<String, Object> getGoodsPriceListShipTypeByPageSeqNo(Session session, Pageable pageable, HttpServletRequest request,
//                                                                    @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
//                                                                    @RequestParam(value = "first", required = false) Long first,
//                                                                    @RequestParam(value = "second", required = false) Long second,
//                                                                    @RequestParam(value = "third", required = false) Long third,
//                                                                    @RequestParam(value = "isWholesale", required = false) Boolean isWholesale) throws ResultCodeException {
//        try {
//
//            if(isWholesale == null){
//                isWholesale = false;
//            }
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsPrice> page = goodsService.getGoodsPriceListShipTypeByPageSeqNo(pageSeqNo, first, second, third, isWholesale, pageable);
//
//
//            System.out.println("/goods/price/shipTypeIsLuckybol[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/price/shipTypeByPageSeqNo[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/price/shipTypeByPageSeqNoOnlyNormal")
//    public Map<String, Object> getGoodsPriceListShipTypeByPageSeqNoOnlyNormal(Session session, Pageable pageable, HttpServletRequest request,
//                                                                              @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo,
//                                                                              @RequestParam(value = "first", required = false) Long first,
//                                                                              @RequestParam(value = "second", required = false) Long second,
//                                                                              @RequestParam(value = "third", required = false) Long third) throws ResultCodeException {
//        try {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsPrice> page = goodsService.getGoodsPriceListShipTypeByPageSeqNoOnlyNormal(pageSeqNo, first, second, third, pageable);
//
//
//            System.out.println("/goods/price/shipTypeIsLuckybol[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/price/shipTypeByPageSeqNo[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/price/shipTypeIsWholesale")
//    public Map<String, Object> getGoodsPriceListShipTypeIsWholesale(Session session, Pageable pageable, HttpServletRequest request,
//                                                                    @RequestParam(value = "first", required = false) Long first,
//                                                                    @RequestParam(value = "second", required = false) Long second,
//                                                                    @RequestParam(value = "third", required = false) Long third,
//                                                                    @RequestParam(value = "popular", required = false) Boolean popular,
//                                                                    @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo) throws ResultCodeException {
//        try {
//
//            Map<String, String> sortMap = new HashMap<String, String>();
//            pageable = this.nativePageable(request, pageable, sortMap);
//            Page<GoodsPrice> page = goodsService.getGoodsPriceListShipTypeIsWholesale(pageSeqNo, first, second, third, popular, pageable);
//
//
//            System.out.println("/goods/price/shipTypeIsLuckybol[GET] : " + page.toString());
//            return result(Const.E_SUCCESS, "row", page);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/price/shipTypeIsWholesale[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/price")
//    public Map<String, Object> getGoodsPriceBySeqNo(Session session, @RequestParam(value = "seqNo", required = false) Long seqNo) throws ResultCodeException {
//
//        try {
//
//            GoodsPrice goodsPrice = goodsService.getGoodsPriceBySeqNo(seqNo);
//
//
//            return result(Const.E_SUCCESS, "row", goodsPrice);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/detail/shipTypeByPageSeqNo[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @SkipSessionCheck
//    @GetMapping(value = baseUri + "/goods/price/goodsSeqNoAndPageSeqNo")
//    public Map<String, Object> getGoodsPriceByGoodsSeqNoAndPageSeqNo(Session session, @RequestParam(value = "goodsSeqNo", required = true) Long goodsSeqNo,
//                                                                     @RequestParam(value = "pageSeqNo", required = true) Long pageSeqNo) throws ResultCodeException {
//
//        try {
//
//            GoodsPrice goodsPrice = goodsService.getGoodsPriceByGoodsSeqNoAndPageSeqNo(goodsSeqNo, pageSeqNo);
//
//
//            return result(Const.E_SUCCESS, "row", goodsPrice);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsException("/goods/detail/shipTypeByPageSeqNo[GET]", "select.goods ERROR");
//        }
//
//    }
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/price/insert")
//    public Map<String, Object> insertGoodsPrice(Session session, @RequestBody GoodsPriceOnly goodsPrice) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", goodsService.insertGoodsPriceOnly(goodsPrice));
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/price/update")
//    public Map<String, Object> updateGoodsPrice(Session session, @RequestBody GoodsPriceOnly goodsPrice) throws ResultCodeException {
//
//        return result(Const.E_SUCCESS, "row", goodsService.saveGoodsPriceOnly(goodsPrice));
//    }
//
//    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/price/updateStatus")
//    public Map<String, Object> updateGoodsPrice(Session session, @RequestParam(value = "goodsPriceSeqNo") Long goodsPriceSeqNo
//            , @RequestParam(value = "status") Integer status) throws ResultCodeException {
//        goodsService.updateGoodsPriceStatusBySeqNo(goodsPriceSeqNo, status);
//
//        return result(Const.E_SUCCESS);
//    }
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goods/price/delete")
//    public Map<String, Object> deleteGoodsPrice(Session session, @RequestParam(value = "goodsPriceSeqNo") Long goodsPriceSeqNo) throws ResultCodeException {
//        goodsService.updateGoodsPriceStatusBySeqNo(goodsPriceSeqNo, -999);
//
//        return result(Const.E_SUCCESS);
//    }
//}
