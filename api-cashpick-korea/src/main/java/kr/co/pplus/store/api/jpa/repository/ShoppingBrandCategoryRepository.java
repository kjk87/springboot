package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buff;
import kr.co.pplus.store.api.jpa.model.ShoppingBrandCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ShoppingBrandCategoryRepository extends JpaRepository<ShoppingBrandCategory, Long> {

    List<ShoppingBrandCategory> findAllByStatusOrderByArrayAsc(String status);

}
