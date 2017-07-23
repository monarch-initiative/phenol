package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntryFormatVersion;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueFormatVersionContext;

public class Antlr4OboParserTestStanzaEntryFormatVersion extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "format-version: 1.2\n";
    final Antlr4OboParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.FORMAT_VERSION, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueFormatVersion() {
    final String text = "format-version: 1.2\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueFormatVersionContext ctx = parser.keyValueFormatVersion();
    final StanzaEntryFormatVersion stanzaEntry =
        (StanzaEntryFormatVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.FORMAT_VERSION, stanzaEntry.getType());
    assertEquals("1.2", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueFormatVersion() {
    final String text = "format-version: 1.2 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueFormatVersionContext ctx = parser.keyValueFormatVersion();
    final StanzaEntryFormatVersion stanzaEntry =
        (StanzaEntryFormatVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.FORMAT_VERSION, stanzaEntry.getType());
    assertEquals("1.2", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueFormatVersion() {
    final String text = "format-version: 1.2 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueFormatVersionContext ctx = parser.keyValueFormatVersion();
    final StanzaEntryFormatVersion stanzaEntry =
        (StanzaEntryFormatVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.FORMAT_VERSION, stanzaEntry.getType());
    assertEquals("1.2", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueFormatVersion() {
    final String text = "format-version: 1.2 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueFormatVersionContext ctx = parser.keyValueFormatVersion();
    final StanzaEntryFormatVersion stanzaEntry =
        (StanzaEntryFormatVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.FORMAT_VERSION, stanzaEntry.getType());
    assertEquals("1.2", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
