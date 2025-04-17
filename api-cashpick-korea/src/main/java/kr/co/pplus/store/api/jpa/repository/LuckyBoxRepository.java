package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Agent;
import kr.co.pplus.store.api.jpa.model.LuckyBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxRepository extends JpaRepository<LuckyBox, Long> {

    List<LuckyBox> findAllByStatusOrderByArrayAsc(String status);

    LuckyBox findBySeqNo(Long seqNo);
}