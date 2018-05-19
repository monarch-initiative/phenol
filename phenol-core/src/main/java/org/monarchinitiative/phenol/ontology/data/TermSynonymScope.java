package org.monarchinitiative.phenol.ontology.data;

/**
 * Enumeration for describing the synonym's scope.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum TermSynonymScope {
  /** Synonym value is exact match. */
  EXACT,
  /** Synonym value is broader than annotated term. */
  BROAD,
  /** Synonym value is narrower than annoated term. */
  NARROW,
  /** None of the others (<code>EXACT</code>, <code>BROAD</code>, <code>NARROW</code>). */
  RELATED
}
