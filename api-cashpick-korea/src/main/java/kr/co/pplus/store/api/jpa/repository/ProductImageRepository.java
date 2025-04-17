package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	int countByProductSeqNo(Long productSeqNo);

	@Transactional
	void deleteAllByProductSeqNo(Long productSeqNo);

	List<ProductImage> findAllByProductSeqNoAndDeligate(Long productSeqNo, Boolean deligate);

}
