package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * An {@link HpoDiseases} implementation with lazy initialization of {@link #diseaseById} returned.
 */
class HpoDiseasesDefault implements HpoDiseases {

  private final String version; // nullable
  private final List<HpoDisease> hpoDiseases;
  /*
  We use double check locking for lazy initialization.
  See more at https://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
   */
  private volatile Map<TermId, HpoDisease> diseaseById;

  HpoDiseasesDefault(String version, List<HpoDisease> hpoDiseases) {
    this.version = version; // nullable
    this.hpoDiseases = Objects.requireNonNull(hpoDiseases, "HPO diseases must not be null");
  }

  @Override
  public Iterator<HpoDisease> iterator() {
    return hpoDiseases.iterator();
  }

  @Override
  public Optional<HpoDisease> diseaseById(TermId diseaseId) {
    return Optional.ofNullable(diseaseById().get(diseaseId));
  }

  /**
   * @return number of known disease models
   */
  @Override
  public int size() {
    return hpoDiseases.size();
  }

  @Override
  public Map<TermId, HpoDisease> diseaseById() {
    if (diseaseById == null) {
      synchronized (this) {
        if (diseaseById == null) {
          diseaseById = hpoDiseases.stream()
            .collect(Collectors.toUnmodifiableMap(HpoDisease::id, Function.identity()));
        }
      }
    }
    return diseaseById;
  }

  @Override
  public Set<TermId> diseaseIds() {
    return diseaseById().keySet();
  }

  @Override
  public Optional<String> version() {
    return Optional.ofNullable(version);
  }
}
