package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GiftishowBuy;
import kr.co.pplus.store.api.jpa.model.GiftishowCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftishowCategoryRepository extends JpaRepository<GiftishowCategory, Long>{

    List<GiftishowCategory> findAllByStatusOrderByPriorityDesc(String status);

}
