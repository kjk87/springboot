package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BolHistory;
import kr.co.pplus.store.api.jpa.model.VisitorPointGiveHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface VisitorPointGiveHistoryRepository extends JpaRepository<VisitorPointGiveHistory, Long> {

    Page<VisitorPointGiveHistory> findAllByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqualAndTypeNotIn(Long pageSeqNo, String startDatetime, String endDatetime, List<String> types, Pageable pageable);

    Integer countByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, String startDatetime, String endDatetime);

    Integer countByPageSeqNoAndReceiverSeqNoAndType(Long pageSeqNo, Long receiverSeqNo, String type);

    VisitorPointGiveHistory findByPageSeqNoAndReceiverSeqNoAndType(Long pageSeqNo, Long receiverSeqNo, String type);

    @Query(value="select ifnull(sum(price), 0) from visitor_point_give_history"
            +" where 1=1 "
            +" AND page_seq_no = :pageSeqNo "
            +" AND reg_datetime >= :startDatetime "
            +" AND reg_datetime <= :endDatetime",
            nativeQuery = true)
    Integer sumPrice(Long pageSeqNo, String startDatetime, String endDatetime) ;

    VisitorPointGiveHistory findBySeqNo(Long seqNo);

}
