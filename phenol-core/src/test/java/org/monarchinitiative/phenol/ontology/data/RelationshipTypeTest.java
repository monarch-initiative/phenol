package org.monarchinitiative.phenol.ontology.data;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipTypeTest {



  @Test
  public void testHasModifier() throws PhenolException {
    RelationshipType hasMod = RelationshipType.fromString("http://purl.obolibrary.org/obo/RO_0002573");
    assertEquals(RelationshipType.HAS_MODIFIER, hasMod);
    assertNotEquals(RelationshipType.PART_OF,hasMod);
    assertFalse(hasMod.propagates());
  }

  @Test
  public void testPartOf() throws PhenolException {
    RelationshipType partOf = RelationshipType.fromString("http://purl.obolibrary.org/obo/BFO_0000050");
    assertEquals(RelationshipType.PART_OF,partOf);
    assertTrue(partOf.propagates());
  }
}
