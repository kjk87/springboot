package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ThemeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ThemeCategoryRepository extends JpaRepository<ThemeCategory, Long> {

    List<ThemeCategory> findAllByStatusOrderByArrayAsc(String status);
}