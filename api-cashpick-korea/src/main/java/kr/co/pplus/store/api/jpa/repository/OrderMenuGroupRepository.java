package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderMenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderMenuGroupRepository extends JpaRepository<OrderMenuGroup, Long> {

    List<OrderMenuGroup> findAllByPageSeqNoAndDeletedOrderByArrayAsc(Long pageSeqNo, Boolean deleted);

}