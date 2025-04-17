package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("LottoWinNumber")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LottoWinNumber extends AbstractModel implements Serializable {

	private static final long serialVersionUID = -3648008871116770450L;

	private Long seqNo;
	private Long eventSeqNo;
	Integer lottoNumber;
	private Date regDatetime;

}
