package kr.co.pplus.store.api.jpa.repository.delivery;

import kr.co.pplus.store.api.jpa.model.delivery.Delivery;
import kr.co.pplus.store.api.jpa.model.delivery.DeliveryGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface DeliveryGoodsRepository extends JpaRepository<DeliveryGoods, Long> {

    DeliveryGoods findBySeqNo(Long seqNo) ;
}