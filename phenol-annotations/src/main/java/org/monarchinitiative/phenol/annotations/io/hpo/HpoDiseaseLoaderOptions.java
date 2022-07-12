package org.monarchinitiative.phenol.annotations.io.hpo;

import java.util.Objects;
import java.util.Set;

public class HpoDiseaseLoaderOptions {
  /**
   * Default disease databases.
   */
  public static final Set<DiseaseDatabase> DATABASE_PREFIXES = Set.of(DiseaseDatabase.OMIM, DiseaseDatabase.ORPHANET, DiseaseDatabase.DECIPHER);
  /**
   * The assumed cohort size used to parse annotation lines with no explicit phenotype frequency data.
   */
  public static final int DEFAULT_COHORT_SIZE = 5;
  private final Set<DiseaseDatabase> includedDatabases;
  private final boolean salvageNegatedFrequencies;
  private final int cohortSize;

  /**
   * Get {@link HpoDiseaseLoaderOptions} where:
   * <ul>
   *   <li>{@link DiseaseDatabase#OMIM}, {@link DiseaseDatabase#ORPHANET}, and {@link DiseaseDatabase#DECIPHER} diseases will be loaded</li>
   *   <li>suspicious frequencies of negated terms will be salvaged, if possible, and</li>
   *   <li>a cohort is assumed to include {@link #DEFAULT_COHORT_SIZE} members by default</li>
   * </ul>
   */
  public static HpoDiseaseLoaderOptions defaultOptions() {
    return of(DATABASE_PREFIXES, true, DEFAULT_COHORT_SIZE);
  }

  /**
   * Get {@link HpoDiseaseLoaderOptions} for instructing the {@link HpoDiseaseLoader} to load
   * {@link DiseaseDatabase#OMIM} diseases, to try to salvage the suspicious frequencies of negated terms,
   * and to assume a cohort size corresponding to {@link #DEFAULT_COHORT_SIZE}.
   */
  public static HpoDiseaseLoaderOptions defaultOmim() {
    return of(Set.of(DiseaseDatabase.OMIM));
  }

  /**
   * Get {@link HpoDiseaseLoaderOptions} for instructing the {@link HpoDiseaseLoader} to load diseases
   * with given {@code databasePrefixes}, to try to salvage the suspicious frequencies of negated terms,
   * and to assume a cohort size corresponding to {@link #DEFAULT_COHORT_SIZE}.
   */
  public static HpoDiseaseLoaderOptions of(Set<DiseaseDatabase> databasePrefixes) {
    return of(databasePrefixes, true, DEFAULT_COHORT_SIZE);
  }

  /**
   * Create {@link HpoDiseaseLoaderOptions} for parameterizing the disease loading by {@link HpoDiseaseLoader}.
   *
   * @param databasePrefixes a set of {@link DiseaseDatabase} prefixes of diseases to load.
   * @param salvageNegatedFrequencies set to {@code true} if the loader should try to salvage suspicious frequencies of the negated terms.
   * @param cohortSize a positive integer corresponding to the assumed cohort size to parse annotation lines with
   *                   no explicit phenotype frequency data.
   */
  public static HpoDiseaseLoaderOptions of(Set<DiseaseDatabase> databasePrefixes, boolean salvageNegatedFrequencies, int cohortSize) {
    return new HpoDiseaseLoaderOptions(databasePrefixes, salvageNegatedFrequencies, cohortSize);
  }

  private HpoDiseaseLoaderOptions(Set<DiseaseDatabase> includedDatabases, boolean salvageNegatedFrequencies, int cohortSize) {
    this.includedDatabases = Objects.requireNonNull(includedDatabases, "Database prefixes must not be null");
    this.salvageNegatedFrequencies = salvageNegatedFrequencies;
    if (cohortSize <= 0)
      throw new IllegalArgumentException("Cohort size must be positive");
    this.cohortSize = cohortSize;
  }

  public Set<DiseaseDatabase> includedDatabases() {
    return includedDatabases;
  }

  public boolean salvageNegatedFrequencies() {
    return salvageNegatedFrequencies;
  }

  public int cohortSize() {
    return cohortSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseLoaderOptions options = (HpoDiseaseLoaderOptions) o;
    return salvageNegatedFrequencies == options.salvageNegatedFrequencies && cohortSize == options.cohortSize && Objects.equals(includedDatabases, options.includedDatabases);
  }

  @Override
  public int hashCode() {
    return Objects.hash(includedDatabases, salvageNegatedFrequencies, cohortSize);
  }

  @Override
  public String toString() {
    return "HpoDiseaseLoaderOptions{" +
      "includedDatabases=" + includedDatabases +
      ", salvageNegatedFrequencies=" + salvageNegatedFrequencies +
      ", cohortSize=" + cohortSize +
      '}';
  }
}
