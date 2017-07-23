package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntryIsAnonymous;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsAnonymousContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsAnonymous extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "is_anonymous: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "is_anonymous: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_anonymous: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsAnonymous() {
    final String text = "is_anonymous: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAnonymousContext ctx = parser.keyValueIsAnonymous();
    final StanzaEntryIsAnonymous stanzaEntry =
        (StanzaEntryIsAnonymous) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsAnonymous() {
    final String text = "is_anonymous: true {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAnonymousContext ctx = parser.keyValueIsAnonymous();
    final StanzaEntryIsAnonymous stanzaEntry =
        (StanzaEntryIsAnonymous) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsAnonymous() {
    final String text = "is_anonymous: true ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAnonymousContext ctx = parser.keyValueIsAnonymous();
    final StanzaEntryIsAnonymous stanzaEntry =
        (StanzaEntryIsAnonymous) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsAnonymous() {
    final String text = "is_anonymous: true {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsAnonymousContext ctx = parser.keyValueIsAnonymous();
    final StanzaEntryIsAnonymous stanzaEntry =
        (StanzaEntryIsAnonymous) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_ANONYMOUS, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
