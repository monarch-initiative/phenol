package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueXRefContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.TypedefStanzaKeyValueContext;

public class Antlr4OBOParserTestStanzaEntryXRef extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "xref: Other:ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "xref: Other:ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "xref: Other:ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueID() {
    final String text = "xref: Other:ID\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXRef stanzaEntry = (StanzaEntryXRef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:ID, description=null, trailingModifier=null]",
        stanzaEntry.getDBXRef().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueID() {
    final String text = "xref: Other:ID {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXRef stanzaEntry = (StanzaEntryXRef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:ID, description=null, trailingModifier=TrailingModifier "
        + "[keyValue=[KeyValue [key=key, value=value]]]]", stanzaEntry.getDBXRef().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueID() {
    final String text = "xref: Other:ID ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXRef stanzaEntry = (StanzaEntryXRef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:ID, description=null, trailingModifier=null]",
        stanzaEntry.getDBXRef().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueID() {
    final String text = "xref: Other:ID {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXRef stanzaEntry = (StanzaEntryXRef) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:ID, description=null, trailingModifier=TrailingModifier "
        + "[keyValue=[KeyValue [key=key, value=value]]]]", stanzaEntry.getDBXRef().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
