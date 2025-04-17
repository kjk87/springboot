package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageManagement {

    Long pageSeqNo = null ;

    Double deliveryRadius = null ;

    Float deliveryFee = null ;

    Float deliveryMinPrice = null ;

    List<PageOpentime> opentimeList = null ;

    List<PageClosed> closedList = null ;

    String originDesc = null ;
}
