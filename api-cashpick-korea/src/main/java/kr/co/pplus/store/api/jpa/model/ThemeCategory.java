package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="themeCategory") // This tells Hibernate to make a table out of this class
@Table(name="theme_category")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThemeCategory {


    public ThemeCategory() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo;
    String name;
    String status;
    Integer array;
    String icon;
    String banner;
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;


}