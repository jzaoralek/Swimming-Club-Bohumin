package com.sportologic.common.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute == null ? "0" : (attribute ? "1" : "0");
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return dbData == null ? Boolean.FALSE : (dbData.equals("1") ? Boolean.TRUE : Boolean.FALSE);
    }
}
