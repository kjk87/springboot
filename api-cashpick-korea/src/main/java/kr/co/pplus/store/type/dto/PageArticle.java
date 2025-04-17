package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Article;
import kr.co.pplus.store.type.model.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageArticle")
public class PageArticle extends AbstractModel {
	private Page page;
	private Article post;
}
