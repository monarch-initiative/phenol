package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueIsCyclicContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryIsCyclic extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "is_cyclic: true\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_CYCLIC, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIsCyclic() {
    final String text = "is_cyclic: true\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIsCyclicContext ctx = parser.keyValueIsCyclic();
    final StanzaEntryIsCyclic stanzaEntry = (StanzaEntryIsCyclic) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_CYCLIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIsCyclic() {
    final String text = "is_cyclic: true {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIsCyclicContext ctx = parser.keyValueIsCyclic();
    final StanzaEntryIsCyclic stanzaEntry = (StanzaEntryIsCyclic) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_CYCLIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIsCyclic() {
    final String text = "is_cyclic: true ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIsCyclicContext ctx = parser.keyValueIsCyclic();
    final StanzaEntryIsCyclic stanzaEntry = (StanzaEntryIsCyclic) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_CYCLIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIsCyclic() {
    final String text = "is_cyclic: true {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIsCyclicContext ctx = parser.keyValueIsCyclic();
    final StanzaEntryIsCyclic stanzaEntry = (StanzaEntryIsCyclic) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.IS_CYCLIC, stanzaEntry.getType());
    assertEquals(true, stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
