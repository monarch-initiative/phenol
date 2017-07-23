package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>created_by</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryCreatedBy extends StanzaEntry {

  /** Creator name in stanza entry. */
  private final String creator;

  /**
   * Constructor.
   *
   * @param creator The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryCreatedBy(String creator, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.CREATED_BY, trailingModifier, comment);
    this.creator = creator;
  }

  /**
   * @return The entry's creator value.
   */
  public String getCreator() {
    return creator;
  }

  @Override
  public String toString() {
    return "StanzaEntryCreatedBy [creator=" + creator + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
