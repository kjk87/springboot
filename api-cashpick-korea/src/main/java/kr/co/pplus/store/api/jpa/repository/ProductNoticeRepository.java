package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductAuth;
import kr.co.pplus.store.api.jpa.model.ProductNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductNoticeRepository extends JpaRepository<ProductNotice, Long> {

	List<ProductNotice> findAllByProductSeqNo(Long productSeqNo);
}
