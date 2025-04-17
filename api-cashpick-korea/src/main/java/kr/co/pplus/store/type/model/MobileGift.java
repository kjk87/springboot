package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MobileGift")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileGift extends NoOnlyKey {
	
	private static final long serialVersionUID = -8502640761944727634L;

	private String name;
	private String code;
	private String companyName;
	private String companyCode;
	private Long userPrice;
	private Long salesPrice;
	private String useArea;
	private Boolean tax;
	private String useLimit;
	private String useNote;
	private String outputType;
	private Boolean sales;
	private String oldCode;
	private Long offerPrice;
	private String useTerm;
	private Boolean includeVat;
	private String baseImage;
	private String viewImage1;
	private String viewImage2;
	private String viewImage3;
	private String detailImage;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private List<MobileGiftImage> imageList;
}
