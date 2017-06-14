package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueIdMappingContext;

public class Antlr4OBOParserTestStanzaEntryIDMapping extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "id-mapping: FROM: TO:\n";
    final Antlr4OBOParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID_MAPPING, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIDMapping() {
    final String text = "id-mapping: FROM: TO:\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIDMapping stanzaEntry = (StanzaEntryIDMapping) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceID());
    assertEquals("TO:", stanzaEntry.getTargetID());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIDMapping() {
    final String text = "id-mapping: FROM: TO: {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIDMapping stanzaEntry = (StanzaEntryIDMapping) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceID());
    assertEquals("TO:", stanzaEntry.getTargetID());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIDMapping() {
    final String text = "id-mapping: FROM: TO: ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIDMapping stanzaEntry = (StanzaEntryIDMapping) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceID());
    assertEquals("TO:", stanzaEntry.getTargetID());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIDMapping() {
    final String text = "id-mapping: FROM: TO: {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdMappingContext ctx = parser.keyValueIdMapping();
    final StanzaEntryIDMapping stanzaEntry = (StanzaEntryIDMapping) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID_MAPPING, stanzaEntry.getType());
    assertEquals("FROM:", stanzaEntry.getSourceID());
    assertEquals("TO:", stanzaEntry.getTargetID());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
