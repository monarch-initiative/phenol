package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueDisjointFromContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryDisjointFrom extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "disjoint_from: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DISJOINT_FROM, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDisjointFrom() {
    final String text = "disjoint_from: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDisjointFromContext ctx = parser.keyValueDisjointFrom();
    final StanzaEntryDisjointFrom stanzaEntry =
        (StanzaEntryDisjointFrom) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DISJOINT_FROM, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDisjointFrom() {
    final String text = "disjoint_from: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDisjointFromContext ctx = parser.keyValueDisjointFrom();
    final StanzaEntryDisjointFrom stanzaEntry =
        (StanzaEntryDisjointFrom) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DISJOINT_FROM, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDisjointFrom() {
    final String text = "disjoint_from: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDisjointFromContext ctx = parser.keyValueDisjointFrom();
    final StanzaEntryDisjointFrom stanzaEntry =
        (StanzaEntryDisjointFrom) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DISJOINT_FROM, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDisjointFrom() {
    final String text = "disjoint_from: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDisjointFromContext ctx = parser.keyValueDisjointFrom();
    final StanzaEntryDisjointFrom stanzaEntry =
        (StanzaEntryDisjointFrom) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DISJOINT_FROM, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
