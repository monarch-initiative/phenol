package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueNameContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryName extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "name: Term Name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "name: Term Name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "name: Term Name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueName() {
    final String text = "name: Term Name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueNameContext ctx = parser.keyValueName();
    final StanzaEntryName stanzaEntry = (StanzaEntryName) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertEquals("Term Name", stanzaEntry.getName());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueName() {
    final String text = "name: Term Name {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueNameContext ctx = parser.keyValueName();
    final StanzaEntryName stanzaEntry = (StanzaEntryName) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertEquals("Term Name", stanzaEntry.getName());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueName() {
    final String text = "name: Term Name ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueNameContext ctx = parser.keyValueName();
    final StanzaEntryName stanzaEntry = (StanzaEntryName) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertEquals("Term Name", stanzaEntry.getName());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueName() {
    final String text = "name: Term Name {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueNameContext ctx = parser.keyValueName();
    final StanzaEntryName stanzaEntry = (StanzaEntryName) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.NAME, stanzaEntry.getType());
    assertEquals("Term Name", stanzaEntry.getName());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
