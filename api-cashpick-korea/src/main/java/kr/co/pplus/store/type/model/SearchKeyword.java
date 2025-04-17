package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("SearchKeyword")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchKeyword extends AbstractModel {
	private static final long serialVersionUID = -4711591421290716540L;
	private String keyword;
	private Integer adPrice;
	private String status;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;
}
