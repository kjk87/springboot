package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductOptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductOptionDetailRepository extends JpaRepository<ProductOptionDetail, Long>{

	List<ProductOptionDetail> findByProductSeqNoAndUsableAndStatusOrderBySeqNoAsc(Long productSeqNo, Boolean usable, Integer status);

	ProductOptionDetail findBySeqNo(Long seqNo);

	@Modifying
	@Query(value = "update product_option_detail set sold_count = sold_count + :amount where seq_no = :seqNo", nativeQuery = true)
	void updatePlusSoldCountBySeqNo(@Param("seqNo") Long seqNo, @Param("amount") Integer amount);

	@Modifying
	@Query(value = "update product_option_detail set sold_count = sold_count - :amount where seq_no = :seqNo", nativeQuery = true)
	void updateMinusSoldCountBySeqNo(@Param("seqNo") Long seqNo, @Param("amount") Integer amount);

}
