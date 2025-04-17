package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyGoodsOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyGoodsOptionRepository extends JpaRepository<BuyGoodsOption, Long>{

    List<BuyGoodsOption> findAllByBuyGoodsSeqNo(Long buyGoodsSeqNo);

}
