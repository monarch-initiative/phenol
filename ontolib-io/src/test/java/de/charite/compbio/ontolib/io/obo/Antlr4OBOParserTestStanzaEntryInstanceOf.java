package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.InstanceStanzaKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueInstanceOfContext;

public class Antlr4OBOParserTestStanzaEntryInstanceOf extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsInstanceKeyValue() {
    final String text = "instance_of: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final InstanceStanzaKeyValueContext ctx = parser.instanceStanzaKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INSTANCE_OF, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueInstanceOf() {
    final String text = "instance_of: HP:1\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInstanceOfContext ctx = parser.keyValueInstanceOf();
    final StanzaEntryInstanceOf stanzaEntry = (StanzaEntryInstanceOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INSTANCE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueInstanceOf() {
    final String text = "instance_of: HP:1 {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInstanceOfContext ctx = parser.keyValueInstanceOf();
    final StanzaEntryInstanceOf stanzaEntry = (StanzaEntryInstanceOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INSTANCE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueInstanceOf() {
    final String text = "instance_of: HP:1 ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInstanceOfContext ctx = parser.keyValueInstanceOf();
    final StanzaEntryInstanceOf stanzaEntry = (StanzaEntryInstanceOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INSTANCE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueInstanceOf() {
    final String text = "instance_of: HP:1 {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueInstanceOfContext ctx = parser.keyValueInstanceOf();
    final StanzaEntryInstanceOf stanzaEntry = (StanzaEntryInstanceOf) getListener().getValue(ctx);

    assertEquals(StanzaEntryType.INSTANCE_OF, stanzaEntry.getType());
    assertEquals("HP:1", stanzaEntry.getId());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
