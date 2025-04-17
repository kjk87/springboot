package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategoryMajor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CategoryMajorRepository extends JpaRepository<CategoryMajor, Long>, JpaSpecificationExecutor<CategoryMajor>{

	CategoryMajor findBySeqNo(Long seqNo);

	List<CategoryMajor> findByStatusAndTypeInOrderByArrayAsc(String status, List<String> type);

	List<CategoryMajor> findAllByStatusOrderByArrayAsc(String status);
}
