package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BolHistory;
import kr.co.pplus.store.api.jpa.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByMemberSeqNoOrderByRepresentDescIdDesc(Long memberSeqNo);


}
