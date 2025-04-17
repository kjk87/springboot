package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductReviewCountEval {

    private Integer count = null ;

    @Column(name="eval")
    private Integer eval; // '구매 상품 평가 점수:1-5',
    
}
