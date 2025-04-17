package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LottoMemberCount;
import kr.co.pplus.store.api.jpa.model.LottoWinNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LottoMemberCountRepository extends JpaRepository<LottoMemberCount, Long> {

    Integer countByEventSeqNoAndIsWinner(Long eventSeqNo, Boolean isWinner);

}