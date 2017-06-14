package de.charite.compbio.ontolib.io.obo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is just a wrapper around a {@link List} of {@link StanzaEntry} objects as this is what an
 * OBO header is.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Header {

  /**
   * Factory method.
   *
   * @param stanzaKeyValues List of {@link StanzaKey} entry objects to construct with.
   * @return Constructed {@link Header}.
   */
  public static Header create(List<StanzaEntry> stanzaKeyValues) {
    return new Header(stanzaKeyValues);
  }

  /** Wrapped {@link List} of {@link StanzaEntry} objects. */
  private final List<StanzaEntry> entries;

  /** Mapping from {@link StanzaEntryType} to list of objects of this stanza. */
  private final Map<StanzaEntryType, List<StanzaEntry>> entryByType;

  /**
   * Constructor.
   *
   * @param entries {@link ListStanzaEntry} objects to initalize with.
   */
  public Header(List<StanzaEntry> entries) {
    this.entries = new ArrayList<>(entries);

    this.entryByType = new HashMap<>();
    for (StanzaEntry e : entries) {
      if (!entryByType.containsKey(e.getType())) {
        entryByType.put(e.getType(), new ArrayList<>());
      }
      entryByType.get(e.getType()).add(e);
    }
  }

  /**
   * @return List of {@link StanzaEntry} objects by occurence in header.
   */
  public List<StanzaEntry> getEntries() {
    return entries;
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
    return "Header [entries=" + entries + ", entryByType=" + new TreeMap<>(entryByType) + "]";
  }

}
