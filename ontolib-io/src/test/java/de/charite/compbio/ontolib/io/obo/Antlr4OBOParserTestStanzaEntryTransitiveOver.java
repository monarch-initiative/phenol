package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueTransitiveOverContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryTransitiveOver extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "transitive_over: other\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.TRANSITIVE_OVER, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueTransitiveOver() {
    final String text = "transitive_over: other\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueTransitiveOverContext ctx = parser.keyValueTransitiveOver();
    final StanzaEntryTransitiveOver stanzaEntry =
        (StanzaEntryTransitiveOver) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.TRANSITIVE_OVER, stanzaEntry.getType());
    assertEquals("other", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueTransitiveOver() {
    final String text = "transitive_over: other {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueTransitiveOverContext ctx = parser.keyValueTransitiveOver();
    final StanzaEntryTransitiveOver stanzaEntry =
        (StanzaEntryTransitiveOver) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.TRANSITIVE_OVER, stanzaEntry.getType());
    assertEquals("other", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueTransitiveOver() {
    final String text = "transitive_over: other ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueTransitiveOverContext ctx = parser.keyValueTransitiveOver();
    final StanzaEntryTransitiveOver stanzaEntry =
        (StanzaEntryTransitiveOver) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.TRANSITIVE_OVER, stanzaEntry.getType());
    assertEquals("other", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueTransitiveOver() {
    final String text = "transitive_over: other {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueTransitiveOverContext ctx = parser.keyValueTransitiveOver();
    final StanzaEntryTransitiveOver stanzaEntry =
        (StanzaEntryTransitiveOver) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.TRANSITIVE_OVER, stanzaEntry.getType());
    assertEquals("other", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
