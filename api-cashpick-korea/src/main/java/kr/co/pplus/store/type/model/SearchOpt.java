package kr.co.pplus.store.type.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("SearchOpt")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchOpt extends Duration {
	private static final long serialVersionUID = -1143424526957088609L;

	private Long no;
	private Integer pg;
	private Integer sz;
	private String search;
	private String align;
	private String orderColumn;
	private String orderAsc;
	private List<String> filter;
	
	
	public Integer getSz() {
		if (sz == null)
			return 20;
		return sz;
	}
	
	public Integer getSkipCount() {
		if (pg == null)
			return 0;
		return (pg - 1) * getSz();
	}
	
}
