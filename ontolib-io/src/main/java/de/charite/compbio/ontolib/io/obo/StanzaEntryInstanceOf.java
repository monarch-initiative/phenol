package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>instance_of</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryInstanceOf extends StanzaEntry {

  /** Id of referenced term. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The Id of the referenced term.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryInstanceOf(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.INSTANCE_OF, trailingModifier, comment);
    this.id = id;
  }

  /**
   * @return The Id of the referenced term.
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "StanzaEntryInstanceOf [id=" + id + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
