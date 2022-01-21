package org.monarchinitiative.phenol.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class OntologyLoaderMondoTest {

  private static Ontology mondo;

  @BeforeAll
  public static void beforeAll() throws Exception {
    try (InputStream is = OntologyLoaderMondoTest.class.getResourceAsStream("/mondo_small.json")) {
      mondo = OntologyLoader.loadOntology(is);
    }
  }

  @Test
  public void ifMondoNotNull_thenOK() {
    assertNotNull(mondo);
  }

  /**
   * In mondo_small.obo, there are 3 terms plus owl:thing
   */
  @Test
  public void ifThreeTermsFound_thenOK() {
    assertEquals(4, mondo.countAllTerms());
  }

  @Test
  public void testGetExactSynomym() {
    Term maxillofacialDysostosis = mondo.getTermMap().get(TermId.of("MONDO:0007952"));
    List<TermSynonym> synonymList = maxillofacialDysostosis.getSynonyms();
    assertEquals(1, synonymList.size());
    TermSynonym syn = synonymList.get(0);
    assertEquals(TermSynonymScope.EXACT, syn.getScope());
    List<TermXref> xreflist = syn.getTermXrefs();
    assertEquals(1, xreflist.size());
    TermXref xref = xreflist.get(0);
    assertEquals(TermId.of("OMIM:155000"), xref.id());
  }


}
