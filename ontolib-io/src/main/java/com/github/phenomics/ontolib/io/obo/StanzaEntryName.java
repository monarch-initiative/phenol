package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>name</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryName extends StanzaEntry {

  /** Name value of stanza entry. */
  private final String name;

  /**
   * Constructor.
   *
   * @param name The value of the name stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryName(String name, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.NAME, trailingModifier, comment);
    this.name = name;
  }

  /**
   * @return The entry's name value.
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "StanzaEntryName [name=" + name + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }

}
