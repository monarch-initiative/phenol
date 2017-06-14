package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueGenericContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryGeneric extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "some-tag: Here is some arbitrary value\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "some-tag: Here is some arbitrary value\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "some-tag: Here is some arbitrary value\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueGeneric() {
    final String text = "some-tag: Here is some arbitrary value\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueGenericContext ctx = parser.keyValueGeneric();
    final StanzaEntryGeneric stanzaEntry = (StanzaEntryGeneric) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertEquals("Here is some arbitrary value", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueGeneric() {
    final String text = "some-tag: Here is some arbitrary value {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueGenericContext ctx = parser.keyValueGeneric();
    final StanzaEntryGeneric stanzaEntry = (StanzaEntryGeneric) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertEquals("Here is some arbitrary value", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueGeneric() {
    final String text = "some-tag: Here is some arbitrary value ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueGenericContext ctx = parser.keyValueGeneric();
    final StanzaEntryGeneric stanzaEntry = (StanzaEntryGeneric) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertEquals("Here is some arbitrary value", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueGeneric() {
    final String text = "some-tag: Here is some arbitrary value {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueGenericContext ctx = parser.keyValueGeneric();
    final StanzaEntryGeneric stanzaEntry = (StanzaEntryGeneric) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.GENERIC, stanzaEntry.getType());
    assertEquals("Here is some arbitrary value", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
