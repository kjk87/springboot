package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategoryMinor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryMinorRepository extends JpaRepository<CategoryMinor, Long>{

	CategoryMinor findBySeqNo(Long seqNo);

	List<CategoryMinor> findByMajorAndStatusOrderByArrayAsc(Long major, String status);

}
