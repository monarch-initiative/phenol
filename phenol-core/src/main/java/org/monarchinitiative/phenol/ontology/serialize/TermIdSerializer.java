package org.monarchinitiative.phenol.ontology.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

/**
 * Serialize the {@link TermId}'s {@code value} as a {@link String}.
 */
public class TermIdSerializer extends StdSerializer<TermId> {

  public TermIdSerializer() {
    super(TermId.class);
  }

  public TermIdSerializer(StdSerializer<?> src) {
    super(src);
  }

  @Override
  public void serialize(TermId id, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(id.getValue());
  }
}
