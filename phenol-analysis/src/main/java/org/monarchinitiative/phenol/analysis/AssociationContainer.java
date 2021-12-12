package org.monarchinitiative.phenol.analysis;

import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Set;

/**
 * This is the interface for the Ontology (usually: Gene Ontology) annotations.
 * It organizes the relationships netween the annotated items (e.g., genes/proteins)
 * and the ontology terms that annotate them (e.g., GO terms). It also allows bot
 * explicit (direct) and inferred (indirect) annotations to be retrieved. The
 * {@link GoAssociationContainer} is the implementation that we use mainly for
 * Gene Ontology annotations. Other implementations are made for isopret.
 * @author Peter N Robinson
 */
public interface AssociationContainer {

  Multimap<TermId, TermId> getTermToItemMultimap();

  ItemAssociations get(TermId dbObjectId) throws PhenolException;

  Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds);

  Set<TermId> getAllAnnotatedGenes();

}
