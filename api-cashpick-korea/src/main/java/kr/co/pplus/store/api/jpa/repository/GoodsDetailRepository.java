package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsDetailRepository extends JpaRepository<GoodsDetail, Long> {

    static final String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude))))";

    public static final String GOODS_COLUMNS =
            " g.seq_no          ,"
                    + " g.page_seq_no     ,"
                    + " g.category_seq_no ,"
                    + " g.name            ,"
                    + " g.hashtag         ,"
                    + " g.description     ,"
                    + " g.count           ,"
                    + " g.sold_count      ,"
                    + " g.status          ,"
                    + " g.goods_prop      ,"
                    + " g.reg_datetime    ,"
                    + " g.mod_datetime    ,"
                    + " g.price           ,"
                    + " g.origin_price    ,"
                    + " g.expire_datetime ,"
                    + " g.expire_day      ,"
                    + " g.lang            ,"
                    + " g.type            ,"
                    + " g.is_hotdeal      ,"
                    + " g.is_plus         ,"
                    + " g.start_time      ,"
                    + " g.end_time        ,"
                    + " g.reward_luckybol ,"
                    + " g.reward_pr_link  ,"
                    + " g.reward_pr_review_link  ,"
                    + " g.attachments     ,"
                    + " g.news_datetime    ,"
                    + " g.register_type    ,"
                    + " g.register    ,"
                    + " g.blind    ,"
                    + " g.represent    ,"
                    + " g.note    ,"
                    + " g.reason    ,"
                    + " g.service_condition    ,"
                    + " g.time_option    ,"
                    + " g.is_deleted    ,"
                    + " g.discount_ratio    ,"
                    + " g.is_coupon    ,"
                    + " g.all_days    ,"
                    + " g.all_weeks    ,"
                    + " g.day_of_weeks    ,"
                    + " g.sales_type    ,"
                    + " g.is_packing    ,"
                    + " g.is_store    ,"
                    + " g.delivery_fee    ,"
                    + " g.refund_delivery_fee    ,"
                    + " g.delivery_add_fee    ,"
                    + " g.delivery_min_price    ,"
                    + " g.reservation_min_number    ,"
                    + " g.reservation_max_number    ,"
                    + " g.is_delivery_plus    ,"
                    + " g.is_delivery_hotdeal    ,"
                    + " g.external_url    ,"
                    + " g.option_type    ,"
                    + " g.buyable_count    ,"
                    + " g.detail_type    ,"
                    + " g.review_point    ,"
                    + " g.recommend    ,"
                    + " g.sales_types    ,"
                    + " g.market_type    ,"
                    + " g.first    ,"
                    + " g.second    ,"
                    + " g.third    ,"
                    + " g.wholesale_code    ";

    public static final String SELECT_GOODS_COLUMNS =
            " select " + GOODS_COLUMNS + ", ";

    static final String MY_PLUS_PAGE = " if( isnull(ps.seq_no), 0, 1) as plus, ";

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g "
            + " where g.seq_no = :seqNo "
            , nativeQuery = true)
    GoodsDetail findBySeqNo(@Param("seqNo") Long seq_no);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            +" from goods g where g.page_seq_no = :pageSeqNo and g.status = 1 and g.is_hotdeal = true and g.sales_types like '%1%' order by g.seq_no desc limit 1"
            , nativeQuery = true)
    GoodsDetail findFirstByPageSeqNoAndIsHotdealTrue(@Param("pageSeqNo") Long pageSeqNo);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            +" from goods g where g.page_seq_no = :pageSeqNo and g.status = 1 and g.is_hotdeal = true and g.sales_types like '%3%' order by g.seq_no desc limit 1"
            , nativeQuery = true)
    GoodsDetail findFirstByPageSeqNoAndIsHotdealTrueShipType(@Param("pageSeqNo") Long pageSeqNo);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            +" from goods g where g.page_seq_no = :pageSeqNo and g.status != 1 and g.status != -999 and g.is_hotdeal = true and g.sales_types like '%1%' "
            ,
            countQuery = " select count(1) from goods g where g.page_seq_no = :pageSeqNo and g.status != 1 and g.status != -999 and g.is_hotdeal = true and g.sales_types like '%1%' "
            , nativeQuery = true)
    Page<GoodsDetail> findAllByPageSeqNoOldHotdeal(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            +" from goods g where g.page_seq_no = :pageSeqNo and g.status != 1 and g.status != -999 and g.is_hotdeal = true and g.sales_types like '%3%' "
            ,
            countQuery = " select count(1) from goods g where g.page_seq_no = :pageSeqNo and g.status != 1 and g.status != -999 and g.is_hotdeal = true and g.sales_types like '%3%' "
            , nativeQuery = true)
    Page<GoodsDetail> findAllByPageSeqNoOldHotdealShipType(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            +" from goods g where g.page_seq_no = :pageSeqNo and g.status = 1 and g.is_coupon = true order by g.seq_no desc limit 1"
            , nativeQuery = true)
    GoodsDetail findFirstByPageSeqNoAndIsCouponTrue(@Param("pageSeqNo") Long pageSeqNo);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            +" from goods g where g.page_seq_no = :pageSeqNo and g.status != 1 and g.status != -999 and g.is_coupon = true"
            ,
            countQuery = " select count(1) from goods g where g.page_seq_no = :pageSeqNo and g.status != 1 and g.status != -999 and g.is_coupon = true"
            , nativeQuery = true)
    Page<GoodsDetail> findAllByPageSeqNoOldCoupon(@Param("pageSeqNo") Long pageSeqNo, Pageable pageable);

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
            + " where 1 = 1 "
            + "   and g.sales_types like '%1%'"
            + "   and ( case when :isCoupon = true then g.is_coupon = 1 else g.is_coupon = 0 end) = 1"
            + "   and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
            + "   and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
            + "   and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
            + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + "   and ( g.status is null or g.status <> -999 ) "
            + "   and ( isnull(:status) = 1  or g.status = :status )"
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
            + "   and ( isnull(:rewardPrLink) = 1 or  g.reward_pr_link > :rewardPrLink ) "
            + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
            + "   and ( isnull(:expired) = 1 or "
            + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
            + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) ) "
            + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
            + "   and (case when :status = 1 then g.blind != true else 1 end) = 1"
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
                    + "             where 1 = 1 "
                    + "               and g.sales_types like '%1%'"
                    + "               and ( case when :isCoupon = true then g.is_coupon = 1 else g.is_coupon = 0 end) = 1"
                    + "               and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
                    + "               and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
                    + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
                    + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + "               and ( g.status is null or g.status <> -999 ) "
                    + "               and ( isnull(:status) = 1 or g.status = :status )"
                    + "               and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "               and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                    + "                     or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                    + "                     or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                    + "                     or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                    + "                     or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "                   )"
                    + "               and ( isnull(:name) = 1 or  g.name like :name ) "
                    + "               and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
                    + "               and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
                    + "               and ( isnull(:rewardPrLink) = 1 or  g.reward_pr_link > :rewardPrLink ) "
                    + "   and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
                    + "   and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
                    + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
                    + "   and ( isnull(:expired) = 1 or "
                    + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
                    + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) )"
                    + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
                    + "   and (case when :status = 1 then g.blind != true else 1 end) = 1"

            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllByWith(@Param("categoryMinorSeqNo") final Long categoryMinorSeqNo, @Param("categoryMajorSeqNo") final Long categoryMajorSeqNo,
                                    @Param("memberSeqNo") Long memberSeqNo, @Param("pageSeqNo") Long pageSeqNo, @Param("goodsCategorySeqNo") Long goodsCategorySeqNo,
                                    @Param("name") String name, @Param("minPrice") Float minPrice, @Param("maxPrice") Float maxPrice,
                                    @Param("minOriginPrice") Float minOriginPrice, @Param("maxOriginPrice") Float maxOriginPrice,
                                    @Param("expired") Boolean expired, @Param("openBounds") String openBounds, @Param("status") Integer status, @Param("type") Integer type,
                                    @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, @Param("isCoupon") Boolean isCoupon,
                                    @Param("rewardPrLink") Integer rewardPrLink, @Param("woodongyi") Boolean woodongyi, Pageable pageable
    );


    @Query(value = " select " + GOODS_COLUMNS + ", case WHEN :distance is null THEN 0.0 ELSE " + HAVERSINE_PART + " END as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
            + " where 1 = 1 "
            + "   and g.blind != true"
            + "   and g.sales_types like '%1%'"
            + "   and g.is_coupon = 0 "
            + "   and ( case when :represent = true then g.represent = 1 else 1 end) = 1"
            + "   and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
            + "   and ( isnull(:distance) = 1 or " + HAVERSINE_PART + " < :distance ) "
            + "   and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
            + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
            + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + "   and ( g.status = 1 ) "
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
            + "   and ( g.expire_datetime is null or g.expire_datetime > now() ) "
            + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
            + "   and (case  when :isRealTime = true then g.day_of_weeks REGEXP weekday(sysdate()) and (case when g.start_time < g.end_time then g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') and g.end_time >= DATE_FORMAT(now(), '%H:%i:%s')"
            + "                                                                                                                    else g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') or g.end_time >= DATE_FORMAT(now(), '%H:%i:%s') end) else 1 end) = 1"
//            + "   order by distance"
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
                    + "             where 1 = 1 "
                    + "               and g.blind != true"
                    + "               and g.sales_types like '%1%'"
                    + "               and g.is_coupon = 0 "
                    + "               and ( case when :represent = true then g.represent = 1 else 1 end) = 1"
                    + "               and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
                    + "               and ( isnull(:distance) = 1 or " + HAVERSINE_PART + " < :distance ) "
                    + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
                    + "               and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
                    + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + "   and ( g.status = 1 ) "
                    + "               and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                    + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                    + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                    + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                    + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "        )"
                    + "               and ( isnull(:name) = 1 or  g.name like :name ) "
                    + "               and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
                    + "               and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
                    + "                 and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
                    + "                 and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
                    + "   and ( g.count = -1 or g.count > g.sold_count ) "
                    + "   and ( g.expire_datetime is null or g.expire_datetime > now() ) "
                    + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
                    + "   and (case  when :isRealTime = true then g.day_of_weeks REGEXP weekday(sysdate()) and (case when g.start_time < g.end_time then g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') and g.end_time >= DATE_FORMAT(now(), '%H:%i:%s')"
                    + "                                                                                                                    else g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') or g.end_time >= DATE_FORMAT(now(), '%H:%i:%s') end) else 1 end) = 1"
            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllByWithLocationNotOrderBy(@Param("latitude") final Double latitude,
                                            @Param("longitude") final Double longitude,
                                            @Param("distance") final Double distance,
                                            @Param("categoryMinorSeqNo") final Long categoryMinorSeqNo, @Param("categoryMajorSeqNo") final Long categoryMajorSeqNo,
                                            @Param("goodsCategorySeqNo") Long goodsCategorySeqNo,
                                            @Param("memberSeqNo") Long memberSeqNo, @Param("pageSeqNo") Long pageSeqNo,
                                            @Param("name") String name, @Param("minPrice") Float minPrice, @Param("maxPrice") Float maxPrice,
                                            @Param("minOriginPrice") Float minOriginPrice, @Param("maxOriginPrice") Float maxOriginPrice, @Param("openBounds") String openBounds,
                                            @Param("type") Integer type,
                                            @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, @Param("represent") Boolean represent, @Param("woodongyi") Boolean woodongyi, @Param("isRealTime") Boolean isRealTime, Pageable pageable
    );

    @Query(value = " select " + GOODS_COLUMNS + ", case WHEN :distance is null THEN 0.0 ELSE " + HAVERSINE_PART + " END as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
            + " where 1 = 1 "
            + "   and g.blind != true"
            + "   and g.sales_types like '%1%'"
            + "   and g.is_coupon = 0 "
            + "   and ( case when :represent = true then g.represent = 1 else 1 end) = 1"
            + "   and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
            + "   and ( isnull(:distance) = 1 or " + HAVERSINE_PART + " < :distance ) "
            + "   and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
            + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
            + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + "   and ( g.status = 1 ) "
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
            + "   and ( g.expire_datetime is null or g.expire_datetime > now() ) "
            + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
            + "   and (case  when :isRealTime = true then g.day_of_weeks REGEXP weekday(sysdate()) and (case when g.start_time < g.end_time then g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') and g.end_time >= DATE_FORMAT(now(), '%H:%i:%s')"
            + "                                                                                                                    else g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') or g.end_time >= DATE_FORMAT(now(), '%H:%i:%s') end) else 1 end) = 1"
            + "   order by distance"
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
                    + "             where 1 = 1 "
                    + "               and g.blind != true"
                    + "               and g.sales_types like '%1%'"
                    + "               and g.is_coupon = 0 "
                    + "               and ( case when :represent = true then g.represent = 1 else 1 end) = 1"
                    + "               and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
                    + "               and ( isnull(:distance) = 1 or " + HAVERSINE_PART + " < :distance ) "
                    + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
                    + "               and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
                    + "               and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + "               and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + "               and ( g.status = 1 ) "
                    + "               and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "               and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                    + "                 or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                    + "                 or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                    + "                 or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                    + "                 or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "                 )"
                    + "               and ( isnull(:name) = 1 or  g.name like :name ) "
                    + "               and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
                    + "               and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
                    + "               and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
                    + "               and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
                    + "               and ( g.count = -1 or g.count > g.sold_count ) "
                    + "               and ( g.expire_datetime is null or g.expire_datetime > now() ) "
                    + "               and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
                    + "               and (case  when :isRealTime = true then g.day_of_weeks REGEXP weekday(sysdate()) and (case when g.start_time < g.end_time then g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') and g.end_time >= DATE_FORMAT(now(), '%H:%i:%s')"
                    + "                                                                                                                    else g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') or g.end_time >= DATE_FORMAT(now(), '%H:%i:%s') end) else 1 end) = 1"
            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllByWithLocation(@Param("latitude") final Double latitude,
                                            @Param("longitude") final Double longitude,
                                            @Param("distance") final Double distance,
                                            @Param("categoryMinorSeqNo") final Long categoryMinorSeqNo, @Param("categoryMajorSeqNo") final Long categoryMajorSeqNo,
                                            @Param("goodsCategorySeqNo") Long goodsCategorySeqNo,
                                            @Param("memberSeqNo") Long memberSeqNo, @Param("pageSeqNo") Long pageSeqNo,
                                            @Param("name") String name, @Param("minPrice") Float minPrice, @Param("maxPrice") Float maxPrice,
                                            @Param("minOriginPrice") Float minOriginPrice, @Param("maxOriginPrice") Float maxOriginPrice, @Param("openBounds") String openBounds,
                                            @Param("type") Integer type,
                                            @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, @Param("represent") Boolean represent, @Param("woodongyi") Boolean woodongyi, @Param("isRealTime") Boolean isRealTime, Pageable pageable
    );

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
            + " where 1 = 1 "
            + "   and g.blind != true"
            + "   and g.sales_types like '%3%'"
            + "   and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
            + "   and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
            + "   and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
            + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + "   and ( g.status = 1 ) "
            + "   and ( isnull(:type) = 1 or  g.type = :type ) "
            + "   and ( isnull(:name) = 1 or  g.name like :name ) "
            + "   and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
            + "   and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
            + "   and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
            + "   and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
            + "   and ( g.count = -1 or g.count > g.sold_count ) "
            + "   and ( isnull(:isHotdeal) = 1 or  g.is_hotdeal = :isHotdeal) "
            + "   and ( isnull(:isPlus) = 1 or  g.is_plus = :isPlus) "
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
                    + "             where 1 = 1 "
                    + "               and g.blind != true"
                    + "               and g.sales_types like '%3%'"
                    + "               and ( isnull(:memberSeqNo) = 1 or p.member_seq_no = :memberSeqNo) "
                    + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
                    + "               and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
                    + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + "               and ( g.status = 1 ) "
                    + "               and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "               and ( isnull(:name) = 1 or  g.name like :name ) "
                    + "               and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
                    + "               and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
                    + "                 and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
                    + "                 and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
                    + "   and ( g.count = -1 or g.count > g.sold_count ) "
                    + "   and ( isnull(:isHotdeal) = 1 or  g.is_hotdeal = :isHotdeal) "
                    + "   and ( isnull(:isPlus) = 1 or  g.is_plus = :isPlus) "
            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllShipType(@Param("categoryMinorSeqNo") final Long categoryMinorSeqNo, @Param("categoryMajorSeqNo") final Long categoryMajorSeqNo,
                                            @Param("goodsCategorySeqNo") Long goodsCategorySeqNo,
                                            @Param("memberSeqNo") Long memberSeqNo, @Param("pageSeqNo") Long pageSeqNo,
                                            @Param("name") String name, @Param("minPrice") Float minPrice, @Param("maxPrice") Float maxPrice,
                                            @Param("minOriginPrice") Float minOriginPrice, @Param("maxOriginPrice") Float maxOriginPrice, @Param("openBounds") String openBounds,
                                            @Param("type") Integer type, @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, Pageable pageable
    );

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no "
            + " where 1 = 1 "
            + "   and g.sales_types like '%3%'"
            + "   and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
            + "   and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
            + "   and ( isnull(:isHotdeal) = 1 or  g.is_hotdeal = :isHotdeal) "
            + "   and ( isnull(:isPlus) = 1 or  g.is_plus = :isPlus) "
            + "   and g.status <> -999 "
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no "
                    + "             where 1 = 1 "
                    + "               and g.sales_types like '%3%'"
                    + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
                    + "               and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
                    + "               and ( isnull(:isHotdeal) = 1 or  g.is_hotdeal = :isHotdeal) "
                    + "               and ( isnull(:isPlus) = 1 or  g.is_plus = :isPlus) "
                    + "               and g.status <> -999 "
            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllShipTypeAllByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo, @Param("goodsCategorySeqNo") Long goodsCategorySeqNo, @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, Pageable pageable);

    @Query(value = " select " + GOODS_COLUMNS + ", case WHEN :distance is null THEN 0.0 ELSE " + HAVERSINE_PART + " END as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
            + " where 1 = 1 "
            + "   and g.blind != true"
            + "   and g.sales_types like '%1%'"
            + "   and ( case when :isCoupon = true then g.is_coupon = 1 else g.is_coupon = 0 end) = 1"
            + "   and ( case when :represent = true then g.represent = 1 else 1 end) = 1"
            + "   and ( isnull(:distance) = 1 or " + HAVERSINE_PART + " < :distance ) "
            + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
            + "   and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + "   and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + "   and ( g.status is null or g.status <> -999 ) "
            + "   and ( isnull(:status) = 1 or  g.status = :status ) "
            + "   and ( isnull(:type) = 1 or  g.type = :type ) "
            + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "        )"
            + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
            + "   and ( isnull(:expired) = 1 or "
            + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
            + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) ) "
            + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
            + "   and (case  when :isRealTime = true then g.day_of_weeks REGEXP weekday(sysdate()) and (case when g.start_time < g.end_time then g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') and g.end_time >= DATE_FORMAT(now(), '%H:%i:%s')"
            + "                                                                                                                    else g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') or g.end_time >= DATE_FORMAT(now(), '%H:%i:%s') end) else 1 end) = 1"
            + "   and (p.latitude BETWEEN :bottom AND :top and p.longitude BETWEEN :left AND :right)"
            + "   order by distance"
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
                    + "             where 1 = 1 "
                    + "               and g.blind != true"
                    + "               and g.sales_types like '%1%'"
                    + "               and ( case when :isCoupon = true then g.is_coupon = 1 else g.is_coupon = 0 end) = 1"
                    + "               and ( case when :represent = true then g.represent = 1 else 1 end) = 1"
                    + "               and ( isnull(:distance) = 1 or " + HAVERSINE_PART + " < :distance ) "
                    + "               and ( isnull(:goodsCategorySeqNo) = 1 or  g.category_seq_no = :goodsCategorySeqNo ) "
                    + "               and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + "               and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + "               and ( g.status is null or g.status <> -999 ) "
                    + "               and ( isnull(:status) = 1 or  g.status = :status ) "
                    + "               and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                    + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                    + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                    + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                    + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "        )"
                    + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
                    + "   and ( isnull(:expired) = 1 or "
                    + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
                    + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) )"
                    + "   and (case when :woodongyi = true then p.woodongyi = 1 else 1 end) = 1"
                    + "   and (case  when :isRealTime = true then g.day_of_weeks REGEXP weekday(sysdate()) and (case when g.start_time < g.end_time then g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') and g.end_time >= DATE_FORMAT(now(), '%H:%i:%s')"
                    + "                                                                                                                    else g.start_time <= DATE_FORMAT(now(), '%H:%i:%s') or g.end_time >= DATE_FORMAT(now(), '%H:%i:%s') end) else 1 end) = 1"
                    + "   and (p.latitude BETWEEN :bottom AND :top and p.longitude BETWEEN :left AND :right)"
            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllByWithLocationArea(@Param("latitude") final Double latitude,
                                            @Param("longitude") final Double longitude,
                                            @Param("distance") final Double distance,
                                            @Param("top") Double top, @Param("bottom") Double bottom, @Param("left") Double left, @Param("right") Double right,
                                            @Param("categoryMinorSeqNo") final Long categoryMinorSeqNo, @Param("categoryMajorSeqNo") final Long categoryMajorSeqNo,
                                            @Param("goodsCategorySeqNo") Long goodsCategorySeqNo,
                                            @Param("expired") Boolean expired, @Param("openBounds") String openBounds, @Param("status") Integer status,
                                            @Param("type") Integer type,
                                            @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus,
                                            @Param("isCoupon") Boolean isCoupon, @Param("represent") Boolean represent, @Param("woodongyi") Boolean woodongyi, @Param("isRealTime") Boolean isRealTime, Pageable pageable
    );


    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like  where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
            + "              inner join plus ps on ps.page_seq_no = p.seq_no and ps.block = 'N' and ps.member_seq_no = :memberSeqNo "
            + " where 1 = 1 "
            + "   and g.blind != true"
            + "   andg.sales_types like '%1%'"
            + "   and ( g.status is null or g.status <> -999 ) "
            + "   and ( isnull(:status) = 1 or  g.status = :status ) "
            + "   and ( isnull(:type) = 1 or  g.type = :type ) "
            + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "        ) "
            + "   and ( isnull(:name) = 1 or  g.name like :name ) "
            + "   and ( isnull(:minPrice) = 1 or  :minPrice < g.price ) "
            + "   and ( isnull(:maxPrice) = 1 or  g.price < :maxPrice) "
            + "   and ( isnull(:minOriginPrice) = 1 or  :minOriginPrice < g.origin_price ) "
            + "   and ( isnull(:maxOriginPrice) = 1 or  g.origin_price < :maxOriginPrice) "
            + "   and ( isnull(:rewardPrLink) = 1 or  g.reward_pr_link > :rewardPrLink ) "
            + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
            + "   and ( isnull(:expired) = 1 or "
            + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
            + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) )"
            ,
            countQuery = "select count(1) "
                    + " from goods g inner join page p  on p.seq_no = g.page_seq_no and ( isnull(:openBounds) = 1 or  p.open_bounds = :openBounds ) and p.status = 'normal' "
                    + "              inner join plus ps on ps.page_seq_no = p.seq_no and ps.block = 'N' and ps.member_seq_no = :memberSeqNo "
                    + " where 1 = 1 "
                    + "   and g.blind != true"
                    + "   and g.sales_types like '%1%'"
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
                    + "   and ( isnull(:rewardPrLink) = 1 or  g.reward_pr_link > :rewardPrLink ) "
                    + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
                    + "   and ( isnull(:expired) = 1 or "
                    + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
                    + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now()))  )"
            , nativeQuery = true)
    Page<GoodsDetail> findPlusAllByWith(@Param("memberSeqNo") Long memberSeqNo, @Param("name") String name, @Param("minPrice") Float minPrice, @Param("maxPrice") Float maxPrice,
                                        @Param("minOriginPrice") Float minOriginPrice, @Param("maxOriginPrice") Float maxOriginPrice,
                                        @Param("expired") Boolean expired, @Param("openBounds") String openBounds, @Param("status") Integer status, @Param("type") Integer type,
                                        @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus,
                                        @Param("rewardPrLink") Integer rewardPrLink, Pageable pageable

    );

    @Query(value = " select " + GOODS_COLUMNS + ", 0.0 as distance, "
            + " ( select count(1) from goods_review  where goods_seq_no = g.seq_no ) as review_count, "
            + " ( select avg(eval) from goods_review  where goods_seq_no = g.seq_no ) as avg_eval, "
            + " ( select sum(ifnull(status,0)) from goods_like where goods_seq_no = g.seq_no ) as like_count"
            + " from goods g inner join page p  on p.seq_no = g.page_seq_no and p.status = 'normal' "
            + " where 1 = 1 "
            + "   and g.sales_types like '%1%'"
            + "   and ( case when :isCoupon = true then g.is_coupon = 1 else g.is_coupon = 0 end) = 1"
            + "   and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
            + "   and ( g.status is null or g.status <> -999 ) "
            + "   and ( isnull(:status) = 1  or g.status = :status )"
            + "   and ( isnull(:type) = 1 or  g.type = :type ) "
            + "   and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "         or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "         or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "         or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "         or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "        )"
            + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
            + "   and ( isnull(:expired) = 1 or "
            + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
            + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) ) "
            ,
            countQuery = " select count(1) "
                    + "             from goods g inner join page p  on p.seq_no = g.page_seq_no  and p.status = 'normal' "
                    + "             where 1 = 1 "
                    + "               and g.sales_types like '%1%'"
                    + "               and ( case when :isCoupon = true then g.is_coupon = 1 else g.is_coupon = 0 end) = 1"
                    + "               and ( isnull(:pageSeqNo) = 1 or  p.seq_no = :pageSeqNo ) "
                    + "               and ( g.status is null or g.status <> -999 ) "
                    + "               and ( isnull(:status) = 1 or g.status = :status )"
                    + "               and ( isnull(:type) = 1 or  g.type = :type ) "
                    + "               and (    ( :isHotdeal = true   and :isPlus = true  and (g.is_hotdeal = 1 or g.is_plus = 1) )  "
                    + "                     or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
                    + "                     or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
                    + "                     or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
                    + "                     or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "                   )"
                    + "   and ( g.count = -1 or (isnull(:status)=1 or (:status <> 0 and g.count > g.sold_count)) ) "
                    + "   and ( isnull(:expired) = 1 or "
                    + "         (:expired = 0 and (g.expire_datetime is null or g.expire_datetime > now())) or   "
                    + "         (:expired = 1 and (g.expire_datetime is not null and g.expire_datetime <= now())) )"
            ,
            nativeQuery = true)
    Page<GoodsDetail> findAllByWithByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo,
                                    @Param("expired") Boolean expired, @Param("status") Integer status, @Param("type") Integer type,
                                    @Param("isHotdeal") Boolean isHotdeal, @Param("isPlus") Boolean isPlus, @Param("isCoupon") Boolean isCoupon, Pageable pageable
    );
}
