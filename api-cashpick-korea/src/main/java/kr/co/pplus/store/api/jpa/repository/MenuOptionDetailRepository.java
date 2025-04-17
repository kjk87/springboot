package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MenuOptionDetail;
import kr.co.pplus.store.api.jpa.model.OrderMenuWithOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MenuOptionDetailRepository extends JpaRepository<MenuOptionDetail, Long> {

    MenuOptionDetail findBySeqNo(Long seqNo);

}