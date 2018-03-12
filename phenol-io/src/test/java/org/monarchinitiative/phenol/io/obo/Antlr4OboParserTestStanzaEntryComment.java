package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueCommentContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryComment extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "comment: My comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "comment: My comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "comment: My comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueComment() {
    final String text = "comment: My comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueComment() {
    final String text = "comment: My comment {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueComment() {
    final String text = "comment: My comment ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueComment() {
    final String text = "comment: My comment {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }
}
