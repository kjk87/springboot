package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductPriceOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductPriceOnlyRepository extends JpaRepository<ProductPriceOnly, Long>{

	ProductPriceOnly findBySeqNo(Long seqNo);

	ProductPriceOnly findByCode(String code);

	ProductPriceOnly findByProductSeqNoAndMarketType(Long productSeqNo, Integer marketType);

	List<ProductPriceOnly> findByProductSeqNo(Long productSeqNo);

	@Modifying
	@Query(value = "UPDATE product_price set status = :status where product_seq_no = :productSeqNo", nativeQuery = true)
	void updateProductPriceStatusByProductSeqNo(@Param("status") Integer status, @Param("productSeqNo") Long productSeqNo);

	@Modifying
	@Query(value = "UPDATE product_price set status = :status where product_seq_no = :productSeqNo and status = 0", nativeQuery = true)
	void updateProductPriceStatusByProductSeqNoAndSoldOut(@Param("status") Integer status, @Param("productSeqNo") Long productSeqNo);

	@Modifying
	@Query(value = "UPDATE product_price set status = :status where seq_no = :seqNo", nativeQuery = true)
	void updateProductPriceStatusBySeqNo(@Param("status") Integer status, @Param("seqNo") Long seqNo);

	@Modifying
	@Query(value = "UPDATE product_price set pick = :pick where seq_no = :seqNo", nativeQuery = true)
	void updateProductPricePickBySeqNo(@Param("pick") Boolean pick, @Param("seqNo") Long seqNo);

	@Modifying
	@Query(value = "UPDATE product_price set pick = :pick where page_seq_no = :pageSeqNo", nativeQuery = true)
	void updateProductPricePickByPageSeqNo(@Param("pick") Boolean pick, @Param("pageSeqNo") Long pageSeqNo);
}
