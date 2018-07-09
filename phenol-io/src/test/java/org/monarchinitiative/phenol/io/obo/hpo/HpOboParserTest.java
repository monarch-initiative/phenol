package org.monarchinitiative.phenol.io.obo.hpo;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class HpOboParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File hpoHeadFile;

  @Before
  public void setUp() throws IOException {
    hpoHeadFile = tmpFolder.newFile("hp_head.obo");
    ResourceUtils.copyResourceToFile("/hp_head.obo", hpoHeadFile);
  }

  @Test
  public void testParseHpoHead() throws IOException {
    final HpOboParser parser = new HpOboParser(hpoHeadFile, true);
    final Optional<HpoOntology> ontologyOpt = parser.parse();
    assertTrue(ontologyOpt.isPresent());
    HpoOntology ontology = ontologyOpt.get();
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertNotNull(graph);

  }



}
