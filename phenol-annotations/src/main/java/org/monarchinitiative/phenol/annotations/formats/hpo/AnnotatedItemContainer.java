package org.monarchinitiative.phenol.annotations.formats.hpo;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A container for {@link AnnotatedItem}.
 *
 * @param <T> e.g. {@link HpoDisease}.
 */
public interface AnnotatedItemContainer<T extends AnnotatedItem> extends Iterable<T> {

  default Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

}
