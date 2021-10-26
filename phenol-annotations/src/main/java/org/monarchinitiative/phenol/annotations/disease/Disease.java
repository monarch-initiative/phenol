package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A general model of a disease that is identified by {@link org.monarchinitiative.phenol.ontology.data.TermId} and
 * a <code>name</code>, and that manifests with <code>diseaseFeatures</code>.
 * <p>
 * Each disease feature represents an observable trait. The feature data includes frequency and duration of the trait.
 * If a feature is never observed in the disease, or if a feature cannot be present in an individual with a disease,
 * then {@link DiseaseFeature#frequency()} is <code>zero</code>.
 *
 * @see DiseaseFeature
 */
public interface Disease {

  TermId id();

  String name();

  Stream<DiseaseFeature> diseaseFeatures();

  // -------------------------------------------- derivatives ----------------------------------------------------------

  default Set<TermId> diseaseFeatureTermIds() {
    return diseaseFeatures()
      .map(DiseaseFeature::id)
      .collect(Collectors.toSet());
  }

}
