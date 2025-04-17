package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="banner")
@Table(name="banner")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Banner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private Boolean android = false;
    private Boolean ios = false;
    private String type; // home, shopping
    private String title;
    private String image;
    private Boolean display = true;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_datetime")
    private String startDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "end_datetime")
    private String endDatetime;

    @Column(name = "move_type")
    private String moveType; // none, inner, outer

    @Column(name = "inner_type")
    private String innerType; // eventA, eventB, event, notice, goods, lotto

    @Column(name = "move_target")
    private String moveTarget;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Column(name = "android_array")
    private Integer androidArray;

    @Column(name = "ios_array")
    private Integer iosArray;

    @Column(name="app_type")
    private String appType;

}
