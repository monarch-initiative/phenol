package de.charite.compbio.ontolib.ontology.similarity;

/**
 * Enumeration for describing similaritiy.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum Similarities {
  /** Cosine similarity. */
  COSINE,
  /** Information content similarity. */
  INFORMATION_CONTENT,
  /** IC-weighted Jaccard similarity. */
  JACCARD_IC_WEIGHTED,
  /** Jaccard similarity. */
  JACCARD,
  /** Jiang similarity. */
  JIANG,
  /** Lin similarity. */
  LIN,
  /** Resnik similarity. */
  RESNIK,
  /** Simple feature vector similarity. */
  SIMPLE_FEATURE_VECTOR,
  /** Term overlap similarity. */
  TERM_OVERLAP;
}
