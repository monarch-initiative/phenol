package org.monarchinitiative.phenol.annotations.formats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GeneIdentifierTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void serializeToJson() throws Exception {
    GeneIdentifier gi = GeneIdentifier.of(TermId.of("NCBIGene:6834"), "SURF1");

    String actual = objectMapper.writeValueAsString(gi);

    assertThat(actual, equalTo("{\"id\":\"NCBIGene:6834\",\"symbol\":\"SURF1\"}"));
  }
}
