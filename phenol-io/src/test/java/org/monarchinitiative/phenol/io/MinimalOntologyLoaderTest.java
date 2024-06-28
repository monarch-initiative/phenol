package org.monarchinitiative.phenol.io;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
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

    private final File file = Paths.get("src/test/resources/hp.module.json").toFile();
    private final MinimalOntology hpo = MinimalOntologyLoader.loadOntology(file);

    @Test
    public void metaInfoIsCorrect() {
      Map<String, String> metaInfo = hpo.getMetaInfo();
      assertThat(metaInfo, hasEntry("release", "2024-06-25"));
      assertThat(metaInfo, hasEntry("data-version", "http://purl.obolibrary.org/obo/hp/releases/2024-06-25/hp.json"));
    }

    @Test
    public void versionIsParsed() {
      Optional<String> version = hpo.version();
      assertThat(version.isPresent(), equalTo(true));
      assertThat(version.get(), equalTo("2024-06-25"));
    }

    @Test
    public void rootTerm() {
      assertThat(hpo.getRootTermId().getValue(), equalTo("HP:0000001"));
    }

    @Test
    public void testTheNumberOfParsedTermIds() {
      assertThat(hpo.allTermIdsStream().count(), equalTo(637L));
      assertThat(hpo.nonObsoleteTermIdsStream().count(), equalTo(533L));
      assertThat(hpo.obsoleteTermIdsStream().count(), equalTo(637L - 533L));
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
      // We only have Arachnodactyly here, which inherits from the two ancestors below:
      assertThat(children, containsInAnyOrder("HP:0033127", "HP:0040064"));
      assertThat(children, hasSize(2));
    }
  }

  @Nested
  public class LoadMondo {
    private final File mondoSmall = Paths.get("src/test/resources/mondo_small.json").toFile();
    private final MinimalOntology mondo = MinimalOntologyLoader.loadOntology(mondoSmall,
      CurieUtilBuilder.defaultCurieUtil(),
      OntologyLoaderOptions.builder().discardNonPropagatingRelationships(true).build(),
      "MONDO");

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
