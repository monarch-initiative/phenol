package org.monarchinitiative.phenol.ontology.data;

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

  @Before
  public void setUp() {
    super.setUp();
    annotations =
        Lists.newArrayList(
            new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000001"), "one"),
            new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000001"), "two"),
            new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000002"), "one"),
            new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000002"), "three"));
  }

  @Test
  public void testConstructTermAnnotationToLabelsMap() {
    Map<TermId, Collection<String>> map =
        ImmutableSortedMap.copyOf(
            TermAnnotations.constructTermAnnotationToLabelsMap(ontology, annotations));
    assertEquals(
        "{ImmutableTermId [prefix=TermPrefix [value=HP], id=0000001]=[two, one], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000002]=[two, three, one], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000003]=[two, one], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000004]=[two, one], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000005]=[two, three, one]}",
        map.toString());
  }

  @Test
  public void testConstructTermLabelToAnnotationsMap() {
    Map<String, Collection<TermId>> map =
        ImmutableSortedMap.copyOf(
            TermAnnotations.constructTermLabelToAnnotationsMap(ontology, annotations));
    assertEquals(
        "{one=[ImmutableTermId [prefix=TermPrefix [value=HP], id=0000002], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000003], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000004], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000005], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000001]], three=[ImmutableTermId [prefix=TermPrefix [value=HP], id=0000002], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000005]], two=[ImmutableTermId [prefix=TermPrefix [value=HP], id=0000002], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000003], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000004], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000005], ImmutableTermId [prefix=TermPrefix [value=HP], id=0000001]]}",
        map.toString());
  }
}
