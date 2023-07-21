package org.monarchinitiative.phenol.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RelationTypesTest {

  @Test
  public void isA() {
    assertThat(RelationTypes.isA().id(), equalTo("is_a"));
    assertThat(RelationTypes.isA().label(), equalTo("is_a"));
    assertThat(RelationTypes.isA().propagates(), equalTo(true));
  }

  @Test
  public void partOf() {
    assertThat(RelationTypes.partOf().id(), equalTo("http://purl.obolibrary.org/obo/BFO_0000050"));
    assertThat(RelationTypes.partOf().label(), equalTo("part of"));
    assertThat(RelationTypes.partOf().propagates(), equalTo(true));
  }
}
