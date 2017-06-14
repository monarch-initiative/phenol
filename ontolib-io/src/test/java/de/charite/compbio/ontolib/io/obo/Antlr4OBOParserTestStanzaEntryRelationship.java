package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueRelationshipContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryRelationship extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "relationship: part_of HP:1 HP:2\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2 {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2 ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueRelationship() {
    final String text = "relationship: part_of HP:1 HP:2 {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRelationshipContext ctx = parser.keyValueRelationship();
    final StanzaEntryRelationship stanzaEntry =
        (StanzaEntryRelationship) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.RELATIONSHIP, stanzaEntry.getType());
    assertEquals("part_of", stanzaEntry.getRelationshipType());
    assertEquals("[HP:1, HP:2]", stanzaEntry.getIds().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
