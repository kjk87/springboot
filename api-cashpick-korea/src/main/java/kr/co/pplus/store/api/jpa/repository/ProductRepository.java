package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findBySeqNo(Long seqNo);

	@Modifying
	@Query("update product set sold_count = sold_count - :amount where seqNo = :seqNo")
	void updateMinusSoldCountBySeqNo(@Param("seqNo") Long seqNo, @Param("amount") Integer amount);

	@Modifying
	@Query("update product set sold_count = sold_count + :amount, status = :status, mod_datetime = :modDatetime where seqNo = :seqNo")
	void updateSoldCountAndStatusBySeqNo(@Param("seqNo") Long seqNo, @Param("status") Integer status, @Param("amount") Integer amount, @Param("modDatetime") String modDatetime);

	@Modifying
	@Query(value = "UPDATE product set status = :status where seq_no = :seqNo", nativeQuery = true)
	void updateProductStatusBySeqNo(@Param("status") Integer status, @Param("seqNo") Long seqNo);

	List<Product> findAllByStatusAndEndDateLessThanEqual(Integer status, String now);
}
