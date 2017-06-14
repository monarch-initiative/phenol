package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueDefContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryDef extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "def: \"Term Definition\" [database-ref]\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "def: \"Term Definition\" [database-ref]\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref]\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("\"Term Definition\"", stanzaEntry.getText());
    assertEquals("DBXRefList [dbXRefs=[DBXRef [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXRefList().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("\"Term Definition\"", stanzaEntry.getText());
    assertEquals("DBXRefList [dbXRefs=[DBXRef [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXRefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("\"Term Definition\"", stanzaEntry.getText());
    assertEquals("DBXRefList [dbXRefs=[DBXRef [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXRefList().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("\"Term Definition\"", stanzaEntry.getText());
    assertEquals("DBXRefList [dbXRefs=[DBXRef [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXRefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

  @Test
  public void testParseDBXRefListModifierCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("\"Term Definition\"", stanzaEntry.getText());
    assertEquals("DBXRefList [dbXRefs=[DBXRef [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXRefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
