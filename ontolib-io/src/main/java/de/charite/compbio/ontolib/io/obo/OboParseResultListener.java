package de.charite.compbio.ontolib.io.obo;

/**
 * Listener interface for consuming parser results (fully parsed header, stanza, or file).
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface OboParseResultListener {

  /**
   * Called when the header is completely parsed.
   *
   * @param header {@link Header} parser result.
   */
  void parsedHeader(Header header);

  /**
   * Called when a stanza is completely parsed.
   *
   * @param stanza {@link Stanza} parser result.
   */
  void parsedStanza(Stanza stanza);

  /**
   * Called when the file has been completely parsed.
   */
  void parsedFile();

}
