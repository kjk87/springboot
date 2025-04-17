package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByMemberSeqNoAndSalesType(Long memberSeqNo, Integer salesType);

    Cart findBySeqNoAndMemberSeqNo(Long seqNo, Long memberSeqNo);

    void deleteByMemberSeqNoAndSalesType(Long memberSeqNo, Integer salesType);
}