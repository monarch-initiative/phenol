package org.monarchinitiative.phenol.ontology.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import org.monarchinitiative.phenol.ontology.TestOntology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TermAnnotationsTest {

  private final Ontology ontology = TestOntology.ontology();
  private final List<TestTermAnnotation> annotations = Lists.newArrayList(
      new TestTermAnnotation(TermId.of("HP:0000001"), TermId.of("TEST","one")),
      new TestTermAnnotation(TermId.of("HP:0000001"), TermId.of("TEST","two")),
      new TestTermAnnotation(TermId.of("HP:0000002"), TermId.of("TEST","one")),
      new TestTermAnnotation(TermId.of("HP:0000002"), TermId.of("TEST","three")));

  @Test
  void testConstructTermAnnotationToLabelsMap() {
    Map<TermId, Collection<TermId>> map = TermAnnotations.constructTermAnnotationToLabelsMap(ontology, annotations);
    // 5 annotation classes (to four labels), thus we expect 5 keys
    assertEquals(5, map.size());
  }

  @Test
  void testConstructTermLabelToAnnotationsMap() {
    Map<TermId, Collection<TermId>> map = TermAnnotations.constructTermLabelToAnnotationsMap(ontology, annotations);
    TermId test1 = TermId.of("TEST:one");
    assertTrue(map.containsKey(test1));
    // there are three annotated items, so we expect 3 keys
    assertEquals(3, map.size());
  }

  private void debugPrint(Map<TermId, Collection<TermId>> map, Ontology ontology) {
    for (Map.Entry<TermId, Collection<TermId>> e : map.entrySet()) {
      TermId k = e.getKey();
      System.out.printf("%s [%s]", ontology.getTermMap().get(k), k.getValue());
      for (TermId tid : e.getValue()) {
        System.out.println("\t"+ tid.getValue());
      }
    }
  }

}
