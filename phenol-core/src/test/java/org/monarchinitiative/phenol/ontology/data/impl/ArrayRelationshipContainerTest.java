package org.monarchinitiative.phenol.ontology.data.impl;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.monarchinitiative.phenol.ontology.data.impl.TestData.*;

public class ArrayRelationshipContainerTest {

  @Test
  public void relationshipById() {
    ArrayRelationshipContainer container = ArrayRelationshipContainer.of(TestData.RELATIONSHIPS);
    assertThat(container.relationshipById(0).isPresent(), equalTo(true));
    assertThat(container.relationshipById(9).isPresent(), equalTo(true));
    assertThat(container.relationshipById(10).isPresent(), equalTo(false));
  }

  @Test
  public void relationshipById_missingItems() {
    List<Relationship> relationships = new ArrayList<>(TestData.RELATIONSHIPS);
    relationships.add(new Relationship(TestData.T010, TestData.T022,  11, RelationshipType.PART_OF));
    ArrayRelationshipContainer container = ArrayRelationshipContainer.of(relationships);
    assertThat(container.relationshipById(0).isPresent(), equalTo(true));
    assertThat(container.relationshipById(9).isPresent(), equalTo(true));
    assertThat(container.relationshipById(10).isPresent(), equalTo(false));
    assertThat(container.relationshipById(11).isPresent(), equalTo(true));
    assertThat(container.relationshipById(12).isPresent(), equalTo(false));
  }

  @Test
  public void relationshipById_negativeIndexThrows() {
    ArrayRelationshipContainer container = ArrayRelationshipContainer.of(TestData.RELATIONSHIPS);
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> container.relationshipById(-1));
    assertThat(e.getMessage(), equalTo("Relationship ID (-1) must not be negative!"));
  }

  @Test
  public void weirdRelationshipIndices() {
    ArrayRelationshipContainer container = ArrayRelationshipContainer.of(List.of(
      new Relationship(T01, T1, 100, IS_A),
      new Relationship(T010, T01, 101, IS_A),
      new Relationship(T011, T01, 102, IS_A),
      // Multi-parent
      new Relationship(T0110, T010, 103, IS_A),
      new Relationship(T0110, T011, 104, IS_A),
      new Relationship(T02, T1, 205, IS_A),
      new Relationship(T020, T02, 206, IS_A),
      new Relationship(T021, T02, 207, IS_A),
      new Relationship(T022, T02, 208, IS_A),
      new Relationship(T03, T1, 209, IS_A)
    ));

    assertThat(container.relationshipById(100).isPresent(), equalTo(true));
    Relationship r100 = container.relationshipById(100).get();
    assertThat(r100.getId(), equalTo(100));

    assertThat(container.relationshipById(205).isPresent(), equalTo(true));
    Relationship r205 = container.relationshipById(205).get();
    assertThat(r205.getId(), equalTo(205));

    assertThat(container.relationshipById(99).isPresent(), equalTo(false));
    assertThat(container.relationshipById(105).isPresent(), equalTo(false));
    assertThat(container.relationshipById(204).isPresent(), equalTo(false));
    assertThat(container.relationshipById(210).isPresent(), equalTo(false));
  }
}
