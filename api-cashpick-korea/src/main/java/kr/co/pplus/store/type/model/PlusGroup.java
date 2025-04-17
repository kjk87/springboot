package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PlusGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlusGroup extends RelationGroup {

	private static final long serialVersionUID = 8072113945989113195L;

	private User user;
}
