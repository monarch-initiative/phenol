package org.monarchinitiative.phenol.io.obo.facade;

/**
 * Enumeration for selecting ontology (e.g., for ontology-based priotizer).
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum OntologyType {
  /** Gene Ontology. */
  GO,
  /** Mammalian Phenotype Ontology. */
  MPO,
  /** Human Phenotype Ontology. */
  HPO,
  /** Uberpheno Ontology. */
  UBERPHENO,
  /** Upheno Ontology. */
  UPHENO;
}
