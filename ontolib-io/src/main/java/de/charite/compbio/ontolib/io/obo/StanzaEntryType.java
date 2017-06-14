package de.charite.compbio.ontolib.io.obo;

/**
 * Enumeration for describing {@link StanzaEntry} types.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum StanzaEntryType {
  /** Stanza entry starting with <code>id</code>. */
  ID,
  /** Stanze entry starting with <code>name</code>. */
  NAME,
  /** Stanza entry starting with <code>is_anonymous</code>. */
  IS_ANONYMOUS,
  /** Stanza entry starting with <code>alt_id</code> */
  ALT_ID;
}
