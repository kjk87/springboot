package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolNumberRepository extends JpaRepository<LuckyBolNumber, Long> {

    LuckyBolNumber findByUniqueKey(String uniqueKey);

    long countByLuckyBolSeqNoAndUsed(Long luckyBolSeqNo, Boolean used);

    List<LuckyBolNumber> findAllByLuckyBolSeqNoAndUsed(Long luckyBolSeqNo, Boolean used);

    boolean existsByUniqueKeyInAndUsed(List<String> uniqueKeyList, Boolean used);

    @Query(value = "select * from lucky_bol_number where lucky_bol_seq_no = :luckyBolSeqNo and used = false order by rand() limit 1", nativeQuery = true)
    LuckyBolNumber findRandom(Long luckyBolSeqNo);

    @Modifying
    @Query(value = "update lucky_bol_number set used = true where unique_key in (:uniqueKeyList) ", nativeQuery = true)
    void updateLuckyBolNumberUse(@Param("uniqueKeyList") List<String> uniqueKeyList);
}