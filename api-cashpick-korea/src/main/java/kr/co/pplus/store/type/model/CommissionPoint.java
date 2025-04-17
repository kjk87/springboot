package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("CommissionPoint")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommissionPoint extends AbstractModel {

    public CommissionPoint() {

    }

    private Long id;
    private Float commission;
    private Float point;
    private Float card;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    private Boolean woodongyi;
}
