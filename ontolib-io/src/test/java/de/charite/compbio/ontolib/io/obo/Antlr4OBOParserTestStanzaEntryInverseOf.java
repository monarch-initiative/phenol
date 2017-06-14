package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueInverseOfContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryInverseOf extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "inverse_of: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntryInverseOf stanzaEntry = (StanzaEntryInverseOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry = (StanzaEntryInverseOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1 {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry = (StanzaEntryInverseOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1 ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry = (StanzaEntryInverseOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueInverseOf() {
    final String text = "inverse_of: HP:1 {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInverseOfContext ctx = parser.keyValueInverseOf();
    final StanzaEntryInverseOf stanzaEntry = (StanzaEntryInverseOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INVERSE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
