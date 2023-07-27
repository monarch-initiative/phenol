package org.monarchinitiative.phenol.io;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.monarchinitiative.phenol.ontology.data.TermSynonymScope.EXACT;

public class MinimalOntologyLoaderTest {

  @Nested
  public class LoadHpo {

    private final MinimalOntology hpo = MinimalOntologyLoader.loadOntology(Paths.get("src/test/resources/hp.module.json").toFile());

    @Test
    public void metaInfoIsCorrect() {
      Map<String, String> metaInfo = hpo.getMetaInfo();
      assertThat(metaInfo, hasEntry("release", "2021-06-08"));
      assertThat(metaInfo, hasEntry("data-version", "http://purl.obolibrary.org/obo/hp/releases/2021-06-08/hp.json"));
    }

    @Test
    public void versionIsParsed() {
      Optional<String> version = hpo.version();
      assertThat(version.isPresent(), equalTo(true));
      assertThat(version.get(), equalTo("2021-06-08"));
    }

    @Test
    public void rootTerm() {
      assertThat(hpo.getRootTermId().getValue(), equalTo("HP:0000001"));
    }

    @Test
    public void testTheNumberOfParsedTermIds() {
      assertThat(hpo.allTermIdsStream().count(), equalTo(362L));
      assertThat(hpo.nonObsoleteTermIdsStream().count(), equalTo(265L));
      assertThat(hpo.obsoleteTermIdsStream().count(), equalTo(362L - 265L));
    }

    @Test
    public void testArachnodactyly() {
      TermId arachnodactylyId = TermId.of("HP:0001166");
      Optional<Term> termOptional = hpo.termForTermId(arachnodactylyId);
      assertThat(termOptional.isPresent(), equalTo(true));
      Term arachnodactyly = termOptional.get();
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

      TermSynonym syn1 = synonyms.get(0);
      assertThat(syn1.getValue(), is("Long slender fingers"));
      assertThat(syn1.getScope(), is(EXACT));
      assertThat(syn1.isLayperson(), is(true));

      // "Abnormality of the eye"
      TermSynonym syn2 = synonyms.get(1);
      assertThat(syn2.getValue(), is("Spider fingers"));
      assertThat(syn2.getScope(), is(EXACT));
      assertThat(syn2.isLayperson(), is(true));

      //  "Eye disease"
      TermSynonym syn3 = synonyms.get(2);
      assertThat(syn3.getValue(), is("Long, slender fingers"));
      assertThat(syn3.getScope(), is(EXACT));
      assertThat(syn3.isLayperson(), is(false));
    }

    @Test
    public void getArachnodactylyAncestors() {
      List<String> ancestors = hpo.graph()
        .getAncestorsStream(TermId.of("HP:0001166"), false)
        .map(TermId::getValue)
        .collect(Collectors.toList());
      assertThat(ancestors, hasItems("HP:0001238", "HP:0100807", "HP:0001167", "HP:0001155", "HP:0011297",
        "HP:0002817", "HP:0002813", "HP:0040064", "HP:0011844", "HP:0040068", "HP:0000118", "HP:0011842", "HP:0000924",
        "HP:0000001", "HP:0033127"));
      assertThat(ancestors, hasSize(15));
    }

    @Test
    public void getPhenotypicAbnormalityChildren() {
      List<String> children = hpo.graph()
        .getChildrenStream(TermId.of("HP:0000118"), false)
        .map(TermId::getValue)
        .collect(Collectors.toList());
      assertThat(children, hasItems("HP:0000119", "HP:0000152", "HP:0000478", "HP:0000598", "HP:0000707", "HP:0000769",
        "HP:0000818", "HP:0001197", "HP:0001507", "HP:0001574", "HP:0001608", "HP:0001626", "HP:0001871", "HP:0001939",
        "HP:0002086", "HP:0002664", "HP:0002715", "HP:0025031", "HP:0025142", "HP:0025354", "HP:0033127", "HP:0040064",
        "HP:0045027"));
      assertThat(children, hasSize(23));
    }
  }

  @Nested
  public class LoadMondo {
    private final MinimalOntology mondo = MinimalOntologyLoader.loadOntology(Paths.get("src/test/resources/mondo_small.json").toFile());

    @Test
    public void metaInfoIsCorrect() {
      Map<String, String> metaInfo = mondo.getMetaInfo();
      assertThat(metaInfo, hasEntry("release", "2020-01-27"));
      assertThat(metaInfo, hasEntry("data-version", "http://purl.obolibrary.org/obo/mondo/releases/2020-01-27/reasoned.owl.owl/mondo.owl"));
    }

    @Test
    public void versionIsParsed() {
      Optional<String> version = mondo.version();
      assertThat(version.isPresent(), equalTo(true));
      assertThat(version.get(), equalTo("2020-01-27"));
    }

    @Test
    public void rootTerm() {
      assertThat(mondo.getRootTermId().getValue(), equalTo("MONDO:0000001"));
    }

    @Test
    public void testTheNumberOfParsedTermIds() {
      assertThat(mondo.allTermIdsStream().count(), equalTo(3L));
    }

    @Test
    public void getExactSynonym() {
      Optional<Term> termOptional = mondo.termForTermId(TermId.of("MONDO:0007952"));
      assertThat(termOptional.isPresent(), equalTo(true));
      Term maxillofacialDysostosis = termOptional.get();
      List<TermSynonym> synonyms = maxillofacialDysostosis.getSynonyms();
      assertThat(synonyms, hasSize(1));
      TermSynonym syn = synonyms.get(0);
      assertEquals(TermSynonymScope.EXACT, syn.getScope());
      List<TermXref> xreflist = syn.getTermXrefs();
      assertEquals(1, xreflist.size());
      TermXref xref = xreflist.get(0);
      assertEquals(TermId.of("OMIM:155000"), xref.id());
    }
  }

  @Nested
  public class LoadGo {
    private final MinimalOntology go = MinimalOntologyLoader.loadOntology(Paths.get("src/test/resources/go/go_head.json").toFile());

    @Test
    public void metaInfoIsCorrect() {
      Map<String, String> metaInfo = go.getMetaInfo();
      assertThat(metaInfo, hasEntry("release", "2017-06-16"));
      assertThat(metaInfo, hasEntry("data-version", "http://purl.obolibrary.org/obo/go/releases/2017-06-16/go.owl"));
    }

    @Test
    public void versionIsParsed() {
      Optional<String> version = go.version();
      assertThat(version.isPresent(), equalTo(true));
      assertThat(version.get(), equalTo("2017-06-16"));
    }

    @Test
    public void rootTerm() {
      assertThat(go.getRootTermId().getValue(), equalTo("owl:Thing"));
    }

    @Test
    public void testTheNumberOfParsedTermIds() {
      assertThat(go.allTermIdsStream().count(), equalTo(11L));
    }

    @Test
    public void getDescendants() {
      List<String> descendants = go.graph()
        .getDescendantsStream(TermId.of("owl:Thing"), false)
        .map(TermId::getValue)
        .collect(Collectors.toList());
      assertThat(descendants, hasItems("GO:0003674", "GO:0005575", "GO:0008150", "GO:0031386", "GO:0005576", "GO:0006791"));
      assertThat(descendants, hasSize(6));
    }
  }

}
