package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageVirtualNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageVirtualNumberRepository extends JpaRepository<PageVirtualNumber, Long> {

    @Query(value = "select pvn.*, "
            +" vn.type, vn.reserved, vn.open_bounds, vn.action_source, vn.actor_login_id, vn.action_datetime, vn.reserved_datetime, vn.note, vn.reserved_title, vn.reserved_reason, vn.reserved_description, vn.number_prop, vn.deleted  "
            +" from page_virtual_number pvn , virtual_number vn "
            +" where pvn.page_seq_no = :pageSeqNo and vn.virtual_number = pvn.virtual_number ", nativeQuery = true)
    List<PageVirtualNumber> findAllByPageSeqNo(@Param("pageSeqNo") Long pageSeqNo) ;
}