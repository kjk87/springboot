package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ActiveStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Country")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Country extends AbstractModel {
	
	private static final long serialVersionUID = -7706997341881192909L;
	
	private Long no;
	private String number;
	private String code;
	private String name;
	private String engName;
	private String currency;
	private Float profitTaxRate;
	private Float vat;
	private ActiveStatus status;
	private String note;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User reqUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;

	
}
