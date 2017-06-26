package de.charite.compbio.ontolib.ontology.data;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

public class TermAnnotationsTest {

  List<TestTermAnnotation> annotations;

  @Before
  public void setUp() {
    annotations = Lists.newArrayList(
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000001"), "one"),
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000001"), "two"),
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000002"), "one"),
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000002"), "three"));
  }

  @Test
  public void testConstructTermAnnotationToLabelsMap() {
    Map<TermId, Collection<String>> map =
        ImmutableSortedMap.copyOf(TermAnnotations.constructTermAnnotationToLabelsMap(annotations));
    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1]=[two, one], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2]=[three, one]}",
        map.toString());
  }

  @Test
  public void testConstructTermLabelToAnnotationsMap() {
    Map<String, Collection<TermId>> map =
        ImmutableSortedMap.copyOf(TermAnnotations.constructTermLabelToAnnotationsMap(annotations));
    assertEquals(
        "{one=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1]], three=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2]], two=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1]]}",
        map.toString());
  }

}
