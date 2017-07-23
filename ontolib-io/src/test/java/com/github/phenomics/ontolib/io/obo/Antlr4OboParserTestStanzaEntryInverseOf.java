package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntryInverseOf;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueInverseOfContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryInverseOf extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "inverse_of: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntryInverseOf stanzaEntry =
        (StanzaEntryInverseOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry =
        (StanzaEntryInverseOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry =
        (StanzaEntryInverseOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry =
        (StanzaEntryInverseOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry =
        (StanzaEntryInverseOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
