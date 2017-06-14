package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueIDContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryID extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "id: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "id: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "id: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueID() {
    final String text = "id: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIDContext ctx = parser.keyValueID();
    final StanzaEntryID stanzaEntry = (StanzaEntryID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueID() {
    final String text = "id: HP:1 {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIDContext ctx = parser.keyValueID();
    final StanzaEntryID stanzaEntry = (StanzaEntryID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueID() {
    final String text = "id: HP:1 ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIDContext ctx = parser.keyValueID();
    final StanzaEntryID stanzaEntry = (StanzaEntryID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueID() {
    final String text = "id: HP:1 {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIDContext ctx = parser.keyValueID();
    final StanzaEntryID stanzaEntry = (StanzaEntryID) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.ID, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
