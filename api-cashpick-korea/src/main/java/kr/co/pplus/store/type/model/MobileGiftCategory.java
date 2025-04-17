package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MobileGiftCategory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileGiftCategory extends NoOnlyKey {

	private static final long serialVersionUID = 4326573756244891242L;

	private String name;
	private String hierarchy;
	private User regUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private List<MobileGift> mobileGiftList;
}
