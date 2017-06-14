package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>id-mapping</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryIDMapping extends StanzaEntry {

  /** The source ID of mapping. */
  private final String sourceID;

  /** The target ID of mapping. */
  private final String targetID;

  /**
   * Constructor.
   *
   * @param sourceID The source ID of mapping.
   * @param targetID The target ID of mapping.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryIDMapping(String sourceID, String targetID, TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.ID_MAPPING, trailingModifier, comment);
    this.sourceID = sourceID;
    this.targetID = targetID;
  }

  /**
   * @return The mapping's source ID.
   */
  public String getSourceID() {
    return sourceID;
  }

  /**
   * @return The mapping's target ID.
   */
  public String getTargetID() {
    return targetID;
  }

  @Override
  public String toString() {
    return "StanzaEntryIDMapping [sourceID=" + sourceID + ", targetID=" + targetID + ", getType()="
        + getType() + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()="
        + getComment() + "]";
  }


}
