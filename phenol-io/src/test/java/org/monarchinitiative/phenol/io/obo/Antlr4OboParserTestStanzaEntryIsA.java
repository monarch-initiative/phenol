package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsAContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsA extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "is_a: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_A, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_a: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_A, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsA() {
    final String text = "is_a: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAContext ctx = parser.keyValueIsA();
    final StanzaEntryIsA stanzaEntry = (StanzaEntryIsA) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_A, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsA() {
    final String text = "is_a: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAContext ctx = parser.keyValueIsA();
    final StanzaEntryIsA stanzaEntry = (StanzaEntryIsA) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_A, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsA() {
    final String text = "is_a: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAContext ctx = parser.keyValueIsA();
    final StanzaEntryIsA stanzaEntry = (StanzaEntryIsA) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_A, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsA() {
    final String text = "is_a: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAContext ctx = parser.keyValueIsA();
    final StanzaEntryIsA stanzaEntry = (StanzaEntryIsA) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_A, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals(
        "TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }
}
