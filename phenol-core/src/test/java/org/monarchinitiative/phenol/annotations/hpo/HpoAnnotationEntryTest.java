package org.monarchinitiative.phenol.annotations.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;


import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HpoAnnotationEntryTest {


  @BeforeAll
  static void init() {
    // Make a typical entry. All other fields are emtpy.
    String diseaseID="OMIM:123456";
    String diseaseName="MADE-UP SYNDROME";
    String hpoId= "HP:0000528";
    String hpoName="Anophthalmia";
    String age1="";
    String age2="";
    String freq="HP:0040283";
    String sex="";
    String negation="";
    String mod="";
    String description="";
    String pub="OMIM:154700";
    String evidenceCode="IEA";
    String biocuration="HPO:skoehler[2015-07-26]";
    String fields[] ={diseaseID,diseaseName,hpoId,hpoName,age1,age2,freq,sex,negation,mod,description,pub,evidenceCode,biocuration};
    String line = String.join("\t",fields);
  }

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
