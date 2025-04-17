package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CategoryFavorite;
import kr.co.pplus.store.api.jpa.model.CategoryMajorOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(transactionManager = "jpaTransactionManager")
public interface CategoryFavoriteRepository extends JpaRepository<CategoryFavorite, Long>{

    List<CategoryFavorite> findAllByMemberSeqNo(Long memberSeqNo);

    @Modifying
    @Query(value="delete from category_favorite where member_seq_no = :memberSeqNo and category_minor_seq_no = :categoryMinorSeqNo", nativeQuery=true)
    void deleteByMemberSeqNoAndCategoryMinorSeqNo(Long memberSeqNo, Long categoryMinorSeqNo);
}
