package org.monarchinitiative.phenol.io;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;


import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.monarchinitiative.phenol.ontology.data.TermSynonymScope.EXACT;

public class OntologyLoaderHpoTest {

  private Ontology hpo;

  @BeforeEach
  public void beforeEach() {
    hpo = OntologyLoader.loadOntology(Paths.get("src/test/resources/hp.module.json").toFile());
  }

  @Test
  public void testParseHpoToy() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = hpo.getGraph();
    assertNotNull(graph);
  }

  @Test
  public void testMetadata() {
    Map<String, String> metaInfo = hpo.getMetaInfo();
    assertThat(metaInfo, hasEntry("release", "2024-06-25"));
    assertThat(metaInfo, hasEntry("data-version", "http://purl.obolibrary.org/obo/hp/releases/2024-06-25/hp.json"));
  }

  @Test
  public void testGetRightNumberOfTerms() {
    assertEquals(533, hpo.nonObsoleteTermIdCount());
  }

  @Test
  public void testGetRootTerm() {
    TermId rootId = TermId.of("HP:0000001");
    assertEquals(rootId, hpo.getRootTermId());
  }

  @Test
  public void testGetNonRootTerms() {
    // outside the root there are some non-root terms in the ontology
    Map<TermId, Term> termMap = hpo.getTermMap();
    assertThat(termMap, hasKey(TermId.of("HP:0000006")));
    assertThat(termMap, hasKey(TermId.of("HP:0000007")));
    assertThat(termMap, hasKey(TermId.of("HP:0000118")));

    Term modeOfInheritance = termMap.get(TermId.of("HP:0000005"));
    assertThat(modeOfInheritance.getName(), is("Mode of inheritance"));
  }

  @Test
  public void ifHpoNotNull_thenOK() {
    assertNotNull(hpo);
  }

  @Test
  public void testModeOfInheritanceTerm() {
    TermId mode = TermId.of("HP:0000005");
    Term moiTerm = hpo.getTermMap().get(mode);
    assertNotNull(moiTerm);
    List<TermSynonym> synonyms = moiTerm.getSynonyms();
    // HP:0000005 has one EXACT synonym
    assertEquals(1, synonyms.size());
    TermSynonym syn = synonyms.get(0);
    assertEquals(EXACT, syn.getScope());
    assertFalse(syn.hasSynonymType());
  }

  @Test
  public void testArachnodactyly() {
    TermId arachnodactylyId = TermId.of("HP:0001166");
    Term arachnodactyly = hpo.getTermMap().get(arachnodactylyId);
    assertNotNull(arachnodactyly);
    assertEquals("Arachnodactyly", arachnodactyly.getName());
    // There are three synonyms, two of them are layperson
    List<TermSynonym> synonyms = arachnodactyly.getSynonyms();
    List<String> synonymValues = synonyms.stream()
      .map(TermSynonym::getValue)
      .collect(Collectors.toList());
    assertEquals(3, synonymValues.size());
    assertThat(synonymValues, hasItems("Long slender fingers", "Spider fingers", "Long, slender fingers"));
    assertThat(arachnodactyly.getAltTermIds(), hasSize(1));
    assertThat(arachnodactyly.getAltTermIds(), hasItems(TermId.of("HP:0001505")));

    TermSynonym syn0 = synonyms.get(0);
    assertThat(syn0.getValue(), is("Long, slender fingers"));
    assertThat(syn0.getScope(), is(EXACT));
    assertThat(syn0.isLayperson(), is(false));

    TermSynonym syn1 = synonyms.get(1);
    assertThat(syn1.getValue(), is("Long slender fingers"));
    assertThat(syn1.getScope(), is(EXACT));
    assertThat(syn1.isLayperson(), is(true));

    TermSynonym syn2 = synonyms.get(2);
    assertThat(syn2.getValue(), is("Spider fingers"));
    assertThat(syn2.getScope(), is(EXACT));
    assertThat(syn2.isLayperson(), is(true));
  }

  @Test
  public void ontologyHasVersion() {
    Optional<String> version = hpo.version();
    assertThat(version.isPresent(), equalTo(true));
    assertThat(version.get(), equalTo("2024-06-25"));
  }
}
