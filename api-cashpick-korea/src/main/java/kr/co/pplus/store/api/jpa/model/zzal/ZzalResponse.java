package kr.co.pplus.store.api.jpa.model.zzal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZzalResponse {

    String applicationId ;
    String code ;
    String message ;
    String data ;

}
