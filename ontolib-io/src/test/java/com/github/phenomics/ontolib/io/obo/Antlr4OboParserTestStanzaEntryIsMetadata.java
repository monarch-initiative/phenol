package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntryIsMetadata;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsMetadataContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsMetadata extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_metadata: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_METADATA, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsMetadata() {
    final String text = "is_metadata: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsMetadataContext ctx = parser.keyValueIsMetadata();
    final StanzaEntryIsMetadata stanzaEntry =
        (StanzaEntryIsMetadata) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_METADATA, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsMetadata() {
    final String text = "is_metadata: true {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsMetadataContext ctx = parser.keyValueIsMetadata();
    final StanzaEntryIsMetadata stanzaEntry =
        (StanzaEntryIsMetadata) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_METADATA, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsMetadata() {
    final String text = "is_metadata: true ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsMetadataContext ctx = parser.keyValueIsMetadata();
    final StanzaEntryIsMetadata stanzaEntry =
        (StanzaEntryIsMetadata) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_METADATA, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsMetadata() {
    final String text = "is_metadata: true {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsMetadataContext ctx = parser.keyValueIsMetadata();
    final StanzaEntryIsMetadata stanzaEntry =
        (StanzaEntryIsMetadata) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_METADATA, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
