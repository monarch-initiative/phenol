package org.monarchinitiative.phenol.ontology.data;

import java.util.Map;

/**
 * Represent the information of an dbxref from an OBO file.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Dbxref {

  /** @return Name of the dbxref (first component). */
  String getName();

  /** @return Description of the dbxref (second component, optional, {@code null} if missing). */
  String getDescription();

  /**
   * @return Trailing modifiers of the dbxref (third component, optional, {@code null} if missing).
   */
  Map<String, String> getTrailingModifiers();
}
