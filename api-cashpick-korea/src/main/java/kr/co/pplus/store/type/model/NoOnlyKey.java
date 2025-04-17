package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("NoOnlyKey")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoOnlyKey extends AbstractModel {
	private static final long serialVersionUID = 5317373394329927916L;
	private Long no;
	private String objType;
	
	public NoOnlyKey() {
		
	}
	
	public NoOnlyKey(Long no) {
		this.no = no;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NoOnlyKey) {
			return no.equals(((NoOnlyKey)obj).getNo());
		}
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	
	
	
}
