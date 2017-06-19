package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsTransitiveContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsTransitive extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_transitive: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntryIsTransitive stanzaEntry =
        (StanzaEntryIsTransitive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_TRANSITIVE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsTransitive() {
    final String text = "is_transitive: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsTransitiveContext ctx = parser.keyValueIsTransitive();
    final StanzaEntryIsTransitive stanzaEntry =
        (StanzaEntryIsTransitive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_TRANSITIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsTransitive() {
    final String text = "is_transitive: true {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsTransitiveContext ctx = parser.keyValueIsTransitive();
    final StanzaEntryIsTransitive stanzaEntry =
        (StanzaEntryIsTransitive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_TRANSITIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsTransitive() {
    final String text = "is_transitive: true ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsTransitiveContext ctx = parser.keyValueIsTransitive();
    final StanzaEntryIsTransitive stanzaEntry =
        (StanzaEntryIsTransitive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_TRANSITIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsTransitive() {
    final String text = "is_transitive: true {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsTransitiveContext ctx = parser.keyValueIsTransitive();
    final StanzaEntryIsTransitive stanzaEntry =
        (StanzaEntryIsTransitive) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_TRANSITIVE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
