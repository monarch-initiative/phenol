package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntrySubset;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueSubsetContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntrySubset extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "subset: subset-name\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "subset: subset-name\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueSubset() {
    final String text = "subset: subset-name\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueSubset() {
    final String text = "subset: subset-name {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueSubset() {
    final String text = "subset: subset-name ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueSubset() {
    final String text = "subset: subset-name {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
