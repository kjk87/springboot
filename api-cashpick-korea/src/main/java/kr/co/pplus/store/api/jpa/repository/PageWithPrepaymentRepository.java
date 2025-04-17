package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageWithPrepayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageWithPrepaymentRepository extends JpaRepository<PageWithPrepayment, Long> {

    String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude))))";
    String MY_PLUS_PAGE = " if( isnull(pl.seq_no), 0, 1) as plus, ";


    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and (select count(1) from prepayment where page_seq_no = p.seq_no and status = 'normal') > 0 "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and (select count(1) from prepayment where page_seq_no = p.seq_no and status = 'normal') > 0 "
            , nativeQuery = true)
    Page<PageWithPrepayment> findAllWithPrepayment(Double latitude,
                                                   Double longitude,
                                                   Long memberSeqNo, Pageable pageable);

    @Query(value = "select p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " and (select count(1) from prepayment where page_seq_no = p.seq_no and status = 'normal') > 0 "
            + " and (select count(1) from visit_log where page_seq_no = p.seq_no and member_seq_no = :memberSeqNo and status = 'completed') > 0 "
            + " order by distance ",
            countQuery = "select count(1) "
                    + " from page p "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
                    + " and (select count(1) from prepayment where page_seq_no = p.seq_no and status = 'normal') > 0 "
                    + " and (select count(1) from visit_log where page_seq_no = p.seq_no and member_seq_no = :memberSeqNo and status = 'completed') > 0 "
            , nativeQuery = true)
    Page<PageWithPrepayment> findAllWithPrepaymentExistVisitLog(Double latitude,
                                                                Double longitude,
                                                                Long memberSeqNo, Pageable pageable);

}