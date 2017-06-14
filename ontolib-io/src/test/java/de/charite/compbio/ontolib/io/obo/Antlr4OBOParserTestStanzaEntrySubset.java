package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueSubsetContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntrySubset extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "subset: subset-name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "subset: subset-name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueSubset() {
    final String text = "subset: subset-name\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueSubset() {
    final String text = "subset: subset-name {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueSubset() {
    final String text = "subset: subset-name ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueSubset() {
    final String text = "subset: subset-name {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueSubsetContext ctx = parser.keyValueSubset();
    final StanzaEntrySubset stanzaEntry = (StanzaEntrySubset) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.SUBSET, stanzaEntry.getType());
    assertEquals("subset-name", stanzaEntry.getName());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
