package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueCreationDateContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryCreationDate extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "creation_date: 2010-08-10T02:17:28Z\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.CREATION_DATE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "creation_date: 2010-08-10T02:17:28Z\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.CREATION_DATE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueCreationDate() {
    final String text = "creation_date: 2010-08-10T02:17:28Z\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCreationDateContext ctx = parser.keyValueCreationDate();
    final StanzaEntryCreationDate stanzaEntry =
        (StanzaEntryCreationDate) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.CREATION_DATE, stanzaEntry.getType());
    assertEquals("2010-08-10T02:17:28Z", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueCreationDate() {
    final String text = "creation_date: 2010-08-10T02:17:28Z {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCreationDateContext ctx = parser.keyValueCreationDate();
    final StanzaEntryCreationDate stanzaEntry =
        (StanzaEntryCreationDate) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.CREATION_DATE, stanzaEntry.getType());
    assertEquals("2010-08-10T02:17:28Z", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueCreationDate() {
    final String text = "creation_date: 2010-08-10T02:17:28Z ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCreationDateContext ctx = parser.keyValueCreationDate();
    final StanzaEntryCreationDate stanzaEntry =
        (StanzaEntryCreationDate) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.CREATION_DATE, stanzaEntry.getType());
    assertEquals("2010-08-10T02:17:28Z", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueCreationDate() {
    final String text = "creation_date: 2010-08-10T02:17:28Z {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueCreationDateContext ctx = parser.keyValueCreationDate();
    final StanzaEntryCreationDate stanzaEntry =
        (StanzaEntryCreationDate) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.CREATION_DATE, stanzaEntry.getType());
    assertEquals("2010-08-10T02:17:28Z", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
