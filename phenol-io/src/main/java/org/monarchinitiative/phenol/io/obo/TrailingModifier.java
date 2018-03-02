package org.monarchinitiative.phenol.io.obo;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Store the trailing modifier <code>{key=value, key=value, ...}</code> information.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TrailingModifier {

  /** A list of key/value entries. */
  private final ImmutableList<KeyValue> keyValue;

  /**
   * Constructor.
   *
   * @param keyValue The key/value pairs to construct with.
   */
  public TrailingModifier(List<KeyValue> keyValue) {
    this.keyValue = ImmutableList.copyOf(keyValue);
  }

  /**
   * @return The contained key/value list.
   */
  public ImmutableList<KeyValue> getKeyValue() {
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
     * @return The key.
     */
    public String getKey() {
      return key;
    }

    /**
     * @return The value.
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
