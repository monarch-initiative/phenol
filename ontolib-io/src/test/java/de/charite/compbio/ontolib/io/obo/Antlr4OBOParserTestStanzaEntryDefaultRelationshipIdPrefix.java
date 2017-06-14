package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueDefaultRelationshipIdPrefixContext;

public class Antlr4OBOParserTestStanzaEntryDefaultRelationshipIdPrefix
    extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "default-relationship-id-prefix: prefix:\n";
    final Antlr4OBOParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEFAULT_RELATIONSHIP_ID_PREFIX, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDefaultRelationshipIdPrefix() {
    final String text = "default-relationship-id-prefix: prefix:\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefaultRelationshipIdPrefixContext ctx =
        parser.keyValueDefaultRelationshipIdPrefix();
    final StanzaEntryDefaultRelationshipIdPrefix stanzaEntry =
        (StanzaEntryDefaultRelationshipIdPrefix) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEFAULT_RELATIONSHIP_ID_PREFIX, stanzaEntry.getType());
    assertEquals("prefix:", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDefaultRelationshipIdPrefix() {
    final String text = "default-relationship-id-prefix: prefix: {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefaultRelationshipIdPrefixContext ctx =
        parser.keyValueDefaultRelationshipIdPrefix();
    final StanzaEntryDefaultRelationshipIdPrefix stanzaEntry =
        (StanzaEntryDefaultRelationshipIdPrefix) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEFAULT_RELATIONSHIP_ID_PREFIX, stanzaEntry.getType());
    assertEquals("prefix:", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDefaultRelationshipIdPrefix() {
    final String text = "default-relationship-id-prefix: prefix: ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefaultRelationshipIdPrefixContext ctx =
        parser.keyValueDefaultRelationshipIdPrefix();
    final StanzaEntryDefaultRelationshipIdPrefix stanzaEntry =
        (StanzaEntryDefaultRelationshipIdPrefix) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEFAULT_RELATIONSHIP_ID_PREFIX, stanzaEntry.getType());
    assertEquals("prefix:", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDefaultRelationshipIdPrefix() {
    final String text = "default-relationship-id-prefix: prefix: {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefaultRelationshipIdPrefixContext ctx =
        parser.keyValueDefaultRelationshipIdPrefix();
    final StanzaEntryDefaultRelationshipIdPrefix stanzaEntry =
        (StanzaEntryDefaultRelationshipIdPrefix) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEFAULT_RELATIONSHIP_ID_PREFIX, stanzaEntry.getType());
    assertEquals("prefix:", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
