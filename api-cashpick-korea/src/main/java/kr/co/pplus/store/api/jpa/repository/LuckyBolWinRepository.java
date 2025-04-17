package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolWin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolWinRepository extends JpaRepository<LuckyBolWin, Long> {

    Page<LuckyBolWin> findAllByStatus(String status, Pageable pageable);

    List<LuckyBolWin> findAllByLuckyBolSeqNoAndStatusOrderByGiftGradeAsc(Long luckyBolSeqNo, String status);

    List<LuckyBolWin> findAllByLuckyBolSeqNoAndStatusAndMemberSeqNo(Long luckyBolSeqNo, String status, Long memberSeqNo);

    LuckyBolWin findBySeqNo(Long seqNo);

    @Modifying
    @Query(value = "update lucky_bol_win set impression = :impression where seq_no = :seqNo and member_seq_no = :memberSeqNo", nativeQuery = true)
    void updateLuckyBolWinImpression(@Param("seqNo") Long seqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("impression") String impression);
}