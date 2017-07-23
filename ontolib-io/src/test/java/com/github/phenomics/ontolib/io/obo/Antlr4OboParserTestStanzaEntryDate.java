package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntryDate;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueDateContext;

public class Antlr4OboParserTestStanzaEntryDate extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "date: 2009-04-28T10:22:40Z\n";
    final Antlr4OboParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDate() {
    final String text = "date: 2009-04-28T10:22:40Z\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDateContext ctx = parser.keyValueDate();
    final StanzaEntryDate stanzaEntry = (StanzaEntryDate) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATE, stanzaEntry.getType());
    assertEquals("2009-04-28T10:22:40Z", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDate() {
    final String text = "date: 2009-04-28T10:22:40Z {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDateContext ctx = parser.keyValueDate();
    final StanzaEntryDate stanzaEntry = (StanzaEntryDate) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATE, stanzaEntry.getType());
    assertEquals("2009-04-28T10:22:40Z", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDate() {
    final String text = "date: 2009-04-28T10:22:40Z ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDateContext ctx = parser.keyValueDate();
    final StanzaEntryDate stanzaEntry = (StanzaEntryDate) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATE, stanzaEntry.getType());
    assertEquals("2009-04-28T10:22:40Z", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDate() {
    final String text = "date: 2009-04-28T10:22:40Z {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDateContext ctx = parser.keyValueDate();
    final StanzaEntryDate stanzaEntry = (StanzaEntryDate) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATE, stanzaEntry.getType());
    assertEquals("2009-04-28T10:22:40Z", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
