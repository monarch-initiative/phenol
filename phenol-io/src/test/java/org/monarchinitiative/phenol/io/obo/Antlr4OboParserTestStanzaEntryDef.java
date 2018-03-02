package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueDefContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryDef extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "def: \"Term Definition\" [database-ref]\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "def: \"Term Definition\" [database-ref]\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref]\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("Term Definition", stanzaEntry.getText());
    assertEquals("DbXrefList [dbXrefs=[DbXref [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXrefList().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("Term Definition", stanzaEntry.getText());
    assertEquals("DbXrefList [dbXrefs=[DbXref [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXrefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("Term Definition", stanzaEntry.getText());
    assertEquals("DbXrefList [dbXrefs=[DbXref [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXrefList().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("Term Definition", stanzaEntry.getText());
    assertEquals("DbXrefList [dbXrefs=[DbXref [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXrefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

  @Test
  public void testParseDbXrefListModifierCommentAsKeyValueDef() {
    final String text = "def: \"Term Definition\" [database-ref] {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDefContext ctx = parser.keyValueDef();
    final StanzaEntryDef stanzaEntry = (StanzaEntryDef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DEF, stanzaEntry.getType());
    assertEquals("Term Definition", stanzaEntry.getText());
    assertEquals("DbXrefList [dbXrefs=[DbXref [name=database-ref, description=null, "
        + "trailingModifier=null]]]", stanzaEntry.getDbXrefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
