package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueCommentContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryComment extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "comment: My comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "comment: My comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "comment: My comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueComment() {
    final String text = "comment: My comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueComment() {
    final String text = "comment: My comment {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueComment() {
    final String text = "comment: My comment ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueComment() {
    final String text = "comment: My comment {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCommentContext ctx = parser.keyValueComment();
    final StanzaEntryComment stanzaEntry = (StanzaEntryComment) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.COMMENT, stanzaEntry.getType());
    assertEquals("My comment", stanzaEntry.getText());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
