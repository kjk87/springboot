package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "TBL_SUBMIT_QUEUE")
@Table(name = "TBL_SUBMIT_QUEUE")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TBL_SUBMIT_QUEUE implements Serializable{
	
	private static final long serialVersionUID = 6811822014149524419L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer CMP_MSG_ID;
	
	private String CMP_MSG_GROUP_ID;
	private String USR_ID;
	private String USED_CD;
	private String RESERVED_FG;
	private String RESERVED_DTTM;
	private String SAVED_FG;
	private String RCV_PHN_ID;
	private String SND_PHN_ID;
	private String NAT_CD;
	private String ASSIGN_CD;
	private String SND_MSG;
	private String CALLBACK_URL;
	private Integer CONTENT_CNT;
	private String CONTENT_MIME_TYPE;
	private String CONTENT_PATH;
	private String CMP_SND_DTTM;
	private String CMP_RCV_DTTM;
	private String REG_SND_DTTM;
	private String REG_RCV_DTTM;
	private String MACHINE_ID;
	private String SMS_STATUS;
	private String RSLT_VAL;
	private String MSG_TITLE;
	private String TELCO_ID;
	private String ETC_CHAR_1;
	private String ETC_CHAR_2;
	private String ETC_CHAR_3;
	private String ETC_CHAR_4;
	private Integer ETC_INT_5;
	private Integer ETC_INT_6;

	
}
