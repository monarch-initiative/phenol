package de.charite.compbio.ontolib.io.obo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

  /** Mapping from {@link StanzaEntryType} to list of objects of this stanza. */
  private final Map<StanzaEntryType, List<StanzaEntry>> entryByType;

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

    this.entryByType = new HashMap<>();
    for (StanzaEntry e : stanzaEntries) {
      if (!entryByType.containsKey(e.getType())) {
        entryByType.put(e.getType(), new ArrayList<>());
      }
      entryByType.get(e.getType()).add(e);
    }
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

  /**
   * @return {@link Map} from {@link StanzaEntryType} to {@link List} of {@link StanzaEntry}. Will
   *         contain no empty lists, entry is missing if there is no {@link StanzaEntry} for the
   *         type.
   */
  public Map<StanzaEntryType, List<StanzaEntry>> getEntryByType() {
    return entryByType;
  }

  @Override
  public String toString() {
    return "Stanza [type=" + type + ", stanzaEntries=" + stanzaEntries + ", entryByType="
        + new TreeMap<>(entryByType) + "]";
  }

}
