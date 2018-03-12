package org.monarchinitiative.phenol.io.obo;

/**
 * Representation of a stanza entry starting with <code>disjoint_from</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryDisjointFrom extends StanzaEntry {

  /** Id of the disjoint term. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryDisjointFrom(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.DISJOINT_FROM, trailingModifier, comment);
    this.id = id;
  }

  /** @return The referenced term's Id value. */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "StanzaEntryDisjointFrom [id="
        + id
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
