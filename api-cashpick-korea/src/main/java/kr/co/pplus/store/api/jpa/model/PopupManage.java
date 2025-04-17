package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="popupManage")
@Table(name="popup_manage")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PopupManage implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;

    private Boolean android;

    private Boolean ios;

    private String title;

    private String image;

    private Boolean display;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="start_datetime")
    private String startDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="end_datetime")
    private String endDatetime;

    @Column(name="move_type")
    private String moveType;

    @Column(name="inner_type")
    private String innerType;


    @Column(name="move_target")
    private String moveTarget;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Column(name="app_type")
    private String appType;

}
