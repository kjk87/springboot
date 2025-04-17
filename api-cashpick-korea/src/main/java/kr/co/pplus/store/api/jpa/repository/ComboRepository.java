package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Combo;
import kr.co.pplus.store.api.jpa.model.ComboEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ComboRepository extends JpaRepository<Combo, Long> {

    Combo findFirstByMemberSeqNo(Long memberSeqNo);

}