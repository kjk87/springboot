package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategorySecond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorySecondRepository extends JpaRepository<CategorySecond, Long>{

	CategorySecond findBySeqNo(Long seqNo);

	List<CategorySecond> findByFirstOrderByArrayAscNameAsc(Long first);

	List<CategorySecond> findByFirstAndStatusOrderByArrayAscNameAsc(Long first, String status);

}
