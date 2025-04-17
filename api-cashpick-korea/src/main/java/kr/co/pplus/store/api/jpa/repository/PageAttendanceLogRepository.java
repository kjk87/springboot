package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageAttendanceLogRepository extends JpaRepository<PageAttendanceLog, Long> {

    List<PageAttendanceLog> findAllByPageAttendanceSeqNoOrderBySeqNoAsc(Long pageAttendanceSeqNo);


}
