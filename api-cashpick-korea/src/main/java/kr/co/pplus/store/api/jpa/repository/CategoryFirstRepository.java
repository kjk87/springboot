package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategoryFirst;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryFirstRepository extends JpaRepository<CategoryFirst, Long>{

	List<CategoryFirst> findAllByOrderByArrayAscNameAsc();

	CategoryFirst findBySeqNo(Long seqNo);

	List<CategoryFirst> findByStatusOrderByArrayAscNameAsc(String status);

}
