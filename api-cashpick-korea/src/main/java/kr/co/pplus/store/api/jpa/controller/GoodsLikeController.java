//package kr.co.pplus.store.api.jpa.controller;
//
//import kr.co.pplus.store.api.controller.RootController;
//import kr.co.pplus.store.api.jpa.model.Count;
//import kr.co.pplus.store.api.jpa.model.Goods;
//import kr.co.pplus.store.api.jpa.model.GoodsLike;
//import kr.co.pplus.store.api.jpa.model.GoodsLikeDetail;
//import kr.co.pplus.store.api.jpa.repository.GoodsLikeDetailRepository;
//import kr.co.pplus.store.api.jpa.repository.GoodsLikeRepository;
//import kr.co.pplus.store.api.jpa.repository.GoodsRepository;
//import kr.co.pplus.store.api.util.AppUtil;
//import kr.co.pplus.store.exception.InvalidGoodsLikeException;
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.type.Const;
//import kr.co.pplus.store.type.model.Session;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//public class GoodsLikeController extends RootController {
//
//    private Logger logger = LoggerFactory.getLogger(GoodsLikeController.class);
//
//    @Autowired
//    GoodsLikeRepository goodsLikeRepository;
//
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Autowired
//    GoodsLikeDetailRepository goodsLikeDetailRepository;
//
//
//    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsLike")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsLikeException.class)
//    public Map<String, Object> addGoodsLike(Session session, @RequestBody GoodsLike goodsLike) throws ResultCodeException {
//
//
//        try {
//            goodsLike.setStatus(1);
//            goodsLike.setRegDatetime(AppUtil.localDatetimeNowString());
//
//            Goods goods = goodsRepository.findBySeqNo(goodsLike.getGoodsSeqNo());
//            if (StringUtils.isNotEmpty(goods.getExpireDatetime())) {
//                goodsLike.setExpireDatetime(goods.getExpireDatetime());
//            }
//
//            GoodsLike existGoodsLike = goodsLikeRepository.findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndGoodsPriceSeqNo(session.getNo(), goodsLike.getPageSeqNo(), goodsLike.getGoodsSeqNo(), goodsLike.getGoodsPriceSeqNo());
//            if (existGoodsLike == null) {
//                goodsLike = goodsLikeRepository.saveAndFlush(goodsLike);
//            }
//
//
//            if (goodsLike == null)
//                throw new InvalidGoodsLikeException("/goodsLike[POST]", "insert error");
//
//            return result(Const.E_SUCCESS, "row", goodsLike);
//
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsLikeException("/goodsLike[POST]", "insert error");
//        }
//    }
//
//    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsLike")
//    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsLikeException.class)
//    public Map<String, Object> deleteGoodsLike(Session session, @RequestBody GoodsLike goodsLike) throws ResultCodeException {
//        try {
//            GoodsLike tmp = goodsLikeRepository.findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndGoodsPriceSeqNo(goodsLike.getMemberSeqNo(), goodsLike.getPageSeqNo(), goodsLike.getGoodsSeqNo(), goodsLike.getGoodsPriceSeqNo());
//
//            if (tmp != null) {
//                goodsLikeRepository.delete(tmp);
////                tmp.setStatus(0);
////                goodsLike.setRegDatetime(AppUtil.localDatetimeNowString());
////                goodsLikeRepository.saveAndFlush(tmp) ;
//            }
//            return result(Const.E_SUCCESS);
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsLikeException("/goodsLike[DELETE]", e);
//        }
//    }
//
//    @GetMapping(value = baseUri + "/goodsLike")
//    public Map<String, Object> selectGoodsLike(Session session, Pageable pageable,
//                                               @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                               @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                               @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
//                                               @RequestParam(value = "goodsPriceSeqNo", required = false) Long goodsPriceSeqNo) throws ResultCodeException {
//        Page<GoodsLike> page = null;
//        try {
//
//            if (memberSeqNo != null) {
//                if (pageSeqNo != null) {
//                    if (goodsSeqNo != null) {
//                        if(goodsPriceSeqNo != null){
//                            GoodsLike goodsLike = goodsLikeRepository.findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndGoodsPriceSeqNoAndStatus(memberSeqNo, pageSeqNo, goodsSeqNo, goodsPriceSeqNo, 1);
//                            return result(Const.E_SUCCESS, "row", goodsLike);
//                        }else{
//                            GoodsLike goodsLike = goodsLikeRepository.findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndStatus(memberSeqNo, pageSeqNo, goodsSeqNo, 1);
//                            return result(Const.E_SUCCESS, "row", goodsLike);
//                        }
//
//                    } else {
//                        page = goodsLikeRepository.findAllByMemberSeqNoAndPageSeqNoAndStatus(memberSeqNo, pageSeqNo, 1, pageable);
//                    }
//                } else {
//                    if (goodsSeqNo != null) {
//                        page = goodsLikeRepository.findAllByMemberSeqNoAndGoodsSeqNoAndStatus(memberSeqNo, goodsSeqNo, 1, pageable);
//
//                    } else {
//                        page = goodsLikeRepository.findAllByMemberSeqNoAndStatus(memberSeqNo, 1, pageable);
//                    }
//                }
//            } else {
//                if (pageSeqNo != null) {
//                    if (goodsSeqNo != null) {
//                        page = goodsLikeRepository.findAllByPageSeqNoAndGoodsSeqNoAndStatus(pageSeqNo, goodsSeqNo, 1, pageable);
//                    } else {
//                        page = goodsLikeRepository.findAllByPageSeqNoAndStatus(pageSeqNo, 1, pageable);
//                    }
//                } else {
//                    if (goodsSeqNo != null) {
//                        page = goodsLikeRepository.findAllByGoodsSeqNoAndStatus(goodsSeqNo, 1, pageable);
//
//                    } else {
//                        page = goodsLikeRepository.findAllByStatus(1, pageable);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsLikeException("/goodsLike[GET]", e);
//        }
//        return result(Const.E_SUCCESS, "row", page);
//    }
//
//    @GetMapping(value = baseUri + "/goodsLike/detailBySalesType")
//    public Map<String, Object> selectGoodsLikeDetail(Session session, Pageable pageable, Integer salesType) {
//
//        Page<GoodsLikeDetail> page;
//        if(salesType == 3){
//            page = goodsLikeDetailRepository.findAllByMemberSeqNoShipping(session.getNo(), pageable);
//        }else {
//            page = goodsLikeDetailRepository.findAllByMemberSeqNo(session.getNo(), pageable);
//        }
//        return result(Const.E_SUCCESS, "row", page);
//    }
//
//    @GetMapping(value = baseUri + "/goodsLike/detail")
//    public Map<String, Object> selectGoodsLikeDetail(Session session, Pageable pageable,
//                                                     @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                                     @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                                     @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo) throws ResultCodeException {
//        Page<GoodsLikeDetail> page = null;
//        try {
//
//
//            if (memberSeqNo != null) {
//                if (pageSeqNo != null) {
//                    if (goodsSeqNo != null) {
//                        GoodsLikeDetail goodsLikeDetail = goodsLikeDetailRepository.findByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndStatus(memberSeqNo, pageSeqNo, goodsSeqNo, 1);
//                        return result(Const.E_SUCCESS, "row", goodsLikeDetail);
//                    } else {
//                        page = goodsLikeDetailRepository.findAllByMemberSeqNoAndPageSeqNoAndStatus(memberSeqNo, pageSeqNo, 1, pageable);
//                    }
//                } else {
//                    if (goodsSeqNo != null) {
//                        page = goodsLikeDetailRepository.findAllByMemberSeqNoAndGoodsSeqNoAndStatus(memberSeqNo, goodsSeqNo, 1, pageable);
//
//                    } else {
//                        page = goodsLikeDetailRepository.findAllByMemberSeqNoAndStatus(memberSeqNo, 1, pageable);
//                    }
//                }
//            } else {
//                if (pageSeqNo != null) {
//                    if (goodsSeqNo != null) {
//                        page = goodsLikeDetailRepository.findAllByPageSeqNoAndGoodsSeqNoAndStatus(pageSeqNo, goodsSeqNo, 1, pageable);
//                    } else {
//                        page = goodsLikeDetailRepository.findAllByPageSeqNoAndStatus(pageSeqNo, 1, pageable);
//                    }
//                } else {
//                    if (goodsSeqNo != null) {
//                        page = goodsLikeDetailRepository.findAllByGoodsSeqNoAndStatus(goodsSeqNo, 1, pageable);
//
//                    } else {
//                        page = goodsLikeDetailRepository.findAllByStatus(1, pageable);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsLikeException("/goodsLike[GET]", e);
//        }
//        return result(Const.E_SUCCESS, "row", page);
//    }
//
//
//    @GetMapping(value = baseUri + "/goodsLike/count")
//    public Map<String, Object> countGoodsLike(Session session, Pageable pageable,
//                                              @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
//                                              @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
//                                              @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo) throws ResultCodeException {
//        Integer count = null;
//        try {
//
//
//            if (memberSeqNo != null) {
//                if (pageSeqNo != null) {
//                    if (goodsSeqNo != null) {
//                        count = goodsLikeRepository.countAllByMemberSeqNoAndPageSeqNoAndGoodsSeqNoAndStatus(memberSeqNo, pageSeqNo, goodsSeqNo, 1);
//
//                    } else {
//                        count = goodsLikeRepository.countAllByMemberSeqNoAndPageSeqNoAndStatus(memberSeqNo, pageSeqNo, 1);
//                    }
//                } else {
//                    if (goodsSeqNo != null) {
//                        count = goodsLikeRepository.countAllByMemberSeqNoAndGoodsSeqNoAndStatus(memberSeqNo, goodsSeqNo, 1);
//
//                    } else {
//                        count = goodsLikeRepository.countAllByMemberSeqNoAndStatus(memberSeqNo, 1);
//                    }
//                }
//            } else {
//                if (pageSeqNo != null) {
//                    if (goodsSeqNo != null) {
//                        count = goodsLikeRepository.countAllByPageSeqNoAndGoodsSeqNoAndStatus(pageSeqNo, goodsSeqNo, 1);
//                    } else {
//                        count = goodsLikeRepository.countAllByPageSeqNoAndStatus(pageSeqNo, 1);
//                    }
//                } else {
//                    if (goodsSeqNo != null) {
//                        count = goodsLikeRepository.countAllByGoodsSeqNoAndStatus(goodsSeqNo, 1);
//
//                    } else {
//                        count = goodsLikeRepository.countAllByStatus(1);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error(AppUtil.excetionToString(e));
//            throw new InvalidGoodsLikeException("/goodsLike/count[GET]", e);
//        }
//        return result(Const.E_SUCCESS, "row", new Count(count));
//    }
//}
