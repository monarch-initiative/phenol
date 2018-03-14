package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueRelationshipContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryRelationship extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "relationship: part_of HP:1 HP:2\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }
}
