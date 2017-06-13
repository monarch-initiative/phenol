package de.charite.compbio.ontolib.io.obo;

/**
 * "Top-level" key/value pair for stanza entry.
 *
 * <p>
 * The key starts at the beginning of a line up to and excluding the first colon. The value is
 * parsed from everything after the first non-space after the colon. The value parsing depends on
 * the key.
 * </p>
 *
 * <p>
 * This class is not meant to be used directly, use its sub classes instead.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class StanzaEntry {

  /** Description of this entry's type. */
  private final StanzaEntryType type;

  /** {@link TrailingModifier} of this StanzaEntry, <code>null</code> if none. */
  private final TrailingModifier trailingModifier;

  /** String with comment for this stanza entry, <code>null</code> if no trailing comment. */
  private final String comment;

  /**
   * Constructor.
   * 
   * <p>
   * Only to be used by sub classes' constructors.
   * </p>
   *
   * @param type {@link StanzaEntryType} describing this <code>StanzaEntry</code>'s type.
   * @param trailingModifier {@link TrailingModifier} if any, <code>null</code> if none.
   * @param comment <code>String</code> with trailing comment, <code>null</code> if none.
   */
  protected StanzaEntry(StanzaEntryType type, TrailingModifier trailingModifier, String comment) {
    this.type = type;
    this.trailingModifier = trailingModifier;
    this.comment = comment;
  }

  /**
   * @return Enumeration value for this stanza entry's type.
   */
  public StanzaEntryType getType() {
    return type;
  }

  /**
   * @return Trailing modifier if any, <code>null</code> if missing.
   */
  public TrailingModifier getTrailingModifier() {
    return trailingModifier;
  }

  /**
   * @return Trailing comment <code>String</code>, if any, <code>null</code> if missing.
   */
  public String getComment() {
    return comment;
  }

}
