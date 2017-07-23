package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>idspace</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryIdspace extends StanzaEntry {

  /** Local ID space. */
  private final String localIdSpace;

  /** Global Id space. */
  private final String globalIdSpace;

  /** Optional description, <code>null</code> for none. */
  private final String description;

  /**
   * Constructor.
   *
   * @param localIdSpace The local Id space.
   * @param globalIdSpace The global Id space.
   * @param description Optional description string, <code>null</code> for none.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryIdspace(String localIdSpace, String globalIdSpace, String description,
      TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.IDSPACE, trailingModifier, comment);
    this.localIdSpace = localIdSpace;
    this.globalIdSpace = globalIdSpace;
    this.description = description;
  }

  /**
   * @return The local Id space name.
   */
  public String getLocalIdSpace() {
    return localIdSpace;
  }

  /**
   * @return The global Id space name.
   */
  public String getGlobalIdSpace() {
    return globalIdSpace;
  }

  /**
   * @return The optional Id space description, <code>null</code> if empty.
   */
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "StanzaEntryIdspace [localIdSpace=" + localIdSpace + ", globalIdSpace=" + globalIdSpace
        + ", description=" + description + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }



}
