package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RelationshipPredicatesTest {

  @ParameterizedTest
  @CsvSource({
    "1, true",
    "2, false",
    "3, true",
    "4, false",
    "5, true",
    "6, false",
  })
  public void isParent(String value, boolean expected) {
    assertThat(RelationshipPredicates.IS_PARENT.test(Byte.parseByte(value)), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "1,  false",
    "2,  true",
    "3,  true",
    "4,  false",
    "5,  false",
    "6,  true",
    "7,  true",
    "8,  false",
  })
  public void isChild(String value, boolean expected) {
    assertThat(RelationshipPredicates.IS_CHILD.test(Byte.parseByte(value)), equalTo(expected));
  }

}
