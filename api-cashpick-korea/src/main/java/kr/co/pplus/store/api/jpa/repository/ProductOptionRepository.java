package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsOption;
import kr.co.pplus.store.api.jpa.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long>{

	List<ProductOption> findByProductSeqNoOrderBySeqNoAsc(Long productSeqNo);

	ProductOption findBySeqNo(Long seqNo);

	void deleteByProductSeqNo(Long productSeqNo);

}
