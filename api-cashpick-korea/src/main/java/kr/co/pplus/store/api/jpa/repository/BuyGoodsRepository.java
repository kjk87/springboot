package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyGoodsRepository extends JpaRepository<BuyGoods, Long> {

    List<BuyGoods> findAllByBuySeqNo(Long buySeqNo);

    BuyGoods findBySeqNo(Long seqNo) ;

    @Modifying
    void deleteBySeqNo(Long seqNo) ;

    @Modifying
    void deleteByBuySeqNo(Long buySeqNo) ;

    @Query(value = "select * from pplus.buy_goods where process = 1 and expire_datetime < now()", nativeQuery = true)
    List<BuyGoods> findAllExpiredBuyGoodsList() ;

    Integer countAllByPageSeqNoAndProcessAndOrderTypeAndOrderProcessIn(Long pageSeqNo, Integer process, Integer orderType, List<Integer> orderPorcess);


    @Modifying
    @Query("UPDATE buyGoods set mod_datetime = :modDatetime, is_payment_point=:isPaymentPoint where buy_seq_no = :buySeqNo")
    void updateBuyGoodsPaymentPointByBuySeqNo(@Param("buySeqNo") Long buySeqNo, @Param("modDatetime") String modDatetime, @Param("isPaymentPoint") Integer isPaymentPoint);

    @Modifying
    @Query("UPDATE buyGoods set mod_datetime = :modDatetime, is_payment_point=:isPaymentPoint where seq_no = :seqNo")
    void updateBuyGoodsPaymentPointBySeqNo(@Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime, @Param("isPaymentPoint") Integer isPaymentPoint);

    @Modifying
    @Query("UPDATE buyGoods set process = :process, mod_datetime = :modDatetime, use_datetime = :modDatetime, is_payment_point=:isPaymentPoint where buy_seq_no = :buySeqNo")
    void updateUseBuyGoodsByBuySeqNo(@Param("process") Integer process, @Param("buySeqNo") Long buySeqNo, @Param("modDatetime") String modDatetime, @Param("isPaymentPoint") Integer isPaymentPoint);

    @Modifying
    @Query("UPDATE buyGoods set process = :process, mod_datetime = :modDatetime, cancel_datetime = :modDatetime where buy_seq_no = :buySeqNo")
    void updateCancelByBuySeqNo(@Param("process") Integer process, @Param("buySeqNo") Long buySeqNo, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query("UPDATE buyGoods set order_process = :orderProcess, transport_number = :transportNumber, shipping_company = :shippingCompany, shipping_company_code = :shippingCompanyCode, delivery_start_datetime = :modDatetime, mod_datetime = :modDatetime where seq_no = :seqNo")
    void updateTransportNumberBySeqNo(@Param("orderProcess") Integer orderProcess, @Param("seqNo") Long seqNo, @Param("transportNumber") String transportNumber
            , @Param("shippingCompany") String shippingCompany, @Param("shippingCompanyCode") String shippingCompanyCode, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query("UPDATE buyGoods set transport_number = :transportNumber, shipping_company = :shippingCompany, shipping_company_code = :shippingCompanyCode, mod_datetime = :modDatetime where seq_no = :seqNo")
    void updateOnlyTransportNumberBySeqNo(@Param("seqNo") Long seqNo, @Param("transportNumber") String transportNumber
            , @Param("shippingCompany") String shippingCompany, @Param("shippingCompanyCode") String shippingCompanyCode, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query("UPDATE buyGoods set order_process = :orderProcess, delivery_complete_datetime = :modDatetime, mod_datetime = :modDatetime where seq_no = :seqNo")
    void updateDeliveryCompleteBySeqNo(@Param("orderProcess") Integer orderProcess, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query("UPDATE buyGoods set order_process = :orderProcess, complete_datetime = :modDatetime, mod_datetime = :modDatetime where seq_no = :seqNo")
    void updateBuyCompleteBySeqNo(@Param("orderProcess") Integer orderProcess, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);


    @Query(value = "select * from buyGoods where order_type = 3 and process = 1 and order_process = 3 and delivery_complete_datetime < date_add(now(), INTERVAL -7 DAY)", nativeQuery = true)
    List<BuyGoods> findAllNeedCompleteList();

    List<BuyGoods> findAllByOrderProcessAndOrderType(Integer orderProcess, Integer orderType);



    @Query(value="select * from buy_goods"
            + " where 1=1 "
            + " and ( isnull(:buySeqNo) = 1 or buy_seq_no = :buySeqNo ) "
            + " and ( isnull(:memberSeqNo) = 1  or member_seq_no = :memberSeqNo ) "
            + " and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:goodsSeqNo) = 1 or goods_seq_no = :goodsSeqNo ) "
            + " and ( isnull(:process) = 1 or process = :process ) "
            + " and ( isnull(:isReviewExist) = 1 or (bg.is_review_exist = :isReviewExist and (:isReviewExist <> false or g.type <> 0 or bg.order_process = 2)) ) "
            + " and ( isnull(:orderType) = 1 or order_type = :orderType ) "
            + " and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
            + " and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ", nativeQuery = true)
    Page<BuyGoods> findAllBy(@Param("buySeqNo") Long buySeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("isReviewExist") Boolean isReviewExist,
                             @Param("pageSeqNo") Long pageSeqNo, @Param("process") Integer process, @Param("goodsSeqNo") Long goodsSeqNo,
                             @Param("orderType") Integer orderType, @Param("orderProcess") Integer orderProcess,
                             @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration, Pageable pageable) ;


    @Query(value="select count(1) from buy_goods bg inner join goods g on bg.goods_seq_no = g.seq_no "
            + " where 1=1 "
            + " and ( isnull(:buySeqNo) = 1 or bg.buy_seq_no = :buySeqNo ) "
            + " and ( isnull(:memberSeqNo) = 1  or bg.member_seq_no = :memberSeqNo ) "
            + " and ( isnull(:pageSeqNo) = 1 or bg.page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:goodsSeqNo) = 1 or bg.goods_seq_no = :goodsSeqNo ) "
            + " and ( isnull(:goodsPriceSeqNo) = 1 or bg.goods_price_seq_no = :goodsPriceSeqNo ) "
            + " and ( isnull(:isReviewExist) = 1 or (bg.is_review_exist = :isReviewExist and (:isReviewExist <> false or g.type <> 0 or bg.order_process = 2)) ) "
            + " and ( isnull(:process) = 1 or bg.process = :process ) "
            + " and ( isnull(:type) = 1 or g.type = :type ) "
            + " and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "       or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "       or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "       or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "       or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "     )"
            + " and ( g.type = 1 or isnull(:orderType) = 1 or bg.order_type = :orderType ) "
            + " and ( g.type = 1 or isnull(:orderProcess) = 1 or bg.order_process = :orderProcess ) "
            + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration) = 1 or bg.reg_datetime <= :endDuration ) ", nativeQuery = true)
    Integer countAllBy(@Param("buySeqNo") Long buySeqNo, @Param("memberSeqNo") Long memberSeqNo,
                       @Param("type") Integer type, @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus,
                       @Param("isReviewExist") Boolean isReviewExist,
                       @Param("pageSeqNo") Long pageSeqNo, @Param("process") Integer process, @Param("goodsSeqNo") Long goodsSeqNo, @Param("goodsPriceSeqNo") Long goodsPriceSeqNo,
                       @Param("orderType") Integer orderType, @Param("orderProcess") Integer orderProcess,
                       @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration) ;


    @Query(value="select ifnull(sum(bg.price),0) from buy_goods bg inner join goods g on bg.goods_seq_no = g.seq_no "
                + " where 1=1 "
                + " and ( isnull(:buySeqNo) = 1 or bg.buy_seq_no = :buySeqNo ) "
                + " and ( isnull(:memberSeqNo) = 1  or bg.member_seq_no = :memberSeqNo ) "
                + " and ( isnull(:pageSeqNo) = 1 or bg.page_seq_no = :pageSeqNo ) "
                + " and ( isnull(:goodsSeqNo) = 1 or bg.goods_seq_no = :goodsSeqNo ) "
                + " and ( isnull(:isReviewExist) = 1 or (bg.is_review_exist = :isReviewExist and (:isReviewExist <> false or g.type <> 0 or bg.order_process = 2)) ) "
                + " and ( isnull(:process) = 1 or bg.process = :process ) "
                + " and ( isnull(:type) = 1 or g.type = :type ) "
                + " and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                + "       or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                + "       or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                + "       or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                + "       or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                + "     )"
                + " and ( g.type = 1 or isnull(:orderType) = 1 or bg.order_type = :orderType ) "
                + " and ( g.type = 1 or isnull(:orderProcess) = 1 or bg.order_process = :orderProcess ) "
                + " and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
                + " and ( isnull(:endDuration) = 1 or bg.reg_datetime <= :endDuration ) ", nativeQuery = true)
    Float priceAllBy(@Param("buySeqNo") Long buySeqNo, @Param("memberSeqNo") Long memberSeqNo,
                     @Param("type") Integer type, @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus,
                     @Param("isReviewExist") Boolean isReviewExist,
                     @Param("pageSeqNo") Long pageSeqNo, @Param("process") Integer process, @Param("goodsSeqNo") Long goodsSeqNo,
                     @Param("orderType") Integer orderType, @Param("orderProcess") Integer orderProcess,
                     @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration) ;

    Integer countByGoodsSeqNoAndProcess(Long goodsSeqNo, Integer process) ;

    @Query(value="select sum(bg.count) from buy_goods bg "
            + " where 1=1 "
            + " and bg.member_seq_no = :memberSeqNo "
            + " and bg.goods_seq_no = :goodsSeqNo "
            + " and bg.process = 1 "
            + " and ( bg.order_process != 5 ) ", nativeQuery = true)
    Integer countBuyGoodsByGoodsSeqNoAndMemberSeqNo(@Param("memberSeqNo") Long memberSeqNo, @Param("goodsSeqNo") Long goodsSeqNo) ;

    @Query(value = "select ifnull(sum(supply_price + delivery_fee), 0) as price from buy_goods "
            +" where 1=1 "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:supplyPageSeqNo) = 1 or supply_page_seq_no = :supplyPageSeqNo ) "
            +" and ( order_type = 3 )"
            +" and ( process = 1 )"
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Float sumSupplyPrice(@Param("memberSeqNo") Long memberSeqNo,
                              @Param("supplyPageSeqNo") Long supplyPageSeqNo,
                              @Param("startDuration") Date startDuration,
                              @Param("endDuration") Date endDuration) ;

    @Query(value = "select count(1) from buy_goods "
            +" where 1=1 "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:supplyPageSeqNo) = 1 or supply_page_seq_no = :supplyPageSeqNo ) "
            +" and ( order_type = 3 )"
            +" and ( process = 1 )"
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Integer countSupplySales(@Param("memberSeqNo") Long memberSeqNo,
                         @Param("supplyPageSeqNo") Long supplyPageSeqNo,
                         @Param("startDuration") Date startDuration,
                         @Param("endDuration") Date endDuration) ;

}