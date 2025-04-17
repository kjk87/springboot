package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.Advertise;
import kr.co.pplus.store.type.model.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ArticleWrapper")
public class ArticleWrapper extends Article {
	private Advertise lastAdvertise;
}
