package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Combo;
import kr.co.pplus.store.api.jpa.model.ComboJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ComboJoinRepository extends JpaRepository<ComboJoin, Long> {

    ComboJoin findFirstByMemberSeqNoAndComboEventSeqNo(Long memberSeqNo, Long comboEventSeqNo);

}