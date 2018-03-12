package org.monarchinitiative.phenol.ser;

/**
 * Taken from http://stackoverflow.com/a/10034633/84349
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class VersionTokenizer {

  /** String to tokenize. */
  private final String versionString;

  /** Length of the string. */
  private final int length;

  /** Current position. */
  private int position;

  /** Current number. */
  private int number;

  /** Remaining suffix. */
  private String suffix;

  /** Whether the value has been set. */
  private boolean hasValue;

  /**
   * Constructor.
   *
   * @param versionString Version string to parse.
   */
  public VersionTokenizer(String versionString) {
    if (versionString == null) {
      throw new IllegalArgumentException("versionString is null");
    }
    this.versionString = versionString;
    this.length = versionString.length();
  }

  /** @return Parsed number. */
  public int getNumber() {
    return number;
  }

  /** @return Suffix. */
  public String getSuffix() {
    return suffix;
  }

  /** @return Whether value has been set. */
  public boolean getHasValue() {
    return hasValue;
  }

  /**
   * Process next token.
   *
   * @return {@code true} if the next token could be read.
   */
  public boolean moveNext() {
    number = 0;
    suffix = "";
    hasValue = false;

    // No more characters
    if (position >= length) {
      return false;
    }

    hasValue = true;

    while (position < length) {
      char c = versionString.charAt(position);
      if (c < '0' || c > '9') {
        break;
      }
      number = number * 10 + (c - '0');
      position++;
    }

    int suffixStart = position;

    while (position < length) {
      final char c = versionString.charAt(position);
      if (c == '.') {
        break;
      }
      position++;
    }

    suffix = versionString.substring(suffixStart, position);

    if (position < length) {
      position++;
    }

    return true;
  }
}
