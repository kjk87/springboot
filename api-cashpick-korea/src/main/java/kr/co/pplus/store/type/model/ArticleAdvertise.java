package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ArticleAdvertise")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleAdvertise extends Advertise {

	private static final long serialVersionUID = -1654700906271531713L;

	private Article article;
}
