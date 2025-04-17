package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageImage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface PageImageRepository extends JpaRepository<PageImage, Long> {

	int countByPageSeqNo(Long pageSeqNo);

	@Transactional
	void deleteAllByPageSeqNo(Long pageSeqNo);

	List<PageImage> findByPageSeqNoOrderByArrayAsc(Long pageSeqNo);

}
