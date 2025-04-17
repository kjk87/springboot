package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Product;
import kr.co.pplus.store.api.jpa.model.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {

	ProductInfo findByProductSeqNo(Long productSeqNo);
}
