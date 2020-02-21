package org.monarchinitiative.phenol.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OntologyLoaderMondoTest {

  private static Ontology mondo;

  @BeforeAll
  static void init() {
    Path mondoPath = Paths.get("src/test/resources/mondo_small.obo");
    mondo = OntologyLoader.loadOntology(mondoPath.toFile());
  }

  @Test
  void ifMondoNotNull_thenOK() {
    assertNotNull(mondo);
  }

  /**
   * In mondo_small.obo, there are 3 terms plus owl:thing
   */
  @Test
  void ifThreeTermsFound_thenOK() {
    assertEquals(4, mondo.countAllTerms());
  }

  @Test
  void testGetExactSynomym() {
    Term maxillofacialDysostosis = mondo.getTermMap().get(TermId.of("MONDO:0007952"));
    List<TermSynonym> synonymList = maxillofacialDysostosis.getSynonyms();
    assertEquals(1, synonymList.size());
    TermSynonym syn = synonymList.get(0);
    assertEquals(TermSynonymScope.EXACT, syn.getScope());
    System.out.println(syn);
    List<TermXref> xreflist = syn.getTermXrefs();
    assertEquals(1, xreflist.size());
    TermXref xref = xreflist.get(0);
    assertEquals(TermId.of("OMIM:155000"), xref.getId());
  }





}
