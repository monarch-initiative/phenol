package com.github.phenomics.ontolib.ontology.scoredist;

import com.github.phenomics.ontolib.base.OntoLibRuntimeException;

/**
 * Thrown in case of problems in {@link ScoreDistributions}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class CannotMergeScoreDistributions extends OntoLibRuntimeException {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param message Message for the exception.
   * @param cause Causing exception for chained exceptions.
   */
  public CannotMergeScoreDistributions(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Message for the exception.
   */
  public CannotMergeScoreDistributions(String message) {
    super(message);
  }

}
