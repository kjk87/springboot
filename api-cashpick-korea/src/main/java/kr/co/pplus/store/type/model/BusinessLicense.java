package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BusinessLicense")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessLicense extends AbstractModel{

	Long id;
	
	Long page; // page id

	String companyName; // 회사명

	String ceo; // 대표자

	String corporateNumber; // 사업자 등록번호

	String companyAddress; // 사업장 소재지

	String businessType; // 업태

	String items; // 종목

	String regDatetime;

	String businessOperatorType; // individual, corporate

}
