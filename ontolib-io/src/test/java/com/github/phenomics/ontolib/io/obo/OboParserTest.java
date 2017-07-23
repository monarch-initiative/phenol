package com.github.phenomics.ontolib.io.obo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.phenomics.ontolib.io.obo.Header;
import com.github.phenomics.ontolib.io.obo.OboFile;
import com.github.phenomics.ontolib.io.obo.OboParseResultListener;
import com.github.phenomics.ontolib.io.obo.OboParser;
import com.github.phenomics.ontolib.io.obo.Stanza;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Tests for the {@link OboParser} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OboParserTest {

  private static final List<String> MINI_OBO_LINES =
      Lists.newArrayList("format-version: 1.2", "", "[Term]", "id: HP:0000001", "name: All", "");

  private static final String MINI_OBO = Joiner.on('\n').join(MINI_OBO_LINES);

  private final OboParser parser = new OboParser(true);

  @Test
  public void testAllInOneParsing() {
    final OboFile oboFile = parser.parseString(MINI_OBO);
    assertEquals("OBOFile [header=Header [entries=[StanzaEntryFormatVersion [value=1.2, "
        + "getType()=FORMAT_VERSION, getTrailingModifier()=null, getComment()=null]], "
        + "entryByType={FORMAT_VERSION=[StanzaEntryFormatVersion [value=1.2, getType()="
        + "FORMAT_VERSION, getTrailingModifier()=null, getComment()=null]]}], stanzas="
        + "[Stanza [type=TERM, stanzaEntries=[StanzaEntryId [id=HP:0000001, getType()=ID, "
        + "getTrailingModifier()=null, getComment()=null], StanzaEntryName [name=All, "
        + "getType()=NAME, getTrailingModifier()=null, getComment()=null]], entryByType="
        + "{ID=[StanzaEntryId [id=HP:0000001, getType()=ID, getTrailingModifier()=null, "
        + "getComment()=null]], NAME=[StanzaEntryName [name=All, getType()=NAME, "
        + "getTrailingModifier()=null, getComment()=null]]}]]]", oboFile.toString());
  }

  @Test
  public void testEventBasedParsing() {
    final TestListener listener = new TestListener();
    parser.parseString(MINI_OBO, listener);
    assertEquals("Header [entries=[StanzaEntryFormatVersion [value=1.2, getType()=FORMAT_VERSION, "
        + "getTrailingModifier()=null, getComment()=null]], entryByType={FORMAT_VERSION="
        + "[StanzaEntryFormatVersion [value=1.2, getType()=FORMAT_VERSION, getTrailingModifier()="
        + "null, getComment()=null]]}]", listener.getHeader().toString());
    assertEquals(
        "[Stanza [type=TERM, stanzaEntries=[StanzaEntryId [id=HP:0000001, getType()=ID, "
            + "getTrailingModifier()=null, getComment()=null], StanzaEntryName [name=All, "
            + "getType()=NAME, getTrailingModifier()=null, getComment()=null]], entryByType="
            + "{ID=[StanzaEntryId [id=HP:0000001, getType()=ID, getTrailingModifier()=null, "
            + "getComment()=null]], NAME=[StanzaEntryName [name=All, getType()=NAME, "
            + "getTrailingModifier()=null, getComment()=null]]}]]",
        listener.getStanzas().toString());
  }

  /**
   * Helper class for collecting parse result; header and stanzas.
   */
  private final class TestListener implements OboParseResultListener {

    private Header header = null;

    private List<Stanza> stanzas = Lists.newArrayList();

    @Override
    public void parsedHeader(Header header) {
      this.header = header;
    }

    @Override
    public void parsedStanza(Stanza stanza) {
      stanzas.add(stanza);
    }

    @Override
    public void parsedFile() {
      // nop
    }

    /**
     * Query for parsed header.
     *
     * @return Resulting header
     */
    public Header getHeader() {
      return header;
    }

    /**
     * Query for parsed stanzas
     *
     * @return The resulting stanyas.
     */
    public List<Stanza> getStanzas() {
      return stanzas;
    }

  }

}
