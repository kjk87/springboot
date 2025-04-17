package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity(name="qrCode")
@Table(name="qr_code")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrCode {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    String code = null ;

    @Column(name="page_seq_no")
    Long pageSeqNo  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="cont_datetime")
    String conDatetime  = null ;

    @Column(name="qr_image")
    String qrImage = null ;

}
