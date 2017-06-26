package de.charite.compbio.ontolib.ontology.data;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

public class TermAnnotationsTest extends ImmutableOntologyTestBase {

  List<TestTermAnnotation> annotations;

  @Before
  public void setUp() {
    super.setUp();
    annotations = Lists.newArrayList(
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000001"), "one"),
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000001"), "two"),
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000002"), "one"),
        new TestTermAnnotation(ImmutableTermId.constructWithPrefix("HP:0000002"), "three"));
  }

  @Test
  public void testConstructTermAnnotationToLabelsMap() {
    Map<TermId, Collection<String>> map = ImmutableSortedMap
        .copyOf(TermAnnotations.constructTermAnnotationToLabelsMap(ontology, annotations));
    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1]=[two, one], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2]=[two, three, one], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=3]=[two, one], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=4]=[two, one], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=5]=[two, three, one]}",
        map.toString());
  }

  @Test
  public void testConstructTermLabelToAnnotationsMap() {
    Map<String, Collection<TermId>> map = ImmutableSortedMap
        .copyOf(TermAnnotations.constructTermLabelToAnnotationsMap(ontology, annotations));
    assertEquals(
        "{one=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=4], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=3], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=5]], three=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=5]], two=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=4], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=3], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=5]]}",
        map.toString());
  }

}
