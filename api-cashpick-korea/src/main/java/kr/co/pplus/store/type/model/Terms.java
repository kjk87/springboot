package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ActiveStatus;
import kr.co.pplus.store.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Terms")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Terms extends NoOnlyKey {
	private String code;
	private String status;
	private String subject;
	private Boolean compulsory;
	private String name;
	private String contents;
	private String url;
	private String type ; //약관 종류 signup, seller, buy

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date agreeDate;
	
	private User regUser;
	private User modUser;
	
	public Terms() {
		
	}
	
	public Terms(Long no) {
		super(no);
	}
	
	public void setTermsNo(Long no) {
		setNo(no);
	}
}
