package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderMenuGroup;
import kr.co.pplus.store.api.jpa.model.OrderMenuGroupWithMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderMenuGroupWithMenuRepository extends JpaRepository<OrderMenuGroupWithMenu, Long> {

    @Query(value = "select * from order_menu_group omg "
            + " where omg.page_seq_no =:pageSeqNo "
            + " and (select count(1) from order_menu om where om.group_seq_no = omg.seq_no and deleted = false ) > 0"
            + " and omg.deleted = false"
            + " order by array asc", nativeQuery = true)
    List<OrderMenuGroupWithMenu> findAllByPageSeqNoOrderByArrayAsc(Long pageSeqNo);

}