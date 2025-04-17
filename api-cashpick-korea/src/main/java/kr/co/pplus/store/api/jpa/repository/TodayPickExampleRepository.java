package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.TodayPickExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodayPickExampleRepository extends JpaRepository<TodayPickExample, Long> {

	@Modifying
	@Query(value = "update today_pick_example set join_count = join_count + 1 where seq_no = :seqNo", nativeQuery = true)
	void updateJoinCount(@Param("seqNo") Long seqNo);

}
