package kr.co.pplus.store.type.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("StatisticsCount")
public class StatisticsCount extends AbstractModel {

	private static final long serialVersionUID = 5466298101061300527L;

	private String label;
	private Integer count;
}
