package org.monarchinitiative.phenol.ontology.data;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class TermIdsTest extends ImmutableOntologyTestBase {

  @Test
  public void test() {
    Set<TermId> inputIds = Sets.newHashSet(id1);
    Set<TermId> outputIds =
        ImmutableSortedSet.copyOf(TermIds.augmentWithAncestors(ontology, inputIds, true));
    assertEquals(
        "[TermId [prefix=TermPrefix [value=HP], id=0000001], TermId [prefix=TermPrefix [value=HP], id=0000002], TermId [prefix=TermPrefix [value=HP], id=0000003], TermId [prefix=TermPrefix [value=HP], id=0000004], TermId [prefix=TermPrefix [value=HP], id=0000005]]",
        outputIds.toString());
  }
}
