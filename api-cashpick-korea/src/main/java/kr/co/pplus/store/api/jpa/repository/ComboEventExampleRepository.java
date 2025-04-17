package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboEvent;
import kr.co.pplus.store.api.jpa.model.ComboEventExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ComboEventExampleRepository extends JpaRepository<ComboEventExample, Long> {

    List<ComboEventExample> findAllByComboEventSeqNoOrderByArrayAsc(Long comboEventSeqNo);

}