package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategoryThird;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryThirdRepository extends JpaRepository<CategoryThird, Long>{

	CategoryThird findBySeqNo(Long seqNo);

	List<CategoryThird> findBySecondOrderByArrayAscNameAsc(Long second);

	List<CategoryThird> findBySecondAndStatusOrderByArrayAscNameAsc(Long second, String active);

}
