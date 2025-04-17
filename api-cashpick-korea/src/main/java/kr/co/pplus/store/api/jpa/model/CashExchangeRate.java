package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name="cashExchangeRate")
@Table(name="cash_exchange_rate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashExchangeRate {

    public CashExchangeRate(){

    }

    @Id
    @Column(name="seq_no")
    Long seqNo ;

    Integer point  = null ;

    Integer cash  = null ;


}
