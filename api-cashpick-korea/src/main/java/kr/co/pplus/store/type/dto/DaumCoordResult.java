package kr.co.pplus.store.type.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@JsonIgnoreType 
@Data
@Alias("DaumCoordResult")
public class DaumCoordResult {
	private Map meta;
	private List<Map> documents;
}
