package org.monarchinitiative.phenol.annotations.analysis;


import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;

/**
 * This interface represents all HpoOnset annotations for a disease. This includes a list of global
 * HpoOnset annotations that describe the age at which the disease was first brought to medical attention
 * Some of these annotations have frequencies such as 3/7, meaning that say 3 out of 7 patients in a cohort
 * study had disease onset at "Childhood onset (HP:??)". Some diseases additionally have onset information
 * for individual disease features. Implementations of this interface may optionally use this information
 * to provide results about the global onset. For instance, if a disease does not have a global onset annotation
 * but does have an annotation for a phenotypic feature, then we can use this for the global annotation.
 * In some cases, no onset information is available, and implementations need to decide what to do.
 */
public interface HpoOnsetDistribution {

  /**
   * This method can be used to determine whether a patient (as represented in a Phenopacket, for example) has come
   * to medical attention at an Age that corresponds to what we expect for a disease. For instance, an implementation
   * could return true if the age is within one of the HPO Onset terms used to annotate a disease, otherwise false
   *
   * @param interval representing the age of a patient
   * @return true if the age corresponds to the onset information of the disease
   */
  boolean isObservableInAge(HpoDisease disease, TemporalInterval interval);

  /**
   * This method can be used to determine whether a patient (as represented in a Phenopacket, for example) has come
   * to medical attention at an <code>age</code> that corresponds to what we expect for a disease.
   * For instance, an implementation could return <code>true</code> if the <code>age</code> is within one of
   * the HPO Onset terms used to annotate a disease, otherwise <code>false</code>.
   *
   * @param age of the patient
   * @return true if the age corresponds to the onset information of the disease
   */
  default boolean isObservableInAge(HpoDisease disease, Age age) {
    return isObservableInAge(disease, TemporalInterval.openEnd(age));
  }

  /**
   * This method can return a probability of a patient presenting at the indicated age if the patient has the disease.
   * By default, we return 1.0 if the patient is within the age range, otherwise zero, but implementations can
   * implement more sophisticated probability distributions.
   *
   * @param interval representing the age of a patient
   */
  default double probabilityOfPresentationAtAge(HpoDisease disease, TemporalInterval interval) {
    return isObservableInAge(disease, interval) ? 1.0 : 0.0;
  }

}
