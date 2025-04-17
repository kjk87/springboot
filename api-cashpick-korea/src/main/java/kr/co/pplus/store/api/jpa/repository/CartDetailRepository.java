package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

    List<CartDetail> findAllByMemberSeqNoAndSalesType(Long memberSeqNo, Integer salesType);

    Integer countByMemberSeqNoAndSalesType(Long memberSeqNo, Integer salesType);
}