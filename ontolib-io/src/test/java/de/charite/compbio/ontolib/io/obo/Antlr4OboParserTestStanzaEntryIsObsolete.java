package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueIsObsoleteContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryIsObsolete extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "is_obsolete: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "is_obsolete: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_obsolete: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsObsolete() {
    final String text = "is_obsolete: true\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsObsoleteContext ctx = parser.keyValueIsObsolete();
    final StanzaEntryIsObsolete stanzaEntry =
        (StanzaEntryIsObsolete) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsObsolete() {
    final String text = "is_obsolete: true {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsObsoleteContext ctx = parser.keyValueIsObsolete();
    final StanzaEntryIsObsolete stanzaEntry =
        (StanzaEntryIsObsolete) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsObsolete() {
    final String text = "is_obsolete: true ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsObsoleteContext ctx = parser.keyValueIsObsolete();
    final StanzaEntryIsObsolete stanzaEntry =
        (StanzaEntryIsObsolete) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsObsolete() {
    final String text = "is_obsolete: true {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueIsObsoleteContext ctx = parser.keyValueIsObsolete();
    final StanzaEntryIsObsolete stanzaEntry =
        (StanzaEntryIsObsolete) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_OBSOLETE, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
