package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Lotto;
import kr.co.pplus.store.api.jpa.model.MemberAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MemberAttendanceRepository extends JpaRepository<MemberAttendance, Long> {

    MemberAttendance findByMemberSeqNo(Long memberSeqNo);
}