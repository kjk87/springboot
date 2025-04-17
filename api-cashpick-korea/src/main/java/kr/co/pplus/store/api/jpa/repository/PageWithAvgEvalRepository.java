package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageWithAvgEval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageWithAvgEvalRepository extends JpaRepository<PageWithAvgEval, Long> {

    String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude))))";
    String MY_PLUS_PAGE = " if( isnull(pl.seq_no), 0, 1) as plus, ";

    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(omr.eval) from order_menu_review omr where omr.page_seq_no = p.seq_no) as avg_eval"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.seq_no =:seqNo ", nativeQuery = true)
    PageWithAvgEval findBySeqNoWithDistance(Long seqNo,
                                            Double latitude,
                                            Double longitude,
                                            Long memberSeqNo);

    @Query(value = "select p.*, false as plus, 0.0 as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(omr.eval) from order_menu_review omr where omr.page_seq_no = p.seq_no) as avg_eval"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " where p.seq_no =:seqNo ", nativeQuery = true)
    PageWithAvgEval findBySeqNo(Long seqNo);


    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(eval) from order_menu_review where page_seq_no = p.seq_no) as avg_eval"
//            + ", true as is_business_hour"
//            + ", false as is_day_off"
//            + ", false as is_time_off"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + " and (p.order_type like '%2%' or p.order_type like '%3%')"
            + " and " + HAVERSINE_PART + " <= p.rider_distance "
//            + " and case when p.business_hours_type = 4 then true else (select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0 end "
//            + " and (select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) = 0 "
//            + " and (select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) = 0 "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + " and " + HAVERSINE_PART + " <= p.rider_distance "
                    + " and (p.order_type like '%2%' or p.order_type like '%3%')"
//                    + " and case when p.business_hours_type = 4 then true else (select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0 end "
//                    + " and (select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) = 0 "
//                    + " and (select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) = 0 "
            , nativeQuery = true)
    Page<PageWithAvgEval> findAllDeliveryPage(Double latitude,
                                              Double longitude,
                                              Long memberSeqNo,
                                              Long categoryMajorSeqNo,
                                              Long categoryMinorSeqNo, Pageable pageable);

    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(eval) from order_menu_review where page_seq_no = p.seq_no) as avg_eval"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + " and (p.order_type like '%2%' or p.order_type like '%3%')"
            + " and " + HAVERSINE_PART + " <= p.rider_distance "
            + " and (p.page_name LIKE CONCAT('%', :keyword, '%') or p.catchphrase LIKE CONCAT('%',:keyword, '%') or p.hashtag LIKE CONCAT('%',:keyword, '%')) "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + " and " + HAVERSINE_PART + " <= p.rider_distance "
                    + " and (p.order_type like '%2%' or p.order_type like '%3%')"
                    + " and (p.page_name LIKE CONCAT('%', :keyword, '%') or p.catchphrase LIKE CONCAT('%',:keyword, '%') or p.hashtag LIKE CONCAT('%',:keyword, '%')) "
            , nativeQuery = true)
    Page<PageWithAvgEval> findAllDeliveryPageByKeyword(Double latitude,
                                                       Double longitude,
                                                       Long memberSeqNo,
                                                       Long categoryMajorSeqNo,
                                                       Long categoryMinorSeqNo,
                                                       String keyword,
                                                       Pageable pageable);

    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(eval) from order_menu_review where page_seq_no = p.seq_no) as avg_eval"
//            + ", true as is_business_hour"
//            + ", false as is_day_off"
//            + ", false as is_time_off"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + " and p.order_type like '%1%' "
//            + " and case when p.business_hours_type = 4 then true else (select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0 end "
//            + " and (select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) = 0 "
//            + " and (select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) = 0 "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + " and p.order_type like '%1%' "
//                    + " and case when p.business_hours_type = 4 then true else (select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0 end "
//                    + " and (select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) = 0 "
//                    + " and (select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) = 0 "
            , nativeQuery = true)
    Page<PageWithAvgEval> findAllVisitPage(Double latitude,
                                           Double longitude,
                                           Long memberSeqNo,
                                           Long categoryMajorSeqNo,
                                           Long categoryMinorSeqNo, Pageable pageable);

    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(eval) from order_menu_review where page_seq_no = p.seq_no) as avg_eval"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + " and p.order_type like '%1%' "
            + " and (p.page_name LIKE CONCAT('%', :keyword, '%') or p.catchphrase LIKE CONCAT('%',:keyword, '%') or p.hashtag LIKE CONCAT('%',:keyword, '%')) "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + " and p.order_type like '%1%' "
                    + " and (p.page_name LIKE CONCAT('%', :keyword, '%') or p.catchphrase LIKE CONCAT('%',:keyword, '%') or p.hashtag LIKE CONCAT('%',:keyword, '%')) "
            , nativeQuery = true)
    Page<PageWithAvgEval> findAllVisitPageByKeyword(Double latitude,
                                                    Double longitude,
                                                    Long memberSeqNo,
                                                    Long categoryMajorSeqNo,
                                                    Long categoryMinorSeqNo,
                                                    String keyword,
                                                    Pageable pageable);

    @Query(value = "select p.*, false as plus, " + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(eval) from order_menu_review where page_seq_no = p.seq_no) as avg_eval"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.business_category = 'service' "
            + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.business_category = 'service' "
                    + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            , nativeQuery = true)
    Page<PageWithAvgEval> findAllServicePage(Double latitude,
                                           Double longitude,
                                           Long categoryMajorSeqNo,
                                           Long categoryMinorSeqNo, Pageable pageable);

    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", (select count(1) from order_menu_review where page_seq_no = p.seq_no) as review_count"
            + ", (select avg(eval) from order_menu_review where page_seq_no = p.seq_no) as avg_eval"
//            + ", true as is_business_hour"
//            + ", false as is_day_off"
//            + ", false as is_time_off"
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
            + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
            + " and p.order_type like '%1%' "
//            + " and case when p.business_hours_type = 4 then true else (select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0 end "
//            + " and (select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) = 0 "
//            + " and (select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) = 0 "
            + " and (p.latitude between :bottomL and :topL) and (p.longitude between :leftL and :rightL) "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and ( isnull(:categoryMinorSeqNo) = 1 or p.category_minor_seq_no = :categoryMinorSeqNo) "
                    + " and ( isnull(:categoryMajorSeqNo) = 1 or p.category_major_seq_no = :categoryMajorSeqNo) "
                    + " and p.order_type like '%1%' "
//                    + " and case when p.business_hours_type = 4 then true else (select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0 end "
//                    + " and (select count(1) from page_day_off pdo where pdo.page_seq_no = p.seq_no and (pdo.week = 0 or pdo.week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and pdo.day = DAYOFWEEK(now())) = 0 "
//                    + " and (select count(1) from page_time_off pto where pto.page_seq_no = p.seq_no and pto.start < curtime() and pto.end > curtime()) = 0 "
                    + " and (p.latitude between :bottomL and :topL) and (p.longitude between :leftL and :rightL) "
            , nativeQuery = true)
    Page<PageWithAvgEval> findAllVisitPageByArea(Double latitude,
                                           Double longitude,
                                           Double topL,
                                           Double bottomL,
                                           Double leftL,
                                           Double rightL,
                                           Long memberSeqNo,
                                           Long categoryMajorSeqNo,
                                           Long categoryMinorSeqNo, Pageable pageable);
}