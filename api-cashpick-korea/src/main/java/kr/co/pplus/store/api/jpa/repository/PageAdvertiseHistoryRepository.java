package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageAdvertiseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface PageAdvertiseHistoryRepository extends JpaRepository<PageAdvertiseHistory, Long> {

    Page<PageAdvertiseHistory> findAllByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, String startDatetime, String endDatetime, Pageable pageable);

    @Query(value="select ifnull(sum(price), 0) from page_advertise_history"
            +" where 1=1 "
            +" AND page_seq_no = :pageSeqNo "
            +" AND reg_datetime >= :startDatetime "
            +" AND reg_datetime <= :endDatetime",
            nativeQuery = true)
    Integer sumPrice(Long pageSeqNo, String startDatetime, String endDatetime) ;

    @Query(value="select fn_get_total_profit(:agentCode)",
            nativeQuery = true)
    Float totalProfit(String agentCode) ;
}