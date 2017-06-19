package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueSynonymContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;
import de.charite.compbio.ontolib.ontology.data.TermSynonymScope;

public class Antlr4OboParserTestStanzaEntrySynonym extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "synonym: \"Synonym Name\" EXACT\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "synonym: \"Synonym Name\" EXACT\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueSynonym() {
    final String text = "synonym: \"Synonym Name\" EXACT\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSynonymContext ctx = parser.keyValueSynonym();
    final StanzaEntrySynonym stanzaEntry = (StanzaEntrySynonym) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertEquals("Synonym Name", stanzaEntry.getText());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getTermSynonymScope());
    assertNull(stanzaEntry.getSynonymTypeName());
    assertNull(stanzaEntry.getDbXrefList());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueSynonym() {
    final String text = "synonym: \"Synonym Name\" EXACT {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSynonymContext ctx = parser.keyValueSynonym();
    final StanzaEntrySynonym stanzaEntry = (StanzaEntrySynonym) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertEquals("Synonym Name", stanzaEntry.getText());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getTermSynonymScope());
    assertNull(stanzaEntry.getSynonymTypeName());
    assertNull(stanzaEntry.getDbXrefList());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueSynonym() {
    final String text = "synonym: \"Synonym Name\" EXACT ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSynonymContext ctx = parser.keyValueSynonym();
    final StanzaEntrySynonym stanzaEntry = (StanzaEntrySynonym) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertEquals("Synonym Name", stanzaEntry.getText());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getTermSynonymScope());
    assertNull(stanzaEntry.getSynonymTypeName());
    assertNull(stanzaEntry.getDbXrefList());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueSynonym() {
    final String text = "synonym: \"Synonym Name\" EXACT {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSynonymContext ctx = parser.keyValueSynonym();
    final StanzaEntrySynonym stanzaEntry = (StanzaEntrySynonym) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertEquals("Synonym Name", stanzaEntry.getText());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getTermSynonymScope());
    assertNull(stanzaEntry.getSynonymTypeName());
    assertNull(stanzaEntry.getDbXrefList());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

  @Test
  public void testParseFullFeaturedSynonym() {
    final String text =
        "synonym: \"Synonym Name\" EXACT MARKETING [db-xref] {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueSynonymContext ctx = parser.keyValueSynonym();
    final StanzaEntrySynonym stanzaEntry = (StanzaEntrySynonym) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYM, stanzaEntry.getType());
    assertEquals("Synonym Name", stanzaEntry.getText());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getTermSynonymScope());
    assertEquals("MARKETING", stanzaEntry.getSynonymTypeName());
    assertEquals(
        "DbXrefList [dbXrefs=[DbXref [name=db-xref, description=null, trailingModifier=null]]]",
        stanzaEntry.getDbXrefList().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
