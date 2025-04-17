package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Embeddable
public class PageVirtualId implements Serializable {


    @NotNull
    @Column(name="page_seq_no" , updatable = false)
    Long pageSeqNo ; // '페이지 순번',

    @NotNull
    @Column(name="virtual_number", updatable = false)
    String virtualNumber ; // '페이지 순번',

}
