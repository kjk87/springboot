package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="islandsRegion") // This tells Hibernate to make a table out of this class
@Table(name="islands_region")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IslandsRegion {


    public IslandsRegion() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "postcode")
    String postcode = null;

    @Column(name = "address")
    String address = null;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name = "is_jeju")
    Boolean isJeju = null;

}