package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueRangeContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryRange extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "range: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RANGE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueRange() {
    final String text = "range: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRangeContext ctx = parser.keyValueRange();
    final StanzaEntryRange stanzaEntry = (StanzaEntryRange) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RANGE, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueRange() {
    final String text = "range: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRangeContext ctx = parser.keyValueRange();
    final StanzaEntryRange stanzaEntry = (StanzaEntryRange) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RANGE, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueRange() {
    final String text = "range: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRangeContext ctx = parser.keyValueRange();
    final StanzaEntryRange stanzaEntry = (StanzaEntryRange) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RANGE, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueRange() {
    final String text = "range: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueRangeContext ctx = parser.keyValueRange();
    final StanzaEntryRange stanzaEntry = (StanzaEntryRange) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.RANGE, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
