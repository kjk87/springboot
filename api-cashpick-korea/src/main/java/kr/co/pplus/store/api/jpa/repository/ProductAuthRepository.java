package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductAuth;
import kr.co.pplus.store.api.jpa.model.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAuthRepository extends JpaRepository<ProductAuth, Long> {

	ProductAuth findByProductSeqNo(Long productSeqNo);
}
