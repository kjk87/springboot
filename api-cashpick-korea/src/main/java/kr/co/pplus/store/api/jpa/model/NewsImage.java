package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name="newsImage")
@Table(name="news_image")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="news_seq_no")
    private Long newsSeqNo;
    private String image;
    private Integer array;
    private Boolean deligate; // 대표 이미지
}
