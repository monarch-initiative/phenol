package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueRemarkContext;

public class Antlr4OBOParserTestStanzaEntryRemark extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsHeaderKeyValue() {
    final String text = "remark: Here is some text for you \\!\n";
    final Antlr4OBOParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.REMARK, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueRemark() {
    final String text = "remark: Here is some text for you \\!\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRemarkContext ctx = parser.keyValueRemark();
    final StanzaEntryRemark stanzaEntry = (StanzaEntryRemark) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.REMARK, stanzaEntry.getType());
    assertEquals("Here is some text for you \\!", stanzaEntry.getText());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueRemark() {
    final String text = "remark: Here is some text for you \\! {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRemarkContext ctx = parser.keyValueRemark();
    final StanzaEntryRemark stanzaEntry = (StanzaEntryRemark) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.REMARK, stanzaEntry.getType());
    assertEquals("Here is some text for you \\!", stanzaEntry.getText());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueRemark() {
    final String text = "remark: Here is some text for you \\! ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRemarkContext ctx = parser.keyValueRemark();
    final StanzaEntryRemark stanzaEntry = (StanzaEntryRemark) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.REMARK, stanzaEntry.getType());
    assertEquals("Here is some text for you \\!", stanzaEntry.getText());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueRemark() {
    final String text = "remark: Here is some text for you \\! {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueRemarkContext ctx = parser.keyValueRemark();
    final StanzaEntryRemark stanzaEntry = (StanzaEntryRemark) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.REMARK, stanzaEntry.getType());
    assertEquals("Here is some text for you \\!", stanzaEntry.getText());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
