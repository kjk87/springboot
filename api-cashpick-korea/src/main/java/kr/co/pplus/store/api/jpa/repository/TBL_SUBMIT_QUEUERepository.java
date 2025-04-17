package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.TBL_SUBMIT_QUEUE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = "jpaTransactionManager")
public interface TBL_SUBMIT_QUEUERepository extends JpaRepository<TBL_SUBMIT_QUEUE, Long>{

}
