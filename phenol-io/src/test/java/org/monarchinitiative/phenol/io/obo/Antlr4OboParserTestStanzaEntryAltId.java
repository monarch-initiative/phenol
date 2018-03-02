package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueAltIdContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;
import org.junit.Test;

public class Antlr4OboParserTestStanzaEntryAltId extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "alt_id: Alternative-Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "alt_id: Alternative-Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "alt_id: Alternative-Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueAltId() {
    final String text = "alt_id: Alternative-Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueAltIdContext ctx = parser.keyValueAltId();
    final StanzaEntryAltId stanzaEntry = (StanzaEntryAltId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-Id", stanzaEntry.getAltId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueAltId() {
    final String text = "alt_id: Alternative-Id {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueAltIdContext ctx = parser.keyValueAltId();
    final StanzaEntryAltId stanzaEntry = (StanzaEntryAltId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-Id", stanzaEntry.getAltId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueAltId() {
    final String text = "alt_id: Alternative-Id ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueAltIdContext ctx = parser.keyValueAltId();
    final StanzaEntryAltId stanzaEntry = (StanzaEntryAltId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-Id", stanzaEntry.getAltId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueAltId() {
    final String text = "alt_id: Alternative-Id {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueAltIdContext ctx = parser.keyValueAltId();
    final StanzaEntryAltId stanzaEntry = (StanzaEntryAltId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-Id", stanzaEntry.getAltId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
