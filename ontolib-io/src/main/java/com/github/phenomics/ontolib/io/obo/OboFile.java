package com.github.phenomics.ontolib.io.obo;

import com.google.common.collect.Lists;
import java.util.List;

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
public final class OboFile {

  /**
   * The parsed OBO {@link Header}.
   */
  private final Header header;

  /**
   * The parsed OBO {@link Stanza}s.
   */
  private final List<Stanza> stanzas;

  /**
   * Constructor.
   *
   * @param header OBO {@link Header}.
   * @param stanzas {@link List} of {@link Stanza} objects.
   */
  public OboFile(Header header, List<Stanza> stanzas) {
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
