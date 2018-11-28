package org.monarchinitiative.phenol.ontology.data;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

public class TermAnnotationsTest extends ImmutableOntologyTestBase {

  private List<TestTermAnnotation> annotations;

  private TermPrefix TEST_PREFIX = new TermPrefix("TEST");

  @Before
  public void setUp() {
    super.setUp();
    annotations =
        Lists.newArrayList(
            new TestTermAnnotation(TermId.of("HP:0000001"), TermId.of(TEST_PREFIX,"one")),
            new TestTermAnnotation(TermId.of("HP:0000001"), TermId.of(TEST_PREFIX,"two")),
            new TestTermAnnotation(TermId.of("HP:0000002"), TermId.of(TEST_PREFIX,"one")),
            new TestTermAnnotation(TermId.of("HP:0000002"), TermId.of(TEST_PREFIX,"three")));
  }

  @Test
  public void testConstructTermAnnotationToLabelsMap() {
    Map<TermId, Collection<TermId>> map =
        ImmutableSortedMap.copyOf(
            TermAnnotations.constructTermAnnotationToLabelsMap(ontology, annotations));
    debugPrint(map,ontology);
    // 5 annotation classes (to four labels), thus we expect 5 keys
    assertEquals(5, map.size());
  }

  @Test
  public void testConstructTermLabelToAnnotationsMap() {
    Map<TermId, Collection<TermId>> map =
        ImmutableSortedMap.copyOf(
            TermAnnotations.constructTermLabelToAnnotationsMap(ontology, annotations));
    TermId test1 = TermId.of("TEST:one");
    debugPrint(map,ontology);
    assertTrue(map.containsKey(test1));
    // there are three annotated items, so we expect 3 keys
    assertEquals(3,map.size());
  }

  private void debugPrint(Map<TermId, Collection<TermId>> map, Ontology ontology) {
    for (Map.Entry<TermId, Collection<TermId>> e : map.entrySet()) {
      TermId k = e.getKey();
      System.out.println(String.format("%s [%s]", ontology.getTermMap().get(k), k.getValue()));
      for (TermId tid : e.getValue()) {
        System.out.println("\t"+ tid.getValue());
      }
    }
  }

}
