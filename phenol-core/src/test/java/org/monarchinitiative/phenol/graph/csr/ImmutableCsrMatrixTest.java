package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableCsrMatrixTest {

  @ParameterizedTest
  @CsvSource({
    "0, 0;1;2;3",
    "1, 0;1;2;3",
    "2, 0;1;2;3",
    "3, 0;1;2;3",
  })
  public void colIndicesOfVal_full(int idx, String payload) {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.full());
    List<Integer> expected = decodeExpectedPayload(payload);
    List<Integer> actual = intoList(matrix.colIndicesOfVal(idx, k -> true));
    assertThat(actual, equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "0, 0;3",
    "1, 1",
    "2, 2",
    "3, 0;3",
  })
  public void colIndicesOfVal_allEdges(int idx, String payload) {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.allEdges());
    List<Integer> expected = decodeExpectedPayload(payload);
    List<Integer> actual = intoList(matrix.colIndicesOfVal(idx, k -> true));
    assertThat(actual, equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "0, 3,   0",
    "1, '',  1",
    "2, 2,   ''",
    "3, 3,   0",
  })
  public void colIndicesOfVal_allEdges_evenAndOddValues(int idx, String evenPayload, String oddPayload) {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.allEdges());

    List<Integer> evenExpected = decodeExpectedPayload(evenPayload);
    List<Integer> evenActual = intoList(matrix.colIndicesOfVal(idx, k -> k % 2 == 0));
    assertThat(evenActual, equalTo(evenExpected));

    List<Integer> oddExpected = decodeExpectedPayload(oddPayload);
    List<Integer> oddActual = intoList(matrix.colIndicesOfVal(idx, k -> k % 2 == 1));
    assertThat(oddActual, equalTo(oddExpected));
  }

  @ParameterizedTest
  @CsvSource({
    "0, ''",
    "1, ''",
    "2, ''",
  })
  public void colIndicesOfVal_zeroes(int idx, String payload) {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.zeroes());
    List<Integer> expected = decodeExpectedPayload(payload);
    List<Integer> actual = intoList(matrix.colIndicesOfVal(idx, k -> true));
    assertThat(actual, equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "0, '1;3'",
    "1, '0;2;3'",
  })
  public void colIndicesOfVal_rect(int idx, String payload) {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.rect());
    List<Integer> expected = decodeExpectedPayload(payload);
    List<Integer> actual = intoList(matrix.colIndicesOfVal(idx, k -> true));
    assertThat(actual, equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "0, 3,   1",
    "1, 2,   0;3"
  })
  public void colIndicesOfVal_rect_evenAndOddValues(int idx, String evenPayload, String oddPayload) {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.rect());

    List<Integer> evenExpected = decodeExpectedPayload(evenPayload);
    List<Integer> evenActual = intoList(matrix.colIndicesOfVal(idx, k -> k % 2 == 0));
    assertThat(evenActual, equalTo(evenExpected));

    List<Integer> oddExpected = decodeExpectedPayload(oddPayload);
    List<Integer> oddActual = intoList(matrix.colIndicesOfVal(idx, k -> k % 2 == 1));
    assertThat(oddActual, equalTo(oddExpected));
  }

  @Test
  public void colIndicesOfVal_rect_unfound() {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.rect());

    // Here we're searching for `3` in the 0th row, but it is not there.
    assertThat(intoList(matrix.colIndicesOfVal(0, k -> k == 3)),
      is(emptyCollectionOf(Integer.class)));

    // Here we're searching for `2` in the 1st row, but it is not there.
    assertThat(intoList(matrix.colIndicesOfVal(1, k -> k == 2)),
      is(emptyCollectionOf(Integer.class)));
  }

  @Test
  public void accessingNegativeOrBeyondBoundsIndexThrows() {
    ImmutableCsrMatrix<Integer> matrix = makeCsrMatrix(CsrData.allEdges());

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
      () -> matrix.colIndicesOfVal(-1, entry -> true));
    assertThat(e.getMessage(), equalTo("Row index must be in range [0, 3] but got -1"));

    e = assertThrows(IllegalArgumentException.class,
      () -> matrix.colIndicesOfVal(4, entry -> true));
    assertThat(e.getMessage(), equalTo("Row index must be in range [0, 3] but got 4"));
  }

  @Test
  public void invalidIndptrInputDataThrow() {
    // Data is the same as in CsrData.rect() but with one negative index
    int[] indptr = {0, 2, -1};
    int[] indices = {1, 3, 0, 2, 3};
    Integer[] data = {1, 2, 3, 4, 5};
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new ImmutableCsrMatrix<>(indptr, indices, data));

    assertThat(e.getMessage(), equalTo("Expected array of non-negative integers but the following indices were negative: 2"));
  }

  @Test
  public void invalidIndicesInputDataThrow() {
    // Data is the same as in CsrData.rect() but with one negative index
    int[] indptr = {0, 2, 5};
    int[] indices = {-1, 3, 0, 2, 3};
    Integer[] data = {1, 2, 3, 4, 5};
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new ImmutableCsrMatrix<>(indptr, indices, data));

    assertThat(e.getMessage(), equalTo("Expected array of non-negative integers but the following indices were negative: 0"));
  }

  // ---------------------------------------------------------------------------------------------------------------- //

  private static <T> List<T> intoList(Iterator<T> iterator) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE), false)
      .collect(Collectors.toList());
  }

  private static List<Integer> decodeExpectedPayload(String payload) {
    return payload.isBlank()
      ? List.of()
      : Arrays.stream(payload.split(";"))
      .map(Integer::parseInt)
      .collect(Collectors.toList());
  }


  private static <T> ImmutableCsrMatrix<T> makeCsrMatrix(CsrData<T> data) {
    return new ImmutableCsrMatrix<>(data.getIndptr(), data.getIndices(), data.getData());
  }
}
