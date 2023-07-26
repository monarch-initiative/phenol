package org.monarchinitiative.phenol.annotations.obo.uberpheno;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.utils.CurieUtil;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testcases that verify whether Uberpheno ontology is properly parsed and loaded.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
@Disabled("Disabled until we have a toy crossSpeciesPheno_head.json")
public class UberphenoOboParserTest {

  private Ontology ontology;

  public UberphenoOboParserTest() throws IOException {
    final String uberPhenoPath = "/crossSpeciesPheno_head.obo";
    URL uberphenoURL = UberphenoOboParserTest.class.getResource(uberPhenoPath);
    if (uberphenoURL == null) {
      throw new IOException("Could not find uber pheno at " + uberPhenoPath);
    }
    File uberPhenoFile = new File(uberphenoURL.getFile());
    CurieUtil curieUtil = CurieUtilBuilder.withDefaultsAnd(Map.of("UBERPHENO", "http://purl.obolibrary.org/obo/UBERPHENO_"));
    this.ontology = OntologyLoader.loadOntology(uberPhenoFile, curieUtil);
  }

  @Test
  public void testGetAllFourEdges() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertEquals(graph.edgeSet().size(), 4);
  }

  @Test
  public void testGetAllFiveTerms() {
    assertEquals(5, ontology.allTermIdCount());
  }

  @Test
  public void testRelationMap() {
    Map<Integer, Relationship> relmap = ontology.getRelationMap();
    assertEquals(4, relmap.size());
  }

  @Test
  public void testRootTerm() {
    TermId root = TermId.of("UBERPHENO:00000001");
    assertEquals(root, ontology.getRootTermId());
  }

}
