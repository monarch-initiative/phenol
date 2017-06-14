package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueAltIDContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryAltID extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "alt_id: Alternative-ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "alt_id: Alternative-ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "alt_id: Alternative-ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueAltID() {
    final String text = "alt_id: Alternative-ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueAltIDContext ctx = parser.keyValueAltID();
    final StanzaEntryAltID stanzaEntry = (StanzaEntryAltID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-ID", stanzaEntry.getAltID());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueAltID() {
    final String text = "alt_id: Alternative-ID {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueAltIDContext ctx = parser.keyValueAltID();
    final StanzaEntryAltID stanzaEntry = (StanzaEntryAltID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-ID", stanzaEntry.getAltID());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueAltID() {
    final String text = "alt_id: Alternative-ID ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueAltIDContext ctx = parser.keyValueAltID();
    final StanzaEntryAltID stanzaEntry = (StanzaEntryAltID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-ID", stanzaEntry.getAltID());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueAltID() {
    final String text = "alt_id: Alternative-ID {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueAltIDContext ctx = parser.keyValueAltID();
    final StanzaEntryAltID stanzaEntry = (StanzaEntryAltID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ALT_ID, stanzaEntry.getType());
    assertEquals("Alternative-ID", stanzaEntry.getAltID());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
