package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>remark</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryRemark extends StanzaEntry {

  /** Id value of stanza entry. */
  private final String text;

  /**
   * Constructor.
   *
   * @param text The remark's text.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryRemark(String text, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.REMARK, trailingModifier, comment);
    this.text = text;
  }

  /**
   * @return The remark's text.
   */
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return "StanzaEntryRemark [id=" + text + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }

}
