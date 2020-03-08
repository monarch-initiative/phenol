package org.monarchinitiative.phenol.ontology.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationshipTypeTest {

  @Test
  void testHasModifier() {
    RelationshipType hasMod = RelationshipType.of("http://purl.obolibrary.org/obo/RO_0002573", "has modifier");
    assertEquals(RelationshipType.HAS_MODIFIER, hasMod);
    assertNotEquals(RelationshipType.PART_OF, hasMod);
    assertFalse(hasMod.propagates());
  }

  @Test
  void testIsA() {
    assertEquals(RelationshipType.IS_A, RelationshipType.of("is_a", "is_a"));
    assertTrue(RelationshipType.IS_A.propagates());
  }

  @Test
  void testPartOf() {
    assertEquals(RelationshipType.PART_OF, RelationshipType.of("http://purl.obolibrary.org/obo/BFO_0000050", "part of"));
    assertEquals(RelationshipType.PART_OF, RelationshipType.of("part_of", "part of"));
    assertTrue(RelationshipType.PART_OF.propagates());
  }

  @Test
  void allowsForUndefinedRelationship() {
    assertEquals(RelationshipType.of("wibble", "thing"), RelationshipType.of("wibble", "thing"));
  }
}
