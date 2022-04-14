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
  public static final int DEFAULT_COHORT_SIZE = 50;
  private final Set<DiseaseDatabase> includedDatabases;
  private final boolean salvageNegatedFrequencies;
  private final int cohortSize;

  /**
   * Get options where:
   * <ul>
   *   <li><em>OMIM</em>, <em>ORPHA</em>, and <em>DECIPHER</em> diseases will be loaded</li>
   *   <li>suspicious frequencies of negated terms will be salvaged, if possible, and</li>
   *   <li>a cohort is assumed to have <code>50</code> members by default</li>
   * </ul>
   */
  public static HpoDiseaseLoaderOptions defaultOptions() {
    return of(DATABASE_PREFIXES, true, DEFAULT_COHORT_SIZE);
  }

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
    return "Options{" +
      "includedDatabases=" + includedDatabases +
      ", salvageNegatedFrequencies=" + salvageNegatedFrequencies +
      ", cohortSize=" + cohortSize +
      '}';
  }
}
