package org.monarchinitiative.phenol.annotations.hpo;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class HpoAnnotationEntryTest {

  private static Ontology ONTOLOGY;


  @BeforeAll
  public static void init() throws IOException {
    File file = new File(HpoAnnotationEntryTest.class.getResource("/hpo_toy.json").getFile());
    ONTOLOGY = OntologyLoader.loadOntology(file);
  }

  @Test
  public void testEvidenceCodeNotEmpty() throws PhenolException {
    String[] fields = {
      "OMIM:216300",
      "CLEFT PALATE, DEAFNESS, AND OLIGODONTIA",
      "HP:0000007",
      "Autosomal recessive inheritance",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "OMIM:216300",
      "IEA",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line, ONTOLOGY);
    assertEquals("IEA", entry.getEvidenceCode());
    }

  @Test
  public void testEvidenceCodeValid() {
    String[] fields = {
      "OMIM:123456",
      "MADE-UP SYNDROME",
      "HP:0001166",
      "Arachnodactyly",
      "",
      "",
      "76.3%",
      "FEMALE",
      "",
      "",
      "",
      "PMID:9843983",
      "PC",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    PhenolException e = assertThrows(PhenolException.class, () -> HpoAnnotationEntry.fromLine(line, ONTOLOGY));
    assertThat(e.getMessage(), containsString("Invalid evidence code: \"PC\""));
  }

  /**
   * Throws an exception becase the disease name is missing.
   */
  @Test
  public void testMissingDiseaseName() {
    String[] fields = {
      "OMIM:123456",
      "",
      "HP:0001166",
      "Arachnodactyly",
      "",
      "",
      "76.3%",
      "FEMALE",
      "",
      "",
      "",
      "PMID:9843983",
      "PCS",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    HpoAnnotationModelException e = assertThrows(HpoAnnotationModelException.class, () -> HpoAnnotationEntry.fromLine(line, ONTOLOGY));
    assertThat(e.getMessage(), containsString("Missing disease name"));
  }


  /**
   * Test that an exception is thrown because HP:1230528 does not exist.
   */
  @Test
  public void testRecognizeBadHPOId() {
    String[] fields = {
      "OMIM:123456",
      "MADE-UP SYNDROME",
      "HP:1230528",
      "Arachnodactyly",
      "",
      "",
      "76.3%",
      "FEMALE",
      "",
      "",
      "",
      "PMID:9843983",
      "PCS",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    PhenolException e = assertThrows(PhenolException.class, () -> HpoAnnotationEntry.fromLine(line, ONTOLOGY));
    assertThat(e.getMessage(), containsString("Could not find HPO term id (\"HP:1230528\") for \"Arachnodactyly\""));
  }

  /**
   * This should go through with no problems.
   */
  @Test
  public void testFrequency() throws PhenolException {
    String[] fields = {
      "OMIM:123456",
      "MADE-UP SYNDROME",
      "HP:0001166",
      "Arachnodactyly",
      "",
      "",
      "96.7%",
      "FEMALE",
      "",
      "",
      "",
      "PMID:9843983",
      "PCS",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line, ONTOLOGY);
    assertEquals("96.7%", entry.getFrequencyModifier());
  }


  /**
   * The first entry should be OMIM:123456 and not just 123456.
   */
  @Test
  public void testRecognizeBadDatabaseId() {
    String[] fields = {
      "123456",
      "MADE-UP SYNDROME",
      "HP:0001166",
      "Arachnodactyly",
      "",
      "",
      "76.3%",
      "FEMALE",
      "",
      "",
      "",
      "PMID:9843983",
      "PCS",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    HpoAnnotationModelException e = assertThrows(HpoAnnotationModelException.class, () -> HpoAnnotationEntry.fromLine(line, ONTOLOGY));
    assertThat(e.getMessage(), containsString("Could not construct database"));
  }

  /**
   * This should go through with no problems
   */
  @Test
  public void testFreq1() throws PhenolException {
    String[] fields = {
      "OMIM:123456",
      "MADE-UP SYNDROME",
      "HP:0001166",
      "Arachnodactyly",
      "",
      "",
      "7/42",
      "FEMALE",
      "",
      "",
      "",
      "PMID:9843983",
      "PCS",
      "HPO:probinson[2013-01-09]"};
    String line = String.join("\t", fields);

    HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line, ONTOLOGY);
    assertEquals("7/42", entry.getFrequencyModifier());
    }

}
