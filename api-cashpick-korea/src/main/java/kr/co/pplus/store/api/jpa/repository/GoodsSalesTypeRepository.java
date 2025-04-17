package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BolHistory;
import kr.co.pplus.store.api.jpa.model.GoodsSalesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsSalesTypeRepository extends JpaRepository<GoodsSalesType, Long> {

    void deleteAllByGoodsSeqNo(Long goodsSeqNo);
}
