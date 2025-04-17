package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolGiftRepository extends JpaRepository<LuckyBolGift, Long> {

    Page<LuckyBolGift> findAllByLuckyBolSeqNo(Long luckyBolSeqNo, Pageable pageable);

    LuckyBolGift findFirstByLuckyBolSeqNoOrderByGradeAsc(Long luckyBolSeqNo);

    LuckyBolGift findBySeqNo(Long seqNo);

}