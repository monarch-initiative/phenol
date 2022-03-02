package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public interface HpoDiseaseLoader {

  static HpoDiseaseLoader of(Ontology hpo) {
    return of(hpo, Options.defaultOptions());
  }

  static HpoDiseaseLoader of(Ontology hpo, Options options) {
    return new HpoDiseaseLoaderDefault(hpo, options);
  }

  HpoDiseases load(InputStream is) throws IOException;

  /**
   *
   * @param path to HPOA file. The file is assumed to be compressed if the file name ends with <em>*.gz</em>.
   * @return diseases
   * @throws IOException in case of IO errors
   */
  default HpoDiseases load(Path path) throws IOException {
    try (InputStream is = openForReading(path)) {
      return load(is);
    }
  }

  /**
   * Get input stream. The input is assumed to be compressed if the file name ends with <em>*.gz</em>.
   */
  private static BufferedInputStream openForReading(Path path) throws IOException {
    InputStream is = Files.newInputStream(path);
    if (path.toFile().getName().endsWith("gz"))
      is = new GZIPInputStream(is);
    return new BufferedInputStream(is);
  }

  class Options {
    /**
     * Default disease databases.
     */
    public static final Set<String> DATABASE_PREFIXES = Set.of("OMIM", "ORPHA", "DECIPHER");
    /**
     * The assumed cohort size used to parse annotation lines with no explicit phenotype frequency data.
     */
    public static final int DEFAULT_COHORT_SIZE = 50;
    private final Set<String> includedDatabases;
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
    public static Options defaultOptions() {
      return of(DATABASE_PREFIXES, true, DEFAULT_COHORT_SIZE);
    }

    public static Options of(Set<String> databasePrefixes, boolean salvageNegatedFrequencies, int cohortSize) {
      return new Options(databasePrefixes, salvageNegatedFrequencies, cohortSize);
    }

    private Options(Set<String> includedDatabases, boolean salvageNegatedFrequencies, int cohortSize) {
      this.includedDatabases = Objects.requireNonNull(includedDatabases, "Database prefixes must not be null");
      this.salvageNegatedFrequencies = salvageNegatedFrequencies;
      if (cohortSize <= 0)
        throw new IllegalArgumentException("Cohort size must be positive");
      this.cohortSize = cohortSize;
    }

    public Set<String> includedDatabases() {
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
      Options options = (Options) o;
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

}
