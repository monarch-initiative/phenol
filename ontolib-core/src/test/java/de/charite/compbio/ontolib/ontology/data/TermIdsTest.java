package de.charite.compbio.ontolib.ontology.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=2], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=3], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=4], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=5]]",
        outputIds.toString());
  }

}
