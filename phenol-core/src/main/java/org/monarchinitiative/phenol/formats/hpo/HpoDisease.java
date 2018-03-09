package org.monarchinitiative.phenol.formats.hpo;

import java.util.List;

import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableList;

/**
 * Model of a disease from the HPO annotations.
 *
 * <p>The main purpose here is to separate phenotypic abnormalities from mode of inheritance and
 * other annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
@Deprecated
public final class HpoDisease {

  /** Name of the disease from annotation. */
  private final String name;

  /** {@link TermId}s with phenotypic abnormalities and their frequencies. */
  private final List<TermIdWithFrequency> phenotypicAbnormalities;

  /** {@link TermId}s with mode of inheritance and their frequencies. */
  private final List<TermIdWithFrequency> modesOfInheritance;

  /**
   * Constructor.
   *
   * @param name Name of the disease.
   * @param phenotypicAbnormalities {@link List} of phenotypic abnormalities with their frequencies.
   * @param modesOfInheritance {@link List} of modes of inheritance with their frequencies.
   */
  public HpoDisease(
      String name,
      List<TermIdWithFrequency> phenotypicAbnormalities,
      List<TermIdWithFrequency> modesOfInheritance) {
    this.name = name;
    this.phenotypicAbnormalities = ImmutableList.copyOf(phenotypicAbnormalities);
    this.modesOfInheritance = ImmutableList.copyOf(modesOfInheritance);
  }

  /** @return The name of the disease. */
  public String getName() {
    return name;
  }

  /** @return The list of frequency-annotated phenotypic abnormalities. */
  public List<TermIdWithFrequency> getPhenotypicAbnormalities() {
    return phenotypicAbnormalities;
  }

  /** @return The list of frequency-annotated modes of inheritance. */
  public List<TermIdWithFrequency> getModesOfInheritance() {
    return modesOfInheritance;
  }

  @Override
  public String toString() {
    return "HpoDisease [name="
        + name
        + ", phenotypicAbnormalities="
        + phenotypicAbnormalities
        + ", modesOfInheritance="
        + modesOfInheritance
        + "]";
  }

  /** Annotate a {@link TermId} with frequency. */
  public static class TermIdWithFrequency {

    /** The annotated {@link TermId}. */
    private final TermId termId;

    /** The {@link HpoFrequency}. */
    private final HpoFrequency frequency;

    /**
     * Constructor.
     *
     * @param termId Annotated {@link TermId}.
     * @param frequency That the term is annotated with.
     */
    public TermIdWithFrequency(TermId termId, HpoFrequency frequency) {
      this.termId = termId;
      this.frequency = frequency;
    }

    /** @return The annotated {@link TermId}. */
    public TermId getTermId() {
      return termId;
    }

    /** @return The annotating {@link HpoFrequency}. */
    public HpoFrequency getFrequency() {
      return frequency;
    }

    @Override
    public String toString() {
      return "TermIdWithFrequency [termId=" + termId + ", frequency=" + frequency + "]";
    }
  }
}
