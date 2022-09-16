package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.Versioned;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link Versioned} container for {@link HpoDisease}s with a bunch of convenience methods for getting
 * the individual {@link HpoDisease}s.
 */
public interface HpoDiseases extends AnnotatedItemContainer<HpoDisease>, Versioned {

  /**
   * @deprecated use {@link #of(String, List)} instead.
   */
  @Deprecated(forRemoval = true)
  static HpoDiseases of(List<HpoDisease> diseases) {
    return of(null, diseases);
  }

  static HpoDiseases of(String version, List<HpoDisease> diseases) {
    return new HpoDiseasesDefault(version, diseases);
  }

  Optional<HpoDisease> diseaseById(TermId diseaseId);

  /**
   * @return number of {@link HpoDisease}s
   */
  int size();

  default Stream<HpoDisease> hpoDiseases() {
    return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), Spliterator.SIZED), false);
  }

  default Set<TermId> diseaseIds() {
    return hpoDiseases().map(Identified::id).collect(Collectors.toUnmodifiableSet());
  }

  default Map<TermId, HpoDisease> diseaseById() {
    return hpoDiseases()
      .collect(Collectors.toUnmodifiableMap(Identified::id, Function.identity()));
  }

}
