package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageBalanceRepository extends JpaRepository<PageBalance, Long>{

	Page<PageBalance> findAllByPageSeqNoAndIssueDateGreaterThanEqualAndIssueDateLessThanEqual(Long pageSeqNo, String startDate, String endDate, Pageable pageable);


	@Query(value="select ifnull(sum(advertise + cashback), 0) from page_balance"
			+" where 1=1 "
			+" AND page_seq_no = :pageSeqNo "
			+" AND issue_date >= :startDate "
			+" AND issue_date <= :endDate",
			nativeQuery = true)
	Integer sumPrice(Long pageSeqNo, String startDate, String endDate) ;

}
