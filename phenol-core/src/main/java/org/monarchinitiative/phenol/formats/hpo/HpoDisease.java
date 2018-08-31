package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Model of a disease from the HPO annotations. This is an extension of HpoDisease and will be
 * replaced in ontolib
 *
 * <p>The main purpose here is to separate phenotypic abnormalities from mode of inheritance and
 * other annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.2.1 (2017-11-16)
 */
public final class HpoDisease {
  /** Name of the disease from annotation. */
  private final String name;
  /** The disease identifier as a CURIE, e.g., OMIM:600100. */
  private final TermId diseaseDatabaseId;

  /** {@link TermId}s with phenotypic abnormalities and their frequencies. */
  private final List<HpoAnnotation> phenotypicAbnormalities;

  /** {@link TermId}s with mode of inheritance and their frequencies. */
  private final List<TermId> modesOfInheritance;
  /** {@link TermId}s that do NOT characterize this disease. */
  private final List<TermId> negativeAnnotations;

  public TermId getDiseaseDatabaseId() {
    return diseaseDatabaseId;
  }

  /**
   * Constructor.
   *
   * @param name Name of the disease.
   * @param phenotypicAbnormalities {@link List} of phenotypic abnormalities with their frequencies.
   * @param modesOfInheritance {@link List} of modes of inheritance with their frequencies.
   */
  public HpoDisease(
      String name,
      TermId databaseId,
      List<HpoAnnotation> phenotypicAbnormalities,
      List<TermId> modesOfInheritance,
      List<TermId> notTerms) {
    this.name = name;
    this.diseaseDatabaseId = databaseId;
    this.phenotypicAbnormalities = ImmutableList.copyOf(phenotypicAbnormalities);
    this.modesOfInheritance = ImmutableList.copyOf(modesOfInheritance);
    this.negativeAnnotations = ImmutableList.copyOf(notTerms);
  }

  /** @return The name of the disease. */
  public String getName() {
    return name;
  }

  /** @return the count of the non-negated annotations excluding mode of inheritance. */
  public int getNumberOfPhenotypeAnnotations() {
    return this.phenotypicAbnormalities.size();
  }

  /** @return The list of frequency-annotated phenotypic abnormalities. */
  public List<HpoAnnotation> getPhenotypicAbnormalities() {
    return phenotypicAbnormalities;
  }

  /** @return The list of frequency-annotated modes of inheritance. */
  public List<TermId> getModesOfInheritance() {
    return modesOfInheritance;
  }

  public List<TermId> getNegativeAnnotations() {
    return this.negativeAnnotations;
  }

  /**
   * Users can user this function to get the HpoTermId corresponding to a TermId
   *
   * @param id id of the plain {@link TermId} for which we want to have the {@link HpoAnnotation}.
   * @return corresponding {@link HpoAnnotation} or null if not present.
   */
  public HpoAnnotation getAnnotation(TermId id) {
    return phenotypicAbnormalities
        .stream()
        .filter(timd -> timd.getTermId().equals(id))
        .findAny()
        .orElse(null);
  }

  /**
   * @param tid ID of an HPO TermI
   * @return true if there is a direct annotation to tid. Does not include indirect annotations from
   *     annotation propagation rule.
   */
  public boolean isDirectlyAnnotatedTo(TermId tid) {
    for (HpoAnnotation tiwm : phenotypicAbnormalities) {
      if (tiwm.getTermId().equals(tid)) return true;
    }
    return false;
  }
  /**
   * @param tidset Set of ids of HPO Terms
   * @return true if there is a direct annotation to any of the terms in tidset. Does not include
   *     indirect annotations from annotation propagation rule.
   */
  public boolean isDirectlyAnnotatedToAnyOf(Set<TermId> tidset) {
    for (HpoAnnotation tiwm : phenotypicAbnormalities) {
      if (tidset.contains(tiwm.getTermId())) return true;
    }
    return false;
  }

  /**
   * Returns the mean frequency of the feature in the disease.
   *
   * @param tid id of an HPO term
   * @return frequency of the phenotypic feature in individuals with the annotated disease
   */
  public double getFrequencyOfTermInDisease(TermId tid) {
    HpoAnnotation tiwm =
        phenotypicAbnormalities
            .stream()
            .filter(twm -> twm.getTermId().equals(tid))
            .findFirst()
            .orElse(null);
    if (tiwm == null) {
      return 0D; // term not annotated to disease so frequency is zero
    } else return tiwm.getFrequency();
  }

  @Override
  public String toString() {
    String abnormalityList =
        phenotypicAbnormalities
            .stream()
            .map(HpoAnnotation::getIdWithPrefix)
            .collect(Collectors.joining(";"));
    return String.format(
        "HpoDisease [name=%s;%s] phenotypicAbnormalities=\n%s" + ", modesOfInheritance=%s",
        name, diseaseDatabaseId.getIdWithPrefix(), abnormalityList, modesOfInheritance);
  }

  /** @return the {@code DB} field of the annotation, e.g., OMIM, ORPHA, or DECIPHER (prefix of the diseaseId) */
  public String getDatabase() {
    return diseaseDatabaseId.getPrefix().getValue();
  }
}
