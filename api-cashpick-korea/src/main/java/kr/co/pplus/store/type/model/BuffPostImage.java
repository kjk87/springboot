package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BuffPostImage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPostImage extends AbstractModel {


	private Long seqNo;
	private Long buffPostSeqNo;
	private String image;
	private Integer array;
	
}
