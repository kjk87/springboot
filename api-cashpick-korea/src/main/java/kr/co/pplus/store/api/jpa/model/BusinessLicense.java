package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name = "businessLicense") // This tells Hibernate to make a table out of this class
@Table(name = "business_license")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessLicense {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	Long page; // page id
	
	@Column(name="company_name")
	String companyName; // 회사명

	String ceo; // 대표자

	@Column(name="corporate_number")
	String corporateNumber; // 사업자 등록번호

	@Column(name="company_address")
	String companyAddress; // 사업장 소재지

	@Column(name="business_type")
	String businessType; // 업태

	String items; // 종목

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name="reg_datetime", updatable = false)
	String regDatetime;

	@Column(name="business_operator_type")
	String businessOperatorType; // individual, corporate

}
