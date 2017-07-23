package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntryIsAntisymmetric;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsAntisymmetricContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsAntisymmetric extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_antisymmetric: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANTISYMMETRIC, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsAntisymmetric() {
    final String text = "is_antisymmetric: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAntisymmetricContext ctx = parser.keyValueIsAntisymmetric();
    final StanzaEntryIsAntisymmetric stanzaEntry =
        (StanzaEntryIsAntisymmetric) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANTISYMMETRIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsAntisymmetric() {
    final String text = "is_antisymmetric: true {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAntisymmetricContext ctx = parser.keyValueIsAntisymmetric();
    final StanzaEntryIsAntisymmetric stanzaEntry =
        (StanzaEntryIsAntisymmetric) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANTISYMMETRIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsAntisymmetric() {
    final String text = "is_antisymmetric: true ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAntisymmetricContext ctx = parser.keyValueIsAntisymmetric();
    final StanzaEntryIsAntisymmetric stanzaEntry =
        (StanzaEntryIsAntisymmetric) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANTISYMMETRIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsAntisymmetric() {
    final String text = "is_antisymmetric: true {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAntisymmetricContext ctx = parser.keyValueIsAntisymmetric();
    final StanzaEntryIsAntisymmetric stanzaEntry =
        (StanzaEntryIsAntisymmetric) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANTISYMMETRIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
