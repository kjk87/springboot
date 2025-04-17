package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Goods;
import kr.co.pplus.store.api.jpa.model.GoodsCategory;
import kr.co.pplus.store.api.jpa.model.GoodsDetail;
import kr.co.pplus.store.api.jpa.model.PageEvalResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Goods findBySeqNo(Long seqNo) ;

    Integer countByPageSeqNoAndIsCoupon(Long pageSeqNo, Boolean isCoupon);

    @Query(value = "select g.* from goods g inner join buy_goods bg on bg.buy_seq_no = :buySeqNo and bg.goods_seq_no = g.seq_no limit 1", nativeQuery = true)
    Goods findTop1ByBuySeqNo(@Param("buySeqNo") Long buySeqNo) ;

    @Query(value="select count(1) from goods "
                + " where page_seq_no = :pageSeqNo and news_datetime >= :today",nativeQuery = true)
    Long countAllByPageSeqNoAndNewsDatetimeGreaterThan(@Param("pageSeqNo") Long pageSeqNo, @Param("today") Date today) ;

    @Modifying
    @Query(value="update goods set category_seq_no = 1 where page_seq_no = :pageSeqNo and category_seq_no = :categorySeqNo", nativeQuery = true)
    void resetCategory(@Param("pageSeqNo") Long pageSeqNo, @Param("categorySeqNo") Long categorySeqNo) ;

    @Modifying
    @Query(value="update goods set status = -1 where page_seq_no = :pageSeqNo and is_hotdeal = 1 and status != -999", nativeQuery = true)
    void updateHotdealAsFinish(@Param("pageSeqNo") Long pageSeqNo) ;

    @Modifying
    @Query(value="update goods set status = -1 where page_seq_no = :pageSeqNo and is_coupon = 1 and status != -999", nativeQuery = true)
    void updateCouponAsFinish(@Param("pageSeqNo") Long pageSeqNo) ;

    @Modifying
    @Query("update goods set sold_count = sold_count - :amount where seqNo = :seqNo")
    void updateMinusSoldCountBySeqNo(@Param("seqNo") Long seqNo, @Param("amount") Integer amount);

    @Modifying
    @Query(value="update goods set category_seq_no = :currentGoodsCategorySeqNo where page_seq_no = :pageSeqNo and category_seq_no = :beforeGoodsCategorySeqNo", nativeQuery = true)
    void updateCategorySeqNoByPageSeqNo(@Param("currentGoodsCategorySeqNo") Long currentGoodsCategorySeqNo, @Param("beforeGoodsCategorySeqNo") Long beforeGoodsCategorySeqNo, @Param("pageSeqNo") Long pageSeqNo) ;

    @Query(value = "select g.* "
            + " from goods g  "
            + " where 1 = 1 "
            + "   and ( isnull(:pageSeqNo) = 1 or  g.page_seq_no = :pageSeqNo ) "
            + "   and ( g.status is null or g.status <> -999 ) "
            + "   and ( isnull(:status) = 1 or  g.status = :status ) "
            + "   and ( isnull(:type) = 1 or  g.type = :type ) "
            + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "        )"
            + "   and ( isnull(:name) = 1 or  g.name like :name ) "
            + "   and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
            + "   and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
            + "   and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
            + "   and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
            + "   and ( g.count = -1 or g.count > g.sold_count ) "
            + "   and ( isnull(:expired) = 1 or "
            + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
            + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) )"
            ,
            countQuery = "select count(1) "
                    + " from goods g "
                    + " where 1 = 1 "
                    + "   and ( isnull(:pageSeqNo) = 1 or  g.page_seq_no = :pageSeqNo ) "
                    + "   and ( g.status is null or g.status <> -999 ) "
                    + "   and ( isnull(:status) = 1 or  g.status = :status ) "
                    + "   and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                    + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                    + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                    + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                    + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "        )"
                    + "   and ( isnull(:name) = 1 or  g.name like :name ) "
                    + "   and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
                    + "   and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
                    + "   and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
                    + "   and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
                    + "   and ( g.count = -1 or g.count > g.sold_count ) "
                    + "   and ( isnull(:expired) = 1 or "
                    + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
                    + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now()))  )"
            , nativeQuery = true)
    Page<Goods> findAllByWith(@Param("pageSeqNo") Long pageSeqNo, @Param("name") String name, @Param("minPrice") Float minPrice, @Param("maxPrice") Float maxPrice,
                              @Param("minOriginPrice") Float minOriginPrice, @Param("maxOriginPrice") Float maxOriginPrice,
                              @Param("expired") Boolean expired, @Param("status") Integer status,
                              @Param("type") Integer type,
                              @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, Pageable pageable) ;



}