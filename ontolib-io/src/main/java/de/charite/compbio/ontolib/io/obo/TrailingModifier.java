package de.charite.compbio.ontolib.io.obo;

import java.util.ArrayList;
import java.util.List;

// TODO: make immutable, use immutable list, implement builder

/**
 * Store the trailing modifier <code>{key=value, key=value, ...}</code> information.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TrailingModifier {

  /** A list of key/value entries. */
  private final List<KeyValue> keyValue = new ArrayList<>();

  /**
   * Add key value pair.
   *
   * @param key The key of the pair.
   * @param value The value of the pair.
   */
  public void addKeyValue(String key, String value) {
    keyValue.add(new KeyValue(key, value));
  }

  /**
   * @return The contained key/value list.
   */
  public List<KeyValue> getKeyValue() {
    return keyValue;
  }

  /**
   * A key/value entry.
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   */
  public static class KeyValue {

    /** The key of the key/value entry. */
    private final String key;

    /** The value of the key/value entry. */
    private final String value;

    /**
     * Constructor.
     *
     * @param key The key to store.
     * @param value The value to store.
     */
    public KeyValue(String key, String value) {
      this.key = key;
      this.value = value;
    }

    /**
     * @return the key
     */
    public String getKey() {
      return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return "KeyValue [key=" + key + ", value=" + value + "]";
    }

  }

  @Override
  public String toString() {
    return "TrailingModifier [keyValue=" + keyValue + "]";
  }

}
