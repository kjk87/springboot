package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductOptionItem;
import kr.co.pplus.store.api.jpa.model.VirtualNumberManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface VirtualNumberManageRepository extends JpaRepository<VirtualNumberManage, Long>{

	VirtualNumberManage findByVirtualNumberAndStatusAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqual(String virtualNumber, String status, String date1, String date2);

	List<VirtualNumberManage> findAllByNbookAndStatusAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqual(Boolean nbook, String status, String date1, String date2);

}
