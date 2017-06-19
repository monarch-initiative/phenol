package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIdMappingContext;

public class Antlr4OboParserTestStanzaEntryIdMapping extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "id-mapping: FROM: TO:\n";
    final Antlr4OboParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.Id_MAPPING, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIdMapping() {
    final String text = "id-mapping: FROM: TO:\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIdMapping stanzaEntry =
        (StanzaEntryIdMapping) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.Id_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceId());
    assertEquals("TO:", stanzaEntry.getTargetId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIdMapping() {
    final String text = "id-mapping: FROM: TO: {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIdMapping stanzaEntry =
        (StanzaEntryIdMapping) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.Id_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceId());
    assertEquals("TO:", stanzaEntry.getTargetId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIdMapping() {
    final String text = "id-mapping: FROM: TO: ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIdMapping stanzaEntry =
        (StanzaEntryIdMapping) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.Id_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceId());
    assertEquals("TO:", stanzaEntry.getTargetId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIdMapping() {
    final String text = "id-mapping: FROM: TO: {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIdMapping stanzaEntry =
        (StanzaEntryIdMapping) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.Id_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceId());
    assertEquals("TO:", stanzaEntry.getTargetId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
