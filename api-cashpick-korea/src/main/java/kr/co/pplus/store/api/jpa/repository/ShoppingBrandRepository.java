package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ShoppingBrand;
import kr.co.pplus.store.api.jpa.model.ShoppingBrandCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ShoppingBrandRepository extends JpaRepository<ShoppingBrand, Long> {

    ShoppingBrand findBySeqNo(Long seqNo);

    Page<ShoppingBrand> findAllByStatusAndShoppingBrandCategorySeqNoOrderByArrayAsc(String status, Long shoppingBrandCategorySeqNo, Pageable pageable);

}
