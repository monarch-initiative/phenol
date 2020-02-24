package org.monarchinitiative.phenol.io;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.monarchinitiative.phenol.ontology.data.TermSynonymScope.EXACT;
import static org.monarchinitiative.phenol.ontology.data.TermSynonymScope.RELATED;

public class OntologyLoaderHpoTest {

  private Ontology hpo;

  public OntologyLoaderHpoTest() {
    this.hpo = OntologyLoader.loadOntology(Paths.get("src/test/resources/hp_head.obo").toFile());
  }

  @Test
  public void testParseHpoHead() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = hpo.getGraph();
    assertNotNull(graph);
  }

  @Test
  public void testGetRightNumberOfTerms() {
    int expectedTermCount = 22; // there are 22 non-obsolete terms in hp_head.obo
    assertEquals(expectedTermCount, hpo.countAllTerms());
  }

  @Test
  public void testGetRootTerm() {
    TermId rootId = TermId.of("HP:0000001");
    assertEquals(rootId, hpo.getRootTermId());
  }

  @Test
  public void testGetNonRootTerms() {
    // outside of the root these four terms are in the hp_head file
    TermId tid1 = TermId.of("HP:0000005");
    TermId tid2 = TermId.of("HP:0000006");
    TermId tid3 = TermId.of("HP:0000007");
    TermId tid4 = TermId.of("HP:0000118");
    Map<TermId, Term> termMap = hpo.getTermMap();
    assertTrue(termMap.containsKey(tid1));
    assertTrue(termMap.containsKey(tid2));
    assertTrue(termMap.containsKey(tid3));
    assertTrue(termMap.containsKey(tid4));
    Term term1 = termMap.get(tid1);
    System.out.println(term1);
  }

  @Disabled("Currently broken due to obographs JSON deserialisation")
  @Test
  void loadHpJson() {
    System.out.println("Starting full HPO JSON load");
    Instant start = Instant.now();
    Path hpJsonPath = Paths.get("src/test/resources/hp.json");
    Ontology ontology = OntologyLoader.loadOntology(hpJsonPath.toFile());
    Instant end = Instant.now();
    System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
  }

 // @Disabled
  @Test
  public void testParseFullHpo() {
//    for (int i = 0; i < 10; i++) {
      System.out.println("Starting full HPO load");
      Instant start = Instant.now();
      Ontology ontology = OntologyLoader.loadOntology(Paths.get("src/test/resources/hp.obo").toFile());
      Instant end = Instant.now();
      System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//      ontology.getTerms().stream().limit(1240).forEach(System.out::println);
//    }
  }

  @Disabled
  @Test
  public void testParseFullHpoWithOboFormatParser() throws Exception {
//    for (int i = 0; i < 10; i++) {
    System.out.println("Starting full HPO load");
    Instant start = Instant.now();
    OBOFormatParser oboFormatParser = new OBOFormatParser();
    OBODoc oboDoc = oboFormatParser.parse(Paths.get("src/test/resources/hp.obo").toFile());
//    OboDocConverter oboDocConverter = OboDocConverter.convert(oboDoc);

//    System.out.println(oboDocConverter.getRelationships());

    Instant end = Instant.now();
    System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//    }
  }

  @Test
  void ifHpoNotNull_thenOK() {
    assertNotNull(hpo);
  }

  @Test
  void ifHpoHas22Terms_thenOK() {
    int expectedTermCount = 22; // hpo_small.obo
    assertEquals(expectedTermCount, hpo.countAllTerms());
  }

  @Test
  void testModeOfInheritanceTerm() {
    TermId mode = TermId.of("HP:0000005");
    Term moiTerm = hpo.getTermMap().get(mode);
    assertNotNull(moiTerm);
    List<TermSynonym> synonyms =moiTerm.getSynonyms();
    // HP:0000005 has one EXACT synonym
    assertEquals(1, synonyms.size());
    TermSynonym syn = synonyms.get(0);
    assertEquals(EXACT,syn.getScope());
    assertFalse(syn.hasSynonymType());
  }


  @Test
  void trestAbnormalityOfTheEye() {
    TermId eyeId = TermId.of("HP:0000478");
    Term eye = hpo.getTermMap().get(eyeId);
    assertNotNull(eye);
    assertEquals("Abnormality of the eye", eye.getName());
    // There are three synonyms, all of them are layperson
    List<TermSynonym> synonyms =eye.getSynonyms();
    assertEquals(3, synonyms.size());
    Optional<TermSynonym> opt1 = synonyms.stream().filter(s -> s.getValue().equals("Abnormal eye")).findFirst();
    assertTrue(opt1.isPresent());
    TermSynonym syn1 = opt1.get();
    assertNotNull(syn1);
    assertEquals("Abnormal eye", syn1.getValue());
    assertEquals(EXACT, syn1.getScope());
    assertTrue(syn1.isLayperson());
    // "Abnormality of the eye"
    Optional<TermSynonym> opt2 = synonyms.stream().filter(s -> s.getValue().equals("Abnormality of the eye")).findFirst();
    assertTrue(opt2.isPresent());
    TermSynonym syn2 = opt2.get();
    assertNotNull(syn2);
    assertEquals(EXACT, syn2.getScope());
    assertTrue(syn2.isLayperson());
  //  "Eye disease"
    Optional<TermSynonym> opt3 = synonyms.stream().filter(s -> s.getValue().equals("Eye disease")).findFirst();
    assertTrue(opt3.isPresent());
    TermSynonym syn3 = opt3.get();
    assertNotNull(syn3);
    assertEquals(RELATED, syn3.getScope());
    assertTrue(syn3.isLayperson());


  }


}
