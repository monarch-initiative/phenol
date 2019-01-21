package org.monarchinitiative.phenol.annotations.hpo;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HpoAnnotationEntryTest {


  /**
   * This is testing the regex we use in the main class to capture n of m frequency string.s
   */
  @Test
  void testRegex() {
    Pattern n_of_m_pattern = Pattern.compile("^(\\d+)/(\\d+?)");
    String freq1="3/5";
    Matcher m = n_of_m_pattern.matcher(freq1);
    if (m.matches()) {
      String numerator=m.group(1);
      String denominator=m.group(2);
      assertEquals("3",numerator);
      assertEquals("5",denominator);
    }
    String freq2="12/107";
    m = n_of_m_pattern.matcher(freq2);
    if (m.matches()) {
      String numerator=m.group(1);
      String denominator=m.group(2);
      assertEquals("12",numerator);
      assertEquals("107",denominator);
    }
    String freq3="12.107";
    m = n_of_m_pattern.matcher(freq3);
    assertFalse(m.matches());
  }


}
