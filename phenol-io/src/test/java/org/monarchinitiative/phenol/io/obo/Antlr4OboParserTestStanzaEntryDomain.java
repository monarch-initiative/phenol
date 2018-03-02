package org.monarchinitiative.phenol.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueDomainContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryDomain extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "domain: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DOMAIN, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueDomain() {
    final String text = "domain: HP:1\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDomainContext ctx = parser.keyValueDomain();
    final StanzaEntryDomain stanzaEntry = (StanzaEntryDomain) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DOMAIN, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueDomain() {
    final String text = "domain: HP:1 {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDomainContext ctx = parser.keyValueDomain();
    final StanzaEntryDomain stanzaEntry = (StanzaEntryDomain) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DOMAIN, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueDomain() {
    final String text = "domain: HP:1 ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDomainContext ctx = parser.keyValueDomain();
    final StanzaEntryDomain stanzaEntry = (StanzaEntryDomain) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DOMAIN, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueDomain() {
    final String text = "domain: HP:1 {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueDomainContext ctx = parser.keyValueDomain();
    final StanzaEntryDomain stanzaEntry = (StanzaEntryDomain) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.DOMAIN, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getValue());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
