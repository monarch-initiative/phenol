package org.monarchinitiative.phenol.io.obo.hpo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.Assert.assertEquals;

/**
 * TODO refactor to use the small hp file and a small annotation file.
 */
public class HpoDiseaseAnnotationParserTest {
  private HpoDiseaseAnnotationParser parser;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(
    File hpoHeadFile;


    hpoHeadFile = tmpFolder.newFile("hp_head.obo");
    ResourceUtils.copyResourceToFile("/hp_head.obo", hpoHeadFile);
    final HpoOboParser oboParser = new HpoOboParser(hpoHeadFile, true);
    final HpoOntology ontology = oboParser.parse();

    File hpoDiseaseAnnotationToyFile = tmpFolder.newFile("phenotype.100lines.hpoa.tmp");
    ResourceUtils.copyResourceToFile("/phenotype.100lines.hpoa", hpoDiseaseAnnotationToyFile);
    parser = new HpoDiseaseAnnotationParser(hpoDiseaseAnnotationToyFile.getAbsolutePath(), ontology);
  }

  @Test
  public void testParseHpoDiseaseAnnotation() throws PhenolException {
    // Test that the parser correctly parses an hpoa file, and returns a map.
    Map<TermId, HpoDisease> parsedFile = parser.parse();
    HpoDisease testDisease = parsedFile.get(TermId.constructWithPrefix("OMIM:147421"));
    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.constructWithPrefix("OMIM:147421"));
  }

}
