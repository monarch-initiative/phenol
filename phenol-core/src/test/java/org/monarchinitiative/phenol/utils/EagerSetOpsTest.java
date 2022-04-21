package org.monarchinitiative.phenol.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EagerSetOpsTest {

  private final EagerSetOps setOps = new EagerSetOps();

  @ParameterizedTest
  @CsvSource(
    {
      "ABC, DEF, ''",
      "ABC, CDE, C",
      "ABC, BCD, BC",
      "ABC, BC,  BC",
      "BC,  BCD, BC",
      "BC,  '',  ''",
      "'',  BC,  ''",
      "'',  '',  ''"
    }
  )
  public void intersection(String leftPayload, String rightPayload, String expectedPayload) {
    Set<String> left = splitToSet(leftPayload);
    Set<String> right = splitToSet(rightPayload);
    String[] expected = splitToSet(expectedPayload).toArray(String[]::new);


    Set<String> actual = setOps.intersection(left, right);


    assertThat(actual, hasSize(expected.length));
    if (expected.length > 0) {
      assertThat(actual, containsInAnyOrder(expected));
    } else {
      assertThat(actual, is(empty()));
    }
  }

  private static Set<String> splitToSet(String payload) {
    return Arrays.stream(payload.split(""))
      .filter(e -> !e.isBlank())
      .collect(Collectors.toUnmodifiableSet());
  }

  @ParameterizedTest
  @CsvSource({
    "ABC, DEF, ABCDEF",
    "ABC, CDE, ABCDE",
    "ABC, BCD, ABCD",
    "ABC, BC,  ABC",
    "BC,  BCD, BCD",
    "BC,  '',  BC",
    "'',  BC,  BC",
    "'',  '',  ''"
  })
  public void union(String leftPayload, String rightPayload, String expectedPayload) {
    Set<String> left = splitToSet(leftPayload);
    Set<String> right = splitToSet(rightPayload);
    String[] expected = splitToSet(expectedPayload).toArray(String[]::new);


    Set<String> actual = setOps.union(left, right);


    assertThat(actual, hasSize(expected.length));
    if (expected.length > 0) {
      assertThat(actual, containsInAnyOrder(expected));
    } else {
      assertThat(actual, is(empty()));
    }
  }

}
