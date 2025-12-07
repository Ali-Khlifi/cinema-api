package fr.uga.gestioncinema.configurations;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.uga.gestioncinema.exceptions.ErrorDetails;

public class ErrorDetailsSerializer extends JsonSerializer<ErrorDetails> {
    @Override
    public void serialize(ErrorDetails value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("code", value.getErrorCode());
        gen.writeStringField("message", value.getErrorMessage());
        gen.writeEndObject();
    }
}
