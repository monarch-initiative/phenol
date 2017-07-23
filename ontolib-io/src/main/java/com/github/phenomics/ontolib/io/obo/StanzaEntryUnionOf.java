package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>union_of</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryUnionOf extends StanzaEntry {

  /** Id of referenced term. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryUnionOf(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.UNION_OF, trailingModifier, comment);
    this.id = id;
  }

  /**
   * @return The referenced term's Id value.
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "StanzaEntryUnionOf [id=" + id + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }

}
