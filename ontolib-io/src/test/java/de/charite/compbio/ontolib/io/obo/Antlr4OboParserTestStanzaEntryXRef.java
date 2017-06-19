package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.KeyValueXRefContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TermStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OboParser.TypedefStanzaKeyValueContext;

public class Antlr4OboParserTestStanzaEntryXRef extends Antlr4OboParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "xref: Other:Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final TermStanzaKeyValueContext ctx = parser.termStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "xref: Other:Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsTypedefKeyValue() {
    final String text = "xref: Other:Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final TypedefStanzaKeyValueContext ctx = parser.typedefStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueId() {
    final String text = "xref: Other:Id\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXref stanzaEntry = (StanzaEntryXref) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:Id, description=null, trailingModifier=null]",
        stanzaEntry.getDbXRef().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueId() {
    final String text = "xref: Other:Id {key=value}\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXref stanzaEntry = (StanzaEntryXref) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:Id, description=null, trailingModifier=TrailingModifier "
        + "[keyValue=[KeyValue [key=key, value=value]]]]", stanzaEntry.getDbXRef().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueId() {
    final String text = "xref: Other:Id ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXref stanzaEntry = (StanzaEntryXref) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:Id, description=null, trailingModifier=null]",
        stanzaEntry.getDbXRef().toString());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueId() {
    final String text = "xref: Other:Id {key=value} ! comment\n";
    final Antlr4OboParser parser = buildParser(text);
    final KeyValueXRefContext ctx = parser.keyValueXRef();
    final StanzaEntryXref stanzaEntry = (StanzaEntryXref) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.XREF, stanzaEntry.getType());
    assertEquals("DBXRef [name=Other:Id, description=null, trailingModifier=TrailingModifier "
        + "[keyValue=[KeyValue [key=key, value=value]]]]", stanzaEntry.getDbXRef().toString());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
