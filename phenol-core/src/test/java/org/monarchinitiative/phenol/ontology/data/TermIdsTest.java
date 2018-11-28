package org.monarchinitiative.phenol.ontology.data;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class TermIdsTest extends ImmutableOntologyTestBase {

  @Test
  public void test() {
    Set<TermId> inputIds = Sets.newHashSet(id1);
    Set<TermId> outputIds = ImmutableSortedSet.copyOf(TermIds.augmentWithAncestors(ontology, inputIds, true));
    assertEquals(
      ImmutableSet.of(
        TermId.of("HP:0000001"),
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004"),
        TermId.of("HP:0000005")
      ),
      outputIds);
  }
}
