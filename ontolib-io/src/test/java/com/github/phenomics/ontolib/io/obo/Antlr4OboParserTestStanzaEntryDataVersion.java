package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntryDataVersion;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueDataVersionContext;

public class Antlr4OboParserTestStanzaEntryDataVersion extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "data-version: releases/2017-06-10\n";
    final Antlr4OboParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATA_VERSION, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDataVersion() {
    final String text = "data-version: releases/2017-06-10\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDataVersionContext ctx = parser.keyValueDataVersion();
    final StanzaEntryDataVersion stanzaEntry =
        (StanzaEntryDataVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATA_VERSION, stanzaEntry.getType());
    assertEquals("releases/2017-06-10", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDataVersion() {
    final String text = "data-version: releases/2017-06-10 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDataVersionContext ctx = parser.keyValueDataVersion();
    final StanzaEntryDataVersion stanzaEntry =
        (StanzaEntryDataVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATA_VERSION, stanzaEntry.getType());
    assertEquals("releases/2017-06-10", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDataVersion() {
    final String text = "data-version: releases/2017-06-10 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDataVersionContext ctx = parser.keyValueDataVersion();
    final StanzaEntryDataVersion stanzaEntry =
        (StanzaEntryDataVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATA_VERSION, stanzaEntry.getType());
    assertEquals("releases/2017-06-10", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDataVersion() {
    final String text = "data-version: releases/2017-06-10 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDataVersionContext ctx = parser.keyValueDataVersion();
    final StanzaEntryDataVersion stanzaEntry =
        (StanzaEntryDataVersion) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DATA_VERSION, stanzaEntry.getType());
    assertEquals("releases/2017-06-10", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
