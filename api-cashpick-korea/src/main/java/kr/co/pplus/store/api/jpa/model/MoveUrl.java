package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashMap;


@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoveUrl {

    String url ;

    public MoveUrl(String url){
        this.url = url ;
    }

}
