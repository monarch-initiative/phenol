package de.charite.compbio.ontolib.io.obo;

/**
 * Enumeration of {@link Stanza} types.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum StanzaType {
  /** <code>[Term]</code> stanza. */
  TERM,
  /** <code>[Instance]</code> stanza. */
  INSTANCE,
  /** <code>[Typedef]</code> stanza. */
  TYPEDEF;

  /**
   * @return Return the stanza header string.
   */
  public String getHeader() {
    switch (this) {
      case TERM:
        return "[Term]";
      case INSTANCE:
        return "[Instance]";
      case TYPEDEF:
        return "[Typedef]";
      default:
        throw new RuntimeException("Unknown StanzaType, this should not happen!");
    }

  }
}
