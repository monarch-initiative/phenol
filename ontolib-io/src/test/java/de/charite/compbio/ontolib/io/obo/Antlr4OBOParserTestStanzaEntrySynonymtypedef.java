package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueSynonymtypedefContext;
import de.charite.compbio.ontolib.ontology.data.TermSynonymScope;

public class Antlr4OBOParserTestStanzaEntrySynonymtypedef extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "synonymtypedef: UK_SPELLING \"British spelling\" EXACT\n";
    final Antlr4OBOParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYMTYPEDEF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueSynonymtypedef() {
    final String text = "synonymtypedef: UK_SPELLING \"British spelling\" EXACT\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSynonymtypedefContext ctx = parser.keyValueSynonymtypedef();
    final StanzaEntrySynonymtypedef stanzaEntry =
        (StanzaEntrySynonymtypedef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYMTYPEDEF, stanzaEntry.getType());
    assertEquals("UK_SPELLING", stanzaEntry.getTermSynonymScope());
    assertEquals("\"British spelling\"", stanzaEntry.getDescription());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getSynonymScopeIdentifier());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueSynonymtypedef() {
    final String text = "synonymtypedef: UK_SPELLING \"British spelling\" EXACT {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSynonymtypedefContext ctx = parser.keyValueSynonymtypedef();
    final StanzaEntrySynonymtypedef stanzaEntry =
        (StanzaEntrySynonymtypedef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYMTYPEDEF, stanzaEntry.getType());
    assertEquals("UK_SPELLING", stanzaEntry.getTermSynonymScope());
    assertEquals("\"British spelling\"", stanzaEntry.getDescription());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getSynonymScopeIdentifier());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueSynonymtypedef() {
    final String text = "synonymtypedef: UK_SPELLING \"British spelling\" EXACT ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSynonymtypedefContext ctx = parser.keyValueSynonymtypedef();
    final StanzaEntrySynonymtypedef stanzaEntry =
        (StanzaEntrySynonymtypedef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYMTYPEDEF, stanzaEntry.getType());
    assertEquals("UK_SPELLING", stanzaEntry.getTermSynonymScope());
    assertEquals("\"British spelling\"", stanzaEntry.getDescription());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getSynonymScopeIdentifier());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueSynonymtypedef() {
    final String text =
        "synonymtypedef: UK_SPELLING \"British spelling\" EXACT {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSynonymtypedefContext ctx = parser.keyValueSynonymtypedef();
    final StanzaEntrySynonymtypedef stanzaEntry =
        (StanzaEntrySynonymtypedef) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.SYNONYMTYPEDEF, stanzaEntry.getType());
    assertEquals("UK_SPELLING", stanzaEntry.getTermSynonymScope());
    assertEquals("\"British spelling\"", stanzaEntry.getDescription());
    assertEquals(TermSynonymScope.EXACT, stanzaEntry.getSynonymScopeIdentifier());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
