package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventDetail")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDetail extends AbstractModel implements Serializable {

	private Long seqNo;
	private Long eventSeqNo;
	private Integer type;//1:단답형, 2:장문형, 3:객관식 단일선택(radio), 4:객관식 단일선택(dropbox), 5:객관식 복수선택(checkbox)
	private String question;
	private String guide;
	private Boolean compulsory;
}
