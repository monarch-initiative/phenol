package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>comment</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryComment extends StanzaEntry {

  /** Comment value of stanza entry. */
  private final String text;

  /**
   * Constructor.
   *
   * @param text The value of the text stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryComment(String text, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.COMMENT, trailingModifier, comment);
    this.text = text;
  }

  /**
   * @return The entry's text value.
   */
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return "StanzaEntryComment [text=" + text + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
