package de.charite.compbio.ontolib.io.obo;

import java.util.ArrayList;
import java.util.List;

// TODO: make immutable, use immutable list, implement builder

/**
 * Builder class for {@link TrailingModifier}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TrailingModifierBuilder {

  /** A list of key/value entries. */
  private final List<TrailingModifier.KeyValue> keyValue = new ArrayList<>();

  /** Default constructor. */
  public TrailingModifierBuilder() {}

  /**
   * Add key value pair.
   *
   * @param key The key of the pair.
   * @param value The value of the pair.
   */
  public void addKeyValue(String key, String value) {
    keyValue.add(new TrailingModifier.KeyValue(key, value));
  }

  /**
   * @return Construct and return new {@link TrailingModifier} object.
   */
  public TrailingModifier build() {
    return new TrailingModifier(keyValue);
  }

}
