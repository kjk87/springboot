package kr.co.pplus.store.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DefaultObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = -5505360454091842898L;
	
	public DefaultObjectMapper() {
		/*super(Jackson2ObjectMapperBuilder
				.json()
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.serializationInclusion(Include.NON_NULL)
				.modules(new JavaTimeModule())
				.build());*/
		/*disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.enable(SerializationFeature.INDENT_OUTPUT)
			.setDateFormat(df)
			.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
			.setSerializationInclusion(Include.NON_NULL);*/

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.enable(SerializationFeature.INDENT_OUTPUT)
		//.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
		.setSerializationInclusion(Include.NON_NULL)
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		.setDateFormat(df);

	}

	@Override
	public DefaultObjectMapper copy() {
		this._checkInvalidCopy(DefaultObjectMapper.class);
		return new DefaultObjectMapper();
	}
}
