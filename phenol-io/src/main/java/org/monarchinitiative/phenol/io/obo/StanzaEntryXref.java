package org.monarchinitiative.phenol.io.obo;

import org.monarchinitiative.phenol.ontology.data.Dbxref;

/**
 * Representation of a stanza entry starting with <code>xref</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryXref extends StanzaEntry {

  /** dbXref value of stanza entry. */
  private final Dbxref dbXref;

  /**
   * Constructor.
   *
   * @param dbXref The value of the stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryXref(Dbxref dbXref, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.XREF, trailingModifier, comment);
    this.dbXref = dbXref;
  }

  /** @return The entry's Id value. */
  public Dbxref getDbXref() {
    return dbXref;
  }

  @Override
  public String toString() {
    return "StanzaEntryXref [dbXref="
        + dbXref
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
