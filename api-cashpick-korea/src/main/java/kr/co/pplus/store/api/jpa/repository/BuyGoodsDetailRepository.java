package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyGoods;
import kr.co.pplus.store.api.jpa.model.BuyGoodsDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyGoodsDetailRepository extends JpaRepository<BuyGoodsDetail, Long> {

    BuyGoodsDetail findBySeqNo(Long seqNo) ;

    @Query(value="select * from buy_goods bg"
            + "                inner join goods g on bg.goods_seq_no = g.seq_no "
            + "                             and ( isnull(:type) = 1      or g.type = :type )"
            + "                             and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "                                   or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "                                   or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "                                   or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "                                   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "                                 )"
            + " where 1=1 "
            + " and ( isnull(:seqNo)         = 1 or bg.seq_no = :seqNo ) "
            + " and ( isnull(:buySeqNo)      = 1 or bg.buy_seq_no = :buySeqNo ) "
            + " and ( isnull(:memberSeqNo)   = 1  or bg.member_seq_no = :memberSeqNo ) "
            + " and ( isnull(:isReviewExist) = 1 or (bg.is_review_exist = :isReviewExist and (:isReviewExist <> false or g.type <> 0 or bg.order_process = 2)) ) "
            + " and ( isnull(:pageSeqNo)     = 1 or bg.page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:goodsSeqNo)    = 1 or bg.goods_seq_no = :goodsSeqNo ) "
            + " and ( isnull(:goodsPriceSeqNo)    = 1 or bg.goods_price_seq_no = :goodsPriceSeqNo ) "
            +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
            + " and ( isnull(:orderType) = 1 or bg.order_type = :orderType  ) "
            + " and ( isnull(:orderProcess) = 1 or bg.order_process = :orderProcess ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration)   = 1 or bg.reg_datetime <= :endDuration ) ",
            countQuery = "select count(1) from buy_goods bg "
            + "                inner join goods g on bg.goods_seq_no = g.seq_no "
            + "                             and ( isnull(:type) = 1      or g.type = :type )"
            + "                             and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "                                   or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "                                   or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "                                   or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "                                   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "                                 )"
            + " where 1=1 "
            + " and ( isnull(:seqNo)         = 1 or bg.seq_no = :seqNo ) "
            + " and ( isnull(:buySeqNo)      = 1 or bg.buy_seq_no = :buySeqNo ) "
            + " and ( isnull(:memberSeqNo)   = 1  or bg.member_seq_no = :memberSeqNo ) "
            + " and ( isnull(:isReviewExist) = 1 or (bg.is_review_exist = :isReviewExist and (:isReviewExist <> false or g.type <> 0 or bg.order_process = 2)) ) "
            + " and ( isnull(:pageSeqNo)     = 1 or bg.page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:goodsSeqNo)    = 1 or bg.goods_seq_no = :goodsSeqNo ) "
            + " and ( isnull(:goodsPriceSeqNo)    = 1 or bg.goods_price_seq_no = :goodsPriceSeqNo ) "
                    +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
            + " and ( isnull(:orderType) = 1 or bg.order_type = :orderType ) "
            + " and ( isnull(:orderProcess) = 1 or bg.order_process = :orderProcess ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration) = 1   or bg.reg_datetime <= :endDuration ) ", nativeQuery = true)
    Page<BuyGoodsDetail> findAllByWith(@Param("seqNo") Long seqNo, @Param("buySeqNo") Long buySeqNo,
                                       @Param("memberSeqNo") Long memberSeqNo, @Param("type") Integer type,
                                       @Param("isHotdeal") Boolean isHotdeal,
                                       @Param("isPlus") Boolean isPlus,
                                       @Param("isReviewExist") Boolean isReviewExist,
                                       @Param("pageSeqNo") Long pageSeqNo, @Param("process") Integer process, @Param("goodsSeqNo") Long goodsSeqNo, @Param("goodsPriceSeqNo") Long goodsPriceSeqNo,
                                       @Param("orderType") Integer orderType, @Param("orderProcess") Integer orderProcess,
                                       @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration, Pageable pageable) ;

    @Query(value="select ifnull(sum(bg.price), 0) as price  from buy_goods bg"
            + "                inner join goods g on bg.goods_seq_no = g.seq_no "
            + "                             and ( isnull(:type) = 1      or g.type = :type )"
            + "                             and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "                                   or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "                                   or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "                                   or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "                                   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "                                 )"
            + " where 1=1 "
            + " and ( isnull(:seqNo)         = 1 or bg.seq_no = :seqNo ) "
            + " and ( isnull(:buySeqNo)      = 1 or bg.buy_seq_no = :buySeqNo ) "
            + " and ( isnull(:memberSeqNo)   = 1  or bg.member_seq_no = :memberSeqNo ) "
            + " and ( isnull(:isReviewExist) = 1 or (bg.is_review_exist = :isReviewExist and (:isReviewExist <> false or g.type <> 0 or bg.order_process = 2)) ) "
            + " and ( isnull(:pageSeqNo)     = 1 or bg.page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:goodsSeqNo)    = 1 or bg.goods_seq_no = :goodsSeqNo ) "
            + " and ( isnull(:goodsPriceSeqNo)    = 1 or bg.goods_price_seq_no = :goodsPriceSeqNo ) "
            +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
            + " and ( isnull(:orderType) = 1 or bg.order_type = :orderType  ) "
            + " and ( isnull(:orderProcess) = 1 or bg.order_process = :orderProcess ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration)   = 1 or bg.reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Float findAllByWithPrice(@Param("seqNo") Long seqNo, @Param("buySeqNo") Long buySeqNo,
                                       @Param("memberSeqNo") Long memberSeqNo, @Param("type") Integer type,
                                       @Param("isHotdeal") Boolean isHotdeal,
                                       @Param("isPlus") Boolean isPlus,
                                       @Param("isReviewExist") Boolean isReviewExist,
                                       @Param("pageSeqNo") Long pageSeqNo, @Param("process") Integer process, @Param("goodsSeqNo") Long goodsSeqNo, @Param("goodsPriceSeqNo") Long goodsPriceSeqNo,
                                       @Param("orderType") Integer orderType, @Param("orderProcess") Integer orderProcess,
                                       @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration) ;


    @Query(value="select * from buy_goods bg "
            + " where 1=1 "
            + " and bg.supply_page_seq_no is not null "
            + " and bg.goods_price_seq_no is not null "
            + " and bg.supply_page_seq_no <> bg.page_seq_no "
            + " and ( isnull(:pageSeqNo)     = 1 or bg.page_seq_no = :pageSeqNo ) "
            + " and ( ( isnull(:process) = 0 and bg.process = :process ) or ( isnull(:process) = 1 and bg.process > 0 ) ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration)   = 1 or bg.reg_datetime <= :endDuration ) ",
            countQuery = "select count(1) from buy_goods bg "
                    + " where 1=1 "
                    + " and bg.supply_page_seq_no is not null "
                    + " and bg.goods_price_seq_no is not null "
                    + " and bg.supply_page_seq_no <> bg.page_seq_no "
                    + " and ( isnull(:pageSeqNo)     = 1 or bg.page_seq_no = :pageSeqNo ) "
                    + " and ( ( isnull(:process) = 0 and bg.process = :process ) or ( isnull(:process) = 1 and bg.process > 0 ) ) "
                    + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
                    + " and ( isnull(:endDuration) = 1   or bg.reg_datetime <= :endDuration ) ", nativeQuery = true)
    Page<BuyGoodsDetail> findAllByPageSeqNoOnlySupplyGoods(@Param("pageSeqNo") Long pageSeqNo, @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration, Integer process, Pageable pageable) ;

    @Query(value="select * from buy_goods bg "
            + " where 1=1 "
            + " and bg.supply_page_seq_no is not null "
            + " and bg.goods_price_seq_no is not null "
            + " and bg.supply_page_seq_no <> bg.page_seq_no "
            + " and ( isnull(:goodsPriceSeqNo)     = 1 or bg.goods_price_seq_no = :goodsPriceSeqNo ) "
            + " and ( ( isnull(:process) = 0 and bg.process = :process ) or ( isnull(:process) = 1 and bg.process > 0 ) ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration)   = 1 or bg.reg_datetime <= :endDuration ) ",
            countQuery = "select count(1) from buy_goods bg "
                    + " where 1=1 "
                    + " and bg.supply_page_seq_no is not null "
                    + " and bg.goods_price_seq_no is not null "
                    + " and bg.supply_page_seq_no <> bg.page_seq_no "
                    + " and ( isnull(:goodsPriceSeqNo)     = 1 or bg.goods_price_seq_no = :goodsPriceSeqNo ) "
                    + " and ( ( isnull(:process) = 0 and bg.process = :process ) or ( isnull(:process) = 1 and bg.process > 0 ) ) "
                    + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
                    + " and ( isnull(:endDuration) = 1   or bg.reg_datetime <= :endDuration ) ", nativeQuery = true)
    Page<BuyGoodsDetail> findAllByGoodsPriceSeqNoOnlySupplyGoods(@Param("goodsPriceSeqNo") Long goodsPriceSeqNo, @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration, Integer process, Pageable pageable) ;

    @Query(value="select * from buy_goods bg "
            + " where 1=1 "
            + " and bg.supply_page_seq_no = :supplyPageSeqNo "
            + " and ( ( isnull(:process) = 0 and bg.process = :process ) or ( isnull(:process) = 1 and bg.process > 0 ) ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration)   = 1 or bg.reg_datetime <= :endDuration ) ",
            countQuery = "select count(1) from buy_goods bg "
                    + " where 1=1 "
                    + " and bg.supply_page_seq_no = :supplyPageSeqNo "
                    + " and ( ( isnull(:process) = 0 and bg.process = :process ) or ( isnull(:process) = 1 and bg.process > 0 ) ) "
                    + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
                    + " and ( isnull(:endDuration) = 1   or bg.reg_datetime <= :endDuration ) ", nativeQuery = true)
    Page<BuyGoodsDetail> findAllBySupplyPageSeqNo(@Param("supplyPageSeqNo") Long supplyPageSeqNo, @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration, Integer process, Pageable pageable) ;
}
