package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Note")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Note extends NoOnlyKey {

	private static final long serialVersionUID = -7144670007767085679L;

	private String contents;
	private User author;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private Boolean readed;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date readDate;
	private User mainReceiver;
	private Integer receiverCount;
	private Long replyNo;
	private Long originNo;
	
	private List<User> receiverList;
	
	public void setNoteNo(Long no) {
		super.setNo(no);
	}
}
