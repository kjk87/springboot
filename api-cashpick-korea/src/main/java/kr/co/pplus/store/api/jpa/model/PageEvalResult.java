package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageEvalResult {
    private Long reviewCount ;
    private Double avgEval ;

    public PageEvalResult(Long reviewCount, Double avgEval) {
        this.reviewCount = reviewCount ;
        this.avgEval = avgEval ;
    }
}
