package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductPrice;
import kr.co.pplus.store.api.jpa.model.ProductPriceRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductPriceRefRepository extends JpaRepository<ProductPriceRef, Long>, JpaSpecificationExecutor<ProductPriceRef>{

	ProductPriceRef findFirstByPageSeqNoAndStatusAndIsTicketAndPickOrderBySeqNo(Long pageSeqNo, Integer status, Boolean isTicket, Boolean pick);

}
