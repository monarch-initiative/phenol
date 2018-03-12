package org.monarchinitiative.phenol.io.obo;

/**
 * Representation of a stanza entry starting with <code>id-mapping</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryIdMapping extends StanzaEntry {

  /** The source Id of mapping. */
  private final String sourceId;

  /** The target Id of mapping. */
  private final String targetId;

  /**
   * Constructor.
   *
   * @param sourceId The source Id of mapping.
   * @param targetId The target Id of mapping.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryIdMapping(
      String sourceId, String targetId, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.ID_MAPPING, trailingModifier, comment);
    this.sourceId = sourceId;
    this.targetId = targetId;
  }

  /** @return The mapping's source Id. */
  public String getSourceId() {
    return sourceId;
  }

  /** @return The mapping's target Id. */
  public String getTargetId() {
    return targetId;
  }

  @Override
  public String toString() {
    return "StanzaEntryIdMapping [sourceId="
        + sourceId
        + ", targetId="
        + targetId
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
