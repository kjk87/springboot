package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PrepaymentPublish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PrepaymentPublishRepository extends JpaRepository<PrepaymentPublish, Long> {

    Integer countByMemberSeqNoAndStatus(Long memberSeqNo, String status);

    @Query(value = "select *,"
            + " (case when status = 'normal' then 0 "
            + "      when status = 'completed' then 1 "
            + "      when status = 'expired' then 2 end ) as sort"
            + " from prepayment_publish pb "
            + " where pb.page_seq_no =:pageSeqNo "
            + " and pb.member_seq_no = :memberSeqNo"
            + " and pb.status in ('normal', 'completed', 'expired')"
            + " order by sort asc", nativeQuery = true)
    List<PrepaymentPublish> findAllByPageSeqNoAndMemberSeqNoAndStatusIn(Long pageSeqNo, Long memberSeqNo);


    PrepaymentPublish findBySeqNo(Long seqNo);

    @Query(value = "select count(1) from prepayment_publish "
            + " where prepayment_seq_no = :prepaymentSeqNo "
            + " and status in ('normal', 'completed', 'expired')",
            nativeQuery = true)
    Integer countByPrepaymentSeqNo(Long prepaymentSeqNo);
}