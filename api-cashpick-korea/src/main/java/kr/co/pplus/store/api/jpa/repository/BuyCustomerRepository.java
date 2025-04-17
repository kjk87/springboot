package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyCustomerRepository extends JpaRepository<BuyCustomer, Long>{

	BuyCustomer findByMemberSeqNoAndPageSeqNo(Long memberSeqNo, Long pageSeqNo);

}
