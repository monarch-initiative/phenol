package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>replaced_by</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryReplacedBy extends StanzaEntry {

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
  public StanzaEntryReplacedBy(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.REPLACED_BY, trailingModifier, comment);
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
    return "StanzaEntryReplacedBy [id=" + id + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
