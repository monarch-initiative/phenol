package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueSavedByContext;

public class Antlr4OboParserTestStanzaEntrySavedBy extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "saved-by: user1, user2\n";
    final Antlr4OboParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SAVED_BY, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueSavedBy() {
    final String text = "saved-by: user1, user2\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSavedByContext ctx = parser.keyValueSavedBy();
    final StanzaEntrySavedBy stanzaEntry = (StanzaEntrySavedBy) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SAVED_BY, stanzaEntry.getType());
    assertEquals("user1, user2", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueSavedBy() {
    final String text = "saved-by: user1, user2 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSavedByContext ctx = parser.keyValueSavedBy();
    final StanzaEntrySavedBy stanzaEntry = (StanzaEntrySavedBy) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SAVED_BY, stanzaEntry.getType());
    assertEquals("user1, user2", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueSavedBy() {
    final String text = "saved-by: user1, user2 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSavedByContext ctx = parser.keyValueSavedBy();
    final StanzaEntrySavedBy stanzaEntry = (StanzaEntrySavedBy) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SAVED_BY, stanzaEntry.getType());
    assertEquals("user1, user2", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueSavedBy() {
    final String text = "saved-by: user1, user2 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSavedByContext ctx = parser.keyValueSavedBy();
    final StanzaEntrySavedBy stanzaEntry = (StanzaEntrySavedBy) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SAVED_BY, stanzaEntry.getType());
    assertEquals("user1, user2", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
