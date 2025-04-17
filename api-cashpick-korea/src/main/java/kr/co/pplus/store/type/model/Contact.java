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
@Alias("Contact")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact extends AbstractModel {

	private static final long serialVersionUID = 3686839177769929749L;

	private User user;
	private String mobile;
	private User friend;
	private Boolean existsPageInContact;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
