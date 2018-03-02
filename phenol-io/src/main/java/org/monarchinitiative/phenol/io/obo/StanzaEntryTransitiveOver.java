package org.monarchinitiative.phenol.io.obo;

/**
 * Representation of a stanza entry starting with <code>transitive_over</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryTransitiveOver extends StanzaEntry {

  /** Id value of referenced relationship type. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The Id of the referenced relationship type.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryTransitiveOver(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.TRANSITIVE_OVER, trailingModifier, comment);
    this.id = id;
  }

  /**
   * @return The Id of the referenced relationship type.
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "StanzaEntryTransitiveOver [id=" + id + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
