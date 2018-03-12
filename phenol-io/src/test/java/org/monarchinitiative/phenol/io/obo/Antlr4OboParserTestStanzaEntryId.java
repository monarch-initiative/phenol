package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIdContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;
import org.junit.Test;

public class Antlr4OboParserTestStanzaEntryId extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "id: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "id: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "id: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueId() {
    final String text = "id: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdContext ctx = parser.keyValueId();
    final StanzaEntryId stanzaEntry = (StanzaEntryId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueId() {
    final String text = "id: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdContext ctx = parser.keyValueId();
    final StanzaEntryId stanzaEntry = (StanzaEntryId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueId() {
    final String text = "id: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdContext ctx = parser.keyValueId();
    final StanzaEntryId stanzaEntry = (StanzaEntryId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueId() {
    final String text = "id: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIdContext ctx = parser.keyValueId();
    final StanzaEntryId stanzaEntry = (StanzaEntryId) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }
}
