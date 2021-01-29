package org.monarchinitiative.phenol.analysis;

import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Set;

public interface AssociationContainer {

  int getOntologyTermCount();

  Multimap<TermId, TermId> getTermToItemMultimap();

  ItemAssociations get(TermId dbObjectId) throws PhenolException;

  Map<TermId, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds);

  Set<TermId> getAllAnnotatedGenes();

}
