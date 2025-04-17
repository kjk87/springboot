package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategoryMajorOnly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryMajorOnlyRepository extends JpaRepository<CategoryMajorOnly, Long>{

    List<CategoryMajorOnly> findAllByStatusOrderByArrayAsc(String status);
    CategoryMajorOnly findBySeqNo(Long seqNo);
}
