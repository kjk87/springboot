package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity(name="pointHistory")
@Table(name="point_history")
public class PointHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    @Column(name="point_buy_seq_no")
    private Long pointBuySeqNo;
    private String type; // charge, used
    private Float point;
    private String subject;

    @Convert(converter = JpaConverterJson.class)
    @Column(name = "history_prop")
    private Map<String, Object> historyProp;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime;

}
