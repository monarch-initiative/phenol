package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>idspace</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryIdspace extends StanzaEntry {

  /** Local ID space */
  private final String localIDSpace;

  /** Global ID space */
  private final String globalIDSpace;

  /** Optional description, <code>null</code> for none. */
  private final String description;

  /**
   * Constructor.
   *
   * @param localIDSpace The local ID space.
   * @param globalIDSpace The global ID space.
   * @param description Optional description string, <code>null</code> for none.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryIdspace(String localIDSpace, String globalIDSpace, String description,
      TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.IDSPACE, trailingModifier, comment);
    this.localIDSpace = localIDSpace;
    this.globalIDSpace = globalIDSpace;
    this.description = description;
  }

  /**
   * @return The local ID space name.
   */
  public String getLocalIDSpace() {
    return localIDSpace;
  }

  /**
   * @return The global ID space name.
   */
  public String getGlobalIDSpace() {
    return globalIDSpace;
  }

  /**
   * @return The optional ID space description, <code>null</code> if empty.
   */
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "StanzaEntryIdspace [localIDSpace=" + localIDSpace + ", globalIDSpace=" + globalIDSpace
        + ", description=" + description + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }



}
