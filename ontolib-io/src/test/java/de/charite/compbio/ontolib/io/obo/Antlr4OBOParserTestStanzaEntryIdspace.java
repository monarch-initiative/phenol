package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.HeaderKeyValueContext;
import de.charite.compbio.ontolib.io.obo.parser.Antlr4OBOParser.KeyValueIdspaceContext;

public class Antlr4OBOParserTestStanzaEntryIdspace extends Antlr4OBOParserTestBase {

  @Test
  public void testParseNoModifierNoCommentAsTermKeyValue() {
    final String text = "idspace: GO urn:lsid:bioontology.org:GO: \"gene ontology terms\"\n";
    final Antlr4OBOParser parser = buildParser(text);
    final HeaderKeyValueContext ctx = parser.headerKeyValue();
    final StanzaEntry stanzaEntry = (StanzaEntry) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IDSPACE, stanzaEntry.getType());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierNoCommentAsKeyValueIdspace() {
    final String text = "idspace: GO urn:lsid:bioontology.org:GO: \"gene ontology terms\"\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdspaceContext ctx = parser.keyValueIdspace();
    final StanzaEntryIdspace stanzaEntry = (StanzaEntryIdspace) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IDSPACE, stanzaEntry.getType());
    assertEquals("GO", stanzaEntry.getLocalIDSpace());
    assertEquals("urn:lsid:bioontology.org:GO:", stanzaEntry.getGlobalIDSpace());
    assertEquals("gene ontology terms", stanzaEntry.getDescription());
    assertNull(stanzaEntry.getTrailingModifier());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseModifierNoCommentAsKeyValueIdspace() {
    final String text =
        "idspace: GO urn:lsid:bioontology.org:GO: \"gene ontology terms\" {key=value}\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdspaceContext ctx = parser.keyValueIdspace();
    final StanzaEntryIdspace stanzaEntry = (StanzaEntryIdspace) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IDSPACE, stanzaEntry.getType());
    assertEquals("GO", stanzaEntry.getLocalIDSpace());
    assertEquals("urn:lsid:bioontology.org:GO:", stanzaEntry.getGlobalIDSpace());
    assertEquals("gene ontology terms", stanzaEntry.getDescription());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertNull(stanzaEntry.getComment());
  }

  @Test
  public void testParseNoModifierCommentAsKeyValueIdspace() {
    final String text =
        "idspace: GO urn:lsid:bioontology.org:GO: \"gene ontology terms\" ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdspaceContext ctx = parser.keyValueIdspace();
    final StanzaEntryIdspace stanzaEntry = (StanzaEntryIdspace) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IDSPACE, stanzaEntry.getType());
    assertEquals("GO", stanzaEntry.getLocalIDSpace());
    assertEquals("urn:lsid:bioontology.org:GO:", stanzaEntry.getGlobalIDSpace());
    assertEquals("gene ontology terms", stanzaEntry.getDescription());
    assertNull(stanzaEntry.getTrailingModifier());
    assertEquals("comment", stanzaEntry.getComment().toString());
  }

  @Test
  public void testParseModifierCommentAsKeyValueIdspace() {
    final String text =
        "idspace: GO urn:lsid:bioontology.org:GO: \"gene ontology terms\" {key=value} ! comment\n";
    final Antlr4OBOParser parser = buildParser(text);
    final KeyValueIdspaceContext ctx = parser.keyValueIdspace();
    final StanzaEntryIdspace stanzaEntry = (StanzaEntryIdspace) getOuterListener().getValue(ctx);

    assertEquals(StanzaEntryType.IDSPACE, stanzaEntry.getType());
    assertEquals("GO", stanzaEntry.getLocalIDSpace());
    assertEquals("urn:lsid:bioontology.org:GO:", stanzaEntry.getGlobalIDSpace());
    assertEquals("gene ontology terms", stanzaEntry.getDescription());
    assertEquals("TrailingModifier [keyValue=[KeyValue [key=key, value=value]]]",
        stanzaEntry.getTrailingModifier().toString());
    assertEquals("comment", stanzaEntry.getComment());
  }

}
