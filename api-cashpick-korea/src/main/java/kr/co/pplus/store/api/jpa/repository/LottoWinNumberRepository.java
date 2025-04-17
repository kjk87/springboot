package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventJoinWithLottoNumber;
import kr.co.pplus.store.api.jpa.model.LottoWinNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LottoWinNumberRepository extends JpaRepository<LottoWinNumber, Long> {

    List<LottoWinNumber> findAllByEventSeqNoOrderByLottoNumberAsc(Long eventSeqNo);

}