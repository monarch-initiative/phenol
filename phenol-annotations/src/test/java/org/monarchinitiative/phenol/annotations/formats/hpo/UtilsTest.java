package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class UtilsTest {

  @ParameterizedTest
  @CsvSource({
    "A, B, C,       A",
    "A, B, C,       B",
    "A, B, C,       C",
  })
  public void filterIterable(String a, String b, String c, String target) {
    List<String> values = List.of(a, b, c);
    Iterable<String> result = Utils.filterIterable(values, v -> v.equals(target));

    List<String> list = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());
    assertThat(list, hasSize(1));
    assertThat(list, hasItem(target));
  }

  @Test
  public void filterIterable_empty() {
    List<String> values = List.of();
    Iterable<String> whatever = Utils.filterIterable(values, v -> v.equals("Doesn't matter."));

    assertThat(whatever.iterator().hasNext(), equalTo(false));
  }

}
