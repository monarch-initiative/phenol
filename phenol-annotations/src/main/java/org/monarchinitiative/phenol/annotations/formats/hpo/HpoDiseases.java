package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HpoDiseases implements Iterable<HpoDisease> {
  // an interface candidate

  private final List<HpoDisease> hpoDiseases;

  public static HpoDiseases of(List<HpoDisease> hpoDiseases) {
    return new HpoDiseases(hpoDiseases);
  }

  private HpoDiseases(List<HpoDisease> hpoDiseases) {
    this.hpoDiseases = Objects.requireNonNull(hpoDiseases, "HPO diseases must not be null");
  }

  public Stream<HpoDisease> hpoDiseases() {
    return hpoDiseases.stream();
  }

  public Map<TermId, HpoDisease> diseaseById() {
    return hpoDiseases.stream()
      .collect(Collectors.toUnmodifiableMap(HpoDisease::getDiseaseDatabaseId, Function.identity()));
  }

  @Override
  public Iterator<HpoDisease> iterator() {
    return hpoDiseases.iterator();
  }

  /**
   * @return number of known disease models
   */
  public int size() {
    return hpoDiseases.size();
  }
}
