package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BusinessLicense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessLicenseRepository extends JpaRepository<BusinessLicense, Long>{

	Optional<BusinessLicense> findByPage(Long page);

}
