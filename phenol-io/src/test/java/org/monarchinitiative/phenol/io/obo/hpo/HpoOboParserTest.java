package org.monarchinitiative.phenol.io.obo.hpo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import org.monarchinitiative.phenol.ontology.data.TermId;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Testcases that verify whether obo-formatted HPO ontology is properly parsed and loaded.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class HpoOboParserTest {
  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File hpoHeadFile;

  @Before
  public void setUp() throws IOException {
    hpoHeadFile = tmpFolder.newFile("hp_head.obo");
    ResourceUtils.copyResourceToFile("/hp_head.obo", hpoHeadFile);
  }

  @Test
  public void testParseHpoHead() throws IOException {
    final HpoOboParser parser = new HpoOboParser(hpoHeadFile, true);
    final HpoOntology ontology = parser.parse();
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();



    //assertEquals(ontology.countNonObsoleteTerms(), 15);



    assertEquals(
        "TermId [prefix=TermPrefix [value=HP], id=0000001]",
        ontology.getRootTermId().toString());

    assertEquals(
        "{data-version=releases/2017-04-13, saved-by=Peter Robinson, Sebastian Koehler, Sandra Doelken, Chris Mungall, Melissa Haendel, Nicole Vasilevsky, Monarch Initiative, et al.}",
        ontology.getMetaInfo().toString());
  }
}
