package org.monarchinitiative.phenol.ser;

import java.util.Comparator;

/**
 * Compare two version strings.
 *
 * <p>Taken from http://stackoverflow.com/a/10034633/84349
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class VersionComparator implements Comparator<String> {

  /**
   * Compare {@code version1} to {@code version2}.
   *
   * @param version1 Left-hand side.
   * @param version2 Right-hand side.
   * @return {@code int} indicating whether one string is smaller than the other or they are equal.
   */
  public int compare(String version1, String version2) {
    VersionTokenizer tokenizer1 = new VersionTokenizer(version1);
    VersionTokenizer tokenizer2 = new VersionTokenizer(version2);

    int number1 = 0;
    int number2 = 0;
    String suffix1 = "";
    String suffix2 = "";

    while (tokenizer1.moveNext()) {
      if (!tokenizer2.moveNext()) {
        do {
          number1 = tokenizer1.getNumber();
          suffix1 = tokenizer1.getSuffix();
          if (number1 != 0 || suffix1.length() != 0) {
            // Version one is longer than number two, and non-zero
            return 1;
          }
        } while (tokenizer1.moveNext());

        // Version one is longer than version two, but zero
        return 0;
      }

      number1 = tokenizer1.getNumber();
      suffix1 = tokenizer1.getSuffix();
      number2 = tokenizer2.getNumber();
      suffix2 = tokenizer2.getSuffix();

      if (number1 < number2) {
        // Number one is less than number two
        return -1;
      }
      if (number1 > number2) {
        // Number one is greater than number two
        return 1;
      }

      boolean empty1 = suffix1.length() == 0;
      boolean empty2 = suffix2.length() == 0;

      if (empty1 && empty2) continue; // No suffixes
      if (empty1) return 1; // First suffix is empty (1.2 > 1.2b)
      if (empty2) return -1; // Second suffix is empty (1.2a < 1.2)

      // Lexical comparison of suffixes
      int result = suffix1.compareTo(suffix2);
      if (result != 0) return result;
    }

    if (tokenizer2.moveNext()) {
      do {
        number2 = tokenizer2.getNumber();
        suffix2 = tokenizer2.getSuffix();
        if (number2 != 0 || suffix2.length() != 0) {
          // Version one is longer than version two, and non-zero
          return -1;
        }
      } while (tokenizer2.moveNext());

      // Version two is longer than version one, but zero
      return 0;
    }

    return 0;
  }
}
