package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "virtualNumberManage")
@Table(name = "virtual_number_manage")
public class VirtualNumberManage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private String type; // page, event, pages, products, link
    private String status; // normal(정상), remove(해제)
    @Column(name = "virtual_number")
    private String virtualNumber;
    private Integer digit; // 자리수
    // pages, products는 list 형태로 테이블 만들어서 저장
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "event_code")
    private String eventCode; // type.event
    private String url; // type.link

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_datetime")
    private String startDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "end_datetime")
    private String endDatetime;

    private Boolean nbook;
    private String thumbnail;
    private String reason;
    @Column(name = "remove_reason")
    private String removeReason;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "remove_datetime")
    private String removeDatetime;

    @Column(name = "product_type")
    private String productType;//delivery, ticket

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="manage_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("seqNo ASC")
    private List<VirtualNumberGroupItem> itemList = new ArrayList<>();

}

