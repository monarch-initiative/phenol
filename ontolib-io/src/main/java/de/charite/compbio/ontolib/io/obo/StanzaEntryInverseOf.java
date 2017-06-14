package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>inverse_of</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryInverseOf extends StanzaEntry {

  /** ID of referenced term. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The ID of the referenced term.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryInverseOf(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.INVERSE_OF, trailingModifier, comment);
    this.id = id;
  }

  /**
   * @return The ID of the referenced term.
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "StanzaEntryInverseOf [id=" + id + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
