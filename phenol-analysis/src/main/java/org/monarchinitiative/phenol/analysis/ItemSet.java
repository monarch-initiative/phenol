package org.monarchinitiative.phenol.analysis;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Set;

/**
 * Interface for item sets for overrepresentation analysis.
 * The generic type T can be a TermId if the item are represented
 * as {@link TermId}, as may be the case for Gene Ontology.
 * @param <T> The type of the annotated items.
 * @author Peter N Robinson
 */
public interface ItemSet<T> {
  /** Key -- TermId for a domain item. Value -- Object with Ontologuy annotations of the item.*/
  Map<T, DirectAndIndirectTermAnnotations> getAnnotationMap();
  /** Set of annotated items. */
  Set<T> getGeneSet();

  /**
   * @param itemId Id of a domain item.
   * @return number of Ontology terms that annotate the given item.
   */
  int getDirectAnnotationCount(T itemId);
}
