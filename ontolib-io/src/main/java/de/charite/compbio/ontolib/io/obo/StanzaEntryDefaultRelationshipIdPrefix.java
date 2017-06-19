package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>default-relationship-id-prefix</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryDefaultRelationshipIdPrefix extends StanzaEntry {

  /** Value of stanza entry. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryDefaultRelationshipIdPrefix(String value, TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.DEFAULT_RELATIONSHIP_Id_PREFIX, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The entry's value.
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryDefaultRelationshipIdPrefix [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
