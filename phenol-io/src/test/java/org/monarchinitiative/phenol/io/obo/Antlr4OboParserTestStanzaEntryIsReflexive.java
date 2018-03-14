package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsReflexiveContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsReflexive extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_reflexive: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_REFLEXIVE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsReflexive() {
    final String text = "is_reflexive: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsReflexiveContext ctx = parser.keyValueIsReflexive();
    final StanzaEntryIsReflexive stanzaEntry =
        (StanzaEntryIsReflexive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_REFLEXIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsReflexive() {
    final String text = "is_reflexive: true {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsReflexiveContext ctx = parser.keyValueIsReflexive();
    final StanzaEntryIsReflexive stanzaEntry =
        (StanzaEntryIsReflexive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_REFLEXIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsReflexive() {
    final String text = "is_reflexive: true ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsReflexiveContext ctx = parser.keyValueIsReflexive();
    final StanzaEntryIsReflexive stanzaEntry =
        (StanzaEntryIsReflexive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_REFLEXIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsReflexive() {
    final String text = "is_reflexive: true {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsReflexiveContext ctx = parser.keyValueIsReflexive();
    final StanzaEntryIsReflexive stanzaEntry =
        (StanzaEntryIsReflexive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_REFLEXIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }
}
