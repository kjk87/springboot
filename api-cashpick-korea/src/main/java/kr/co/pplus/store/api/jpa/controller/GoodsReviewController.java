package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.Count;
import kr.co.pplus.store.api.jpa.model.GoodsReview;
import kr.co.pplus.store.api.jpa.service.GoodsReviewService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidGoodsReviewException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class GoodsReviewController extends RootController {

    private Logger logger = LoggerFactory.getLogger(GoodsReviewController.class);

    @Autowired
    GoodsReviewService goodsReviewService;


    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsReview")
    @Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsReviewException.class)
    public Map<String, Object> addGoodsReview(Session session, @RequestBody GoodsReview goodsReview) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "row", goodsReviewService.addGoodsReview(session, goodsReview));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("goodsReview data", e);
        }
    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsReview")
    //@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = InvalidGoodsReviewException.class)
    public Map<String, Object> updateGoodsReview(Session session, @RequestBody GoodsReview goodsReview) throws ResultCodeException {
        try {
            return result(Const.E_SUCCESS, "row", goodsReviewService.updateGoodsReview(session, goodsReview));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("goodsReview data", e);
        }

    }

    @PutMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsReview/reply")
    public Map<String, Object> updateGoodsReviewReply(Session session, @RequestBody GoodsReview goodsReview) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", goodsReviewService.updateGoodsReviewReply(session, goodsReview));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("goodsReview data", e);
        }

    }

    @DeleteMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsReview")
    public Map<String, Object> deleteGoodsReview(Session session, @RequestParam(value = "seqNo", required = true) Long seqNo) throws ResultCodeException {
        try {
            goodsReviewService.deleteGoodsReview(session, seqNo);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("goodsReview data", e);
        }
        return result(Const.E_SUCCESS, "row", null);
    }

    @GetMapping(value = baseUri + "/goodsReview")
    public Map<String, Object> selectGoodsReview(Session session, Pageable pageable,
                                                 @RequestParam(value = "seqNo", required = false) Long seqNo,
                                                 @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
                                                 @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
                                                 @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo) throws ResultCodeException {
        try {
            if (seqNo != null) {
                return result(Const.E_SUCCESS, "row", goodsReviewService.selectGoodsReviewBySeqNo(seqNo));
            } else {
                return result(Const.E_SUCCESS, "row", goodsReviewService.selectGoodsReview(pageable, memberSeqNo, pageSeqNo, goodsSeqNo));
            }

        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("select.goodsReview", e);
        }


    }

    @GetMapping(value = baseUri + "/goodsReview/countAll")
    public Map<String, Object> selectGoodsReviewDetail(Session session) {
        return result(Const.E_SUCCESS, "row", goodsReviewService.countAllByMemberSeqNo(session));
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/goodsReview/detail")
    public Map<String, Object> selectGoodsReviewDetail(Session session, Pageable pageable,
                                                       @RequestParam(value = "seqNo", required = false) Long seqNo,
                                                       @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
                                                       @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
                                                       @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
                                                       @RequestParam(value = "goodsPriceSeqNo", required = false) Long goodsPriceSeqNo) throws ResultCodeException {
        try {

            if (seqNo != null) {
                return result(Const.E_SUCCESS, "row", goodsReviewService.selectGoodsReviewDetailBySeqNo(seqNo));
            } else {
                return result(Const.E_SUCCESS, "row", goodsReviewService.selectGoodsReviewDetail(pageable, memberSeqNo, pageSeqNo, goodsSeqNo, goodsPriceSeqNo));
            }
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("[GET]/goodsReview/detail", "select.goodsReview ERROR");
        }

    }

    @SkipSessionCheck
    @GetMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/goodsReview/count")
    public Map<String, Object> countGoodsReviewDetail(Session session,
                                                      @RequestParam(value = "memberSeqNo", required = false) Long memberSeqNo,
                                                      @RequestParam(value = "pageSeqNo", required = false) Long pageSeqNo,
                                                      @RequestParam(value = "goodsSeqNo", required = false) Long goodsSeqNo,
                                                      @RequestParam(value = "goodsPriceSeqNo", required = false) Long goodsPriceSeqNo) throws ResultCodeException {
        try {

            return result(Const.E_SUCCESS, "row", new Count(goodsReviewService.countGoodsReview(memberSeqNo, pageSeqNo, goodsSeqNo, goodsPriceSeqNo)));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidGoodsReviewException("[GET]/goodsReview/count", "select error");
        }

    }
}
