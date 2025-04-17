package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QrCodeRepository extends JpaRepository<QrCode, Long>{

	QrCode findByPageSeqNo(Long pageSeqNo);

	@Query(value = "select max(q.seqNo) from qrCode q")
	Long findMaxSeqNo();

}
