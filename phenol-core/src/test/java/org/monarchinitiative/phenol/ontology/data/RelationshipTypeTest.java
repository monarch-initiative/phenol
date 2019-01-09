package org.monarchinitiative.phenol.ontology.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationshipTypeTest {

  @Test
  void testHasModifier() {
    RelationshipType hasMod = RelationshipType.fromString("http://purl.obolibrary.org/obo/RO_0002573");
    assertEquals(RelationshipType.HAS_MODIFIER, hasMod);
    assertNotEquals(RelationshipType.PART_OF,hasMod);
    assertFalse(hasMod.propagates());
  }

  @Test
  void testPartOf() {
    RelationshipType partOf = RelationshipType.fromString("http://purl.obolibrary.org/obo/BFO_0000050");
    assertEquals(RelationshipType.PART_OF,partOf);
    assertTrue(partOf.propagates());
  }

  @Test
  void returnsUnknownForUnknownRelationship() {
    assertEquals(RelationshipType.UNKNOWN, RelationshipType.fromString("wibble"));
  }
}
