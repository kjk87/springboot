package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.UniqueConstraint;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Attachment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attachment extends NoOnlyKey {
	private static final long serialVersionUID = 4570856792173619691L;

	private String id ;
	private String targetType;
	private String originName;
	private String filePath;
	private String fileName;
	private Long fileSize;
	private String extension;
	private String url;
	@JsonIgnore
	private Integer refCount;
	private Boolean deleted;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	
	public void setAttachmentNo(Long no) {
		setNo(no);
	}
}
