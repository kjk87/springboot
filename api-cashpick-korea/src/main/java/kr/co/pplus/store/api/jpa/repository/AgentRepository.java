package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Agent;
import kr.co.pplus.store.api.jpa.model.PlusOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Agent findByCode(String code);

}