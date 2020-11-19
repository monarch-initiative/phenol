package org.monarchinitiative.phenol.annotations.hpo;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HpoTissueMapParserTest {


  @Test
  public void testMapParser() {
    Map<TermId, HpoMapping> hmapping = HpoTissueMapParser.loadEnhancerMap();
    TermId brain = TermId.of("UBERON:0000955");
    TermId abnBrainMorph = TermId.of("HP:0012443");
    String label = "brain";
    String abnLabel = "Abnormality of brain morphology";
    assertTrue(hmapping.containsKey(brain));
    HpoMapping brainMap = hmapping.get(brain);
    assertEquals(label, brainMap.getOtherOntologyLabel());
    assertEquals(abnBrainMorph, brainMap.getHpoTermId());
    assertEquals(abnLabel, brainMap.getHpoLabel());
  }

}
