package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name="news")
@Table(name="news")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class News {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "page_seq_no")
    private Long pageSeqNo;
    private String type; // link, product
    private String title;
    private String content;
    private String link;
    @Column(name = "product_seq_no")
    private Long productSeqNo;
    private Integer hits; // 조회수

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    private Boolean deleted;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "news_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @OrderBy("array ASC")
    private List<NewsImage> newsImageList = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageRefDetail page;
}
