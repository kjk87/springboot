package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventJoinWithLottoNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface EventJoinWithLottoNumberRepository extends JpaRepository<EventJoinWithLottoNumber, Long> {

    List<EventJoinWithLottoNumber> findAllByEventSeqNoAndMemberSeqNo(Long eventSeqNo, Long memberSeqNo);

}