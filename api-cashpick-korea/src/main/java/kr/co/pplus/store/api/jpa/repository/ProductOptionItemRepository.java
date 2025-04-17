package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsOptionItem;
import kr.co.pplus.store.api.jpa.model.ProductOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductOptionItemRepository extends JpaRepository<ProductOptionItem, Long>{

	List<ProductOptionItem> findByProductSeqNoOrderBySeqNoAsc(Long productSeqNo);

	void deleteByProductSeqNo(Long productSeqNo);

}
