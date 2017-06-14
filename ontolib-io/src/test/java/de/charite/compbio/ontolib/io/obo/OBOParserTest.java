package de.charite.compbio.ontolib.io.obo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Tests for the {@link OBOParser} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OBOParserTest {

  private static final List<String> MINI_OBO_LINES =
      Lists.newArrayList("format-version: 1.2", "", "[Term]", "id: HP:0000001", "name: All", "");

  private static final String MINI_OBO = Joiner.on('\n').join(MINI_OBO_LINES);

  private final OBOParser parser = new OBOParser(true);

  @Test
  public void testAllInOneParsing() {
    final OBOFile oboFile = parser.parseString(MINI_OBO);
    assertEquals(
        "OBOFile [header=Header [entries=[StanzaEntryFormatVersion [value=1.2, "
            + "getType()=FORMAT_VERSION, getTrailingModifier()=null, getComment()=null]], "
            + "entryByType={FORMAT_VERSION=[StanzaEntryFormatVersion [value=1.2, "
            + "getType()=FORMAT_VERSION, getTrailingModifier()=null, getComment()=null]]}], "
            + "stanzas=[Stanza [type=null, stanzaEntries=[StanzaEntryID [id=HP:0000001, "
            + "getType()=ID, getTrailingModifier()=null, getComment()=null], StanzaEntryName "
            + "[name=All, getType()=NAME, getTrailingModifier()=null, getComment()=null]], "
            + "entryByType={ID=[StanzaEntryID [id=HP:0000001, getType()=ID, "
            + "getTrailingModifier()=null, getComment()=null]], NAME=[StanzaEntryName "
            + "[name=All, getType()=NAME, getTrailingModifier()=null, getComment()=null]]}]]]",
        oboFile.toString());
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
        "[Stanza [type=null, stanzaEntries=[StanzaEntryID [id=HP:0000001, getType()=ID, "
            + "getTrailingModifier()=null, getComment()=null], StanzaEntryName [name=All, "
            + "getType()=NAME, getTrailingModifier()=null, getComment()=null]], entryByType={ID="
            + "[StanzaEntryID [id=HP:0000001, getType()=ID, getTrailingModifier()=null, "
            + "getComment()=null]], NAME=[StanzaEntryName [name=All, getType()=NAME, "
            + "getTrailingModifier()=null, getComment()=null]]}]]",
        listener.getStanzas().toString());
  }

  /**
   * Helper class for collecting parse result; header and stanzas.
   */
  private final class TestListener implements OBOParseResultListener {

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
     * @return Resulting header
     */
    public Header getHeader() {
      return header;
    }

    /**
     * @return The resulting stanyas.
     */
    public List<Stanza> getStanzas() {
      return stanzas;
    }

  }

}
