package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface HpoDiseases extends AnnotatedItemContainer<HpoDisease> {

  static HpoDiseases of(List<HpoDisease> diseases) {
    return new HpoDiseasesDefault(diseases);
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
