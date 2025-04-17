package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageDetailRepository extends JpaRepository<PageDetail, Long> {

    String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude))))";
    String MY_PLUS_PAGE = " if( isnull(pl.seq_no), 0, 1) as plus, " ;



    @Query(value = "select distinct p.seq_no as d_seq_no, p.*, " +  MY_PLUS_PAGE +   HAVERSINE_PART + " as distance "
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " inner join product_price pp on pp.page_seq_no = p.seq_no and pp.status = 1 and pp.is_ticket = true "
            + " inner join product pr on pr.seq_no = pp.product_seq_no and pr.blind != true and pr.status = 1 "
            + " where p.open_bounds ='everybody' "
            + " and ( ISNULL(:storeType) = 1 or p.store_type = :storeType ) "
            + " order by distance",
            countQuery = "select distinct count(p.seq_no) "
                    + " from page p "
                    + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
                    + " inner join product_price pp on pp.page_seq_no = p.seq_no and pp.status = 1 and pp.is_ticket = true "
                    + " inner join product pr on pr.seq_no = pp.product_seq_no and pr.blind != true and pr.status = 1 "
                    + " where p.open_bounds ='everybody' "
                    + " and ( ISNULL(:storeType) = 1 or p.store_type = :storeType ) "

            , nativeQuery = true)
    Page<PageDetail> findAllByLocation(@Param("latitude") final Double latitude,
                                       @Param("longitude") final Double longitude,
                                       @Param("memberSeqNo") final Long memberSeqNo,
                                       @Param("storeType") String storeType,
                                       Pageable pageable) ;

    @Query(value = "select distinct p.seq_no as d_seq_no, p.*, " +  MY_PLUS_PAGE +   HAVERSINE_PART + " as distance "
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " inner join product_price pp on pp.page_seq_no = p.seq_no and pp.status = 1 and (pp.is_subscription = true or pp.is_prepayment) "
            + " inner join product pr on pr.seq_no = pp.product_seq_no and pr.blind != true and pr.status = 1 "
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " order by distance",
            countQuery = "select distinct count(p.seq_no) "
                    + " from page p "
                    + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
                    + " inner join product_price pp on pp.page_seq_no = p.seq_no and pp.status = 1 and (pp.is_subscription = true or pp.is_prepayment) "
                    + " inner join product pr on pr.seq_no = pp.product_seq_no and pr.blind != true and pr.status = 1 "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
            , nativeQuery = true)
    Page<PageDetail> findAllByLocationExistSubscription(@Param("latitude") final Double latitude,
                                       @Param("longitude") final Double longitude,
                                       @Param("memberSeqNo") final Long memberSeqNo,
                                       Pageable pageable) ;

    @Query(value = "select p.*, 0 as plus, 0.0 as distance "
            + " from page p "
            + " where p.member_seq_no = :memberSeqNo limit 1", nativeQuery = true)
    PageDetail findByMemberSeqNo(Long memberSeqNo);


    @Modifying
    @Query(value="update page set orderable = :orderable where seq_no = :pageSeqNo", nativeQuery = true)
    void updateOrderable(Long pageSeqNo, Boolean orderable) ;

    @Query(value = "select distinct p.seq_no as d_seq_no, p.*, " + MY_PLUS_PAGE + HAVERSINE_PART + " as distance "
            + ", ((select count(1) from page_business_hours where page_seq_no = p.seq_no and day = DAYOFWEEK(now()) and case when two_days = 0 then open_time <= curtime() and close_time >= curtime() else open_time <= curtime() or close_time >= curtime() end) > 0) as is_business_hour"
            + ", ((select count(1) from page_day_off where page_seq_no = p.seq_no and (week = 0 or week = (week(now(), 5) - week(DATE_SUB(now(), INTERVAL DAYOFMONTH(now())-1 DAY), 5) + 1)) and day = DAYOFWEEK(now())) > 0) as is_day_off"
            + ", ((select count(1) from page_time_off where page_seq_no = p.seq_no and start < curtime() and end > curtime()) > 0) as is_time_off"
            + " from page p "
            + " left join plus pl on pl.member_seq_no = :memberSeqNo and pl.page_seq_no = p.seq_no "
            + " inner join prepayment_publish pb on pb.page_seq_no = p.seq_no and pb.member_seq_no = :memberSeqNo and pb.status in ('normal', 'completed', 'expired')"
            + " where p.open_bounds ='everybody' "
            + " and p.store_type = 'offline' "
            + " and p.status = 'normal' "
            + " and p.orderable = true "
            + " order by pb.seq_no desc ",
            countQuery = "select distinct count(p.seq_no) "
                    + " from page p "
                    + " inner join prepayment_publish pb on pb.page_seq_no = p.seq_no and pb.member_seq_no = :memberSeqNo and pb.status in ('normal', 'completed', 'expired') "
                    + " where p.open_bounds ='everybody' "
                    + " and p.store_type = 'offline' "
                    + " and p.status = 'normal' "
                    + " and p.orderable = true "
            , nativeQuery = true)
    Page<PageDetail> findAllWithPrepaymentPublish(Double latitude,
                                                  Double longitude,
                                                  Long memberSeqNo, Pageable pageable);

}