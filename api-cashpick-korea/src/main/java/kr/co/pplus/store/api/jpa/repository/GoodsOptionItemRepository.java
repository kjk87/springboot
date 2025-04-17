package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsOptionItemRepository extends JpaRepository<GoodsOptionItem, Long>{

	List<GoodsOptionItem> findByGoodsSeqNoOrderBySeqNoAsc(Long goodsSeqNo);

	void deleteByGoodsSeqNo(Long goodsSeqNo);

}
