package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolRepository extends JpaRepository<LuckyBol, Long> {

    LuckyBol findFirstByStatusInAndEngageTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderBySeqNoAsc(List<String> statusList, String engageType, String startDatetime, String endDatetime);

    List<LuckyBol> findAllByStatusInAndEngageTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqual(List<String> statusList, String engageType, String startDatetime, String endDatetime);

    Page<LuckyBol> findAllByStatusInOrderBySeqNoDesc(List<String> statusList, Pageable pageable);

    LuckyBol findBySeqNo(Long seqNo);

    @Modifying
    @Query(value = "update lucky_bol set status = :status where seq_no = :seqNo ", nativeQuery = true)
    void updateLuckyBolStatus(@Param("seqNo") Long seqNo, @Param("status") String status);
}