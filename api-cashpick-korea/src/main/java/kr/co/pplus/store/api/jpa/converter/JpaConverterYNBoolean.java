package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class JpaConverterYNBoolean implements AttributeConverter<Boolean, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Boolean flag) {
        try {
            if( flag != null && flag ) {
                return "Y" ;
            } else {
                return "N";
            }


        } catch (Exception ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public Boolean convertToEntityAttribute(String value) {
        try {

            if( value != null && value.equals("Y")  ) {
                return true ;
            } else {
                return false ;
            }
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

}