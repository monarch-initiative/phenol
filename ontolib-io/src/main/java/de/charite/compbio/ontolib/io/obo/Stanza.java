package de.charite.compbio.ontolib.io.obo;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Base class for stanza representations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class Stanza {

  /**
   * Factory method for creating new {@link Stanza} object.
   *
   * <p>
   * This function was created for forwards compatibility in case sub classing is introduced for the
   * different stanza types.
   * </p>
   *
   * @param type {@link StanzaType} of <code>Stanza</code> to created.
   * @param stanzaEntries {@link List} of {@link StanzaEntry} objects to use.
   * @return Freshly created {@link Stanza} object.
   */
  public static Stanza create(StanzaType type, List<StanzaEntry> stanzaEntries) {
    return new Stanza(type, stanzaEntries);
  }

  /** The type of this stanza. */
  private final StanzaType type;

  /** List of {@link StanzaEntry} objects for this <code>Stanza</code>. */
  private final List<StanzaEntry> stanzaEntries;

  /**
   * Constructor.
   *
   * <p>
   * Private; use {@link #create(StanzaType, List)} instead for forwards compatibility.
   * </p>
   *
   * @param type The {@link StanzaType} of this stanza.
   * @param stanzaEntries {@link List} of {@link StanzaEntry} objects.
   */
  private Stanza(StanzaType type, List<StanzaEntry> stanzaEntries) {
    this.type = type;
    this.stanzaEntries = Lists.newArrayList(stanzaEntries);
  }

  /**
   * @return The {@link StanzaType}.
   */
  public StanzaType getType() {
    return type;
  }

  /**
   * @return the stanzaEntries
   */
  public List<StanzaEntry> getStanzaEntries() {
    return stanzaEntries;
  }

  @Override
  public String toString() {
    return "Stanza [type=" + type + ", stanzaEntries=" + stanzaEntries + "]";
  }

}
