package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;


@Entity // This tells Hibernate to make a table out of this class
@Table(name="attachment")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attachment implements Serializable {

    public Attachment(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '첨부 순번',

    @Column(name="id")
    String id ; // uuid

    @JsonIgnore
    @Column(name="ref_count")
    Integer refCount = 1 ;

    @JsonIgnore
    @Column(name="origin_name")
    String originName = null ;

    @JsonIgnore
    @Column(name="file_path")
    String filePath = null ;

    @JsonIgnore
    @Column(name="file_name")
    String fileName = null ;

    @JsonIgnore
    @Column(name="extension")
    String extension = null ;

    @JsonIgnore
    @Column(name="file_size")
    Long fileSize = null ;

    @Column(name="url")
    String url = null ;

    @JsonIgnore
    @Convert(converter = JpaConverterJson.class)
    @Column(name="attachment_prop", columnDefinition = "TEXT")
    Map<String, Object> attachmentProp = null ;

    @JsonIgnore
    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="deleted")
    Boolean deleted = false ; //`블라인드 여부',

    @Column(name="target_type")
    String targetType = null ;

}
