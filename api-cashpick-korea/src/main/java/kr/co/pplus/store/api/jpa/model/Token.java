package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterUtcDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@Entity(name="token") // This tells Hibernate to make a table out of this class
@Table(name="token")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo = null ;

    @Column(name="type")
    String type = null ; // 'oauth2 client-id',

    @Column(name="client")
    String client = null ; // 'oauth2 client-id',

    @Column(name="client_secret")
    String clientSecret = null ; // 'oauth2 client-secret',

    @Column(name="token")
    String token = null ;

    @Column(name="refresh_token")
    String refreshToken = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_date")
    String expireDate = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="refresh_expire_date")
    String refreshExpireDate = null ;

    @Column(name="scope")
    String scope = "readWrite" ;

    @Column(name="token_type")
    String tokenType = "Bearer" ;

    @Column(name="api_host_url")
    String apiHostUrl = "http://localhost:8080" ;

    @Column(name="base_uri")
    String baseUri = "/store/api" ;

}
