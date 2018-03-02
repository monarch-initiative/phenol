package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueUnionOfContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryUnionOf extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "union_of: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.UNION_OF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueUnionOf() {
    final String text = "union_of: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueUnionOfContext ctx = parser.keyValueUnionOf();
    final StanzaEntryUnionOf stanzaEntry = (StanzaEntryUnionOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.UNION_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueUnionOf() {
    final String text = "union_of: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueUnionOfContext ctx = parser.keyValueUnionOf();
    final StanzaEntryUnionOf stanzaEntry = (StanzaEntryUnionOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.UNION_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueUnionOf() {
    final String text = "union_of: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueUnionOfContext ctx = parser.keyValueUnionOf();
    final StanzaEntryUnionOf stanzaEntry = (StanzaEntryUnionOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.UNION_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueUnionOf() {
    final String text = "union_of: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueUnionOfContext ctx = parser.keyValueUnionOf();
    final StanzaEntryUnionOf stanzaEntry = (StanzaEntryUnionOf) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.UNION_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
