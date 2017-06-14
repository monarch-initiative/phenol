package de.charite.compbio.ontolib.io.obo;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Container for OBO {@link Header} and {@link Stanza} objects.
 *
 * <p>
 * Can be used for parsing a full OBO file in one go (at the cost of collecting all contained
 * objects into memory).
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class OBOFile {

  /** The parsed OBO {@link Header}. */
  private final Header header;

  /** The parsed OBO {@link Stanza}s. */
  private final List<Stanza> stanzas;

  /**
   * Constructor.
   *
   * @param header OBO {@link Header}.
   * @param stanzas {@link List} of {@link Stanza} objects.
   */
  public OBOFile(Header header, List<Stanza> stanzas) {
    this.header = header;
    this.stanzas = Lists.newArrayList(stanzas);
  }

  /**
   * @return The parsed {@link Header}.
   */
  public Header getHeader() {
    return header;
  }

  /**
   * @return {@link List} of {@link Stanza} objects.
   */
  public List<Stanza> getStanzas() {
    return stanzas;
  }

  @Override
  public String toString() {
    return "OBOFile [header=" + header + ", stanzas=" + stanzas + "]";
  }

}
