package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.StanzaEntry;
import com.github.phenomics.ontolib.io.obo.StanzaEntrySubsetdef;
import com.github.phenomics.ontolib.io.obo.StanzaEntryType;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueSubsetdefContext;

public class Antlr4OboParserTestStanzaEntrySubsetdef extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "subsetdef: GO_SLIM \"GO Slim\"\n";
    final Antlr4OboParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSETDEF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueSubsetdef() {
    final String text = "subsetdef: GO_SLIM \"GO Slim\"\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetdefContext ctx = parser.keyValueSubsetdef();
    final StanzaEntrySubsetdef stanzaEntry =
        (StanzaEntrySubsetdef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSETDEF, stanzaEntry.getType());
    assertEquals("GO_SLIM", stanzaEntry.getName());
    assertEquals("GO Slim", stanzaEntry.getDescription());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueSubsetdef() {
    final String text = "subsetdef: GO_SLIM \"GO Slim\" {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetdefContext ctx = parser.keyValueSubsetdef();
    final StanzaEntrySubsetdef stanzaEntry =
        (StanzaEntrySubsetdef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSETDEF, stanzaEntry.getType());
    assertEquals("GO_SLIM", stanzaEntry.getName());
    assertEquals("GO Slim", stanzaEntry.getDescription());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueSubsetdef() {
    final String text = "subsetdef: GO_SLIM \"GO Slim\" ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetdefContext ctx = parser.keyValueSubsetdef();
    final StanzaEntrySubsetdef stanzaEntry =
        (StanzaEntrySubsetdef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSETDEF, stanzaEntry.getType());
    assertEquals("GO_SLIM", stanzaEntry.getName());
    assertEquals("GO Slim", stanzaEntry.getDescription());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueSubsetdef() {
    final String text = "subsetdef: GO_SLIM \"GO Slim\" {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSubsetdefContext ctx = parser.keyValueSubsetdef();
    final StanzaEntrySubsetdef stanzaEntry =
        (StanzaEntrySubsetdef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSETDEF, stanzaEntry.getType());
    assertEquals("GO_SLIM", stanzaEntry.getName());
    assertEquals("GO Slim", stanzaEntry.getDescription());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
